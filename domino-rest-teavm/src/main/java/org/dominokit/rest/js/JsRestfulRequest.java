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
import static java.util.stream.Collectors.joining;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.dominokit.rest.shared.BaseRestfulRequest;
import org.dominokit.rest.shared.MultipartForm;
import org.dominokit.rest.shared.RestfulRequest;
import org.dominokit.rest.shared.request.RequestTimeoutException;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.browser.Window;
import org.teavm.jso.typedarrays.Int8Array;

/**
 * TeaVM implementation of {@link RestfulRequest} backed by {@link XMLHttpRequest}.
 *
 * <p>Replaces:
 *
 * <ul>
 *   <li>{@code elemental2.dom.XMLHttpRequest} → {@code org.teavm.jso.ajax.XMLHttpRequest}
 *   <li>{@code elemental2.dom.FormData / Blob / BlobPropertyBag} → {@code @JSBody} helpers
 *   <li>{@code elemental2.core.Uint8Array / ArrayBuffer} → {@code org.teavm.jso.typedarrays.*}
 *   <li>{@code org.gwtproject.timer.client.Timer} → {@code Window.setTimeout / clearTimeout}
 * </ul>
 */
public class JsRestfulRequest extends BaseRestfulRequest {

  public static final String CONTENT_TYPE = "Content-Type";
  public static final String APPLICATION_PDF = "application/pdf";

  private final XMLHttpRequest request;
  private final Map<String, List<String>> params = new LinkedHashMap<>();
  private final Map<String, String> headers = new LinkedHashMap<>();

  /** Timer ID returned by {@code setTimeout}; {@code -1} when no timer is active. */
  private int timerId = -1;

  public JsRestfulRequest(String uri, String method) {
    super(uri, method);
    request = XMLHttpRequest.create();
  }

  @Override
  public RestfulRequest putHeader(String key, String value) {
    if (CONTENT_TYPE.equalsIgnoreCase(key) && APPLICATION_PDF.equalsIgnoreCase(value)) {
      request.setResponseType("arraybuffer");
    }
    headers.put(key, value);
    return this;
  }

  @Override
  public RestfulRequest putHeaders(Map<String, String> headers) {
    if (nonNull(headers)) {
      headers.forEach(this::putHeader);
    }
    return this;
  }

  @Override
  public Map<String, String> getHeaders() {
    return headers;
  }

  @Override
  public void sendForm(Map<String, String> formData) {
    setContentType("application/x-www-form-urlencoded");
    send(
        formData.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(joining("&")));
  }

  @Override
  public void sendJson(String json) {
    setContentType("application/json");
    send(json);
  }

  @Override
  public void sendMultipartForm(MultipartForm multipartForm) {
    initRequest();
    JSObject formData = newFormData();
    for (MultipartForm.TextMultipart part : multipartForm.getTextMultiParts()) {
      JSObject blob = newBlob(part.value(), part.contentType());
      if (part.fileName().isPresent()) {
        appendFormData(formData, part.name(), blob, part.fileName().get());
      } else {
        appendFormData(formData, part.name(), blob);
      }
    }
    for (MultipartForm.FileMultipart part : multipartForm.getFileMultiParts()) {
      Int8Array view = new Int8Array(part.value().length);
      view.set(part.value());
      JSObject blob = newBlobFromBytes(view, part.contentType());
      if (part.fileName().isPresent()) {
        appendFormData(formData, part.name(), blob, part.fileName().get());
      } else {
        appendFormData(formData, part.name(), blob);
      }
    }
    request.send(formData);
  }

  @Override
  public void send(String data) {
    initRequest();
    request.send(data);
  }

  @Override
  public void send() {
    initRequest();
    request.send();
  }

  @Override
  public void abort() {
    request.setOnReadyStateChange(() -> {});
    cancelTimer();
    request.abort();
  }

  @Override
  public void setWithCredentials(boolean withCredentials) {
    setXhrWithCredentials(request, withCredentials);
  }

  @Override
  public RestfulRequest setResponseType(String responseType) {
    request.setResponseType(responseType);
    return this;
  }

  private void setContentType(String contentType) {
    headers.put(CONTENT_TYPE, contentType);
  }

  private void initRequest() {
    request.open(getMethod(), getUri());
    applyHeaders();
    request.setOnReadyStateChange(
        () -> {
          if (request.getReadyState() == XMLHttpRequest.DONE) {
            request.setOnReadyStateChange(() -> {});
            cancelTimer();
            successHandler.onResponseReceived(new JsResponse(request));
          }
        });
    if (getTimeout() > 0) {
      timerId = Window.setTimeout(this::fireOnTimeout, getTimeout());
    }
  }

  private void fireOnTimeout() {
    timerId = -1;
    request.setOnReadyStateChange(() -> {});
    request.abort();
    errorHandler.onError(new RequestTimeoutException());
  }

  private void applyHeaders() {
    headers.forEach(request::setRequestHeader);
  }

  private void cancelTimer() {
    if (timerId >= 0) {
      Window.clearTimeout(timerId);
      timerId = -1;
    }
  }

  // ── JSBody helpers for browser APIs not exposed by teavm-jso-apis ─────────

  @JSBody(script = "return new FormData();")
  private static native JSObject newFormData();

  @JSBody(
      params = {"fd", "name", "blob"},
      script = "fd.append(name, blob);")
  private static native void appendFormData(JSObject fd, String name, JSObject blob);

  @JSBody(
      params = {"fd", "name", "blob", "fileName"},
      script = "fd.append(name, blob, fileName);")
  private static native void appendFormData(
      JSObject fd, String name, JSObject blob, String fileName);

  @JSBody(
      params = {"text", "contentType"},
      script = "return new Blob([text], {type: contentType});")
  private static native JSObject newBlob(String text, String contentType);

  @JSBody(
      params = {"bytes", "contentType"},
      script = "return new Blob([bytes], {type: contentType});")
  private static native JSObject newBlobFromBytes(JSObject bytes, String contentType);

  @JSBody(
      params = {"xhr", "value"},
      script = "xhr.withCredentials = value;")
  private static native void setXhrWithCredentials(XMLHttpRequest xhr, boolean value);
}
