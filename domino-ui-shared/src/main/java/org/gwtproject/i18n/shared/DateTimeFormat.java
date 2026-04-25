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
package org.gwtproject.i18n.shared;

import elemental2.core.JsDate;
import java.util.Date;
import org.gwtproject.i18n.shared.browser.JsIntlDateTimeFormat;
import org.gwtproject.i18n.shared.browser.JsIntlDateTimeFormatOptions;
import org.gwtproject.i18n.shared.cldr.DateTimeFormatInfo;
import org.gwtproject.i18n.shared.cldr.impl.BrowserDateTimeFormatInfo;

/**
 * Stub of GWT's {@code DateTimeFormat} that delegates formatting to the browser's {@code
 * Intl.DateTimeFormat} API and parsing to JavaScript's {@code Date.parse()} with a {@code
 * java.text.SimpleDateFormat} fallback.
 *
 * <p>Pattern tokens starting with {@code "dtf:"} are internal sentinels produced by {@link
 * BrowserDateTimeFormatInfo} that map to specific {@code Intl} options. All other pattern strings
 * are treated as {@code SimpleDateFormat} patterns.
 *
 * <p>This class is subclassable so that {@code DateFormatter.DefaultFormatter} and {@code
 * TimeFormatter.DefaultFormatter} can extend it, matching the GWT API shape.
 */
public class DateTimeFormat {

  // ---- sentinel prefix ----
  private static final String DTF_PREFIX = "dtf:";

  // ---- instance state ----

  /** The pattern token or SimpleDateFormat pattern this instance was created with. */
  protected final String pattern;

  /** Cached Intl formatter; non-null only when {@link #pattern} is a sentinel. */
  private JsIntlDateTimeFormat intlFormatter;

  // ---- constructors ----

  /** Primary constructor — called by static factories and by subclasses via {@code super(...)}. */
  protected DateTimeFormat(String pattern) {
    this.pattern = pattern;
  }

  // ---- public API ----

  /** Returns the pattern this formatter was created with. */
  public String getPattern() {
    return pattern;
  }

  /**
   * Formats {@code date} using this formatter's locale and pattern.
   *
   * <p>Sentinel patterns delegate to {@code Intl.DateTimeFormat}; explicit patterns use {@code
   * java.text.SimpleDateFormat}.
   */
  public String format(Date date) {
    if (isSentinel(pattern)) {
      return intlFormatter().format(toJsDate(date));
    }
    return new java.text.SimpleDateFormat(pattern).format(date);
  }

