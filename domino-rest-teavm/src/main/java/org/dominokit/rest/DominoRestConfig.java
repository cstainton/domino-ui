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
package org.dominokit.rest;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dominokit.jackson.JacksonContextProvider;
import org.dominokit.rest.js.DefaultServiceRoot;
import org.dominokit.rest.js.JsRegexEngine;
import org.dominokit.rest.js.ServerEventFactory;
import org.dominokit.rest.shared.regex.RegexEngine;
import org.dominokit.rest.shared.request.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TeaVM implementation of {@link RestConfig}.
 *
 * <p>Replaces {@code org.gwtproject.i18n.shared.DateTimeFormat} with {@code
 * java.text.SimpleDateFormat}, which is available via TeaVM's classlib.
 */
public class DominoRestConfig implements RestConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(DominoRestConfig.class);

  private static String defaultServiceRoot;
  private static String defaultResourceRootPath = "service";
  private static String defaultJsonDateFormat = null;

  private static RequestRouter<ServerRequest> serverRouter =
      new ServerRouter(
          new DefaultRequestAsyncSender(new ServerEventFactory(), new RequestSender<>()));
  private static List<DynamicServiceRoot> dynamicServiceRoots = new ArrayList<>();
  private static final List<RequestInterceptor> requestInterceptors = new ArrayList<>();
  private static final List<ResponseInterceptor> responseInterceptors = new ArrayList<>();

  private static Fail defaultFailHandler =
      failedResponse -> {
        if (nonNull(failedResponse.getThrowable())) {
          LOGGER.debug("could not execute request on server: ", failedResponse.getThrowable());
        } else {
          LOGGER.debug(
              "could not execute request on server: status ["
                  + failedResponse.getStatusCode()
                  + "], body ["
                  + failedResponse.getBody()
                  + "]");
        }
      };

  private static DateParamFormatter dateParamFormatter =
      (date, pattern) -> new SimpleDateFormat(pattern).format(date);

  private static NullQueryParamStrategy nullQueryParamStrategy = NullQueryParamStrategy.EMPTY;

  private static final Map<String, String> globalPathParams = new HashMap<>();
  private static final Map<String, String> globalHeaderParams = new HashMap<>();
  private static final Map<String, List<String>> globalQueryParams = new HashMap<>();

  private final JsRegexEngine jsRegexEngine = new JsRegexEngine();
  private RegexValidationMode regexValidationMode = RegexValidationMode.IGNORE;

  public static DominoRestConfig initDefaults() {
    RestfullRequestContext.setFactory(new JsRestfulRequestFactory());
    DominoRestContext.init(DominoRestConfig.getInstance());
    return DominoRestConfig.getInstance();
  }

  public static DominoRestConfig getInstance() {
    return new DominoRestConfig();
  }

  @Override
  public DominoRestConfig setDefaultServiceRoot(String defaultServiceRoot) {
    DominoRestConfig.defaultServiceRoot = defaultServiceRoot;
    return this;
  }

  @Override
  public DominoRestConfig setDefaultJsonDateFormat(String defaultJsonDateFormat) {
    JacksonContextProvider.get().defaultDeserializerParameters().setPattern(defaultJsonDateFormat);
    return this;
  }

  @Override
  public DominoRestConfig addDynamicServiceRoot(DynamicServiceRoot dynamicServiceRoot) {
    dynamicServiceRoots.add(dynamicServiceRoot);
    return this;
  }

  @Override
  public DominoRestConfig addRequestInterceptor(RequestInterceptor interceptor) {
    requestInterceptors.add(interceptor);
    return this;
  }

  @Override
  public DominoRestConfig removeRequestInterceptor(RequestInterceptor interceptor) {
    requestInterceptors.remove(interceptor);
    return this;
  }

  @Override
  public List<RequestInterceptor> getRequestInterceptors() {
    return requestInterceptors;
  }

  @Override
  public DominoRestConfig addResponseInterceptor(ResponseInterceptor responseInterceptor) {
    responseInterceptors.add(responseInterceptor);
    return this;
  }

  @Override
  public DominoRestConfig removeResponseInterceptor(ResponseInterceptor responseInterceptor) {
    responseInterceptors.remove(responseInterceptor);
    return this;
  }

  @Override
  public List<ResponseInterceptor> getResponseInterceptors() {
    return responseInterceptors;
  }

  @Override
  public DominoRestConfig setDefaultFailHandler(Fail fail) {
    if (nonNull(fail)) {
      defaultFailHandler = fail;
    }
    return this;
  }

  @Override
  public Fail getDefaultFailHandler() {
    return defaultFailHandler;
  }

  @Override
  public String getDefaultServiceRoot() {
    if (isNull(defaultServiceRoot)) {
      return DefaultServiceRoot.get() + defaultResourceRootPath + "/";
    }
    return defaultServiceRoot;
  }

  @Override
  public String getDefaultJsonDateFormat() {
    return JacksonContextProvider.get().defaultDeserializerParameters().getPattern();
  }

  @Override
  public List<DynamicServiceRoot> getServiceRoots() {
    return dynamicServiceRoots;
  }

  @Override
  public DominoRestConfig setDefaultResourceRootPath(String rootPath) {
    if (nonNull(rootPath)) {
      defaultResourceRootPath = rootPath;
    }
    return this;
  }

  public void setServerRouter(RequestRouter<ServerRequest> serverRouter) {
    DominoRestConfig.serverRouter = serverRouter;
  }

  @Override
  public RequestRouter<ServerRequest> getServerRouter() {
    return serverRouter;
  }

  @Override
  public String getDefaultResourceRootPath() {
    if (nonNull(defaultResourceRootPath) && !defaultResourceRootPath.trim().isEmpty()) {
      return defaultResourceRootPath + "/";
    }
    return "";
  }

  @Override
  public AsyncRunner asyncRunner() {
    return asyncTask -> {
      try {
        asyncTask.onSuccess();
      } catch (Throwable error) {
        asyncTask.onFailed(error);
      }
    };
  }

  @Override
  public RestConfig setDateParamFormatter(DateParamFormatter formatter) {
    dateParamFormatter = formatter;
    return this;
  }

  @Override
  public DateParamFormatter getDateParamFormatter() {
    return dateParamFormatter;
  }

  @Override
  public NullQueryParamStrategy getNullQueryParamStrategy() {
    return nullQueryParamStrategy;
  }

  @Override
  public RestConfig setNullQueryParamStrategy(NullQueryParamStrategy strategy) {
    if (nonNull(strategy)) {
      nullQueryParamStrategy = strategy;
    }
    return this;
  }

  @Override
  public RegexEngine getRegexEngine() {
    return jsRegexEngine;
  }

  @Override
  public Map<String, String> getGlobalPathParameters() {
    return globalPathParams;
  }

  @Override
  public Map<String, String> getGlobalHeaderParameters() {
    return globalHeaderParams;
  }

  @Override
  public Map<String, List<String>> getGlobalQueryParameters() {
    return globalQueryParams;
  }

  @Override
  public RestConfig setGlobalPathParameter(String name, String value) {
    globalPathParams.put(name, value);
    return this;
  }

  @Override
  public RestConfig setGlobalPathParameters(Map<String, String> pathParameters) {
    globalPathParams.putAll(pathParameters);
    return this;
  }

  @Override
  public RestConfig setGlobalHeaderParameter(String name, String value) {
    globalHeaderParams.put(name, value);
    return this;
  }

  @Override
  public RestConfig setGlobalHeaderParameters(Map<String, String> headerParameters) {
    globalHeaderParams.putAll(headerParameters);
    return this;
  }

  @Override
  public RestConfig setGlobalQueryParameter(String name, String value) {
    globalQueryParams.put(name, new ArrayList<>());
    return addGlobalQueryParameter(name, value);
  }

  @Override
  public RestConfig addGlobalQueryParameter(String name, String value) {
    if (globalQueryParams.containsKey(name)) {
      globalQueryParams.get(name).add(value);
    } else {
      setGlobalQueryParameter(name, value);
    }
    return this;
  }

  @Override
  public RestConfig setGlobalQueryParameters(Map<String, List<String>> parameters) {
    parameters.forEach((k, vs) -> vs.forEach(v -> addGlobalQueryParameter(k, v)));
    return this;
  }

  @Override
  public RestConfig addGlobalQueryParameters(Map<String, List<String>> parameters) {
    parameters.forEach((k, vs) -> vs.forEach(v -> addGlobalQueryParameter(k, v)));
    return this;
  }

  @Override
  public RestConfig setRegexValidationMode(RegexValidationMode mode) {
    regexValidationMode = isNull(mode) ? RegexValidationMode.IGNORE : mode;
    return this;
  }

  @Override
  public RegexValidationMode getRegexValidationMode() {
    return regexValidationMode;
  }
}
