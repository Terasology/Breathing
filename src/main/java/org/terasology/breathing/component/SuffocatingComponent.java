/*
 * Copyright 2015 MovingBlocks
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
package org.terasology.breathing.component;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;

public class SuffocatingComponent implements Component {

    @Replicate(FieldReplicateType.SERVER_TO_OWNER)
    public boolean isBreathing;
    @Replicate(FieldReplicateType.SERVER_TO_OWNER)
    public long endTime;
    @Replicate(FieldReplicateType.SERVER_TO_OWNER)
    public long startTime;
    @Replicate(FieldReplicateType.SERVER_TO_OWNER)
    public long nextSuffocateDamageTime;

    public float getRemainingBreath(long gameTime) {
        // TODO create a logarithmic (diminishing returns) formula for breath recovery that gives faster recharge
        // at lower remaining breath, but with more reasonable recovery rates. This one is too fast too early.
        float capacity = (float) (endTime - startTime);
        float current = (gameTime - startTime);

        float percentage;
        if (isBreathing) {
            percentage = (float) (Math.log(current) / Math.log(capacity));
        } else {
            percentage = current / capacity;
            percentage = 1.0f - percentage;
        }
        return Math.min(Math.max(percentage, 0f), 1f);
    }

}
