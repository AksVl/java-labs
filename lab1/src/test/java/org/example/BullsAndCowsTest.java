package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BullsAndCowsTest {

  @Test
  void generateSecret_shouldReturnStringOfGivenLength() {
    int length = 5;
    String secret = BullsAndCows.generateSecret(length);
    assertNotNull(secret);
    assertEquals(length, secret.length());
    assertTrue(secret.chars().allMatch(Character::isDigit));
  }

  @ParameterizedTest
  @MethodSource("provideGuessValidationScenarios")
  void validateGuess_shouldReturnExpectedResult(String guess, String secret, BullsAndCows.GuessValidationResult expected) {
    assertEquals(expected, BullsAndCows.validateGuess(guess, secret));
  }

  private static Stream<Arguments> provideGuessValidationScenarios() {
    return Stream.of(
            Arguments.of(null, "1234", BullsAndCows.GuessValidationResult.INVALID),
            Arguments.of("", "1234", BullsAndCows.GuessValidationResult.INVALID),
            Arguments.of("q", "1234", BullsAndCows.GuessValidationResult.QUIT),
            Arguments.of("quit", "1234", BullsAndCows.GuessValidationResult.INVALID),
            Arguments.of("123", "1234", BullsAndCows.GuessValidationResult.INVALID),
            Arguments.of("12a4", "1234", BullsAndCows.GuessValidationResult.INVALID),
            Arguments.of("1234", "1234", BullsAndCows.GuessValidationResult.MATCH),
            Arguments.of("5678", "1234", BullsAndCows.GuessValidationResult.MISMATCH)
    );
  }

  @Test
  void getBullsAndCows_shouldReturnCorrectCounts() {
    assertArrayEquals(new int[]{4, 0}, BullsAndCows.getBullsAndCows("1234", "1234"));
    assertArrayEquals(new int[]{0, 4}, BullsAndCows.getBullsAndCows("4321", "1234"));
    assertArrayEquals(new int[]{2, 2}, BullsAndCows.getBullsAndCows("1243", "1234"));
    assertArrayEquals(new int[]{0, 0}, BullsAndCows.getBullsAndCows("5678", "1234"));
  }

  @Test
  void getBullsAndCows_shouldThrowIfLengthsDiffer() {
    assertThrows(IllegalArgumentException.class,
            () -> BullsAndCows.getBullsAndCows("123", "1234"));
  }
}