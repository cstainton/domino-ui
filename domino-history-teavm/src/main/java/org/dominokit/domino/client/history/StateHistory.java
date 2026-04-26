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
package org.dominokit.domino.client.history;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dominokit.domino.history.AppHistory;
import org.dominokit.domino.history.DefaultNormalizedToken;
import org.dominokit.domino.history.DominoDirectState;
import org.dominokit.domino.history.DominoHistory;
import org.dominokit.domino.history.EffectiveToken;
import org.dominokit.domino.history.HistoryInterceptor;
import org.dominokit.domino.history.HistoryToken;
import org.dominokit.domino.history.InterceptorChain;
import org.dominokit.domino.history.NormalizedToken;
import org.dominokit.domino.history.StateHistoryToken;
import org.dominokit.domino.history.StateToken;
import org.dominokit.domino.history.TokenEvent;
import org.dominokit.domino.history.TokenFilter;
import org.dominokit.domino.history.TokenParameter;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.browser.History;
import org.teavm.jso.browser.Location;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.events.Event;

/**
 * TeaVM port of the domino-history-client {@code StateHistory} browser implementation.
 *
 * <p>Every elemental2 / JsInterop dependency has been replaced with TeaVM JSO equivalents:
 *
 * <ul>
 *   <li>{@code DomGlobal.document} → {@code Window.current().getDocument()}
 *   <li>{@code DomGlobal.self.history} → {@code History.current()}
 *   <li>{@code DomGlobal.location} → {@code Location.current()}
 *   <li>{@code DomGlobal.setTimeout} → {@code Window.setTimeout}
 *   <li>{@code elemental2.core.JsMap} → plain JS object via {@code @JSBody}
 *   <li>{@code elemental2.dom.CustomEvent} → {@code new CustomEvent(…)} via {@code @JSBody}
 *   <li>{@code jsinterop.base.Js.cast} → {@link JsState#cast(JSObject)}
 * </ul>
 */
public class StateHistory implements AppHistory {

  private static final Logger LOGGER = Logger.getLogger(StateHistory.class.getName());

  private final Set<HistoryListener> listeners = new HashSet<>();
  private String rootPath;
  private final List<HistoryInterceptor> interceptors = new ArrayList<>();

  public StateHistory() {
    this("");
  }

  public StateHistory(String rootPath) {
    setRootPath(rootPath);

    Window.current()
        .addEventListener(
            "popstate",
            event -> {
              if (isInformOnPopState()) {
                JSObject stateObj = getEventState(event);
                JsState state = nonNull(stateObj) ? JsState.cast(stateObj) : null;
                if (nonNull(state) && nonNull(state.getHistoryToken())) {
                  inform(
                      new EffectiveToken(
                          "",
                          StateToken.of(state.getHistoryToken())
                              .title(state.getTitle())
                              .data(state.getData())));
                } else {
                  inform(
                      new EffectiveToken(
                          "", StateToken.of(windowToken()).title(windowTitle()).data("")));
                }
              }
            });

    Window.current()
        .getDocument()
        .addEventListener(
            "domino-history-event",
            evt -> {
              JSObject detail = getEventDetail(evt);
              String token = getDetailProperty(detail, "token");
              String title = getDetailProperty(detail, "title");
              String stateJson = getDetailProperty(detail, "stateJson");
              callListeners(
                  new EffectiveToken("", StateToken.of(token).title(title).data(stateJson)));
            });
  }

  private void callListeners(EffectiveToken effectiveToken) {
    if (!isSameRoot(effectiveToken.getToken())) {
      return;
    }
    final List<HistoryListener> completedListeners = new ArrayList<>();
    listeners.stream()
        .filter(
            listener -> {
              NormalizedToken normalized =
                  getNormalizedToken(rootPath, effectiveToken.getToken(), listener);
              if (isNull(normalized)) {
                normalized = new DefaultNormalizedToken(rootPath, effectiveToken.getToken());
              }
              return listener
                  .getTokenFilter()
                  .filter(
                      new DominoHistoryState(
                              normalized.getToken().value(),
                              effectiveToken.getTitle(),
                              effectiveToken.getData())
                          .token);
            })
        .forEach(
            listener -> {
              if (listener.isRemoveOnComplete()) {
                completedListeners.add(listener);
              }
              Window.setTimeout(
                  () -> {
                    NormalizedToken normalized =
                        getNormalizedToken(rootPath, effectiveToken.getToken(), listener);
                    listener
                        .getListener()
                        .onPopState(
                            new DominoHistoryState(
                                normalized,
                                effectiveToken.getToken(),
                                effectiveToken.getTitle(),
                                effectiveToken.getData()));
                  },
                  0);
            });

    listeners.removeAll(completedListeners);
  }

