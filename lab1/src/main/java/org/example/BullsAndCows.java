package org.example;

import java.util.Random;

/**
 * provides core game logic for secret generation, guess validation, and bulls and cows calculation.
 */
public interface BullsAndCows {

  enum GuessValidationResult {
    MATCH,
    MISMATCH,
    INVALID,
    QUIT
  }

  /**
   * validates a player's guess
   *
   * @param guess  the player's input
   * @param secret the secret string
   * @return {@code GuessValidationResult} validation outcome
   */
  static GuessValidationResult validateGuess(String guess, String secret) {
    if (guess == null || guess.isEmpty()) {
      return GuessValidationResult.INVALID;
    }
    if (guess.equals("q") || guess.equals("Q")) {
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

  /**
   * generates a secret number of specified length
   *
   * @param length the number of digits in the secret
   * @return a string of {@code length} random digits
   */
  static String generateSecret(int length) {
    Random rand = new Random();
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(rand.nextInt(10));
    }
    return sb.toString();
  }

  /**
   * calculates the number of bulls and cows for a guess
   * bulls are digits that are correct and in the right position
   * cows are digits that are correct but in the wrong position
   *
   * @param guess  player's guess
   * @param secret the secret number
   * @return int array where index 0 - number of bulls, index 1 - number of cows
   * @throws IllegalArgumentException if guess and secret have different lengths
   */
  static int[] getBullsAndCows(String guess, String secret) {
    if (guess.length() != secret.length()) {
      throw new IllegalArgumentException("length mismatch");
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