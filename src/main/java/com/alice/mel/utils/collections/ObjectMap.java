package com.alice.mel.utils.collections;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ObjectMap<K,V> implements Iterable<ObjectMap.Entry<K, V>>  {

    static final Object dummy = new Object();

    public int size;

    K[] keyTable;
    V[] valueTable;

    float loadFactor;
    int threshold;

    protected int shift;
    protected int mask;

    transient Entries entries1, entries2;
    transient Values values1, values2;
    transient Keys keys1, keys2;

    public ObjectMap () {
        this(51, 0.8f);
    }

    public ObjectMap (int initialCapacity) {
        this(initialCapacity, 0.8f);
    }

    public ObjectMap (int initialCapacity, float loadFactor) {
        if (loadFactor <= 0f || loadFactor >= 1f)
            throw new IllegalArgumentException("loadFactor must be > 0 and < 1: " + loadFactor);
        this.loadFactor = loadFactor;

        int tableSize = ObjectSet.tableSize(initialCapacity, loadFactor);
        threshold = (int)(tableSize * loadFactor);
        mask = tableSize - 1;
        shift = Long.numberOfLeadingZeros(mask);

        keyTable = (K[])new Object[tableSize];
        valueTable = (V[])new Object[tableSize];
    }

    public ObjectMap (ObjectMap<? extends K, ? extends V> map) {
        this((int)(map.keyTable.length * map.loadFactor), map.loadFactor);
        System.arraycopy(map.keyTable, 0, keyTable, 0, map.keyTable.length);
        System.arraycopy(map.valueTable, 0, valueTable, 0, map.valueTable.length);
        size = map.size;
    }

    protected int place (K item) {
        return (int)(item.hashCode() * 0x9E3779B97F4A7C15L >>> shift);
    }

    int locateKey (K key) {
        if (key == null) throw new IllegalArgumentException("key cannot be null.");
        K[] keyTable = this.keyTable;
        for (int i = place(key);; i = i + 1 & mask) {
            K other = keyTable[i];
            if (other == null) return -(i + 1); // Empty space is available.
            if (other.equals(key)) return i; // Same key was found.
        }
    }

    public V put (K key,  V value) {
        int i = locateKey(key);
        if (i >= 0) { // Existing key was found.
            V oldValue = valueTable[i];
            valueTable[i] = value;
            return oldValue;
        }
        i = -(i + 1); // Empty space was found.
        keyTable[i] = key;
        valueTable[i] = value;
        if (++size >= threshold) resize(keyTable.length << 1);
        return null;
    }

    public void putAll (ObjectMap<? extends K, ? extends V> map) {
        ensureCapacity(map.size);
        K[] keyTable = map.keyTable;
        V[] valueTable = map.valueTable;
        K key;
        for (int i = 0, n = keyTable.length; i < n; i++) {
            key = keyTable[i];
            if (key != null) put(key, valueTable[i]);
        }
    }

    private void putResize (K key,  V value) {
        K[] keyTable = this.keyTable;
        for (int i = place(key);; i = (i + 1) & mask) {
            if (keyTable[i] == null) {
                keyTable[i] = key;
                valueTable[i] = value;
                return;
            }
        }
    }

    public  <T extends K> V get (T key) {
        int i = locateKey(key);
        return i < 0 ? null : valueTable[i];
    }

    public V get (K key,  V defaultValue) {
        int i = locateKey(key);
        return i < 0 ? defaultValue : valueTable[i];
    }

    public  V remove (K key) {
        int i = locateKey(key);
        if (i < 0) return null;
        K[] keyTable = this.keyTable;
        V[] valueTable = this.valueTable;
        V oldValue = valueTable[i];
        int mask = this.mask, next = i + 1 & mask;
        while ((key = keyTable[next]) != null) {
            int placement = place(key);
            if ((next - placement & mask) > (i - placement & mask)) {
                keyTable[i] = key;
                valueTable[i] = valueTable[next];
                i = next;
            }
            next = next + 1 & mask;
        }
        keyTable[i] = null;
        valueTable[i] = null;
        size--;
        return oldValue;
    }

    public boolean notEmpty () {
        return size > 0;
    }

    public boolean isEmpty () {
        return size == 0;
    }

    public void shrink (int maximumCapacity) {
        if (maximumCapacity < 0) throw new IllegalArgumentException("maximumCapacity must be >= 0: " + maximumCapacity);
        int tableSize = ObjectSet.tableSize(maximumCapacity, loadFactor);
        if (keyTable.length > tableSize) resize(tableSize);
    }

    public void clear (int maximumCapacity) {
        int tableSize = ObjectSet.tableSize(maximumCapacity, loadFactor);
        if (keyTable.length <= tableSize) {
            clear();
            return;
        }
        size = 0;
        resize(tableSize);
    }

    public void clear () {
        if (size == 0) return;
        size = 0;
        Arrays.fill(keyTable, null);
        Arrays.fill(valueTable, null);
    }

    public boolean containsValue ( Object value, boolean identity) {
        V[] valueTable = this.valueTable;
        if (value == null) {
            K[] keyTable = this.keyTable;
            for (int i = valueTable.length - 1; i >= 0; i--)
                if (keyTable[i] != null && valueTable[i] == null) return true;
        } else if (identity) {
            for (int i = valueTable.length - 1; i >= 0; i--)
                if (valueTable[i] == value) return true;
        } else {
            for (int i = valueTable.length - 1; i >= 0; i--)
                if (value.equals(valueTable[i])) return true;
        }
        return false;
    }

    public boolean containsKey (K key) {
        return locateKey(key) >= 0;
    }

    public  K findKey ( Object value, boolean identity) {
        V[] valueTable = this.valueTable;
        if (value == null) {
            K[] keyTable = this.keyTable;
            for (int i = valueTable.length - 1; i >= 0; i--)
                if (keyTable[i] != null && valueTable[i] == null) return keyTable[i];
        } else if (identity) {
            for (int i = valueTable.length - 1; i >= 0; i--)
                if (valueTable[i] == value) return keyTable[i];
        } else {
            for (int i = valueTable.length - 1; i >= 0; i--)
                if (value.equals(valueTable[i])) return keyTable[i];
        }
        return null;
    }

    public void ensureCapacity (int additionalCapacity) {
        int tableSize = ObjectSet.tableSize(size + additionalCapacity, loadFactor);
        if (keyTable.length < tableSize) resize(tableSize);
    }

    final void resize (int newSize) {
        int oldCapacity = keyTable.length;
        threshold = (int)(newSize * loadFactor);
        mask = newSize - 1;
        shift = Long.numberOfLeadingZeros(mask);

        K[] oldKeyTable = keyTable;
        V[] oldValueTable = valueTable;

        keyTable = (K[])new Object[newSize];
        valueTable = (V[])new Object[newSize];

        if (size > 0) {
            for (int i = 0; i < oldCapacity; i++) {
                K key = oldKeyTable[i];
                if (key != null) putResize(key, oldValueTable[i]);
            }
        }
    }

    public int hashCode () {
        int h = size;
        K[] keyTable = this.keyTable;
        V[] valueTable = this.valueTable;
        for (int i = 0, n = keyTable.length; i < n; i++) {
            K key = keyTable[i];
            if (key != null) {
                h += key.hashCode();
                V value = valueTable[i];
                if (value != null) h += value.hashCode();
            }
        }
        return h;
    }

    public boolean equals (Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ObjectMap)) return false;
        ObjectMap other = (ObjectMap)obj;
        if (other.size != size) return false;
        K[] keyTable = this.keyTable;
        V[] valueTable = this.valueTable;
        for (int i = 0, n = keyTable.length; i < n; i++) {
            K key = keyTable[i];
            if (key != null) {
                V value = valueTable[i];
                if (value == null) {
                    if (other.get(key, dummy) != null) return false;
                } else {
                    if (!value.equals(other.get(key))) return false;
                }
            }
        }
        return true;
    }

    public boolean equalsIdentity ( Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ObjectMap)) return false;
        ObjectMap other = (ObjectMap)obj;
        if (other.size != size) return false;
        K[] keyTable = this.keyTable;
        V[] valueTable = this.valueTable;
        for (int i = 0, n = keyTable.length; i < n; i++) {
            K key = keyTable[i];
            if (key != null && valueTable[i] != other.get(key, dummy)) return false;
        }
        return true;
    }

    public String toString (String separator) {
        return toString(separator, false);
    }

    public String toString () {
        return toString(", ", true);
    }

    protected String toString (String separator, boolean braces) {
        if (size == 0) return braces ? "{}" : "";
        java.lang.StringBuilder buffer = new java.lang.StringBuilder(32);
        if (braces) buffer.append('{');
        K[] keyTable = this.keyTable;
        V[] valueTable = this.valueTable;
        int i = keyTable.length;
        while (i-- > 0) {
            K key = keyTable[i];
            if (key == null) continue;
            buffer.append(key == this ? "(this)" : key);
            buffer.append('=');
            V value = valueTable[i];
            buffer.append(value == this ? "(this)" : value);
            break;
        }
        while (i-- > 0) {
            K key = keyTable[i];
            if (key == null) continue;
            buffer.append(separator);
            buffer.append(key == this ? "(this)" : key);
            buffer.append('=');
            V value = valueTable[i];
            buffer.append(value == this ? "(this)" : value);
        }
        if (braces) buffer.append('}');
        return buffer.toString();
    }

    public Entries<K, V> iterator () {
        return entries();
    }

    public Entries<K, V> entries () {
        if (Collections.allocateIterators) return new Entries(this);
        if (entries1 == null) {
            entries1 = new Entries(this);
            entries2 = new Entries(this);
        }
        if (!entries1.valid) {
            entries1.reset();
            entries1.valid = true;
            entries2.valid = false;
            return entries1;
        }
        entries2.reset();
        entries2.valid = true;
        entries1.valid = false;
        return entries2;
    }


    public Values<V> values () {
        if (Collections.allocateIterators) return new Values(this);
        if (values1 == null) {
            values1 = new Values(this);
            values2 = new Values(this);
        }
        if (!values1.valid) {
            values1.reset();
            values1.valid = true;
            values2.valid = false;
            return values1;
        }
        values2.reset();
        values2.valid = true;
        values1.valid = false;
        return values2;
    }


    public Keys<K> keys () {
        if (Collections.allocateIterators) return new Keys(this);
        if (keys1 == null) {
            keys1 = new Keys(this);
            keys2 = new Keys(this);
        }
        if (!keys1.valid) {
            keys1.reset();
            keys1.valid = true;
            keys2.valid = false;
            return keys1;
        }
        keys2.reset();
        keys2.valid = true;
        keys1.valid = false;
        return keys2;
    }

    static public class Entry<K, V> {
        public K key;
        public  V value;

        public String toString () {
            return key + "=" + value;
        }
    }

    static private abstract class MapIterator<K, V, I> implements Iterable<I>, Iterator<I> {
        public boolean hasNext;

        final ObjectMap<K, V> map;
        int nextIndex, currentIndex;
        boolean valid = true;

        public MapIterator (ObjectMap<K, V> map) {
            this.map = map;
            reset();
        }

        public void reset () {
            currentIndex = -1;
            nextIndex = -1;
            findNextIndex();
        }

        void findNextIndex () {
            K[] keyTable = map.keyTable;
            for (int n = keyTable.length; ++nextIndex < n;) {
                if (keyTable[nextIndex] != null) {
                    hasNext = true;
                    return;
                }
            }
            hasNext = false;
        }

        public void remove () {
            int i = currentIndex;
            if (i < 0) throw new IllegalStateException("next must be called before remove.");
            K[] keyTable = map.keyTable;
            V[] valueTable = map.valueTable;
            int mask = map.mask, next = i + 1 & mask;
            K key;
            while ((key = keyTable[next]) != null) {
                int placement = map.place(key);
                if ((next - placement & mask) > (i - placement & mask)) {
                    keyTable[i] = key;
                    valueTable[i] = valueTable[next];
                    i = next;
                }
                next = next + 1 & mask;
            }
            keyTable[i] = null;
            valueTable[i] = null;
            map.size--;
            if (i != currentIndex) --nextIndex;
            currentIndex = -1;
        }
    }

    static public class Entries<K, V> extends MapIterator<K, V, Entry<K, V>> {
        Entry<K, V> entry = new Entry<K, V>();

        public Entries (ObjectMap<K, V> map) {
            super(map);
        }

        public Entry<K, V> next () {
            if (!hasNext) throw new NoSuchElementException();
            if (!valid) throw new RuntimeException("#iterator() cannot be used nested.");
            K[] keyTable = map.keyTable;
            entry.key = keyTable[nextIndex];
            entry.value = map.valueTable[nextIndex];
            currentIndex = nextIndex;
            findNextIndex();
            return entry;
        }

        public boolean hasNext () {
            if (!valid) throw new RuntimeException("#iterator() cannot be used nested.");
            return hasNext;
        }

        public Entries<K, V> iterator () {
            return this;
        }
    }

    static public class Values<V> extends MapIterator<Object, V, V> {
        public Values (ObjectMap<?, V> map) {
            super((ObjectMap<Object, V>)map);
        }

        public boolean hasNext () {
            if (!valid) throw new RuntimeException("#iterator() cannot be used nested.");
            return hasNext;
        }

        public  V next () {
            if (!hasNext) throw new NoSuchElementException();
            if (!valid) throw new RuntimeException("#iterator() cannot be used nested.");
            V value = map.valueTable[nextIndex];
            currentIndex = nextIndex;
            findNextIndex();
            return value;
        }

        public Values<V> iterator () {
            return this;
        }

        public Array<V> toArray () {
            return toArray(new Array(true, map.size));
        }

        public Array<V> toArray (Array<V> array) {
            while (hasNext)
                array.add(next());
            return array;
        }
    }

    static public class Keys<K> extends MapIterator<K, Object, K> {
        public Keys (ObjectMap<K, ?> map) {
            super((ObjectMap<K, Object>)map);
        }

        public boolean hasNext () {
            if (!valid) throw new RuntimeException("#iterator() cannot be used nested.");
            return hasNext;
        }

        public K next () {
            if (!hasNext) throw new NoSuchElementException();
            if (!valid) throw new RuntimeException("#iterator() cannot be used nested.");
            K key = map.keyTable[nextIndex];
            currentIndex = nextIndex;
            findNextIndex();
            return key;
        }

        public Keys<K> iterator () {
            return this;
        }

        public Array<K> toArray () {
            return toArray(new Array<K>(true, map.size));
        }

        public Array<K> toArray (Array<K> array) {
            while (hasNext)
                array.add(next());
            return array;
        }
    }
}


