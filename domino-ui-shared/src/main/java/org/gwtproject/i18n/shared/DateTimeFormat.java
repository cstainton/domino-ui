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

public class DateTimeFormat {
  private String pattern;

  protected DateTimeFormat(String pattern) {
    this.pattern = pattern;
  }

  public static DateTimeFormat getFormat(String pattern) {
    return new DateTimeFormat(pattern);
  }

  public enum PredefinedFormat {
    DATE_FULL,
    DATE_LONG,
    DATE_MEDIUM,
    DATE_SHORT,
    TIME_FULL,
    TIME_LONG,
    TIME_MEDIUM,
    TIME_SHORT,
    DATE_TIME_FULL,
    DATE_TIME_LONG,
    DATE_TIME_MEDIUM,
    DATE_TIME_SHORT
  }

  public static DateTimeFormat getFormat(PredefinedFormat format) {
    return new DateTimeFormat(format.name());
  }

  public static DateTimeFormat getFormat(
      String pattern, org.gwtproject.i18n.shared.cldr.DateTimeFormatInfo dtfi) {
    return new DateTimeFormat(pattern);
  }

  public String getPattern() {
    return this.pattern;
  }

  public String format(java.util.Date date) {
    return "";
  }

  public java.util.Date parse(String text) {
    return new java.util.Date();
  }

  public java.util.Date parseStrict(String text) {
    return new java.util.Date();
  }
}
