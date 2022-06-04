package com.kisman.cc.gui.particle;

import org.lwjgl.util.vector.*;

import i.gishreloaded.gishcode.utils.visual.ColorUtils;

import java.util.*;

import com.kisman.cc.module.client.Config;
import com.kisman.cc.util.Colour;

import org.lwjgl.opengl.*;

public class Particle
{
    private float alpha;
    public Colour color;
    private final Vector2f pos;
    private static final Random random;
    private float size;
    private Vector2f velocity;

    public Particle(final Vector2f velocity, final float x, final float y, final float size) {
        this.velocity = velocity;
        this.pos = new Vector2f(x, y);
        this.size = size;
        this.color = ColorUtils.getRandomColour();
    }

    public static Particle generateParticle() {
        final Vector2f velocity = new Vector2f((float)(Math.random() * 3.0 - 1.0), (float)(Math.random() * 3.0 - 1.0));
        final float x = (float)Particle.random.nextInt(Display.getWidth());
        final float y = (float)Particle.random.nextInt(Display.getHeight());
        final float size = (float)(Math.random() * 4.0) + 2.0f;
        return new Particle(velocity, x, y, size);
    }

    public Vector2f getVelocity() {
        return this.velocity;
    }

    public void setVelocity(final Vector2f velocity) {
        this.velocity = velocity;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public float getDistanceTo(final Particle particle1) {
        return this.getDistanceTo(particle1.getX(), particle1.getY());
    }

    public float getDistanceTo(final float f, final float f2) {
        return (float)ParticleSystem.distance(this.getX(), this.getY(), f, f2);
    }

    public float getSize() {
        return this.size;
    }

    public float getX() {
        return this.pos.getX();
    }

    public float getY() {
        return this.pos.getY();
    }

    public void setSize(final float f) {
        this.size = f;
    }

    public void setX(final float f) {
        this.pos.setX(f);
    }

    public void setY(final float f) {
        this.pos.setY(f);
    }

    public void tick(final int delta, final float speed) {
        if(Config.instance.particleTest.getValBoolean()) color.nextColor();
        final Vector2f pos = this.pos;
        pos.x += this.velocity.getX() * delta * speed;
        final Vector2f pos2 = this.pos;
        pos2.y += this.velocity.getY() * delta * speed;
        if (this.alpha < 255.0f) {
            this.alpha += 0.05f * delta;
        }
        if (this.pos.getX() > Display.getWidth()) {
            this.pos.setX(0.0f);
        }
        if (this.pos.getX() < 0.0f) {
            this.pos.setX((float)Display.getWidth());
        }
        if (this.pos.getY() > Display.getHeight()) {
            this.pos.setY(0.0f);
        }
        if (this.pos.getY() < 0.0f) {
            this.pos.setY((float)Display.getHeight());
        }
    }

    static {
        random = new Random();
    }
}
