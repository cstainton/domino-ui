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
package org.gwtproject.i18n.shared.browser;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Options bag for the {@code Intl.DateTimeFormat} constructor. Only the fields used by domino-ui
 * are represented; additional fields can be set via {@link #set(String, Object)}.
 */
@JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
public class JsIntlDateTimeFormatOptions {

  /** Convenience factory — returns an empty options object. */
  @JsOverlay
  public static JsIntlDateTimeFormatOptions create() {
    return Js.uncheckedCast(JsPropertyMap.of());
  }

  /** Creates options for a date-only format (no time component). */
  @JsOverlay
  public static JsIntlDateTimeFormatOptions forDateStyle(String style) {
    JsIntlDateTimeFormatOptions opts = create();
    opts.dateStyle = style;
    return opts;
  }

  /** Creates options for a time-only format (no date component). */
  @JsOverlay
  public static JsIntlDateTimeFormatOptions forTimeStyle(String style) {
    JsIntlDateTimeFormatOptions opts = create();
    opts.timeStyle = style;
    return opts;
  }

  /** Creates options for a combined date + time format. */
  @JsOverlay
  public static JsIntlDateTimeFormatOptions forDateTimeStyle(String dateStyle, String timeStyle) {
    JsIntlDateTimeFormatOptions opts = create();
    opts.dateStyle = dateStyle;
    opts.timeStyle = timeStyle;
    return opts;
  }

  /** Sets an arbitrary option key. Useful for fields not explicitly modelled here. */
  @JsOverlay
  public final JsIntlDateTimeFormatOptions set(String key, Object value) {
    Js.<JsPropertyMap<Object>>cast(this).set(key, value);
    return this;
  }

  // ---- standard Intl.DateTimeFormat option fields ----

  /**
   * Date representation style: {@code "full"}, {@code "long"}, {@code "medium"}, {@code "short"}.
   * Mutually exclusive with individual component fields.
   */
  public String dateStyle;

  /**
   * Time representation style: {@code "full"}, {@code "long"}, {@code "medium"}, {@code "short"}.
   * Mutually exclusive with individual component fields.
   */
  public String timeStyle;

  /** Weekday representation: {@code "long"}, {@code "short"}, {@code "narrow"}. */
  public String weekday;

  /**
   * Month representation: {@code "long"}, {@code "short"}, {@code "narrow"}, {@code "2-digit"},
   * {@code "numeric"}.
   */
  public String month;

  /** Year representation: {@code "numeric"}, {@code "2-digit"}. */
  public String year;

  /** Day representation: {@code "numeric"}, {@code "2-digit"}. */
  public String day;

  /** Hour representation: {@code "numeric"}, {@code "2-digit"}. */
  public String hour;

  /** Minute representation: {@code "numeric"}, {@code "2-digit"}. */
  public String minute;

  /** Second representation: {@code "numeric"}, {@code "2-digit"}. */
  public String second;

  /** {@code true} forces 12-hour time; {@code false} forces 24-hour time. */
  public boolean hour12;
}
