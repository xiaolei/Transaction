package io.github.xiaolei.enterpriselibrary.utility;

import android.graphics.Color;

import java.util.Random;

/**
 * TODO: add comment
 */
public class ColorUtil {
    public static int generateRandomColor() {
        Random rnd = new Random();
        int r = rnd.nextInt(256);
        int g = rnd.nextInt(256);
        int b = rnd.nextInt(256);

        if ((r == 0 && g == 0 && b == 0) || (r == 255 && g == 255 && b == 255)) {
            return generateRandomColor();
        }

        int color = Color.argb(30, r, g, b);

        return color;
    }

    public static int getDarkerColor(int color) {
        return (color & 0xfefefe) >> 1;
    }

    public static int getBrighterColor(int color) {
        return (color & 0x7f7f7f) << 1;
    }
}
