package au.org.ala.profile.hub.reports;

import java.awt.*;

import static java.lang.Math.pow;
import static java.lang.Math.round;

public class ColourUtils {
    public static int clamp(long n) {
        if (n < 0) return 0;
        if (n > 255) return 255;
        return (int)n;
    }

    public static int clamp(int n) {
        if (n < 0) return 0;
        if (n > 255) return 255;
        return n;
    }

    public static int clamp(double n) {
        return clamp(Math.round(n));
    }

    public static double contrastRatio(double l1, double l2) {
        return (l1 + 0.05) / (l2 + 0.05);
    }

    public static double relativeLuminance(int red, int green, int blue) {
//        Note 1: For the sRGB colorspace, the relative luminance of a color is defined as L = 0.2126 * R + 0.7152 * G + 0.0722 * B where R, G and B are defined as:
//
//        if RsRGB <= 0.03928 then R = RsRGB/12.92 else R = ((RsRGB+0.055)/1.055) ^ 2.4
//        if GsRGB <= 0.03928 then G = GsRGB/12.92 else G = ((GsRGB+0.055)/1.055) ^ 2.4
//        if BsRGB <= 0.03928 then B = BsRGB/12.92 else B = ((BsRGB+0.055)/1.055) ^ 2.4
        double r = red / 255.0;
        double g = green / 255.0;
        double b = blue / 255.0;

        double R = enbiggen(r);
        double G = enbiggen(g);
        double B = enbiggen(b);

        return 0.2126 * R + 0.7152 * G + 0.0722 * B;
    }

    private static double enbiggen(double c) {
        if (c <= 0.03928) {
            return c / 12.92;
        } else {
            return pow((c+0.055)/1.055, 2.4);
        }
    }

    public static Color lighten(Color color, double percent) {
        return new Color(clamp(round(color.getRed() + 255.0 * percent)), clamp(round(color.getGreen() + 255.0 * percent)), clamp(round(color.getBlue() + 255.0 * percent)));
    }

    public static Color darken(Color color, double percent) {
        return new Color(clamp(round(color.getRed() - 255.0 * percent)), clamp(round(color.getGreen() - 255.0 * percent)), clamp(round(color.getBlue() - 255.0 * percent)));
    }

    public static Color withAlpha(Color color, double alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), clamp(255.0 * alpha));
    }

    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static int minOf(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    public static int maxOf(int a, int b, int c) {
        return Math.max(Math.max(a, b), c);
    }

    public static Color desaturate(Color p) {
        int bw = clamp((minOf(p.getRed(), p.getGreen(), p.getBlue()) + maxOf(p.getRed(), p.getGreen(), p.getBlue())) / 2);
        return new Color(bw, bw, bw, p.getAlpha());
    }

    public static Color blend(Color fg, Color bg) {
        double fga = (double)fg.getAlpha() / 255.0;
        double bga = (double)bg.getAlpha() / 255.0;

        double outa = fga + bga * (1.0 - fga);

        if (outa <= 0.0) return new Color(0,0,0,0);

        double fgr = (double)fg.getRed() / 255.0;
        double bgr = (double)bg.getRed() / 255.0;
        double outr = (fgr * fga + bgr * bga * (1.0 - fga)) / outa;

        double fgg = (double)fg.getGreen() / 255.0;
        double bgg = (double)bg.getGreen() / 255.0;
        double outg = (fgg * fga + bgg * bga * (1.0 - fga)) / outa;

        double fgb = (double)fg.getBlue() / 255.0;
        double bgb = (double)bg.getBlue() / 255.0;
        double outb = (fgb * fga + bgb * bga * (1.0 - fga)) / outa;

        return new Color(clamp(outr * 255.0), clamp(outg * 255.0), clamp(outb * 255.0), clamp(outa * 255.0));
    }
}
