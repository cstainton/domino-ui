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

import org.dominokit.domino.client.history.StateHistory;
import org.dominokit.domino.history.AppHistory;
import org.dominokit.domino.history.StateToken;
import org.dominokit.domino.history.TokenFilter;
import org.dominokit.rest.DominoRestConfig;
import org.dominokit.rest.shared.RestfulRequest;
import org.teavm.jso.browser.Navigator;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLInputElement;

/**
 * TeaVM entry point for the Geo Todo List demo.
 *
 * <p>Exercises:
 *
 * <ul>
 *   <li>domino-rest-teavm — REST GET / POST against a local PostgREST instance
 *   <li>domino-jackson — APT-generated JSON mapper ({@link GeoTodo_MapperImpl})
 *   <li>domino-history-teavm — HTML5 pushState routing via {@link StateHistory}
 *   <li>TeaVM Geolocation API — {@code Navigator.getGeolocation().getCurrentPosition()}
 * </ul>
 *
 * <p>Backend: Postgres + PostgREST running via Docker (see supabase-local/docker-compose.yml).
 * lat/lng are stored as real columns and round-trip through the API.
 *
 * <p>Build: {@code mvn -pl domino-history-teavm,domino-rest-teavm,domino-ui-demo -Pteavm package}
 *
 * <p>Serve: copy {@code target/teavm-js/demo.js} next to {@code
 * src/main/resources/static/index.html} and run {@code python3 -m http.server 8080}.
 */
public class DemoApp {

  /** PostgREST base URL — change host/port if your Docker setup differs. */
  private static final String API = "http://localhost:3000";

  private static AppHistory history;
  private static HTMLElement contentEl;

  /** Lat/lng captured on the Add form; null when not yet acquired. */
  private static Double pendingLat = null;

  private static Double pendingLng = null;

  public static void main(String[] args) {
    DominoRestConfig.initDefaults();
    history = new StateHistory();

    HTMLDocument doc = Window.current().getDocument();

    HTMLElement root = doc.getElementById("app");
    if (root == null) {
      root = doc.createElement("div");
      root.setAttribute("id", "app");
      doc.getBody().appendChild(root);
    }
    root.setInnerHTML("");

    HTMLElement header = doc.createElement("header");
    HTMLElement h1 = doc.createElement("h1");
    h1.setInnerText("Geo Todo List");
    HTMLElement sub = doc.createElement("p");
    sub.setInnerText("Persistent todos backed by Postgres + PostgREST, with optional GPS tagging");
    header.appendChild(h1);
    header.appendChild(sub);
    root.appendChild(header);

    HTMLElement nav = doc.createElement("nav");
    nav.appendChild(navButton(doc, "Todos", ""));
    nav.appendChild(navButton(doc, "Add Todo", "add"));
    root.appendChild(nav);

    contentEl = doc.createElement("main");
    root.appendChild(contentEl);

    // Single dispatch listener avoids all-matching TokenFilter.any() double-firing.
    history.listen(
        TokenFilter.any(),
        state -> {
          String token = state.token().path();
          if (token.startsWith("todo/")) {
            try {
              renderDetail(Integer.parseInt(token.substring("todo/".length())));
            } catch (NumberFormatException e) {
              renderList();
            }
          } else if (token.startsWith("add")) {
            renderAdd();
          } else {
            renderList();
          }
        });

    history.fireCurrentStateHistory();
  }

  // ── List view ─────────────────────────────────────────────────────────────

  private static void renderList() {
    HTMLDocument doc = Window.current().getDocument();
    showMessage(doc, "Loading todos...");

    // PostgREST: GET /todos?limit=10&order=id
    get(API + "/todos?limit=10&order=id")
        .onSuccess(
            response -> {
              GeoTodo[] todos =
                  GeoTodo_MapperImpl.INSTANCE.readArray(response.getBodyAsString(), GeoTodo[]::new);
              showList(doc, todos);
            })
        .onError(err -> showMessage(doc, "Error loading todos: " + err.getMessage()))
        .send();
  }

