package com.tripExpenseTracker.tetProject.service.impl;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tripExpenseTracker.tetProject.entity.PasswordResetToken;
import com.tripExpenseTracker.tetProject.entity.User;
import com.tripExpenseTracker.tetProject.repository.PasswordResetTokenRepository;
import com.tripExpenseTracker.tetProject.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResetPasswordService {

	private final JavaMailSender mailSender;
	private final UserRepository userRepo;
	private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${FRONTEND_URL}") 
    private String frontendURL;
    
    @Async
    @Transactional
    public void processForgotPassword(String email) throws MessagingException {
        // 1. Find user by email
        Optional<User> userOptional = userRepo.findByEmail(email);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 2. Generate unique token
            String token = UUID.randomUUID().toString();
            
            // 3. Create and save the token entity
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUser(user);
            resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
            
            // Delete any old tokens for this user first
            tokenRepository.deleteByUser(user);
            tokenRepository.save(resetToken);

            // 4. Prepare the Email
            String resetUrl = String.format("%s?token=%s", frontendURL, token);

        	String htmlContent = String.format("""
        	    <div style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; border: 1px solid #e1e1e1; padding: 20px; border-radius: 10px;">
        	        <h2 style="color: #2e6c80; text-align: center;">Password Reset Request</h2>
        	        <p>Dear User,</p>
        	        <p>We received a request to reset the password from your <strong>Travel Expense Tracker</strong> account. Click the button below to proceed with the reset:</p>
        	        
        	        <div style="text-align: center; margin: 30px 0;">
        	            <a href="%s" style="background-color: #2e6c80; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;">Reset Password</a>
        	        </div>
        	        
        	        <p style="font-size: 0.9em; color: #666;">
        	            <strong>Note:</strong> This link is valid for <strong>30 minutes</strong> only. If you did not request a password reset, please ignore this email or contact support if you have concerns.
        	        </p>
        	        
        	        <hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;">
        	        
        	        <p style="font-size: 0.8em; color: #999; text-align: center;">
        	            If the button doesn't work, copy and paste this link into your browser:<br>
        	            <span style="color: #2e6c80; word-break: break-all;">%s</span>
        	        </p>
        	    </div>
        	    """, resetUrl, resetUrl);

            // 5. Send it
             sendHtmlEmail(email, "Reset your Travel_Expense_Tracker account Password", htmlContent);
        	
        }
        // If user is not present, we do nothing (security best practice)
    }
    
    @Transactional
    public void updatePassword(String token, String newPassword) {
        // 1. Find the token in the database
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid password reset token."));

        // 2. Check if the token has expired
        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new IllegalArgumentException("Reset link has expired. Please request a new one.");
        }

        // 3. Update the User's password
        User user = resetToken.getUser();
        // CRITICAL: Always encode the password before saving!
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        // 4. Delete the token so it's "One-Time Use" only
        tokenRepository.delete(resetToken);
    }
    
    @Async
    public void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        
        // "true" indicates this is a multipart message (needed for HTML/Attachments)
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // The second parameter "true" makes it HTML
        helper.setFrom("duos78550@gmail.com");

        mailSender.send(message);
    }
    
    @Async
    public void sendEmailWithAttachment(String to, String subject, String body, File file) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);
        
        // Adding the attachment
        helper.addAttachment(file.getName(), file);

        mailSender.send(message);
    }
}
