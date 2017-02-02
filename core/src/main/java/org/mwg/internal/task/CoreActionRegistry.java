/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
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
package org.mwg.internal.task;

import org.mwg.plugin.ActionDeclaration;
import org.mwg.plugin.ActionRegistry;

import java.util.HashMap;
import java.util.Map;

public class CoreActionRegistry implements ActionRegistry {

    private final Map<String, ActionDeclaration> backend = new HashMap<String, ActionDeclaration>();

    public CoreActionRegistry() {

    }

    @Override
    public final synchronized ActionDeclaration declaration(String name) {
        ActionDeclaration previous = backend.get(name);
        if (previous == null) {
            previous = new CoreActionDeclaration(name);
            backend.put(name, previous);
        }
        return previous;
    }

    @Override
    public final ActionDeclaration[] declarations() {
        ActionDeclaration[] result = backend.values().toArray(new ActionDeclaration[backend.size()]);
        return result;
    }

}