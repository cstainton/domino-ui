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

import org.dominokit.rest.shared.Event;
import org.dominokit.rest.shared.EventProcessor;
import org.dominokit.rest.shared.EventsBus;

/**
 * TeaVM event bus for server-request events.
 *
 * <p>The original GWT implementation used {@code SimpleEventBus} as a dispatch layer. In TeaVM that
 * entire indirection is unnecessary: {@link EventProcessor#process(Event)} simply calls {@code
 * event.process()}, so the bus just delegates straight to the processor.
 */
public class DominoSimpleEventsBus implements EventsBus<Event> {

  /** Singleton shared by all request events. */
  public static final EventsBus<Event> INSTANCE = new DominoSimpleEventsBus(new EventProcessor());

  private final EventProcessor eventProcessor;

  public DominoSimpleEventsBus(EventProcessor eventProcessor) {
    this.eventProcessor = eventProcessor;
  }

  @Override
  public void publishEvent(RequestEvent<Event> event) {
    eventProcessor.process(event.asEvent());
  }
}
