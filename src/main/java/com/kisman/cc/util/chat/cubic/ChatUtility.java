package com.kisman.cc.util.chat.cubic;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Module;
import com.kisman.cc.util.ReflectUtil;
import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.Map;

public class ChatUtility {

    private static final Minecraft mc;

    static final Map<Class<?>, Module> moduleMapping;

    private static final AbstractChatMessage COMPLETE = new ChatComplete();

    private static final AbstractChatMessage ERROR = new ChatError();

    private static final AbstractChatMessage INFO = new ChatInfo();

    private static final AbstractChatMessage MESSAGE = new ChatMessage();

    private static final AbstractChatMessage WARNING = new ChatWarning();

    static {
        mc = Minecraft.getMinecraft();
        moduleMapping = new HashMap<>(Kisman.instance.moduleManager.modules.size());
        initMappings();
    }

    private static void initMappings(){
        int size = Kisman.instance.moduleManager.modules.size();
        for(int i = 0; i < size; i++){
            Module m = Kisman.instance.moduleManager.modules.get(i);
            moduleMapping.put(m.getClass(), m);
        }
    }


    public static AbstractChatMessage complete(){
        COMPLETE.updateCaller(ReflectUtil.getCallerClass());
        return COMPLETE;
    }

    public static AbstractChatMessage error(){
        ERROR.updateCaller(ReflectUtil.getCallerClass());
        return ERROR;
    }

    public static AbstractChatMessage info(){
        INFO.updateCaller(ReflectUtil.getCallerClass());
        return INFO;
    }

    public static AbstractChatMessage message(){
        MESSAGE.updateCaller(ReflectUtil.getCallerClass());
        return MESSAGE;
    }

    public static AbstractChatMessage warning(){
        WARNING.updateCaller(ReflectUtil.getCallerClass());
        return WARNING;
    }
}
