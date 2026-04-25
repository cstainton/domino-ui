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
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

/**
 * JsInterop binding for the browser's {@code Intl.NumberFormat} API. Works under both GWT/J2CL and
 * TeaVM since both honour {@code @JsType(isNative=true)}.
 */
@JsType(isNative = true, name = "NumberFormat", namespace = "Intl")
public class JsIntlNumberFormat {

  @JsConstructor
  public JsIntlNumberFormat(JsArray<String> locales, JsIntlNumberFormatOptions options) {}

  /** Factory — uses the browser's runtime locale with the supplied options. */
  @JsOverlay
  public static JsIntlNumberFormat create(JsIntlNumberFormatOptions options) {
    return new JsIntlNumberFormat(new JsArray<>(), options);
  }

  /** Factory — decimal format with the browser's runtime locale and default options. */
  @JsOverlay
  public static JsIntlNumberFormat decimal() {
    return create(JsIntlNumberFormatOptions.decimal());
  }

  /** Formats {@code value} as a localised number string. */
  public native String format(double value);

  /**
   * Returns the formatted number as an array of typed parts, allowing callers to identify the
   * decimal separator, grouping separator, minus sign, etc.
   */
  public native JsArray<JsIntlFormatPart> formatToParts(double value);
}
