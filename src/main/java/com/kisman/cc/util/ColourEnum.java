package com.kisman.cc.util;

import com.kisman.cc.Kisman;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;

import java.awt.*;

public enum ColourEnum {
        Rainbow(Colour.COLOR_RAINBOW),
        Astolfo(Colour.COLOR_ASTOLFO),
        Static(Colour.COLOR_PRIMARY);

        public int mode, delay = 1, offset = 100;
        public Colour primary = new Colour(Color.RED);
        
        ColourEnum(int mode) {
            this.mode = mode;
        }

        public Colour getColour() {
                if(mode == Colour.COLOR_RAINBOW) return new Colour(ColorUtils.rainbow(delay, offset));
                if(mode == Colour.COLOR_ASTOLFO) return Kisman.canUseImprAstolfo ? new Colour(ColorUtils.getAstolfoRainbow(offset)) : new Colour(ColorUtils.astolfoColors(offset, offset));
                return primary;
        }
}