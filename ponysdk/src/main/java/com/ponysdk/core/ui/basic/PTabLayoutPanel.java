/*
 * Copyright (c) 2011 PonySDK
 *  Owners:
 *  Luciano Broussal  <luciano.broussal AT gmail.com>
 *  Mathieu Barbier   <mathieu.barbier AT gmail.com>
 *  Nicolas Ciaravola <nicolas.ciaravola.pro AT gmail.com>
 *
 *  WebSite:
 *  http://code.google.com/p/pony-sdk/
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ponysdk.core.ui.basic;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.json.JsonObject;

import com.ponysdk.core.model.ClientToServerModel;
import com.ponysdk.core.model.HandlerModel;
import com.ponysdk.core.model.ServerToClientModel;
import com.ponysdk.core.model.WidgetType;
import com.ponysdk.core.ui.basic.event.HasPBeforeSelectionHandlers;
import com.ponysdk.core.ui.basic.event.HasPSelectionHandlers;
import com.ponysdk.core.ui.basic.event.PBeforeSelectionEvent;
import com.ponysdk.core.ui.basic.event.PBeforeSelectionHandler;
import com.ponysdk.core.ui.basic.event.PSelectionEvent;
import com.ponysdk.core.ui.basic.event.PSelectionHandler;
import com.ponysdk.core.ui.model.ServerBinaryModel;

/**
 * A panel that represents a tabbed set of pages, each of which contains another
 * widget. Its child widgets are shown as the user selects the various tabs
 * associated with them. The tabs can contain arbitrary text, HTML, or widgets.
 * <p>
 * This widget will <em>only</em> work in standards mode, which requires that
 * the HTML page in which it is run have an explicit &lt;!DOCTYPE&gt;
 * declaration.
 * </p>
 * <h3>CSS Style Rules</h3>
 * <dl>
 * <dt>.gwt-TabLayoutPanel
 * <dd>the panel itself
 * <dt>.gwt-TabLayoutPanel .gwt-TabLayoutPanelTabs
 * <dd>the tab bar element
 * <dt>.gwt-TabLayoutPanel .gwt-TabLayoutPanelTab
 * <dd>an individual tab
 * <dt>.gwt-TabLayoutPanel .gwt-TabLayoutPanelTabInner
 * <dd>an element nested in each tab (useful for styling)
 * <dt>.gwt-TabLayoutPanel .gwt-TabLayoutPanelContent
 * <dd>applied to all child content widgets
 * </dl>
 * <p>
 * The children of a TabLayoutPanel element are laid out in &lt;g:tab> elements.
 * Each tab can have one widget child and one of two types of header elements. A
 * &lt;g:header> element can hold html, or a &lt;g:customHeader> element can
 * hold a widget.
 */