  /**
   * Parses {@code text} leniently, returning a {@link Date} or {@code null} if parsing fails.
   *
   * <p>For sentinel patterns the browser's {@code Date.parse()} is tried first (it understands the
   * strings it produces via {@code Intl}); on failure a best-effort {@code SimpleDateFormat} is
   * used. For explicit patterns {@code SimpleDateFormat} is used directly.
   */
  public Date parse(String text) {
    try {
      return parseInternal(text, false);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Parses {@code text} strictly, throwing {@link IllegalArgumentException} if it does not match
   * the expected format.
   */
  public Date parseStrict(String text) {
    try {
      return parseInternal(text, true);
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalArgumentException("Cannot parse date: " + text, e);
    }
  }

  // ---- static factories ----

  /**
   * Returns a {@link DateTimeFormat} for the given pattern or sentinel token. The returned instance
   * uses {@code Intl.DateTimeFormat} if {@code pattern} is a sentinel, otherwise {@code
   * SimpleDateFormat}.
   */
  public static DateTimeFormat getFormat(String pattern) {
    return new DateTimeFormat(pattern);
  }

  /** Returns a {@link DateTimeFormat} for the given predefined format. */
  public static DateTimeFormat getFormat(PredefinedFormat predefined) {
    return getFormat(predefined.token());
  }

  /**
   * Returns a {@link DateTimeFormat} for the given pattern, with locale hints from {@code dtfi}. In
   * this stub the {@code dtfi} argument is unused because locale is determined by the browser at
   * runtime; it is accepted for API compatibility with the GWT original.
   */
  public static DateTimeFormat getFormat(String pattern, DateTimeFormatInfo dtfi) {
    return getFormat(pattern);
  }

  // ---- PredefinedFormat enum ----

  /**
   * Mirrors the subset of GWT's {@code PredefinedFormat} values used by domino-ui. Each value maps
   * to a {@code "dtf:"} sentinel token that {@link DateTimeFormat} can route to the correct {@code
   * Intl.DateTimeFormat} options.
   */
  public enum PredefinedFormat {
    DATE_FULL(BrowserDateTimeFormatInfo.DATE_FULL),
    DATE_LONG(BrowserDateTimeFormatInfo.DATE_LONG),
    DATE_MEDIUM(BrowserDateTimeFormatInfo.DATE_MEDIUM),
    DATE_SHORT(BrowserDateTimeFormatInfo.DATE_SHORT),
    TIME_FULL(BrowserDateTimeFormatInfo.TIME_FULL),
    TIME_LONG(BrowserDateTimeFormatInfo.TIME_LONG),
    TIME_MEDIUM(BrowserDateTimeFormatInfo.TIME_MEDIUM),
    TIME_SHORT(BrowserDateTimeFormatInfo.TIME_SHORT),
    DATE_TIME_FULL(BrowserDateTimeFormatInfo.DATE_FULL),
    DATE_TIME_LONG(BrowserDateTimeFormatInfo.DATE_LONG),
    DATE_TIME_MEDIUM(BrowserDateTimeFormatInfo.DATE_MEDIUM),
    DATE_TIME_SHORT(BrowserDateTimeFormatInfo.DATE_SHORT),
    ISO_8601("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
    RFC_2822("EEE, dd MMM yyyy HH:mm:ss Z");

    private final String token;

    PredefinedFormat(String token) {
      this.token = token;
    }

    /** The internal token or SimpleDateFormat pattern for this predefined format. */
    public String token() {
      return token;
    }
  }

  // ---- private helpers ----

  private static boolean isSentinel(String pattern) {
    return pattern != null && pattern.startsWith(DTF_PREFIX);
  }

  /** Returns (and lazily builds) the {@code Intl.DateTimeFormat} for this sentinel pattern. */
  private JsIntlDateTimeFormat intlFormatter() {
    if (intlFormatter == null) {
      intlFormatter = JsIntlDateTimeFormat.create(sentinelToOptions(pattern));
    }
    return intlFormatter;
  }

  private static JsIntlDateTimeFormatOptions sentinelToOptions(String sentinel) {
    switch (sentinel) {
      case BrowserDateTimeFormatInfo.DATE_FULL:
        return JsIntlDateTimeFormatOptions.forDateStyle("full");
      case BrowserDateTimeFormatInfo.DATE_LONG:
        return JsIntlDateTimeFormatOptions.forDateStyle("long");
      case BrowserDateTimeFormatInfo.DATE_MEDIUM:
        return JsIntlDateTimeFormatOptions.forDateStyle("medium");
      case BrowserDateTimeFormatInfo.DATE_SHORT:
        return JsIntlDateTimeFormatOptions.forDateStyle("short");
      case BrowserDateTimeFormatInfo.TIME_FULL:
        return JsIntlDateTimeFormatOptions.forTimeStyle("full");
      case BrowserDateTimeFormatInfo.TIME_LONG:
        return JsIntlDateTimeFormatOptions.forTimeStyle("long");
      case BrowserDateTimeFormatInfo.TIME_MEDIUM:
        return JsIntlDateTimeFormatOptions.forTimeStyle("medium");
      case BrowserDateTimeFormatInfo.TIME_SHORT:
        return JsIntlDateTimeFormatOptions.forTimeStyle("short");
      case BrowserDateTimeFormatInfo.TIME_H24MS:
        return JsIntlDateTimeFormatOptions.create()
            .set("hour", "2-digit")
            .set("minute", "2-digit")
            .set("second", "2-digit")
            .set("hour12", false);
      case BrowserDateTimeFormatInfo.TIME_H24M:
        return JsIntlDateTimeFormatOptions.create()
            .set("hour", "2-digit")
            .set("minute", "2-digit")
            .set("hour12", false);
      case BrowserDateTimeFormatInfo.TIME_H12MS:
        return JsIntlDateTimeFormatOptions.create()
            .set("hour", "numeric")
            .set("minute", "2-digit")
            .set("second", "2-digit")
            .set("hour12", true);
      case BrowserDateTimeFormatInfo.TIME_H12M:
        return JsIntlDateTimeFormatOptions.create()
            .set("hour", "numeric")
            .set("minute", "2-digit")
            .set("hour12", true);
      default:
        // Unknown sentinel — fall back to medium date style
        return JsIntlDateTimeFormatOptions.forDateStyle("medium");
    }
  }

  private Date parseInternal(String text, boolean strict) {
    if (text == null || text.trim().isEmpty()) {
      if (strict) throw new IllegalArgumentException("Cannot parse empty date string");
      return null;
    }

    if (isSentinel(pattern)) {
      // Try JavaScript's native Date.parse first — it can often round-trip strings produced by Intl
      double millis = JsDate.parse(text);
      if (!Double.isNaN(millis)) {
        return new Date((long) millis);
      }
      if (strict) throw new IllegalArgumentException("Cannot parse date: " + text);
      return null;
    }

    // Explicit pattern: use SimpleDateFormat
    try {
      java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(pattern);
      sdf.setLenient(!strict);
      return sdf.parse(text);
    } catch (java.text.ParseException e) {
      if (strict)
        throw new IllegalArgumentException(
            "Cannot parse date '" + text + "' with pattern '" + pattern + "'", e);
      return null;
    }
  }

  private static JsDate toJsDate(Date date) {
    return new JsDate((double) date.getTime());
  }
}
