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
package org.dominokit.domino.ui.utils;

public class DominoI18n {

  private static DominoI18nProvider provider;
  private static DominoI18nProvider fallbackProvider;

  public static DominoI18nProvider getInstance() {
    if (provider == null && fallbackProvider != null) {
      return fallbackProvider;
    }
    return provider;
  }

  public static void setProvider(DominoI18nProvider provider) {
    DominoI18n.provider = provider;
  }

  public static void setFallbackProvider(DominoI18nProvider provider) {
    DominoI18n.fallbackProvider = provider;
  }
}
