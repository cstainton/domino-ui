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
package org.gwtproject.i18n.client;

public class NumberFormat {
  private String pattern;

  protected NumberFormat(String pattern) {
    this.pattern = pattern;
  }

  public static NumberFormat getFormat(String pattern) {
    return new NumberFormat(pattern);
  }

  public static NumberFormat getDecimalFormat() {
    return new NumberFormat("0.###");
  }

  public String format(Number number) {
    return number != null ? number.toString() : "";
  }

  public double parse(String text) {
    return Double.parseDouble(text);
  }
}
