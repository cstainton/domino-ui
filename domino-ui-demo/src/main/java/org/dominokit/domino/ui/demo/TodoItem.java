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
package org.dominokit.domino.ui.demo;

import org.dominokit.jackson.annotation.JSONMapper;

/**
 * A simple todo item model. The {@code @JSONMapper} annotation drives domino-jackson's APT
 * processor to generate {@link TodoItem_MapperImpl} at compile time.
 */
@JSONMapper
public class TodoItem {

  private long id;
  private String title;
  private boolean done;

  public TodoItem() {}

  public TodoItem(long id, String title, boolean done) {
    this.id = id;
    this.title = title;
    this.done = done;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public boolean isDone() {
    return done;
  }

  public void setDone(boolean done) {
    this.done = done;
  }
}
