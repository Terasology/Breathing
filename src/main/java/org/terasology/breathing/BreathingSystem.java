/*
 * Copyright 2013 MovingBlocks
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
package org.terasology.breathing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.breathing.component.BreatherComponent;
import org.terasology.breathing.component.SuffocatingComponent;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.logic.characters.events.OnEnterBlockEvent;
import org.terasology.logic.health.DoDamageEvent;
import org.terasology.logic.health.EngineDamageTypes;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.Vector3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;

/**
 * @author Josephtsessions, based on Immortius's drowning code
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class BreathingSystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    private static final Logger logger = LoggerFactory.getLogger(BreathingSystem.class);

    @In
    private BlockEntityRegistry blockEntityProvider;

    @In
    private Time time;

    @In
    private EntityManager entityManager;

    @In
    private WorldProvider worldProvider;

    @Override
    public void update(float delta) {
        for (EntityRef entity : entityManager.getEntitiesWith(SuffocatingComponent.class, BreatherComponent.class, LocationComponent.class)) {
            SuffocatingComponent suffocating = entity.getComponent(SuffocatingComponent.class);
            LocationComponent loc = entity.getComponent(LocationComponent.class);
            BreatherComponent breather = entity.getComponent(BreatherComponent.class);

            long gameTime = time.getGameTimeInMs();
            // check for out of breath and full breath conditions
            if (suffocating.isBreathing && gameTime > suffocating.endTime) {
                // clean up the org.terasology.breathing component
                entity.removeComponent(SuffocatingComponent.class);
            } else {
                if (gameTime > suffocating.nextSuffocateDamageTime) {
                    // damage the entity
                    EntityRef liquidBlock = blockEntityProvider.getBlockEntityAt(loc.getWorldPosition());
                    entity.send(new DoDamageEvent(breather.suffocateDamage, EngineDamageTypes.DROWNING.get(), liquidBlock));
                    // set the next damage time
                    suffocating.nextSuffocateDamageTime = gameTime + breather.timeBetweenSuffocateDamage;
                    entity.saveComponent(suffocating);
                }
            }
        }
    }

    @ReceiveEvent(components = {BreatherComponent.class})
    public void onEnterBlock(OnEnterBlockEvent event, EntityRef entity, BreatherComponent breather) {
        // only trigger org.terasology.breathing if liquid is covering the top of the character (aka,  the head)
        if (isAtHeadLevel(event.getCharacterRelativePosition(), entity)) {
            SuffocatingComponent suffocating = entity.getComponent(SuffocatingComponent.class);
            if (!blockIsBreathable(entity, event.getNewBlock())) {
                if (suffocating != null) {
                    if (suffocating.isBreathing) {
                        setBreathing(false, suffocating, breather);
                        entity.saveComponent(suffocating);
                    }
                } else {
                    suffocating = new SuffocatingComponent();
                    setBreathing(false, suffocating, breather);
                    entity.addComponent(suffocating);
                }
            } else {
                if (suffocating != null && !suffocating.isBreathing) {
                    setBreathing(true, suffocating, breather);
                    entity.saveComponent(suffocating);
                }
            }
        }
    }

    private boolean isAtHeadLevel(Vector3i relativePosition, EntityRef entity) {
        CharacterMovementComponent characterMovementComponent = entity.getComponent(CharacterMovementComponent.class);
        return (int) Math.ceil(characterMovementComponent.height) - 1 == relativePosition.y;
    }

    private boolean blockIsBreathable(EntityRef entity, Block block) {
        // TODO Once blocks become prefabs/entities, check to see if entity can breathe any of the block's elements
        // This is simply a placeholder for now
        BreatherComponent breather = entity.getComponent(BreatherComponent.class);
        BlockManager blockManager = CoreRegistry.get(BlockManager.class);

        for (int i = 0; i < breather.breathes.size(); i++) {
            String breathableMaterial = breather.breathes.get(i);

            if (breathableMaterial.equalsIgnoreCase("Oxygen") && block.equals(BlockManager.getAir()) ||
                breathableMaterial.equalsIgnoreCase("Water") && block.equals(blockManager.getBlock("core:water"))) {

                return true;
            }
        }
        return false;
    }

    private void setBreathing(boolean isBreathing, SuffocatingComponent suffocating, BreatherComponent breather) {
        long gameTime = time.getGameTimeInMs();
        // if this is a new org.terasology.breathing component, set to 0% so that it starts from the current time
        float currentPercentage = suffocating.startTime != 0 ? suffocating.getRemainingBreath(gameTime) : 1f;
        if (!isBreathing) {
            currentPercentage = 1f - currentPercentage;
        }
        float scale = isBreathing ? breather.breathRechargeRate : 1.0f;

        suffocating.isBreathing = isBreathing;
        suffocating.startTime = gameTime - (int) (breather.breathCapacity * currentPercentage / scale);
        suffocating.endTime = gameTime + (int) (breather.breathCapacity * (1f - currentPercentage) / scale);

        if (isBreathing) {
            suffocating.nextSuffocateDamageTime = Long.MAX_VALUE;
        } else {
            suffocating.nextSuffocateDamageTime = suffocating.endTime + breather.timeBetweenSuffocateDamage;
        }
    }
}