  private void inform(EffectiveToken effectiveToken) {
    if (!isSameRoot(effectiveToken.getToken())) {
      return;
    }
    try {
      JSObject detail =
          createTokenDetail(
              effectiveToken.getToken(), effectiveToken.getTitle(), effectiveToken.getData());
      Event tokenEvent = createCustomEvent("domino-history-event", detail);
      Window.current().getDocument().dispatchEvent(tokenEvent);
    } catch (Exception ex) {
      LOGGER.log(
          Level.WARNING,
          "Custom events not supported for this browser, multi-app support wont work."
              + " will inform local app listeners only");
      callListeners(effectiveToken);
    }
  }

  private NormalizedToken getNormalizedToken(
      String rootPath, String token, HistoryListener listener) {
    return listener.getTokenFilter().normalizeToken(rootPath, token);
  }

  private boolean isSameRoot(String token) {
    if (this.rootPath.isEmpty()) {
      return true;
    }
    return token.startsWith(rootPath);
  }

  @Override
  public DirectState listen(StateListener listener) {
    return listen(TokenFilter.any(), listener, false);
  }

  @Override
  public DirectState listen(TokenFilter tokenFilter, StateListener listener) {
    return listen(tokenFilter, listener, false);
  }

  @Override
  public DirectState listen(StateListener listener, boolean removeOnComplete) {
    return listen(TokenFilter.any(), listener, removeOnComplete);
  }

  @Override
  public DirectState listen(
      TokenFilter tokenFilter, StateListener listener, boolean removeOnComplete) {
    HistoryListener historyListener = new HistoryListener(listener, tokenFilter, removeOnComplete);
    listeners.add(historyListener);
    return new DominoDirectState(tokenFilter, currentState(), listener)
        .onCompleted(
            dominoDirectState -> {
              if (historyListener.isRemoveOnComplete()) {
                listeners.remove(historyListener);
              }
            });
  }

  @Override
  public void removeListener(StateListener listener) {
    listeners.remove(listener);
  }

  @Override
  public void back() {
    History.current().back();
  }

  @Override
  public void forward() {
    History.current().forward();
  }

  @Override
  public int getHistoryEntriesCount() {
    return History.current().getLength();
  }

  @Override
  public void pushState(StateToken stateToken, TokenParameter... parameters) {
    pushState(stateToken, () -> {}, parameters);
  }

  private void pushState(
      StateToken stateToken, Runnable onPushHandler, TokenParameter... parameters) {
    InterceptorChain interceptorChain =
        new InterceptorChain(
            interceptors,
            () -> {
              EffectiveToken effectiveToken = new EffectiveToken(rootPath, stateToken, parameters);
              if (nonNull(currentToken().value())
                  && !currentToken().value().equals(effectiveToken.getToken())) {
                History.current()
                    .pushState(
                        JsState.state(effectiveToken),
                        Optional.ofNullable(effectiveToken.getTitle()).orElse(windowTitle()),
                        "/" + effectiveToken.getToken());
                setPageTitle(effectiveToken);
                onPushHandler.run();
              }
            });
    interceptorChain.intercept(new TokenEvent(rootPath, stateToken));
  }

  private static void setPageTitle(EffectiveToken effectiveToken) {
    if (nonNull(effectiveToken.getTitle()) && !effectiveToken.getTitle().isEmpty()) {
      Window.current().getDocument().setTitle(effectiveToken.getTitle());
    }
  }

  @Override
  public void fireState(StateToken token) {
    fireState(token, new TokenParameter[0]);
  }

  @Override
  public void fireState(StateToken token, TokenParameter... parameters) {
    pushState(token, this::fireCurrentStateHistory, parameters);
  }

  @Override
  public void pushState(StateToken token) {
    pushState(token, new TokenParameter[0]);
  }

  @Override
  public void replaceState(StateToken stateToken) {
    replaceState(new EffectiveToken(rootPath, stateToken));
  }

  private void replaceState(EffectiveToken effectiveToken) {
    History.current()
        .replaceState(
            JsState.state(effectiveToken),
            Optional.ofNullable(effectiveToken.getTitle()).orElse(windowTitle()),
            "/" + effectiveToken.getToken());
    setPageTitle(effectiveToken);
  }

  @Override
  public StateHistoryToken currentToken() {
    return new StateHistoryToken(rootPath, windowToken());
  }

  @Override
  public String getRootPath() {
    return this.rootPath;
  }

  @Override
  public void setRootPath(String path) {
    this.rootPath = isNull(path) ? "" : path.trim();
  }

  @Override
  public void addInterceptor(HistoryInterceptor interceptor) {
    if (nonNull(interceptor)) {
      this.interceptors.add(interceptor);
    }
  }

  @Override
  public void removeInterceptor(HistoryInterceptor interceptor) {
    if (nonNull(interceptor)) {
      this.interceptors.remove(interceptor);
    }
  }

  @Override
  public void fireCurrentStateHistory() {
    fireCurrentStateHistory(windowTitle());
  }

