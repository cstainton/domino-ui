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
 * A Scheduler abstraction that uses Elemental2 setTimeout instead of the GWT specific Scheduler.
 * Provides the most commonly used Scheduler operations without depending on the GWT runtime so the
 * library can be compiled with TeaVM.
 */
public final class DominoScheduler {

  private static final DominoScheduler INSTANCE = new DominoScheduler();

  private DominoScheduler() {}

  public static DominoScheduler get() {
    return INSTANCE;
  }

  /** Schedule a one-shot command to run after the current event loop turn. */
  public void scheduleDeferred(ScheduledCommand command) {
    DomGlobal.setTimeout((p0) -> command.execute(), 0);
  }

  /**
   * Schedule a repeating command after a fixed delay. The command is rescheduled with the same
   * delay as long as {@link RepeatingCommand#execute()} returns {@code true}.
   */
  public void scheduleFixedDelay(RepeatingCommand command, int delayMs) {
    DomGlobal.setTimeout(
        (p0) -> {
          if (command.execute()) {
            scheduleFixedDelay(command, delayMs);
          }
        },
        delayMs);
  }

  /**
   * Schedule a repeating command at a fixed period. The command continues to be invoked at the
   * given period as long as {@link RepeatingCommand#execute()} returns {@code true}.
   */
  public void scheduleFixedPeriod(RepeatingCommand command, int periodMs) {
    final double[] handle = new double[1];
    handle[0] =
        DomGlobal.setInterval(
            (p0) -> {
              if (!command.execute()) {
                DomGlobal.clearInterval(handle[0]);
              }
            },
            periodMs);
  }

  @FunctionalInterface
  public interface ScheduledCommand {
    void execute();
  }

  @FunctionalInterface
  public interface RepeatingCommand {
    boolean execute();
  }
}
