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

import java.util.Optional;
import org.dominokit.domino.history.EffectiveToken;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 * TeaVM JSO representation of the browser history state object.
 *
 * <p>Replaces the elemental2 {@code @JsType(isNative=true, name="Object")} pattern with TeaVM's
 * abstract-class-implements-JSObject pattern. {@code @JSProperty} maps each getter/setter to the
 * matching camelCase JS property name so the state object round-trips correctly through {@code
 * history.pushState} / {@code history.state}.
 */
public abstract class JsState implements JSObject {

  @JSProperty("historyToken")
  public abstract String getHistoryToken();

  @JSProperty("historyToken")
  public abstract void setHistoryToken(String token);

  @JSProperty
  public abstract String getData();

  @JSProperty
  public abstract void setData(String data);

  @JSProperty
  public abstract String getTitle();

  @JSProperty
  public abstract void setTitle(String title);

  /** Creates a new empty plain JS {@code Object} ({@code {}}) backed by this type. */
  @JSBody(script = "return {};")
  public static native JsState create();

  /**
   * Casts any {@link JSObject} to {@link JsState}. Safe when the object was originally created via
   * {@link #create()} or retrieved from {@code history.state} after a {@code pushState} call that
   * used this type.
   */
  @JSBody(params = "obj", script = "return obj;")
  public static native JsState cast(JSObject obj);

  /** Reads {@code document.title} directly from the JS side. */
  @JSBody(script = "return document.title;")
  private static native String documentTitle();

  /**
   * Builds a state object from an {@link EffectiveToken}, falling back to the current document
   * title when the token carries no title.
   */
  public static JsState state(EffectiveToken token) {
    JsState state = create();
    state.setHistoryToken(token.getToken());
    state.setData(Optional.ofNullable(token.getData()).orElse(""));
    state.setTitle(Optional.ofNullable(token.getTitle()).orElse(documentTitle()));
    return state;
  }
}
