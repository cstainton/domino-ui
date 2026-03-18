package org.dominokit.domino.ui.utils;

public interface DominoI18nProvider {

  /** Formats a number according to a given pattern. */
  String formatNumber(Number number, String pattern);

  /** Parses a formatted string back into a number according to a given pattern. */
  Number parseNumber(String text, String pattern);

  /** Formats a date according to a given pattern. */
  String formatDate(java.util.Date date, String pattern);

  /** Parses a formatted string back into a date according to a given pattern. */
  java.util.Date parseDate(String text, String pattern);

  /** Provides an array of localized full month names (e.g. ["January", "February", ...]) */
  String[] getMonthsNames();

  /** Provides an array of localized short month names (e.g. ["Jan", "Feb", ...]) */
  String[] getMonthsShortNames();

  /**
   * Provides an array of localized full weekday names starting with Sunday (e.g. ["Sunday",
   * "Monday", ...])
   */
  String[] getWeekdaysNames();

  /** Provides an array of localized short weekday names (e.g. ["Sun", "Mon", ...]) */
  String[] getWeekdaysShortNames();
}
