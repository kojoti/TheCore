/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils
 */
package me.esshd.hcf;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;
import me.esshd.hcf.DateTimeFormats;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

public class DurationFormatter {
    private static final long MINUTE = TimeUnit.MINUTES.toMillis(1);
    private static final long HOUR = TimeUnit.HOURS.toMillis(1);

    public static String getRemaining(long millis, boolean milliseconds) {
        return DurationFormatter.getRemaining(millis, milliseconds, true);
    }

    public static String getRemaining(long duration, boolean milliseconds, boolean trail) {
        if (milliseconds && duration < MINUTE) {
            return String.valueOf((trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format((double)duration * 0.001)) + 's';
        }
        return DurationFormatUtils.formatDuration((long)duration, (String)(String.valueOf(duration >= HOUR ? "HH:" : "") + "mm:ss"));
    }
}

