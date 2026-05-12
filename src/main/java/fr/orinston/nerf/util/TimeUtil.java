package fr.orinston.nerf.util;

public final class TimeUtil {
    private TimeUtil() {}
    public static String remaining(long untilMillis) {
        long diff = Math.max(0, untilMillis - System.currentTimeMillis()) / 1000;
        long h = diff / 3600;
        long m = (diff % 3600) / 60;
        long s = diff % 60;
        if (h > 0) return h + "h " + m + "m";
        if (m > 0) return m + "m " + s + "s";
        return s + "s";
    }
    public static long hourToMinecraftTick(int hour) {
        return Math.floorMod(hour - 6, 24) * 1000L;
    }
}
