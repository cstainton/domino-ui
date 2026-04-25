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
package org.gwtproject.i18n.shared.cldr;

/**
 * Stub of GWT's {@code DateTimeFormatInfo} interface, covering the subset of methods used by
 * domino-ui. The browser-backed implementation ({@code BrowserDateTimeFormatInfo}) uses the {@code
 * Intl.DateTimeFormat} API to derive locale-specific values at runtime so no CLDR data needs to be
 * bundled.
 */
public interface DateTimeFormatInfo {

  // ---- locale data arrays ----

  /** Full weekday names, Sunday-first (index 0 = Sunday). */
  String[] weekdaysFull();

  /** Short weekday names, Sunday-first (index 0 = Sunday). */
  String[] weekdaysShort();

  /** Full month names, January-first (index 0 = January). */
  String[] monthsFull();

  /** Short month names, January-first (index 0 = January). */
  String[] monthsShort();

  /** AM / PM marker strings: index 0 = AM, index 1 = PM. */
  String[] ampms();

  // ---- week / weekend config ----

  /**
   * The first day of the week, as an integer in the range 0–6 where 0 = Sunday and 6 = Saturday.
   */
  int firstDayOfTheWeek();

  /** The day of the week on which the weekend starts (0 = Sunday). */
  int weekendStart();

  /** The day of the week on which the weekend ends (0 = Sunday). */
  int weekendEnd();

  // ---- date format tokens ----

  /** Token representing the locale's "full" date format (e.g. {@code "Tuesday, April 5, 2022"}). */
  String dateFormatFull();

  /** Token representing the locale's "long" date format (e.g. {@code "April 5, 2022"}). */
  String dateFormatLong();

  /** Token representing the locale's "medium" date format (e.g. {@code "Apr 5, 2022"}). */
  String dateFormatMedium();

  /** Token representing the locale's "short" date format (e.g. {@code "4/5/22"}). */
  String dateFormatShort();

  // ---- time format tokens ----

  /** Token representing the locale's "full" time format. */
  String timeFormatFull();

  /** Token representing the locale's "long" time format. */
  String timeFormatLong();

  /** Token representing the locale's "medium" time format. */
  String timeFormatMedium();

  /** Token representing the locale's "short" time format. */
  String timeFormatShort();

  // ---- explicit time-component tokens ----

  /** Token for a 24-hour {@code HH:mm:ss} pattern. */
  String formatHour24MinuteSecond();

  /** Token for a 24-hour {@code HH:mm} pattern. */
  String formatHour24Minute();

  /** Token for a 12-hour {@code h:mm:ss a} pattern. */
  String formatHour12MinuteSecond();

  /** Token for a 12-hour {@code h:mm a} pattern. */
  String formatHour12Minute();
}
