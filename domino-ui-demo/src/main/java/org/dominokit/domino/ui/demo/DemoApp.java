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

import java.util.ArrayList;
import java.util.List;
import org.dominokit.domino.history.HistoryToken;
import org.dominokit.domino.history.StateHistoryToken;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLInputElement;

/**
 * TeaVM entry point demonstrating:
 *
 * <ul>
 *   <li>domino-jackson — APT-generated JSON mapper round-trips a {@link TodoItem}
 *   <li>domino-history-shared — pure-Java URL-token parsing (HistoryToken)
 *   <li>TeaVM JSO DOM API — direct browser DOM manipulation
 * </ul>
 *
 * <p>Build: {@code mvn -pl domino-ui-demo -Pteavm package}
 *
 * <p>Run: copy {@code target/teavm-js/demo.js} next to {@code
 * src/main/resources/static/index.html} and open it in a browser.
 */
public class DemoApp {

  private static final List<TodoItem> ITEMS = new ArrayList<>();
  private static HTMLElement contentEl;

  public static void main(String[] args) {
    ITEMS.add(new TodoItem(1, "Port domino-ui to TeaVM", true));
    ITEMS.add(new TodoItem(2, "Wire domino-jackson serialization", true));
    ITEMS.add(new TodoItem(3, "Integrate domino-history routing", false));

    HTMLDocument doc = Window.current().getDocument();

    HTMLElement root = doc.getElementById("app");
    if (root == null) {
      root = doc.createElement("div");
      root.setAttribute("id", "app");
      doc.getBody().appendChild(root);
    }
    root.setInnerHTML("");

    HTMLElement header = doc.createElement("header");
    header.setInnerHTML("<h1>domino-ui TeaVM demo</h1>");
    root.appendChild(header);

    HTMLElement nav = doc.createElement("nav");
    nav.appendChild(navLink(doc, "Todo list", "#/"));
    nav.appendChild(navLink(doc, "Add item", "#/add"));
    root.appendChild(nav);

    contentEl = doc.createElement("main");
    root.appendChild(contentEl);

    // Hash-based routing via domino-history-shared's HistoryToken parser
    Window.current()
        .addEventListener(
            "hashchange",
            (EventListener<Event>) evt -> route(currentPath()));

    route(currentPath());
  }

  private static String currentPath() {
    String hash = Window.current().getLocation().getHash();
    // strip leading '#' to get a clean token for domino-history-shared
    return hash.startsWith("#") ? hash.substring(1) : "/";
  }

  private static void route(String rawPath) {
    // Use domino-history-shared's StateHistoryToken for URL parsing
    HistoryToken token = new StateHistoryToken(rawPath);
    String path = token.path();
    if (path.startsWith("/add")) {
      renderAdd();
    } else {
      renderList();
    }
  }

  private static void renderList() {
    HTMLDocument doc = Window.current().getDocument();
    contentEl.setInnerHTML("");

    HTMLElement h2 = doc.createElement("h2");
    h2.setInnerText("Todo items");
    contentEl.appendChild(h2);

    HTMLElement ul = doc.createElement("ul");
    for (TodoItem item : ITEMS) {
      HTMLElement li = doc.createElement("li");
      li.setInnerText((item.isDone() ? "[x] " : "[ ] ") + item.getTitle());
      ul.appendChild(li);
    }
    contentEl.appendChild(ul);

    if (!ITEMS.isEmpty()) {
      // domino-jackson: serialise item[0] to JSON and read it back
      String json = TodoItem_MapperImpl.INSTANCE.write(ITEMS.get(0));

      HTMLElement pre = doc.createElement("pre");
      pre.setInnerText("domino-jackson serialised item[0]:\n" + json);
      contentEl.appendChild(pre);

      TodoItem rt = TodoItem_MapperImpl.INSTANCE.read(json);
      HTMLElement p = doc.createElement("p");
      p.setInnerText(
          "Round-trip ok: id="
              + rt.getId()
              + " title=\""
              + rt.getTitle()
              + "\" done="
              + rt.isDone());
      contentEl.appendChild(p);
    }
  }

  private static void renderAdd() {
    HTMLDocument doc = Window.current().getDocument();
    contentEl.setInnerHTML("");

    HTMLElement h2 = doc.createElement("h2");
    h2.setInnerText("Add a todo item");
    contentEl.appendChild(h2);

    HTMLElement form = doc.createElement("form");

    HTMLInputElement input = doc.createElement("input").cast();
    input.setAttribute("type", "text");
    input.setAttribute("placeholder", "What needs doing?");
    input.setAttribute("id", "new-item-title");
    form.appendChild(input);

    HTMLElement button = doc.createElement("button");
    button.setAttribute("type", "button");
    button.setInnerText("Add");
    button.addEventListener(
        "click",
        (EventListener<Event>)
            evt -> {
              HTMLInputElement titleInput = doc.getElementById("new-item-title").cast();
              String title = titleInput.getValue().trim();
              if (!title.isEmpty()) {
                ITEMS.add(new TodoItem(ITEMS.size() + 1, title, false));
                titleInput.setValue("");
                Window.current().getLocation().setHash("#/");
              }
            });
    form.appendChild(button);
    contentEl.appendChild(form);
  }

  private static HTMLElement navLink(HTMLDocument doc, String label, String href) {
    HTMLElement a = doc.createElement("a");
    a.setAttribute("href", href);
    a.setInnerText(label);
    a.setAttribute("style", "margin-right:1em");
    return a;
  }
}
