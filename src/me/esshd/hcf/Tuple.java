/*
 * Decompiled with CFR 0_119.
 */
package me.esshd.hcf;

public class Tuple<K, V> {
    private final K key;
    private final V value;

    public Tuple(K k, V value) {
        this.key = k;
        this.value = value;
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }
}

