// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.breathing.component;

import com.google.common.collect.Lists;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Use to signify an entity is subject to org.terasology.breathing
 */
public class BreatherComponent implements Component<BreatherComponent> {

    public long breathCapacity = 15000;
    public float breathRechargeRate = 2.0f;
    public int timeBetweenSuffocateDamage = 1000;
    public int suffocateDamage = 10;

    public List<String> breathes = Arrays.asList("Oxygen");

    @Override
    public void copyFrom(BreatherComponent other) {
        this.breathCapacity = other.breathCapacity;
        this.breathRechargeRate = other.breathRechargeRate;
        this.timeBetweenSuffocateDamage = other.timeBetweenSuffocateDamage;
        this.suffocateDamage = other.suffocateDamage;
        this.breathes = Lists.newArrayList(other.breathes);
    }
}
