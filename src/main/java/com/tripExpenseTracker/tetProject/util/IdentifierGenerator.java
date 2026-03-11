package com.tripExpenseTracker.tetProject.util;

import java.util.UUID;

public class IdentifierGenerator {
	public static String generate(String prefix) {
		String UID = UUID.randomUUID().toString().toUpperCase().substring(0, 8);
		return prefix.toUpperCase() + "-" + UID;
	}
}
