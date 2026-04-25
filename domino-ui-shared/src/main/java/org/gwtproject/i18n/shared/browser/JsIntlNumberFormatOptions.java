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

/** Options bag for the {@code Intl.NumberFormat} constructor. */
@JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
public class JsIntlNumberFormatOptions {

  @JsOverlay
  public static JsIntlNumberFormatOptions create() {
    return Js.uncheckedCast(JsPropertyMap.of());
  }

  @JsOverlay
  public static JsIntlNumberFormatOptions decimal() {
    JsIntlNumberFormatOptions opts = create();
    opts.style = "decimal";
    return opts;
  }

  /**
   * Number style: {@code "decimal"} (default), {@code "currency"}, {@code "percent"}, {@code
   * "unit"}.
   */
  public String style;

  /** Minimum number of integer digits: 1–21. */
  public int minimumIntegerDigits;

  /** Minimum fraction digits: 0–20. */
  public int minimumFractionDigits;

  /** Maximum fraction digits: 0–20. */
  public int maximumFractionDigits;

  /** Minimum significant digits: 1–21. */
  public int minimumSignificantDigits;

  /** Maximum significant digits: 1–21. */
  public int maximumSignificantDigits;

  /** Whether to use grouping separators (thousands separators). */
  public boolean useGrouping;
}
