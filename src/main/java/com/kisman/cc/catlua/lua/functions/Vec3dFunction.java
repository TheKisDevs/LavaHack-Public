package com.kisman.cc.catlua.lua.functions;

import com.kisman.cc.catlua.common.trait.Nameable;
import net.minecraft.util.math.Vec3d;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

public class Vec3dFunction extends ThreeArgFunction implements Nameable {

    @Override public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        return userdataOf(new Vec3d(arg1.todouble(), arg2.todouble(), arg3.todouble()));
    }

    @Override public String getName() {
        return "vec3d";
    }

}
