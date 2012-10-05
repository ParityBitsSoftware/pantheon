package org.paritybits.pantheon.common;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * This is a map implementation that does not simply replace
 * the value of an existing key during puts. It wraps another map and can
 * have functions that determine the values of keys when existing
 * keys are replaced.  When a key is put that already exists, the
 * PutFunction for that key is used to determine the value to be put in the wrapped
 * map.  Each key can have a unique function, and the map can have a default
 * function for all nondefined keys.  If no default function is defined,
 * the map will do a simple replace for that key.
 * <p/>
 * This is useful if your access to a map has a lot of checks for an existing value,
 * operations using the old value to calculate a new value that is then put in the map.  An example;
 * <p/>
 * <pre>
 * BigDecimal new = null;
 * BigDecimal old = map.get(key1);
 * <p/>
 * if(old != null) {
 *     new = old.add(number1);
 * }
 * map.put(key1, new);
 * ....
 * old = map.get(key2);
 * if(old != null) {
 *      new = old.add(number2);
 * }
 * map.put(key2, new);
 * etc.
 * </pre>
 * <p/>
 * This could be simplified to
 * <p/>
 * <pre>
 * FunctionMap fMap = new FunctionMap(map, new PutFunction<BigDecimal>() {
 *      Integer determineValue(BigDecimal oldValue, BigDecimal newValue) {
 *          return oldValue.add(newValue);
 *      }
 * });
 * fmap.put(key1, number1);
 * fmap.put(key2, number2);
 * etc.
 * </pre>
 * <p/>
 * <p/>
 * If no wrapped map is specified, a new HashMap will be used.
 * <p/>
 * This class is not thread safe even if one uses a thread safe wrapped
 * map.
 *
 * @author andrew
 * @version 0.9
 * @param <K> The type of key.
 * @param <V> The type of value
 */
public final class FunctionMap<K, V> extends AbstractMap<K, V> {

    private final Map<K, V> wrappedMap;
    private final PutFunction<V> defaultFunction;
    private final Map<K, PutFunction<V>> keyFunctions =
            new HashMap<K, PutFunction<V>>();

    private static final PutFunction SIMPLE_REPLACE = new PutFunction() {
        public Object determineValue(Object oldValue, Object newValue) {
            return newValue;
        }
    };

    /**
     * Create a new empty FunctionMap with a HashMap as the underlying implementation.
     */
    public FunctionMap() {
        this(SIMPLE_REPLACE);
    }

    /**
     * Create a new FunctionMap with the given default function.  Again, a HashMap will be used for
     * the underlying map
     *
     * @param defaultFunction The default function for puts.
     */
    public FunctionMap(final PutFunction<V> defaultFunction) {
        this(new HashMap<K, V>(), defaultFunction);
    }

    /**
     * Create a new FunctionMap that wraps the given map.
     *
     * @param wrappedMap The map that will be the underlying map for new FunctionMap
     */
    public FunctionMap(final Map<K, V> wrappedMap) {
        this(wrappedMap, SIMPLE_REPLACE);
    }

    /**
     * Create a new FunctionMap that wraps the given map and has a default puts function.
     *
     * @param wrappedMap      The map that will be the underlying map for new FunctionMap
     * @param defaultFunction The default function for puts.
     */
    public FunctionMap(final Map<K, V> wrappedMap,
                       final PutFunction<V> defaultFunction) {
        this.wrappedMap = wrappedMap;
        this.defaultFunction = defaultFunction;
    }

    /**
     * @return The entrySet of the underlying wrapped map.
     */
    public Set<Entry<K, V>> entrySet() {
        return wrappedMap.entrySet();
    }

    /**
     * Puts a new value in the Map for the given key.  The actual value that will be put in the underlying map
     * will be determined by the PutsFunction for the key.
     *
     * @param key   The key to update
     * @param value The new value to use in the update
     * @return The old value of the key.
     */
    @Override
    public V put(final K key, final V value) {
        V newValue = value;
        V oldValue = wrappedMap.get(key);
        if (oldValue != null) {
            newValue = getKeyFunction(key).determineValue(oldValue, newValue);
        }
        return wrappedMap.put(key, newValue);
    }

    private PutFunction<V> getKeyFunction(K key) {
        if (keyFunctions.containsKey(key)) {
            return keyFunctions.get(key);
        } else {
            return defaultFunction;
        }
    }

    /**
     * Sets a key function for the given key.
     *
     * @param key         The key to set the function for.
     * @param keyFunction The key function that will be used for all future calles to put for this given key.
     */
    public void setKeyFunction(final K key, final PutFunction<V> keyFunction) {
        this.keyFunctions.put(key, keyFunction);
    }

    /**
     * This interface define how the value of an existing key
     * will be replaced in a FunctionMap.
     *
     * @author andrew
     * @param <V>
     */
    public static interface PutFunction<V> {

        /**
         * The function used to determine replacement when an existing
         * key is found in the map.
         *
         * @param oldValue The value currently in the map.
         * @param newValue The new value getting put in.
         * @return The value will be actually be used for the mapped key.
         */
        V determineValue(V oldValue, V newValue);
    }
}
