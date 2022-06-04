package com.kisman.cc.util.enums;

import net.minecraft.util.math.Vec3i;

import java.util.*;

public enum SelfTrapVectors {
    Head(Arrays.asList(new Vec3i(0, 1, 0), new Vec3i(1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(-1, 0, 0), new Vec3i(0, 0, -1))),
    AroundFull(Arrays.asList(new Vec3i(0, 1, 0), new Vec3i(0, -1, 0), new Vec3i(1, 0, 0), new Vec3i(-1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(0, 0, -1))),
    Around(Arrays.asList(new Vec3i(1, 0, 0), new Vec3i(-1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(0, 0, -1)));
        
    public List<Vec3i> vec;
    SelfTrapVectors(List<Vec3i> vec) {this.vec = vec;}
    public List<Vec3i> clear(Direction dir) {
        List<Vec3i> vectors = new ArrayList<>();
        for(Vec3i vec : this.vec) if(!vec.equals(dir.vec)) vectors.add(vec);
        return vectors;
    }
    public List<Vec3i> sort(Direction primary) {
        List<Vec3i> vectors = new ArrayList<>();
        int index = -1;
        for(Vec3i vec : this.vec) if(vec.equals(primary.vec)) {
            vectors.add(vec);
            index = this.vec.indexOf(vec);
            break;
        }
        for(Vec3i vec : this.vec) if(this.vec.indexOf(vec) != index) vectors.add(vec);
        return vectors;
    }
}