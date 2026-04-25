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

import org.gwtproject.i18n.shared.cldr.DateTimeFormatInfo;

/**
 * Stub of GWT's {@code DateTimeFormatInfo_factory} that returns a {@link BrowserDateTimeFormatInfo}
 * instance. In GWT this factory used deferred binding to select a locale-specific implementation at
 * compile time; here, the browser's {@code Intl} API handles localisation at runtime instead.
 */
public class DateTimeFormatInfo_factory {

  private static final DateTimeFormatInfo INSTANCE = new BrowserDateTimeFormatInfo();

  private DateTimeFormatInfo_factory() {}

  /** Returns the shared {@link DateTimeFormatInfo} backed by the browser's locale. */
  public static DateTimeFormatInfo create() {
    return INSTANCE;
  }
}
