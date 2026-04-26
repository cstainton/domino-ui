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

import static java.util.Objects.nonNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.dominokit.rest.shared.Response;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Int8Array;

/**
 * TeaVM implementation of {@link Response} wrapping {@link XMLHttpRequest}.
 *
 * <p>Replaces {@code elemental2.core.ArrayBuffer/Int8Array} with {@code
 * org.teavm.jso.typedarrays.*} and removes the {@code jsinterop.base.Js} cast utilities.
 */
public class JsResponse implements Response {

  private final XMLHttpRequest request;
  private Object responseBean;

  JsResponse(XMLHttpRequest request) {
    this.request = request;
  }

  @Override
  public List<String> getHeader(String header) {
    return Collections.singletonList(request.getResponseHeader(header));
  }

  @Override
  public Map<String, List<String>> getHeaders() {
    String allHeaders = request.getAllResponseHeaders();
    String[] lines = allHeaders.split("\r\n");
    return Stream.of(lines)
        .filter(h -> !h.isEmpty())
        .map(h -> h.split(":", 2))
        .collect(
            Collectors.toMap(
                parts -> parts[0], parts -> Collections.singletonList(parts[1].trim())));
  }

  @Override
  public int getStatusCode() {
    return request.getStatus();
  }

  @Override
  public String getStatusText() {
    return request.getStatusText();
  }

  @Override
  public String getBodyAsString() {
    return request.getResponseText();
  }

  /**
   * Returns the raw {@link ArrayBuffer} from the response when the response type was set to {@code
   * "arraybuffer"}.
   */
  public ArrayBuffer getResponseArrayBuffer() {
    return castToArrayBuffer(request.getResponse());
  }

  /** Returns the underlying {@link XMLHttpRequest}. */
  public XMLHttpRequest getRequest() {
    return request;
  }

  @Override
  public byte[] getBodyAsBytes() {
    return toByteArray(getResponseArrayBuffer());
  }

  /**
   * Converts an {@link ArrayBuffer} to a signed Java {@code byte[]}.
   *
   * <p>Uses {@link Int8Array} (signed 8-bit view) so values map directly to Java {@code byte}.
   */
  public static byte[] toByteArray(ArrayBuffer buffer) {
    Int8Array view = new Int8Array(buffer);
    int len = view.getLength();
    byte[] out = new byte[len];
    for (int i = 0; i < len; i++) {
      out[i] = view.get(i);
    }
    return out;
  }

  /**
   * Copies a slice of an {@link ArrayBuffer} into a Java {@code byte[]}.
   *
   * @param byteOffset byte offset into the buffer
   * @param length number of bytes to copy
   */
  public static byte[] toByteArray(ArrayBuffer buffer, int byteOffset, int length) {
    Int8Array view = new Int8Array(buffer, byteOffset, length);
    int len = view.getLength();
    byte[] out = new byte[len];
    for (int i = 0; i < len; i++) {
      out[i] = view.get(i);
    }
    return out;
  }

  public void setResponseBean(Object responseBean) {
    this.responseBean = responseBean;
  }

  @Override
  public Optional<Object> getBean() {
    return Optional.ofNullable(responseBean);
  }

  @Override
  public void setBean(Object bean) {
    if (nonNull(this.responseBean)) {
      throw new IllegalStateException("The response bean has already been set");
    }
    this.responseBean = bean;
  }

  @JSBody(params = "obj", script = "return obj;")
  private static native ArrayBuffer castToArrayBuffer(JSObject obj);
}
