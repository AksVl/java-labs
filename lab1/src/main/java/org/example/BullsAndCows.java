package org.example;

import java.util.Random;

public interface BullsAndCows {
  public enum GuessValidationResult {
    MATCH,
    MISMATCH,
    INVALID,
    QUIT
  }

  public static GuessValidationResult validateGuess(String guess, String secret) {
    if (guess == null || guess.isEmpty()) {
      return GuessValidationResult.INVALID;
    }
    if (guess.charAt(0) == 'q') {
      return GuessValidationResult.QUIT;
    }
    if (guess.length() != secret.length()) {
      return GuessValidationResult.INVALID;
    }
    for (int i = 0; i < guess.length(); i++) {
      if (!Character.isDigit(guess.charAt(i))) {
        return GuessValidationResult.INVALID;
      }
    }
    if (guess.equals(secret)) {
      return GuessValidationResult.MATCH;
    }
    return GuessValidationResult.MISMATCH;
  }

  public static String generateSecret(int length) {
    Random rand = new Random();
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(rand.nextInt(10));
    }
    return sb.toString();
  }

  public static int[] getBullsAndCows(String guess, String secret) {
    if (guess.length() != secret.length()) {
      throw new IllegalArgumentException("Guess length must match secret length");
    }
    int bulls = 0;
    int[] secretCount = new int[10];
    int[] guessCount = new int[10];

    for (int i = 0; i < secret.length(); i++) {
      char s = secret.charAt(i);
      char g = guess.charAt(i);
      if (s == g) {
        bulls++;
      } else {
        secretCount[s - '0']++;
        guessCount[g - '0']++;
      }
    }

    int cows = 0;
    for (int i = 0; i < 10; i++) {
      cows += Math.min(secretCount[i], guessCount[i]);
    }
    return new int[]{bulls, cows};
  }
}