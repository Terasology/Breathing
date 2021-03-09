// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.breathing.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.breathing.component.BreatherComponent;
import org.terasology.breathing.component.SuffocatingComponent;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.layers.hud.CoreHudWidget;
import org.terasology.nui.databinding.ReadOnlyBinding;
import org.terasology.nui.widgets.UIIconBar;

public class BreathingBar extends CoreHudWidget {

    private static final Logger logger = LoggerFactory.getLogger(BreathingBar.class);

    @In
    private Time time;

    @Override
    public void initialise() {

        UIIconBar breathBar = find("breathBar", UIIconBar.class);
        breathBar.bindVisible(new ReadOnlyBinding<Boolean>() {
            @Override
            public Boolean get() {
                EntityRef characterEntity =  CoreRegistry.get(LocalPlayer.class).getCharacterEntity();
                return characterEntity.hasComponent(SuffocatingComponent.class) && characterEntity.hasComponent(BreatherComponent.class);
            }
        });
        breathBar.setMaxValue(1.0f);
        breathBar.bindValue(new ReadOnlyBinding<Float>() {
            @Override
            public Float get() {
                EntityRef characterEntity = CoreRegistry.get(LocalPlayer.class).getCharacterEntity();
                SuffocatingComponent suffocatingComponent = characterEntity.getComponent(SuffocatingComponent.class);

                if (suffocatingComponent != null) {
                    return suffocatingComponent.getRemainingBreath(time.getGameTimeInMs());
                }
                return 0f;
            }
        });

    }

}
