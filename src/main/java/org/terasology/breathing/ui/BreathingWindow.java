/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.breathing.ui;

import org.terasology.breathing.component.DrowningComponent;
import org.terasology.breathing.component.DrownsComponent;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.layers.hud.CoreHudWidget;
import org.terasology.rendering.nui.widgets.UIIconBar;

/**
 * @author Marcin Sciesinski <marcins78@gmail.com>
 */
public class BreathingWindow extends CoreHudWidget {

    @In
    private Time time;

    @Override
    public void initialise() {

        UIIconBar breathBar = find("breathBar", UIIconBar.class);
        breathBar.bindVisible(new ReadOnlyBinding<Boolean>() {
            @Override
            public Boolean get() {
                EntityRef characterEntity =  CoreRegistry.get(LocalPlayer.class).getCharacterEntity();
                return characterEntity.hasComponent(DrowningComponent.class) && characterEntity.hasComponent(DrownsComponent.class);
            }
        });
        breathBar.setMaxValue(1.0f);
        breathBar.bindValue(new ReadOnlyBinding<Float>() {
            @Override
            public Float get() {
                EntityRef characterEntity = CoreRegistry.get(LocalPlayer.class).getCharacterEntity();
                DrowningComponent drowningComponent = characterEntity.getComponent(DrowningComponent.class);

                if (drowningComponent != null) {
                    return drowningComponent.getRemainingBreath(time.getGameTimeInMs());
                }
                return 0f;
            }
        });

    }

}