  @Override
  public void invoke() {
    StateToken stateToken =
        StateToken.of(windowToken()).title(windowTitle()).data(stateData(windowState()));
    callListeners(new EffectiveToken(rootPath, stateToken));
  }

  @Override
  public void fireCurrentStateHistory(String title) {
    fireStateInternal(StateToken.of(windowToken()).title(title).data(stateData(windowState())));
  }

  @Override
  public void reload() {
    Location.current().reload();
  }

  private void fireStateInternal(StateToken stateToken) {
    EffectiveToken effectiveToken = new EffectiveToken(rootPath, stateToken);
    replaceState(effectiveToken);
    inform(effectiveToken);
  }

  private DominoHistory.State windowState() {
    if (isNullState() || isNullToken()) {
      return nullState();
    }
    JsState jsState = getJsState();
    if (isNull(jsState)) {
      return new DominoHistoryState(windowToken(), windowTitle(), "");
    }
    return new DominoHistoryState(jsState.getHistoryToken(), jsState.getTitle(), jsState.getData());
  }

  private boolean isNullToken() {
    JsState jsState = getJsState();
    return isNull(jsState) || isNull(jsState.getHistoryToken());
  }

  private JsState getJsState() {
    JSObject state = History.current().getState();
    return nonNull(state) ? JsState.cast(state) : null;
  }

  private boolean isNullState() {
    return isNull(History.current().getState());
  }

  private DominoHistory.State nullState() {
    return new DominoHistory.State() {
      @Override
      public String rootPath() {
        return StateHistory.this.rootPath;
      }

      @Override
      public HistoryToken token() {
        return new StateHistoryToken(windowToken());
      }

      @Override
      public Optional<String> data() {
        return Optional.empty();
      }

      @Override
      public String title() {
        return windowTitle();
      }

      @Override
      public NormalizedToken normalizedToken() {
        return new DefaultNormalizedToken(new StateHistoryToken(windowToken()));
      }

      @Override
      public void setNormalizedToken(NormalizedToken normalizedToken) {}
    };
  }

  private String windowTitle() {
    return Window.current().getDocument().getTitle();
  }

  private String windowToken() {
    Location loc = Location.current();
    return loc.getPathName().substring(1) + loc.getSearch() + loc.getHash();
  }

  private DominoHistory.State currentState() {
    return new DominoHistoryState(windowToken(), windowTitle(), stateData(windowState()));
  }

  private String stateData(DominoHistory.State state) {
    return state.data().isPresent() ? state.data().get() : "";
  }

  // ── JSBody helpers ──────────────────────────────────────────────────────────

  /** Returns {@code event.state} from a popstate event. */
  @JSBody(params = "event", script = "return event.state;")
  private static native JSObject getEventState(Event event);

  /** Returns {@code event.detail} from a CustomEvent. */
  @JSBody(params = "event", script = "return event.detail;")
  private static native JSObject getEventDetail(Event event);

  /**
   * Creates a plain JS object {@code {token, title, stateJson}} for use as a CustomEvent detail.
   */
  @JSBody(
      params = {"token", "title", "stateJson"},
      script = "return {token: token, title: title, stateJson: stateJson};")
  private static native JSObject createTokenDetail(String token, String title, String stateJson);

  /** Creates a bubbling {@code CustomEvent} with the given type and detail object. */
  @JSBody(
      params = {"type", "detail"},
      script = "return new CustomEvent(type, {bubbles: true, detail: detail});")
  private static native Event createCustomEvent(String type, JSObject detail);

  /** Reads a string property from a plain JS object by name. */
  @JSBody(
      params = {"obj", "key"},
      script = "return obj[key];")
  private static native String getDetailProperty(JSObject obj, String key);

  // ── Inner DominoHistory.State implementation ──────────────────────────────────────────────

  private class DominoHistoryState implements DominoHistory.State {

    private final HistoryToken token;
    private final String data;
    private final String title;
    private NormalizedToken normalizedToken;

    DominoHistoryState(String token, String title, String data) {
      this.token = new StateHistoryToken(rootPath, token);
      this.data = data;
      this.title = title;
      this.normalizedToken = new DefaultNormalizedToken(new StateHistoryToken(rootPath, token));
    }

    DominoHistoryState(NormalizedToken normalizedToken, String token, String title, String data) {
      this.token = new StateHistoryToken(rootPath, token);
      this.data = data;
      this.title = title;
      this.normalizedToken = normalizedToken;
    }

    @Override
    public String rootPath() {
      return StateHistory.this.rootPath;
    }

    @Override
    public HistoryToken token() {
      return this.token;
    }

    @Override
    public Optional<String> data() {
      return Optional.ofNullable(this.data);
    }

    @Override
    public String title() {
      return this.title;
    }

    @Override
    public NormalizedToken normalizedToken() {
      return normalizedToken;
    }

    @Override
    public void setNormalizedToken(NormalizedToken normalizedToken) {
      this.normalizedToken = normalizedToken;
    }
  }
}
