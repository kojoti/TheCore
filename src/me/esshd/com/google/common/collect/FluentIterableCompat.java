/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.GwtCompatible
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  javax.annotation.CheckReturnValue
 */
package me.esshd.com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.Iterator;
import javax.annotation.CheckReturnValue;

@GwtCompatible(emulated = true)
public abstract class FluentIterableCompat<E>
implements Iterable<E> {
    private final Iterable<E> iterable;

    FluentIterableCompat(Iterable<E> iterable) {
        this.iterable = (Iterable)Preconditions.checkNotNull(iterable);
    }

    @CheckReturnValue
    public static <E> FluentIterableCompat<E> from(final Iterable<E> iterable) {
        return iterable instanceof FluentIterableCompat ? (FluentIterableCompat)iterable : new FluentIterableCompat<E>(iterable){

            @Override
            public Iterator<E> iterator() {
                return iterable.iterator();
            }
        };
    }

    @CheckReturnValue
    public String toString() {
        return Iterables.toString(this.iterable);
    }

    @CheckReturnValue
    public final FluentIterableCompat<E> filter(Predicate<? super E> predicate) {
        return FluentIterableCompat.from(Iterables.filter(this.iterable, predicate));
    }

    @CheckReturnValue
    public final <T> FluentIterableCompat<T> transform(Function<? super E, T> function) {
        return FluentIterableCompat.from(Iterables.transform(this.iterable, function));
    }

    @CheckReturnValue
    public final ImmutableList<E> toList() {
        return ImmutableList.copyOf(this.iterable);
    }

}