public class PTabLayoutPanel extends PComplexPanel
        implements HasPBeforeSelectionHandlers<Integer>, HasPSelectionHandlers<Integer>, PSelectionHandler<Integer>, PAnimatedLayout {

    private final List<PBeforeSelectionHandler<Integer>> beforeSelectionHandlers = new ArrayList<>();
    private final List<PSelectionHandler<Integer>> selectionHandlers = new ArrayList<>();

    private Integer selectedItemIndex;
    private Duration animationDuration;

    protected PTabLayoutPanel() {
    }

    @Override
    protected void init0() {
        super.init0();
        saveAddHandler(HandlerModel.HANDLER_SELECTION);
    }

    @Override
    protected WidgetType getWidgetType() {
        return WidgetType.TAB_LAYOUT_PANEL;
    }

    public void insert(final IsPWidget widget, final String tabText, final int beforeIndex) {
        insert(asWidgetOrNull(widget), tabText, beforeIndex);
    }

    public void insert(final IsPWidget widget, final IsPWidget tabWidget, final int beforeIndex) {
        insert(asWidgetOrNull(widget), asWidgetOrNull(tabWidget), beforeIndex);
    }

    public void insert(final PWidget child, final PWidget tabWidget, final int beforeIndex) {
        assertNotMe(child);

        if (child.getWindow() == null || child.getWindow() == window) {
            child.removeFromParent();

            children.insert(child, beforeIndex);
            adopt(child);
            tabWidget.attach(window, frame);
            child.attach(window, frame);
            child.saveAdd(child.getID(), ID, new ServerBinaryModel(ServerToClientModel.TAB_WIDGET, tabWidget.getID()),
                new ServerBinaryModel(ServerToClientModel.BEFORE_INDEX, beforeIndex));
        } else {
            throw new IllegalAccessError("Widget " + child + " already attached to an other window, current window : "
                    + child.getWindow() + ", new window : " + window);
        }
    }

    public void insert(final PWidget child, final String tabText, final int beforeIndex) {
        if (child.getWindow() == null || child.getWindow() == window) {
            child.removeFromParent();

            children.insert(child, beforeIndex);
            adopt(child);
            child.attach(window, frame);
            child.saveAdd(child.getID(), ID, new ServerBinaryModel(ServerToClientModel.TAB_TEXT, tabText),
                new ServerBinaryModel(ServerToClientModel.BEFORE_INDEX, beforeIndex));
        } else {
            throw new IllegalAccessError("Widget " + child + " already attached to an other window, current window : "
                    + child.getWindow() + ", new window : " + window);
        }
    }

    public void add(final IsPWidget w, final IsPWidget tabWidget) {
        add(asWidgetOrNull(w), asWidgetOrNull(tabWidget));
    }

    public void add(final IsPWidget w, final String tabText) {
        add(asWidgetOrNull(w), tabText);
    }

    public void add(final PWidget widget, final String tabText) {
        insert(widget, tabText, getWidgetCount());
    }

    public void add(final PWidget widget, final PWidget tabWidget) {
        insert(widget, tabWidget, getWidgetCount());
    }

    public void selectTab(final int index) {
        if (index >= getWidgetCount()) throw new IndexOutOfBoundsException();
        this.selectedItemIndex = index;
        saveUpdate(writer -> writer.write(ServerToClientModel.SELECTED_INDEX, index));
    }

    @Override
    public void add(final PWidget w) {
        throw new UnsupportedOperationException("A tabText parameter must be specified with add().");
    }

    @Override
    public void addBeforeSelectionHandler(final PBeforeSelectionHandler<Integer> handler) {
        beforeSelectionHandlers.add(handler);
        saveAddHandler(HandlerModel.HANDLER_BEFORE_SELECTION);
    }

    @Override
    public void removeBeforeSelectionHandler(final PBeforeSelectionHandler<Integer> handler) {
        beforeSelectionHandlers.remove(handler);
    }

    @Override
    public void addSelectionHandler(final PSelectionHandler<Integer> handler) {
        selectionHandlers.add(handler);
    }

    @Override
    public void removeSelectionHandler(final PSelectionHandler<Integer> handler) {
        selectionHandlers.remove(handler);
    }

    @Override
    public void onClientData(final JsonObject instruction) {
        if (instruction.containsKey(ClientToServerModel.HANDLER_SELECTION.toStringValue())) {
            for (final PSelectionHandler<Integer> handler : selectionHandlers) {
                handler.onSelection(
                    new PSelectionEvent<>(this, instruction.getInt(ClientToServerModel.HANDLER_SELECTION.toStringValue())));
            }
        } else if (instruction.containsKey(ClientToServerModel.HANDLER_BEFORE_SELECTION.toStringValue())) {
            for (final PBeforeSelectionHandler<Integer> handler : beforeSelectionHandlers) {
                handler.onBeforeSelection(new PBeforeSelectionEvent<>(this,
                    instruction.getInt(ClientToServerModel.HANDLER_BEFORE_SELECTION.toStringValue())));
            }
        } else {
            super.onClientData(instruction);
        }
    }

    @Override
    public void onSelection(final PSelectionEvent<Integer> event) {
        selectedItemIndex = event.getSelectedItem();
    }

    public Integer getSelectedItemIndex() {
        return selectedItemIndex;
    }

    /**
     * Set whether or not transitions slide in vertically or horizontally.
     *
     * @param isVertical
     *            true for vertical transitions, false for horizontal
     */
    public void setAnimationVertical(final boolean isVertical) {
        saveUpdate(ServerToClientModel.VERTICAL, isVertical);
    }

    @Override
    public void animate(final Duration duration) {
        saveUpdate(writer -> writer.write(ServerToClientModel.ANIMATE, (int) duration.toMillis()));
    }

    public Duration getAnimationDuration() {
        return animationDuration;
    }

    /**
     * Set the duration of the animated transition between tabs.
     */
    public void setAnimationDuration(final Duration duration) {
        if (Objects.equals(this.animationDuration, duration)) return;
        this.animationDuration = duration;
        saveUpdate(ServerToClientModel.ANIMATION_DURATION, (int) duration.toMillis());
    }

}
