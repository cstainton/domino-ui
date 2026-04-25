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
package org.dominokit.domino.ui.teavm;

/**
 * Minimal TeaVM entry point for library compilation verification.
 *
 * <p>Real applications supply their own entry point that instantiates whichever domino-ui
 * components they need. This class exists so that {@code mvn -Pteavm package} can run
 * {@code teavm:build} against the library and confirm that no TeaVM-incompatible bytecode is
 * reachable.
 */
public class TeaVMEntryPoint {

  public static void main(String[] args) {
    // Intentionally empty — tree-shaking means only reachable classes are compiled.
    // Downstream apps replace this with real UI bootstrapping code.
  }
}
