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

package com.ponysdk.ui.server.basic;

import java.util.Objects;

import com.ponysdk.ui.model.ServerToClientModel;
import com.ponysdk.ui.terminal.WidgetType;
import com.ponysdk.ui.terminal.basic.PHorizontalAlignment;
import com.ponysdk.ui.terminal.basic.PVerticalAlignment;

/**
 * A panel that lays all of its widgets out in a single horizontal column.
 */
public class PHorizontalPanel extends PCellPanel implements HasPAlignment {

    private PHorizontalAlignment horizontalAlignment = PHorizontalAlignment.ALIGN_LEFT;
    private PVerticalAlignment verticalAlignment = PVerticalAlignment.ALIGN_TOP;

    @Override
    protected WidgetType getWidgetType() {
        return WidgetType.HORIZONTAL_PANEL;
    }

    @Override
    public void setHorizontalAlignment(final PHorizontalAlignment horizontalAlignment) {
        if (Objects.equals(this.horizontalAlignment, horizontalAlignment)) return;
        this.horizontalAlignment = horizontalAlignment;
        saveUpdate(ServerToClientModel.HORIZONTAL_ALIGNMENT, horizontalAlignment.getValue());
    }

    @Override
    public void setVerticalAlignment(final PVerticalAlignment verticalAlignment) {
        if (Objects.equals(this.verticalAlignment, verticalAlignment)) return;
        this.verticalAlignment = verticalAlignment;
        saveUpdate(ServerToClientModel.VERTICAL_ALIGNMENT, verticalAlignment.getValue());
    }

    public PHorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public PVerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

}