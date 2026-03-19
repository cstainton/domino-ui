/*
 * Copyright © 2019 Dominokit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

  /** Returns the localized AM string. */
  String getAm();

  /** Returns the localized PM string. */
  String getPm();

  /** Returns the first day of the week (e.g. 0 for Sunday, 1 for Monday). */
  int getFirstDayOfTheWeek();

  /** Returns the localized minus sign character. */
  String getMinusSign();

  /** Returns the localized decimal separator. */
  String getDecimalSeparator();
}
