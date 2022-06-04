package com.kisman.cc.gui.alts;

import java.lang.reflect.Field;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import com.kisman.cc.Kisman;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.*;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class AltManager {
	private static final List<AltEntry> alts = new ArrayList<>();

	public static YggdrasilUserAuthentication logIn(String email, String password, boolean setSession) {
		YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
		auth.setUsername(email);
		auth.setPassword(password);

		new Thread(() -> {
			try {
				auth.logIn();
				if(setSession) setSession(new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang"));
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}).start();

		return auth;
	}
	
	public static List<AltEntry> getAlts() {
		return AltManager.alts;
	}

	public static void setSession(Session newSession) {
		Class<? extends Minecraft> mc = Minecraft.getMinecraft().getClass();
        try {
            Field session = null;
            for (Field field : mc.getDeclaredFields()) {
                if (field.getType().isInstance(newSession)) {
                    session = field;
                    Kisman.LOGGER.info("Attempting Injection into Session.");
                }
            }
            if (session == null) throw new IllegalStateException("No field of type " + Session.class.getCanonicalName() + " declared.");
            session.setAccessible(true);
            session.set(Minecraft.getMinecraft(), newSession);
            session.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
