package com.kisman.cc.util.enums;

import net.minecraft.util.math.Vec3i;

import java.util.*;

public enum SurroundVectors {
    STANDARD(new ArrayList<>(Arrays.asList(new Vec3i(0, -1, 0), new Vec3i(1, 0, 0), new Vec3i(-1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(0, 0, -1))));

    public final List<Vec3i> vectors;
    SurroundVectors(List<Vec3i> vectors) {
        this.vectors = vectors;
    }
}
