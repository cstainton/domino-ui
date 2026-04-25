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
 * Stub of GWT's {@code NumberConstants} covering the methods used by domino-ui. The browser-backed
 * implementation uses {@code Intl.NumberFormat.formatToParts()} to derive locale-specific symbols
 * at runtime.
 */
public interface NumberConstants {

  /** The locale's decimal separator (e.g. {@code "."} in en-US, {@code ","} in de-DE). */
  String decimalSeparator();

  /** The locale's minus sign (usually {@code "-"} but may differ in some locales). */
  String minusSign();

  /** The locale's grouping (thousands) separator. */
  String groupingSeparator();

  /** The locale's percent sign. */
  String percent();

  /** The locale's per-mille sign. */
  String perMill();

  /** The pattern for positive numbers (e.g. {@code "#,##0.###"}). */
  String decimalPattern();

  /** The pattern for scientific notation. */
  String scientificPattern();

  /** The pattern for percent values. */
  String percentPattern();

  /** The pattern for currency values. */
  String currencyPattern();

  /** The default number of decimal digits for currency values. */
  String defCurrencyCode();
}
