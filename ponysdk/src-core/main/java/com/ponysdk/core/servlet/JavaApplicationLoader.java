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

package com.ponysdk.core.servlet;

import javax.servlet.ServletException;

import com.ponysdk.core.AbstractApplicationManager;
import com.ponysdk.core.ApplicationManagerOption;
import com.ponysdk.core.UIContext;
import com.ponysdk.core.event.EventBus;
import com.ponysdk.core.event.RootEventBus;
import com.ponysdk.core.main.EntryPoint;
import com.ponysdk.ui.server.basic.PHistory;

public class JavaApplicationLoader extends AbstractApplicationLoader {

    @Override
    public AbstractApplicationManager createApplicationManager(final ApplicationManagerOption applicationManagerOption) {
        return new AbstractApplicationManager(applicationManagerOption) {

            @Override
            protected EntryPoint initializePonySession(final UIContext ponySession) throws ServletException {
                final Class<? extends EntryPoint> entryPointClassName = applicationManagerOption.getEntryPointClass();
                EntryPoint entryPoint = null;
                try {
                    entryPoint = entryPointClassName.newInstance();
                } catch (final Exception e) {
                    throw new ServletException("Failed to instantiate the EntryPoint #" + entryPointClassName, e);
                }

                final EventBus rootEventBus = new RootEventBus();

                final PHistory history = new PHistory();

                ponySession.setRootEventBus(rootEventBus);
                ponySession.setHistory(history);

                return entryPoint;
            }
        };
    }

}
