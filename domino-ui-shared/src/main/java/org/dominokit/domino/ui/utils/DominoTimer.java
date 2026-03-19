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
package org.dominokit.domino.ui.utils;

import elemental2.dom.DomGlobal;

/**
 * A Timer abstraction that uses Elemental2 setTimeout / setInterval instead of the GWT specific
 * timer.
 */
public abstract class DominoTimer {

  private double timerId = -1;
  private boolean isRunning = false;
  private boolean isRepeating = false;

  public DominoTimer() {}

  public abstract void run();

  public void schedule(int delayMillis) {
    if (delayMillis < 0) {
      throw new IllegalArgumentException("must be non-negative");
    }
    if (isRunning) {
      cancel();
    }
    isRepeating = false;
    isRunning = true;
    timerId =
        DomGlobal.setTimeout(
            (p0) -> {
              isRunning = false;
              run();
            },
            delayMillis);
  }

  public void scheduleRepeating(int periodMillis) {
    if (periodMillis <= 0) {
      throw new IllegalArgumentException("must be strictly positive");
    }
    if (isRunning) {
      cancel();
    }
    isRepeating = true;
    isRunning = true;
    timerId = DomGlobal.setInterval((p0) -> run(), periodMillis);
  }

  public void cancel() {
    if (!isRunning) {
      return;
    }
    if (isRepeating) {
      DomGlobal.clearInterval(timerId);
    } else {
      DomGlobal.clearTimeout(timerId);
    }
    isRunning = false;
  }

  public boolean isRunning() {
    return isRunning;
  }
}
