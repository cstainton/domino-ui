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

import elemental2.core.JsArray;
import org.gwtproject.i18n.shared.browser.JsIntlFormatPart;
import org.gwtproject.i18n.shared.browser.JsIntlNumberFormat;
import org.gwtproject.i18n.shared.browser.JsIntlNumberFormatOptions;

/**
 * Stub of GWT's {@code LocaleInfo} that derives number constants from {@code Intl.NumberFormat} at
 * runtime.
 */
public class LocaleInfo {

  private static final LocaleInfo CURRENT = new LocaleInfo();

  private NumberConstants numberConstants;

  private LocaleInfo() {}

  /** Returns the singleton {@code LocaleInfo} for the browser's current locale. */
  public static LocaleInfo getCurrentLocale() {
    return CURRENT;
  }

  /** Returns locale-specific number constants derived from {@code Intl.NumberFormat}. */
  public NumberConstants getNumberConstants() {
    if (numberConstants == null) {
      numberConstants = buildNumberConstants();
    }
    return numberConstants;
  }

  private static NumberConstants buildNumberConstants() {
    JsIntlNumberFormatOptions opts = JsIntlNumberFormatOptions.decimal();
    opts.minimumFractionDigits = 3;
    opts.maximumFractionDigits = 3;
    JsIntlNumberFormat fmt = JsIntlNumberFormat.create(opts);

    // Format 1000.1 and extract parts to discover the locale's separators
    JsArray<JsIntlFormatPart> parts = fmt.formatToParts(1000.1);
    String decimal = ".";
    String group = ",";
    for (int i = 0; i < (int) parts.length; i++) {
      JsIntlFormatPart part = parts.getAt(i);
      if ("decimal".equals(part.type)) decimal = part.value;
      else if ("group".equals(part.type)) group = part.value;
    }

    // Derive minus sign from a negative number
    JsArray<JsIntlFormatPart> minusParts =
        JsIntlNumberFormat.create(JsIntlNumberFormatOptions.decimal()).formatToParts(-1);
    String minus = "-";
    for (int i = 0; i < (int) minusParts.length; i++) {
      if ("minusSign".equals(minusParts.getAt(i).type)) {
        minus = minusParts.getAt(i).value;
        break;
      }
    }

    final String decimalSep = decimal;
    final String groupSep = group;
    final String minusSign = minus;

    return new NumberConstants() {
      @Override
      public String decimalSeparator() {
        return decimalSep;
      }

      @Override
      public String groupingSeparator() {
        return groupSep;
      }

      @Override
      public String minusSign() {
        return minusSign;
      }

      @Override
      public String percent() {
        return "%";
      }

      @Override
      public String perMill() {
        return "‰";
      }

      @Override
      public String decimalPattern() {
        return "#,##0.###";
      }

      @Override
      public String scientificPattern() {
        return "#E0";
      }

      @Override
      public String percentPattern() {
        return "#,##0%";
      }

      @Override
      public String currencyPattern() {
        return "¤#,##0.00";
      }

      @Override
      public String defCurrencyCode() {
        return "USD";
      }
    };
  }
}
