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
package org.gwtproject.i18n.shared.cldr.impl;

import elemental2.core.JsDate;
import org.gwtproject.i18n.shared.browser.JsIntlDateTimeFormat;
import org.gwtproject.i18n.shared.browser.JsIntlDateTimeFormatOptions;
import org.gwtproject.i18n.shared.cldr.DateTimeFormatInfo;

/**
 * {@link DateTimeFormatInfo} implementation that derives every locale-specific value from the
 * browser's {@code Intl.DateTimeFormat} API at runtime. No CLDR data is bundled; formatting
 * automatically reflects the user's browser locale.
 *
 * <p>Arrays (month names, weekday names, AM/PM strings) are computed lazily on first access and
 * then cached for the lifetime of this instance.
 */
public class BrowserDateTimeFormatInfo implements DateTimeFormatInfo {

  // ---- internal format tokens understood by DateTimeFormat ----

  public static final String DATE_FULL = "dtf:date:full";
  public static final String DATE_LONG = "dtf:date:long";
  public static final String DATE_MEDIUM = "dtf:date:medium";
  public static final String DATE_SHORT = "dtf:date:short";
  public static final String TIME_FULL = "dtf:time:full";
  public static final String TIME_LONG = "dtf:time:long";
  public static final String TIME_MEDIUM = "dtf:time:medium";
  public static final String TIME_SHORT = "dtf:time:short";
  public static final String TIME_H24MS = "dtf:h24ms";
  public static final String TIME_H24M = "dtf:h24m";
  public static final String TIME_H12MS = "dtf:h12ms";
  public static final String TIME_H12M = "dtf:h12m";

  private final String locale;

  /** Use the browser's default locale. */
  public BrowserDateTimeFormatInfo() { this(null); }

  /** Use an explicit BCP 47 locale for calendar labels. */
  public BrowserDateTimeFormatInfo(String locale) { this.locale = locale; }

  private JsIntlDateTimeFormat formatter(JsIntlDateTimeFormatOptions options) {
    elemental2.core.JsArray<String> locales = new elemental2.core.JsArray<>();
    if (locale != null && !locale.isEmpty()) locales.push(locale);
    return new JsIntlDateTimeFormat(locales, options);
  }

  // ---- cached locale data (computed on first call) ----

  private String[] monthsFull;
  private String[] monthsShort;
  private String[] weekdaysFull;
  private String[] weekdaysShort;
  private String[] ampms;

  // ---- DateTimeFormatInfo impl ----

  @Override
  public String[] monthsFull() {
    if (monthsFull == null) {
      monthsFull = buildMonthNames("long");
    }
    return monthsFull;
  }

  @Override
  public String[] monthsShort() {
    if (monthsShort == null) {
      monthsShort = buildMonthNames("short");
    }
    return monthsShort;
  }

  @Override
  public String[] weekdaysFull() {
    if (weekdaysFull == null) {
      weekdaysFull = buildWeekdayNames("long");
    }
    return weekdaysFull;
  }

  @Override
  public String[] weekdaysShort() {
    if (weekdaysShort == null) {
      weekdaysShort = buildWeekdayNames("short");
    }
    return weekdaysShort;
  }

  @Override
  public String[] ampms() {
    if (ampms == null) {
      ampms = buildAmpms();
    }
    return ampms;
  }

  @Override
  public int firstDayOfTheWeek() {
    // 0 = Sunday (ISO default for many locales; a future improvement can read
    // Intl.Locale.weekInfo.firstDay when it has wider browser support).
    return 0;
  }

  @Override
  public int weekendStart() {
    return 6; // Saturday
  }

  @Override
  public int weekendEnd() {
    return 0; // Sunday
  }

  // ---- date format tokens ----

  @Override
  public String dateFormatFull() {
    return DATE_FULL;
  }

  @Override
  public String dateFormatLong() {
    return DATE_LONG;
  }

  @Override
  public String dateFormatMedium() {
    return DATE_MEDIUM;
  }

  @Override
  public String dateFormatShort() {
    return DATE_SHORT;
  }

  // ---- time format tokens ----

  @Override
  public String timeFormatFull() {
    return TIME_FULL;
  }

  @Override
  public String timeFormatLong() {
    return TIME_LONG;
  }

  @Override
  public String timeFormatMedium() {
    return TIME_MEDIUM;
  }

  @Override
  public String timeFormatShort() {
    return TIME_SHORT;
  }

  // ---- explicit component tokens ----

  @Override
  public String formatHour24MinuteSecond() {
    return TIME_H24MS;
  }

  @Override
  public String formatHour24Minute() {
    return TIME_H24M;
  }

  @Override
  public String formatHour12MinuteSecond() {
    return TIME_H12MS;
  }

  @Override
  public String formatHour12Minute() {
    return TIME_H12M;
  }

  // ---- private helpers ----

  /**
   * Builds a 12-element array of month names by formatting the 15th day of each month (year 2000)
   * with the given {@code month} style. Index 0 = January.
   */
  private String[] buildMonthNames(String style) {
    JsIntlDateTimeFormatOptions opts = JsIntlDateTimeFormatOptions.create();
    opts.month = style;
    JsIntlDateTimeFormat fmt = formatter(opts);
    String[] names = new String[12];
    for (int m = 0; m < 12; m++) {
      names[m] = fmt.format(new JsDate(2000, m, 15));
    }
    return names;
  }

  /**
   * Builds a 7-element array of weekday names, Sunday-first (index 0 = Sunday), by formatting known
   * dates in year 2000 (Jan 2 = Sunday) with the given {@code weekday} style.
   */
  private String[] buildWeekdayNames(String style) {
    JsIntlDateTimeFormatOptions opts = JsIntlDateTimeFormatOptions.create();
    opts.weekday = style;
    JsIntlDateTimeFormat fmt = formatter(opts);
    String[] names = new String[7];
    // 2000-01-02 is a Sunday; step forward one day at a time
    for (int d = 0; d < 7; d++) {
      names[d] = fmt.format(new JsDate(2000, 0, 2 + d));
    }
    return names;
  }

  /**
   * Derives AM / PM strings by formatting a morning and an afternoon time and extracting the {@code
   * "dayPeriod"} part from {@code formatToParts()}.
   */
  private String[] buildAmpms() {
    JsIntlDateTimeFormatOptions opts = JsIntlDateTimeFormatOptions.create();
    opts.hour = "numeric";
    opts.hour12 = true;
    JsIntlDateTimeFormat fmt = formatter(opts);

    String am = extractPart(fmt, new JsDate(2000, 0, 1, 9, 0, 0), "dayPeriod");
    String pm = extractPart(fmt, new JsDate(2000, 0, 1, 15, 0, 0), "dayPeriod");

    // Fall back to ASCII if the browser does not include dayPeriod parts
    if (am == null || am.isEmpty()) am = "AM";
    if (pm == null || pm.isEmpty()) pm = "PM";
    return new String[] {am, pm};
  }

  /** Returns the {@code value} of the first part whose {@code type} matches {@code partType}. */
  private static String extractPart(JsIntlDateTimeFormat fmt, JsDate date, String partType) {
    elemental2.core.JsArray<org.gwtproject.i18n.shared.browser.JsIntlFormatPart> parts =
        fmt.formatToParts(date);
    for (int i = 0; i < (int) parts.length; i++) {
      if (partType.equals(parts.getAt(i).type)) {
        return parts.getAt(i).value;
      }
    }
    return null;
  }
}
