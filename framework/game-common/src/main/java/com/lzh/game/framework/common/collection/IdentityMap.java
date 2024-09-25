package com.lzh.game.framework.common.collection;

// https://github.com/EsotericSoftware/kryo/blob/135df69526615bb3f6b34846e58ba3fec3b631c3/src/com/esotericsoftware/kryo/util/IdentityMap.java

/**
 * @author zehong.l
 * @since 2024-08-23 15:50
 **/
public class IdentityMap<K, V> extends ObjectMap<K, V> {
    /** Creates a new map with an initial capacity of 51 and a load factor of 0.8. */
    public IdentityMap () {
        super();
    }

    /** Creates a new map with a load factor of 0.8.
     * @param initialCapacity If not a power of two, it is increased to the next nearest power of two. */
    public IdentityMap (int initialCapacity) {
        super(initialCapacity);
    }

    /** Creates a new map with the specified initial capacity and load factor. This map will hold initialCapacity items before
     * growing the backing table.
     * @param initialCapacity If not a power of two, it is increased to the next nearest power of two. */
    public IdentityMap (int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /** Creates a new map identical to the specified map. */
    public IdentityMap (IdentityMap<K, V> map) {
        super(map);
    }

    protected int place (K item) {
        return System.identityHashCode(item) & mask;
    }

    public <T extends K> V get (T key) {
        for (int i = place(key);; i = i + 1 & mask) {
            K other = keyTable[i];
            if (other == null) return null;
            if (other == key) return valueTable[i];
        }
    }

    public V get (K key, V defaultValue) {
        for (int i = place(key);; i = i + 1 & mask) {
            K other = keyTable[i];
            if (other == null) return defaultValue;
            if (other == key) return valueTable[i];
        }
    }

    int locateKey (K key) {
        if (key == null) throw new IllegalArgumentException("key cannot be null.");
        K[] keyTable = this.keyTable;
        for (int i = place(key);; i = i + 1 & mask) {
            K other = keyTable[i];
            if (other == null) return -(i + 1); // Empty space is available.
            if (other == key) return i; // Same key was found.
        }
    }

    public int hashCode () {
        int h = size;
        K[] keyTable = this.keyTable;
        V[] valueTable = this.valueTable;
        for (int i = 0, n = keyTable.length; i < n; i++) {
            K key = keyTable[i];
            if (key != null) {
                h += System.identityHashCode(key);
                V value = valueTable[i];
                if (value != null) h += value.hashCode();
            }
        }
        return h;
    }
}