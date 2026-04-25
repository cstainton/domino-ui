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
 * Stub of GWT's {@code SafeHtmlUtils} covering the full utility API so domino-ui can be compiled
 * with TeaVM without depending on the gwt-safehtml runtime.
 */
public final class SafeHtmlUtils {

  /** An empty {@link SafeHtml} constant. */
  public static final SafeHtml EMPTY_SAFE_HTML = () -> "";

  private SafeHtmlUtils() {}

  /**
   * Returns a {@link SafeHtml} whose content is {@code text} with HTML special characters escaped.
   * Use for any user-supplied or untrusted content.
   */
  public static SafeHtml fromString(String text) {
    final String escaped = htmlEscape(text);
    return () -> escaped;
  }

  /**
   * Returns a {@link SafeHtml} wrapping {@code html} without any escaping. The caller is
   * responsible for ensuring {@code html} is safe to embed directly in an HTML document.
   */
  public static SafeHtml fromTrustedString(String html) {
    return () -> html;
  }

  /**
   * HTML-escapes the five XML special characters in {@code text} and returns the result as a plain
   * {@code String} (not yet wrapped in {@link SafeHtml}).
   *
   * <p>The characters escaped are: {@code &}, {@code <}, {@code >}, {@code "}, {@code '}.
   */
  public static String htmlEscape(String text) {
    if (text == null) {
      return "";
    }
    StringBuilder sb = null; // lazy allocation
    for (int i = 0, len = text.length(); i < len; i++) {
      char c = text.charAt(i);
      String replacement;
      switch (c) {
        case '&':
          replacement = "&amp;";
          break;
        case '<':
          replacement = "&lt;";
          break;
        case '>':
          replacement = "&gt;";
          break;
        case '"':
          replacement = "&quot;";
          break;
        case '\'':
          replacement = "&#39;";
          break;
        default:
          if (sb != null) {
            sb.append(c);
          }
          continue;
      }
      if (sb == null) {
        sb = new StringBuilder(len + 16);
        sb.append(text, 0, i);
      }
      sb.append(replacement);
    }
    return sb == null ? text : sb.toString();
  }

  /**
   * Returns the {@link SafeHtml} argument unchanged. Present for API parity with the GWT version,
   * which accepts both {@code String} and {@link SafeHtml} overloads.
   */
  public static SafeHtml htmlEscape(SafeHtml html) {
    return html;
  }
}
