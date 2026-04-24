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
package org.gwtproject.safehtml.shared;

/**
 * Lightweight stub of GWT's {@code SafeHtmlBuilder} so domino-ui can be compiled with TeaVM without
 * depending on the gwt-safehtml runtime. Only the small subset of methods used by domino-ui is
 * implemented.
 */
public final class SafeHtmlBuilder {

  private final StringBuilder buffer = new StringBuilder();

  public SafeHtmlBuilder() {}

  public SafeHtmlBuilder appendHtmlConstant(String html) {
    buffer.append(html);
    return this;
  }

  public SafeHtmlBuilder append(SafeHtml html) {
    buffer.append(html.asString());
    return this;
  }

  public SafeHtml toSafeHtml() {
    final String html = buffer.toString();
    return () -> html;
  }
}
