// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.breathing;

import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.NUIManager;

/**
 * Based on hunger/thirst module UI registration code
 */
@RegisterSystem(RegisterMode.CLIENT)
public class BreathingClientSystem extends BaseComponentSystem {
    @In
    private NUIManager nuiManager;

    @Override
    public void preBegin() {
        nuiManager.getHUD().addHUDElement("Breathing:breathing");
    }
}
