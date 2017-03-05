/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package me.esshd.com.google.common.collect;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;

public class GuavaCompat {
    public static <T extends Enum<T>> Optional<T> getIfPresent(Class<T> enumClass, String value) {
        Preconditions.checkNotNull(enumClass);
        Preconditions.checkNotNull((Object)value);
        try {
            return Optional.of(Enum.valueOf(enumClass, value));
        }
        catch (IllegalArgumentException iae) {
            return Optional.absent();
        }
    }

    public static <T> T firstNonNull(@Nullable T first, @Nullable T second) {
        return first != null ? first : Preconditions.checkNotNull(second);
    }
}

