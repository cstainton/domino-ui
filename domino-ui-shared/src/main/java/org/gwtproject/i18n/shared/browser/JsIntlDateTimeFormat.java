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

import elemental2.core.JsArray;
import elemental2.core.JsDate;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

/**
 * JsInterop binding for the browser's {@code Intl.DateTimeFormat} API. Works under both GWT/J2CL
 * and TeaVM since both honour {@code @JsType(isNative=true)}.
 *
 * <p>Use the {@link #create(JsIntlDateTimeFormatOptions)} factory rather than constructing directly
 * so that the browser's current locale is selected automatically.
 */
@JsType(isNative = true, name = "DateTimeFormat", namespace = "Intl")
public class JsIntlDateTimeFormat {

  /**
   * Creates an {@code Intl.DateTimeFormat} that uses the given options and resolves locales from
   * the runtime environment (browser language settings).
   *
   * <p>Passing an empty JS array as the locale list causes the implementation to fall back to the
   * browser's default locale — equivalent to calling {@code new Intl.DateTimeFormat(undefined,
   * opts)} in JavaScript.
   */
  @JsConstructor
  public JsIntlDateTimeFormat(JsArray<String> locales, JsIntlDateTimeFormatOptions options) {}

  /** Factory — always uses the browser's runtime locale. */
  @JsOverlay
  public static JsIntlDateTimeFormat create(JsIntlDateTimeFormatOptions options) {
    return new JsIntlDateTimeFormat(new JsArray<>(), options);
  }

  /** Formats {@code date} according to this formatter's locale and options. */
  public native String format(JsDate date);

  /**
   * Returns the formatted date as an array of typed parts (type + value pairs), allowing callers to
   * inspect individual components such as the month name or the literal separator.
   */
  public native JsArray<JsIntlFormatPart> formatToParts(JsDate date);
}
