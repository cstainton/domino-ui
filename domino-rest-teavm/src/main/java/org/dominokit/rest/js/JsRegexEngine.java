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
package org.dominokit.rest.js;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.dominokit.rest.shared.regex.DominoCompiledPattern;
import org.dominokit.rest.shared.regex.DominoMatcher;
import org.dominokit.rest.shared.regex.RegexEngine;
import org.dominokit.rest.shared.regex.Replacer;

/**
 * TeaVM implementation of {@link RegexEngine} using {@code java.util.regex}.
 *
 * <p>Replaces the GWT {@code org.gwtproject.regexp.shared.RegExp} implementation. TeaVM's classlib
 * ships a full {@code java.util.regex} implementation, so the port is a direct API translation.
 */
public final class JsRegexEngine implements RegexEngine {

  @Override
  public DominoCompiledPattern compile(String pattern) {
    return new JavaCompiledPattern(Pattern.compile(pattern), "");
  }

  @Override
  public DominoCompiledPattern compile(String pattern, String flags) {
    return new JavaCompiledPattern(
        Pattern.compile(pattern, toJavaFlags(flags)), flags == null ? "" : flags);
  }

  @Override
  public String replaceAll(String input, DominoCompiledPattern compiled, Replacer replacer) {
    JavaCompiledPattern cp = (JavaCompiledPattern) compiled;
    Matcher m = cp.pattern.matcher(input);
    StringBuilder out = new StringBuilder(input.length() + 16);
    int lastEnd = 0;
    while (m.find()) {
      out.append(input, lastEnd, m.start());
      out.append(replacer.replace(new JavaMatcherView(m)));
      int newEnd = m.end();
      if (newEnd == lastEnd) {
        // Zero-length match guard: advance one character to avoid infinite loop.
        if (lastEnd < input.length()) {
          out.append(input.charAt(lastEnd));
        }
        newEnd = lastEnd + 1;
      }
      lastEnd = newEnd;
    }
    out.append(input, lastEnd, input.length());
    return out.toString();
  }

  @Override
  public boolean matches(String pattern, String candidate) {
    return Pattern.compile("^(?:" + pattern + ")$").matcher(candidate).matches();
  }

  @Override
  public boolean matches(String pattern, String candidate, String flags) {
    String f = flags == null ? "" : flags.replace("g", "");
    return Pattern.compile("^(?:" + pattern + ")$", toJavaFlags(f)).matcher(candidate).matches();
  }

  private static int toJavaFlags(String flags) {
    if (flags == null) return 0;
    int f = 0;
    if (flags.contains("i")) f |= Pattern.CASE_INSENSITIVE;
    if (flags.contains("m")) f |= Pattern.MULTILINE;
    return f;
  }

  // ── inner types ────────────────────────────────────────────────────────────

  private static final class JavaCompiledPattern implements DominoCompiledPattern {
    final Pattern pattern;
    final String flags;

    JavaCompiledPattern(Pattern pattern, String flags) {
      this.pattern = pattern;
      this.flags = flags;
    }

    @Override
    public DominoMatcher matcher(String input) {
      return new JavaMatcherView(pattern.matcher(input));
    }

    @Override
    public String pattern() {
      return pattern.pattern();
    }

    @Override
    public String flags() {
      return flags;
    }
  }

  /**
   * Wraps a {@link Matcher} as a {@link DominoMatcher}. When constructed from within {@link
   * #replaceAll} the matcher is already positioned at a match; callers outside of that context must
   * call {@link #find()} before accessing group/position methods.
   */
  private static final class JavaMatcherView implements DominoMatcher {
    private final Matcher m;
    private boolean matched = true;

    JavaMatcherView(Matcher m) {
      this.m = m;
    }

    @Override
    public boolean find() {
      matched = m.find();
      return matched;
    }

    @Override
    public boolean isMatch() {
      return matched;
    }

    @Override
    public String group(int index) {
      return m.group(index);
    }

    @Override
    public int groupCount() {
      return m.groupCount() + 1; // include group 0, matching GWT MatchResult.getGroupCount()
    }

    @Override
    public int start() {
      return m.start();
    }

    @Override
    public int end() {
      return m.end();
    }

    @Override
    public void reset() {
      m.reset();
      matched = false;
    }
  }
}
