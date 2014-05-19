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

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.engine.Time;
import org.terasology.breathing.component.DrowningComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.layers.hud.CoreHudWidget;
import org.terasology.rendering.nui.widgets.UILoadBar;
import org.terasology.registry.In;

/**
 * @author Marcin Sciesinski <marcins78@gmail.com>
 */
public class BreathingWindow extends CoreHudWidget {

    @In
    private Time time;

    @Override
    public void initialise() {
        UILoadBar breathing = find("breathing", UILoadBar.class);
        breathing.bindValue(
                new Binding<Float>() {
                    @Override
                    public Float get() {
                        EntityRef character = CoreRegistry.get(LocalPlayer.class).getCharacterEntity();
                        DrowningComponent breathingComponent = character.getComponent(DrowningComponent.class);
                        return breathingComponent.getRemainingBreath(time.getGameTimeInMs());
                    }

                    @Override
                    public void set(Float value) {
                    }
                }
        );

    }
}
