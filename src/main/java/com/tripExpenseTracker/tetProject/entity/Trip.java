package com.tripExpenseTracker.tetProject.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Trip name is required")
    private String name;

    private String tripType; // "solo" or "group"
    
    @Column(nullable = false)
    private String tripUID;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private String tripStatus = "CREATED";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<Expense> expenses = new ArrayList<>();
    
    public void addParticipant(Participant participant) {
        participants.add(participant);
        participant.setTrip(this);
    }
}