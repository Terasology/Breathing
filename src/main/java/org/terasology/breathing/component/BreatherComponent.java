// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.breathing.component;

import org.terasology.engine.entitySystem.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Use to signify an entity is subject to org.terasology.breathing
 */
public class BreatherComponent implements Component {

    public long breathCapacity = 15000;
    public float breathRechargeRate = 2.0f;
    public int timeBetweenSuffocateDamage = 1000;
    public int suffocateDamage = 10;

    public List<String> breathes = Arrays.asList("Oxygen");

}
