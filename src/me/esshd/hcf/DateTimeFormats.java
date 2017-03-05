/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.org.apache.commons.lang3.time.FastDateFormat
 */
package me.esshd.hcf;

import java.text.DecimalFormat;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;
import net.minecraft.util.org.apache.commons.lang3.time.FastDateFormat;

public final class DateTimeFormats {
    public static final TimeZone SERVER_TIME_ZONE = TimeZone.getTimeZone("EST");
    public static final ZoneId SERVER_ZONE_ID = SERVER_TIME_ZONE.toZoneId();
    public static final FastDateFormat DAY_MTH_HR_MIN_SECS = FastDateFormat.getInstance((String)"dd/MM HH:mm:ss", (TimeZone)SERVER_TIME_ZONE, (Locale)Locale.ENGLISH);
    public static final FastDateFormat DAY_MTH_YR_HR_MIN_AMPM = FastDateFormat.getInstance((String)"dd/MM/yy hh:mma", (TimeZone)SERVER_TIME_ZONE, (Locale)Locale.ENGLISH);
    public static final FastDateFormat DAY_MTH_HR_MIN_AMPM = FastDateFormat.getInstance((String)"dd/MM hh:mma", (TimeZone)SERVER_TIME_ZONE, (Locale)Locale.ENGLISH);
    public static final FastDateFormat HR_MIN_AMPM = FastDateFormat.getInstance((String)"hh:mma", (TimeZone)SERVER_TIME_ZONE, (Locale)Locale.ENGLISH);
    public static final FastDateFormat HR_MIN_AMPM_TIMEZONE = FastDateFormat.getInstance((String)"hh:mma z", (TimeZone)SERVER_TIME_ZONE, (Locale)Locale.ENGLISH);
    public static final FastDateFormat HR_MIN = FastDateFormat.getInstance((String)"hh:mm", (TimeZone)SERVER_TIME_ZONE, (Locale)Locale.ENGLISH);
    public static final FastDateFormat MIN_SECS = FastDateFormat.getInstance((String)"mm:ss", (TimeZone)SERVER_TIME_ZONE, (Locale)Locale.ENGLISH);
    public static final FastDateFormat KOTH_FORMAT = FastDateFormat.getInstance((String)"m:ss", (TimeZone)SERVER_TIME_ZONE, (Locale)Locale.ENGLISH);
    public static final ThreadLocal<DecimalFormat> REMAINING_SECONDS = new ThreadLocal<DecimalFormat>(){

        @Override
        protected DecimalFormat initialValue() {
            return new DecimalFormat("0.#");
        }
    };
    public static final ThreadLocal<DecimalFormat> REMAINING_SECONDS_TRAILING = new ThreadLocal<DecimalFormat>(){

        @Override
        protected DecimalFormat initialValue() {
            return new DecimalFormat("0.0");
        }
    };

    private DateTimeFormats() {
    }

}