  private static void showList(HTMLDocument doc, GeoTodo[] todos) {
    contentEl.setInnerHTML("");

    HTMLElement h2 = doc.createElement("h2");
    h2.setInnerText("Todo List");
    contentEl.appendChild(h2);

    HTMLElement table = doc.createElement("table");

    HTMLElement thead = doc.createElement("thead");
    HTMLElement hdr = doc.createElement("tr");
    th(doc, hdr, "#");
    th(doc, hdr, "Title");
    th(doc, hdr, "Status");
    th(doc, hdr, "Location");
    th(doc, hdr, "");
    thead.appendChild(hdr);
    table.appendChild(thead);

    HTMLElement tbody = doc.createElement("tbody");
    for (GeoTodo todo : todos) {
      HTMLElement tr = doc.createElement("tr");
      td(doc, tr, String.valueOf(todo.getId()));
      td(doc, tr, todo.getTitle());
      td(doc, tr, todo.isCompleted() ? "done" : "open");

      HTMLElement tdLoc = doc.createElement("td");
      if (todo.getLat() != null && todo.getLng() != null) {
        tdLoc.setInnerText(fmtCoord(todo.getLat()) + ", " + fmtCoord(todo.getLng()));
      }
      tr.appendChild(tdLoc);

      HTMLElement tdAction = doc.createElement("td");
      HTMLElement viewBtn = doc.createElement("button");
      viewBtn.setInnerText("View");
      int id = todo.getId();
      viewBtn.addEventListener("click", evt -> history.fireState(StateToken.of("todo/" + id)));
      tdAction.appendChild(viewBtn);
      tr.appendChild(tdAction);

      tbody.appendChild(tr);
    }
    table.appendChild(tbody);
    contentEl.appendChild(table);

    if (todos.length > 0) {
      HTMLElement note = doc.createElement("p");
      note.setAttribute("class", "note");
      note.setInnerText("domino-jackson: " + GeoTodo_MapperImpl.INSTANCE.write(todos[0]));
      contentEl.appendChild(note);
    }
  }

  // ── Add view ──────────────────────────────────────────────────────────────

  private static void renderAdd() {
    HTMLDocument doc = Window.current().getDocument();
    pendingLat = null;
    pendingLng = null;
    contentEl.setInnerHTML("");

    HTMLElement h2 = doc.createElement("h2");
    h2.setInnerText("Add a Todo");
    contentEl.appendChild(h2);

    HTMLElement titleRow = doc.createElement("p");
    HTMLElement label = doc.createElement("label");
    label.setInnerText("Title: ");
    HTMLInputElement titleInput = doc.createElement("input").cast();
    titleInput.setAttribute("type", "text");
    titleInput.setAttribute("id", "todo-title");
    titleInput.setAttribute("placeholder", "What needs doing?");
    label.appendChild(titleInput);
    titleRow.appendChild(label);
    contentEl.appendChild(titleRow);

    HTMLElement locSection = doc.createElement("div");
    locSection.setAttribute("class", "loc-section");

    HTMLElement locStatus = doc.createElement("p");
    locStatus.setAttribute("id", "loc-status");
    locStatus.setInnerText("No location captured.");
    locSection.appendChild(locStatus);

    HTMLElement geoBtn = doc.createElement("button");
    geoBtn.setAttribute("type", "button");
    geoBtn.setInnerText("Capture My Location");
    geoBtn.addEventListener("click", evt -> captureLocation(doc));
    locSection.appendChild(geoBtn);
    contentEl.appendChild(locSection);

    HTMLElement submitRow = doc.createElement("p");
    HTMLElement submitBtn = doc.createElement("button");
    submitBtn.setAttribute("type", "button");
    submitBtn.setInnerText("Add Todo");
    HTMLElement msgEl = doc.createElement("span");
    msgEl.setAttribute("id", "submit-msg");
    submitBtn.addEventListener("click", evt -> submitTodo(doc, msgEl));
    submitRow.appendChild(submitBtn);
    submitRow.appendChild(msgEl);
    contentEl.appendChild(submitRow);
  }

  private static void captureLocation(HTMLDocument doc) {
    HTMLElement display = doc.getElementById("loc-status");
    if (!Navigator.isGeolocationAvailable()) {
      display.setInnerText("Geolocation is not available in this browser.");
      return;
    }
    display.setInnerText("Acquiring location...");
    Navigator.getGeolocation()
        .getCurrentPosition(
            pos -> {
              pendingLat = pos.getCoords().getLatitude();
              pendingLng = pos.getCoords().getLongitude();
              display.setInnerText(
                  "Location: " + fmtCoord(pendingLat) + ", " + fmtCoord(pendingLng));
            },
            err ->
                display.setInnerText(
                    "Could not get location (code " + err.getCode() + "): " + err.getMessage()));
  }

  private static void submitTodo(HTMLDocument doc, HTMLElement msgEl) {
    HTMLInputElement titleInput = doc.getElementById("todo-title").cast();
    String title = titleInput.getValue().trim();
    if (title.isEmpty()) {
      msgEl.setInnerText("  Please enter a title.");
      return;
    }
    msgEl.setInnerText("  Saving...");

    GeoTodo draft = new GeoTodo();
    draft.setUserId(1);
    draft.setTitle(title);
    draft.setCompleted(false);
    draft.setLat(pendingLat);
    draft.setLng(pendingLng);
    String json = GeoTodo_MapperImpl.INSTANCE.write(draft);

    // Prefer: return=representation asks PostgREST to return the inserted row (with generated id).
    post(API + "/todos")
        .putHeader("Prefer", "return=representation")
        .onSuccess(
            response -> {
              // PostgREST returns an array even for single inserts
              GeoTodo[] created =
                  GeoTodo_MapperImpl.INSTANCE.readArray(response.getBodyAsString(), GeoTodo[]::new);
              if (created.length > 0) {
                history.fireState(StateToken.of("todo/" + created[0].getId()));
              } else {
                history.fireState(StateToken.of(""));
              }
            })
        .onError(err -> msgEl.setInnerText("  Error: " + err.getMessage()))
        .sendJson(json);
  }

