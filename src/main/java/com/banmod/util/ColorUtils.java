package com.banmod.util;

/**
 * Utility methods for hex-colour string handling.
 */
public final class ColorUtils {

    private ColorUtils() {}

    /**
     * Parses a hex colour string of the form {@code #RRGGBB} or {@code #RGB}
     * and returns it as a packed ARGB int with full opacity (alpha = 0xFF).
     *
     * @param hex      the hex string (leading {@code #} is optional)
     * @param fallback value returned on parse failure
     */
    public static int parseHex(String hex, int fallback) {
        if (hex == null || hex.isEmpty()) return fallback;
        String s = hex.startsWith("#") ? hex.substring(1) : hex;
        try {
            if (s.length() == 3) {
                // Expand shorthand #RGB → #RRGGBB
                s = String.valueOf(s.charAt(0)) + s.charAt(0)
                        + s.charAt(1) + s.charAt(1)
                        + s.charAt(2) + s.charAt(2);
            }
            int rgb = Integer.parseInt(s, 16);
            return 0xFF000000 | (rgb & 0xFFFFFF);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    /**
     * Parses {@code #RRGGBB} and applies the given opacity (0.0–1.0) as the alpha channel.
     */
    public static int parseHexWithOpacity(String hex, float opacity, int fallback) {
        int base = parseHex(hex, fallback) & 0x00FFFFFF;
        int alpha = Math.round(opacity * 255f) & 0xFF;
        return (alpha << 24) | base;
    }

    /**
     * Linearly interpolates between two colours.
     *
     * @param t  interpolation factor 0.0–1.0
     */
    public static int lerpColor(int colorA, int colorB, float t) {
        int aA = (colorA >> 24) & 0xFF, rA = (colorA >> 16) & 0xFF,
                gA = (colorA >> 8) & 0xFF,  bA = colorA & 0xFF;
        int aB = (colorB >> 24) & 0xFF, rB = (colorB >> 16) & 0xFF,
                gB = (colorB >> 8) & 0xFF,  bB = colorB & 0xFF;
        int a = lerp(aA, aB, t), r = lerp(rA, rB, t),
                g = lerp(gA, gB, t), b = lerp(bA, bB, t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int lerp(int a, int b, float t) {
        return Math.round(a + (b - a) * t);
    }

    /**
     * Converts a packed ARGB int back to a {@code #RRGGBB} hex string.
     */
    public static String toHexString(int argb) {
        return String.format("#%06X", argb & 0xFFFFFF);
    }

    /** Returns {@code true} if the string looks like a valid hex colour. */
    public static boolean isValid(String hex) {
        if (hex == null) return false;
        String s = hex.startsWith("#") ? hex.substring(1) : hex;
        return s.matches("[0-9A-Fa-f]{3}|[0-9A-Fa-f]{6}");
    }
}
