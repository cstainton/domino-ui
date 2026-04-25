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
 * Stub of GWT's {@code SafeHtmlBuilder} backed by a plain {@link StringBuilder}. Covers the full
 * builder API so domino-ui can be compiled with TeaVM without depending on the gwt-safehtml
 * runtime.
 */
public final class SafeHtmlBuilder {

  private final StringBuilder buffer = new StringBuilder();

  public SafeHtmlBuilder() {}

  /**
   * Appends a string that is known to be safe HTML (no escaping is applied). Use only for
   * compile-time HTML constants, never for user-supplied content.
   */
  public SafeHtmlBuilder appendHtmlConstant(String html) {
    buffer.append(html);
    return this;
  }

  /** Appends the result of another {@link SafeHtml} value. */
  public SafeHtmlBuilder append(SafeHtml html) {
    buffer.append(html.asString());
    return this;
  }

  /**
   * HTML-escapes {@code text} and appends it. Use this for any user-supplied or untrusted content.
   */
  public SafeHtmlBuilder appendEscaped(String text) {
    buffer.append(SafeHtmlUtils.htmlEscape(text));
    return this;
  }

  /**
   * HTML-escapes {@code text}, replaces newline characters with {@code <br>}, and appends the
   * result.
   */
  public SafeHtmlBuilder appendEscapedLines(String text) {
    buffer.append(SafeHtmlUtils.htmlEscape(text).replace("\n", "<br>"));
    return this;
  }

  /**
   * URL-encodes {@code url} and appends it. Suitable for use inside {@code href} or {@code src}
   * attribute values.
   */
  public SafeHtmlBuilder appendEscapedUrlParam(String url) {
    try {
      buffer.append(java.net.URLEncoder.encode(url, "UTF-8").replace("+", "%20"));
    } catch (java.io.UnsupportedEncodingException e) {
      // UTF-8 is always supported
      buffer.append(url);
    }
    return this;
  }

  /** Builds and returns the accumulated {@link SafeHtml}. */
  public SafeHtml toSafeHtml() {
    final String html = buffer.toString();
    return () -> html;
  }
}