  // ── Detail view ───────────────────────────────────────────────────────────

  private static void renderDetail(int id) {
    HTMLDocument doc = Window.current().getDocument();
    showMessage(doc, "Loading todo #" + id + "...");

    // PostgREST: filter by primary key using ?id=eq.<value>
    get(API + "/todos?id=eq." + id)
        .onSuccess(
            response -> {
              GeoTodo[] rows =
                  GeoTodo_MapperImpl.INSTANCE.readArray(response.getBodyAsString(), GeoTodo[]::new);
              if (rows.length > 0) {
                showDetail(doc, rows[0]);
              } else {
                showMessage(doc, "Todo #" + id + " not found.");
              }
            })
        .onError(err -> showMessage(doc, "Could not load todo #" + id))
        .send();
  }

  private static void showDetail(HTMLDocument doc, GeoTodo todo) {
    contentEl.setInnerHTML("");

    HTMLElement h2 = doc.createElement("h2");
    h2.setInnerText("Todo #" + todo.getId());
    contentEl.appendChild(h2);

    HTMLElement table = doc.createElement("table");
    table.setAttribute("class", "detail");
    detailRow(doc, table, "Title", todo.getTitle());
    detailRow(doc, table, "Status", todo.isCompleted() ? "Completed" : "Open");
    detailRow(doc, table, "User ID", String.valueOf(todo.getUserId()));

    if (todo.getLat() != null && todo.getLng() != null) {
      detailRow(
          doc,
          table,
          "Coordinates",
          fmtCoord(todo.getLat()) + " N,  " + fmtCoord(todo.getLng()) + " E");

      HTMLElement tr = doc.createElement("tr");
      HTMLElement th = doc.createElement("th");
      th.setInnerText("Map");
      HTMLElement td = doc.createElement("td");
      HTMLElement a = doc.createElement("a");
      String osmUrl =
          "https://www.openstreetmap.org/?mlat="
              + String.format("%.6f", todo.getLat())
              + "&mlon="
              + String.format("%.6f", todo.getLng())
              + "&zoom=15";
      a.setAttribute("href", osmUrl);
      a.setAttribute("target", "_blank");
      a.setInnerText("View on OpenStreetMap");
      td.appendChild(a);
      tr.appendChild(th);
      tr.appendChild(td);
      table.appendChild(tr);
    }
    contentEl.appendChild(table);

    HTMLElement pre = doc.createElement("pre");
    pre.setInnerText("JSON (domino-jackson):\n" + GeoTodo_MapperImpl.INSTANCE.write(todo));
    contentEl.appendChild(pre);

    HTMLElement backBtn = doc.createElement("button");
    backBtn.setInnerText("Back to list");
    backBtn.addEventListener("click", evt -> history.fireState(StateToken.of("")));
    contentEl.appendChild(backBtn);
  }

  // ── Request helpers ───────────────────────────────────────────────────────

  /** GET with Accept header for PostgREST JSON responses. */
  private static RestfulRequest get(String url) {
    return RestfulRequest.get(url).putHeader("Accept", "application/json");
  }

  /** POST with Content-Type set for JSON body. */
  private static RestfulRequest post(String url) {
    return RestfulRequest.post(url).putHeader("Content-Type", "application/json");
  }

  // ── DOM helpers ───────────────────────────────────────────────────────────

  private static HTMLElement navButton(HTMLDocument doc, String label, String token) {
    HTMLElement btn = doc.createElement("button");
    btn.setAttribute("type", "button");
    btn.setInnerText(label);
    btn.addEventListener("click", evt -> history.fireState(StateToken.of(token)));
    return btn;
  }

  private static void th(HTMLDocument doc, HTMLElement tr, String text) {
    HTMLElement cell = doc.createElement("th");
    cell.setInnerText(text);
    tr.appendChild(cell);
  }

  private static void td(HTMLDocument doc, HTMLElement tr, String text) {
    HTMLElement cell = doc.createElement("td");
    cell.setInnerText(text);
    tr.appendChild(cell);
  }

  private static void detailRow(HTMLDocument doc, HTMLElement table, String label, String value) {
    HTMLElement tr = doc.createElement("tr");
    HTMLElement th = doc.createElement("th");
    th.setInnerText(label);
    HTMLElement td = doc.createElement("td");
    td.setInnerText(value);
    tr.appendChild(th);
    tr.appendChild(td);
    table.appendChild(tr);
  }

  private static void showMessage(HTMLDocument doc, String msg) {
    contentEl.setInnerHTML("");
    HTMLElement p = doc.createElement("p");
    p.setInnerText(msg);
    contentEl.appendChild(p);
  }

  private static String fmtCoord(double v) {
    return String.format("%.6f", v);
  }
}
