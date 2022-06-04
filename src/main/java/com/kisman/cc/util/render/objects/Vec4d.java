package com.kisman.cc.util.render.objects;

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.opengl.GL11;

public class Vec4d {
    public double x1, x2, y1, y2, x3, x4, y3, y4;
    
    public Vec4d(double[] first, double[] second, double[] third, double[] four) {
        x1 = first[0];
        y1 = first[1];
        x2 = second[0];
        y2 = second[1];
        x3 = third[0];
        y3 = third[1];
        x4 = four[0];
        y4 = four[1];
    }

    public ArrayList<double[]> toArray() {
        return new ArrayList<>(Arrays.asList(new double[] {x1, y1}, new double[] {x2, y2}, new double[] {x3, y3}, new double[] {x4, y4}));
    }

    public float getMinX() {return (float) Math.min(x1, Math.min(x2, Math.min(x3, x4)));}
    public float getMinY() {return (float) Math.min(y1, Math.min(y2, Math.min(y3, y4)));}
    public float getMaxX() {return (float) Math.max(x1, Math.max(x2, Math.max(x3, x4)));}
    public float getMaxY() {return (float) Math.max(y1, Math.max(y2, Math.max(y3, y4)));}
    public void setupVectors() {
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x3, y3);
        GL11.glVertex2d(x4, y4);
    }
}
