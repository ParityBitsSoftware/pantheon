package org.paritybits.pantheon.common;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Common utility functions for items in the common package as well as items in java.util
 */
public final class CommonUtil {

    private CommonUtil() {
    }

    private static final Set<Class> IMMUTABLE_CLASSES = Collections.unmodifiableSet(new HashSet<Class>(Arrays.asList(
            Boolean.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class,
            Class.class, BigInteger.class, BigDecimal.class, Currency.class)));

    private static final Comparator<Comparable> DEFAULT_COMPARATOR = new Comparator<Comparable>() {
        @Override
        public int compare(final Comparable thisComp, final Comparable thatComp) {
            return thisComp.compareTo(thatComp);
        }
    };

    /**
     * Gets either the value in the map for a key or the default value if there is no value.
     *
     * @param map          The map to get the value from.
     * @param key          The key to get
     * @param defaultValue The default value if there is no value for the key in the map
     * @return Either the value in the map for key, or the default value passed in.
     * @throws NullPointerException If either map or key are null.
     */
    public static <K, V> V getValue(final Map<K, ? extends V> map, final K key, final V defaultValue) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            return defaultValue;
        }
    }

    /**
     * Takes a Iterable object and creates a map using the items in the Iterable as keys and with values generated
     * by passing the keys to the function.
     *
     * @param from The iterable source of if items that will be the keys in the returned map.
     * @param using The function used to calculate the values.
     * @return A new Map that has keys from <i>from</i> and values that are the result of <i>using</i> function.
     * @throws NullPointerException If <i>from</i> or <i>using</i> are null.
     */
    public static <K, V> Map<K, V> map(final Iterable<K> from, final Function<K, V> using) {
        Map<K, V> newMap = new HashMap<K, V>();
        mapInto(from, newMap, using);
        return newMap;
    }

    /**
     * Populates a map with the items in the Iterable as keys and with values generated
     * by passing the keys to the function.
     *
     * @param from The iterable source of if items that will be the keys in the returned map.
     * @param into The map that will have keys from <i>from</i> and values that are the result of <i>using</i> function
     * added to it.
     * @param using The function used to calculate the values.      
     * @throws NullPointerException If <i>from</i> <i>into</i>, or <i>using</i> are null.
     */
    public static <K, V> void mapInto(final Iterable<K> from, final Map<K, V> into,
                                      final Function<K, V> using) {
        for (K key : from) {
            into.put(key, using.evaluate(key));
        }
    }

    /**
     * Returns the list of values that result from applying a function to the items returned from
     * an iterable source.
     *
     * @param from The iterable source of items that will be the passed to the function.
     * @param using The function used to calculate the values.
     * @return A List of objects that are a result of applying the function to the items in the source.
     * @throws NullPointerException If <i>from</i> or <i>using</i> are null.
     */
    public static <K, V> List<V> collect(final Iterable<K> from, final Function<K, V> using) {
        List<V> result = new ArrayList<V>();
        collectInto(from, result, using);
        return result;
    }

    /**
     * Puts into a collection the results of running the given function on the given iterable source
     *
     * @param from The iterable source of if items that will be the passed to the function.
     * @param into The collection that will have values from the function added to it.  If there
     * are duplicate results and the this is a Set, subsequent results could be ignored.
     * @param using The function used to calculate the values.
     * @throws NullPointerException If <i>from</i> <i>into</i>, or <i>using</i> are null.
     */
    public static <K, V> void collectInto(final Iterable<K> from, final Collection<V> into,
                                          final Function<K, V> using) {
        for (K item : from) {
            into.add(using.evaluate(item));
        }
    }

    /**
     * Reverse a map so that the values become keys to the original keys. If the there are duplicate values, in the
     * original list, only one will be used for the new map.
     *
     * @param original The original map to reverse
     * @return A new map with keys that are the values of the original map and the values are the keys of
     *         the original map.
     */
    public static <V, K> Map<V, K> reverse(final Map<K, V> original) {
        Map<V, K> newMap = new HashMap<V, K>();
        for (Map.Entry<K, V> entry : original.entrySet()) {
            newMap.put(entry.getValue(), entry.getKey());
        }
        return newMap;
    }

    /**
     * Tests if the given object is Immutable.  An object is considered Immutable either: <ol>
     * <li>One of the following
     * classes in the Standard Java Library: .</li>
     * <li>Marked with the org.paritybits.pantheon.common.Immutable<li> annotation.
     * @param object The object under test.
     * @return True if immutable
     * @throws NullPointerException If object is null.
     */
    public static boolean isImmutable(final Object object) {
        Class objClass = object.getClass();
        return IMMUTABLE_CLASSES.contains(objClass) ||
                objClass.isAnnotationPresent(Immutable.class)||
                objClass.isAnnotationPresent(Idempotent.class);
    }


    /**
     * Checks that the item is between the two bounds, inclusive.
     *
     * @param bound1 The first bound, could be the high or low bound.
     * @param bound2 The second bound, could be the high or low bound.
     * @param item The item to check is between the bounds.
     * @param <T> The type of objects being compared, must implement Comparable.
     * @return True if item is between the bounds, false otherwise.
     */
    public static <T extends Comparable> boolean between(final T bound1, final T bound2,
                                                         final T item) {
        return between(bound1, bound2, item, false);
    }

    /**
     * Checks that the item is between the two bounds, inclusive.  Uses a custom comparator.
     *
     * @param bound1 The first bound, could be the high or low bound.
     * @param bound2 The second bound, could be the high or low bound.
     * @param item The item to check is between the bounds.
     * @param comparator The comparator used to compare the objects in the test.
     * @param <T> The type of objects being compared.
     * @return True if item is between the bounds, false otherwise.
     * @throws NullPointerException If the comparator is null.
     */
    public static <T> boolean between(final T bound1, final T bound2,
                                      final T item, final Comparator<T> comparator) {
        return between(bound1, bound2, item, false, comparator);
    }

    /**
     * Checks that the item is between the two bounds.  If marked as strict it
     * is not an inclusive test, bound1 and bound2 will not return true if passed in as the item.
     *
     * @param bound1 The first bound, could be the high or low bound.
     * @param bound2 The second bound, could be the high or low bound.
     * @param item The item to check is between the bounds.
     * @param strict If true, the check will not be inclusive of the bounds.
     * @param <T> The type of objects being compared, must implement Comparable.
     * @return True if item is between the bounds, false otherwise.
     */
    public static <T extends Comparable> boolean between(final T bound1, final T bound2,
                                                         final T item, final boolean strict) {
        return between(bound1, bound2, item, strict, DEFAULT_COMPARATOR);
    }

    /**
     * Checks that the item is between the two bounds.  Uses a custom comparator.
     * If marked as strict it is not an inclusive test,
     * bound1 and bound2 will not return true if passed in as the item.
     *
     * @param bound1 The first bound, could be the high or low bound.
     * @param bound2 The second bound, could be the high or low bound.
     * @param item The item to check is between the bounds.
     * @param strict If true, the check will not be inclusive of the bounds.
     * @param comparator The comparator used to compare the objects in the test.
     * @param <T> The type of objects being compared.
     * @return True if item is between the bounds, false otherwise.
     * @throws NullPointerException If the comparator is null.
     */
    public static <T> boolean between(final T bound1, final T bound2,
                                      final T item, final boolean strict,
                                      final Comparator<T> comparator) {

        T low = comparator.compare(bound1, bound2) < 0 ? bound1 : bound2;
        T high = low == bound1 ? bound2 : bound1;

        return (!strict && (comparator.compare(bound1, item) == 0
                || comparator.compare(bound2, item) == 0))
                || comparator.compare(low, item) < 0
                && comparator.compare(item, high) < 0;
    }

    /**
     * Allows a contains test to be made for any type of Iterable object.  If the iterable object
     * is a collection the contains method will be used, otherwise a naive iteration over
     * all the items will be used.
     *
     * @param iterable The iterable object that could contain the element
     * @param o element whose presence in this collection is to be tested.
     * @param <T> The type of object being used.
     * @return True if item was returned by the iterator.
     * @throws NullPointerException if iterable or item are null.
     */
    public static <T> boolean contains(final Iterable<T> iterable, final T o) {
        if(iterable instanceof Collection) return ((Collection) iterable).contains(o);
        for(T t : iterable) if(t.equals(o)) return true;
        return false;
    }

    /**
     * Determines that all of the items returned in the given iterator evaluate to true against
     * the given predicate.  For empty items, will return true.
     *
     * @param items The iterable items that will be evaluated.
     * @param predicate The predicate used to evaluate against.
     * @param <T> The type of items
     * @return True if all the items evaluated to true on the predicate.
     * @throws NullPointerException if iterable or predicate are null.
     */
    public static <T> boolean all(final Iterable<T> items, final Predicate<T> predicate) {
        for(T item : items) if(!predicate.evaluate(item)) return false;
        return true;
    }

    /**
     * Determines that any of the items returned in the given iterator evaluate to true against
     * the given predicate. For empty items, will return false.
     *
     * @param items The iterable items that will be evaluated.
     * @param predicate The predicate used to evaluate against.
     * @param <T> The type of items
     * @return True if any the items evaluated to true on the predicate.
     * @throws NullPointerException if iterable or predicate are null.
     */
    public static <T> boolean any(final Iterable<T> items, final Predicate<T> predicate) {
        return inBetween(items, predicate, 1, Integer.MAX_VALUE);
    }

    /**
     * Determines that none of the items returned in the given iterator evaluate to true against
     * the given predicate. For empty items, will return true.
     *
     * @param items The iterable items that will be evaluated.
     * @param predicate The predicate used to evaluate against.
     * @param <T> The type of items
     * @return True if none the items evaluated to true on the predicate.
     * @throws NullPointerException if iterable or predicate are null.
     */
    public static <T> boolean none(final Iterable<T> items, final Predicate<T> predicate) {
        return inBetween(items, predicate, 0, 0);
    }

    /**
     * Determines that at least the given number of the items evaluate to true against
     * the given predicate.
     *
     * @param items The iterable items that will be evaluated.
     * @param predicate The predicate used to evaluate against.
     * @param atLeast The minimum number of items that need to be evaluated to true.
     * @param <T> The type of items
     * @return True if at least the given number the items evaluated to true on the predicate.
     * @throws NullPointerException if iterable or predicate are null.
     * @throws IllegalArgumentException If atLeast is less then 0.
     */
    public static <T> boolean atLeast(final Iterable<T> items, final Predicate<T> predicate, int atLeast) {
        return inBetween(items, predicate, atLeast, Integer.MAX_VALUE);
    }

    /**
     * Determines that at most the given number of the items evaluate to true against
     * the given predicate.
     *
     * @param items The iterable items that will be evaluated.
     * @param predicate The predicate used to evaluate against.
     * @param atMost The maximum number of items that can to be evaluated to true.
     * @param <T> The type of items
     * @return True if at most the given number the items evaluated to true on the predicate.
     * @throws NullPointerException if iterable or predicate are null.
     * @throws IllegalArgumentException If atMost is less then 0.
     */
    public static <T> boolean atMost(final Iterable<T> items, final Predicate<T> predicate, int atMost) {
        return inBetween(items, predicate, 0, atMost);
    }

    /**
     * Determines that the items in an iterator evaluate to true on the given predicate between the given minimum
     * and maximum number of times.
     *
     * @param items The iterable items that will be evaluated.
     * @param predicate The predicate used to evaluate against.
     * @param atLeast The minimum number of items that need to be evaluated to true.
     * @param atMost The maximum number of items that can to be evaluated to true.
     * @param <T> The type of items
     * @return True if the items evaluate to true within the range of times given.
     * @throws NullPointerException if iterable or predicate are null.
     * @throws IllegalArgumentException If either atLeast or atMost is less then 0 or atMost < atLeast
     */
    public static <T> boolean inBetween(final Iterable<T> items, final Predicate<T> predicate,
                                        int atLeast, int atMost) {
        if(atLeast < 0 || atMost < 0 || (atMost < atLeast)) throw new IllegalArgumentException("atLeast and " +
                "atMost values must not be negative and atMost cannot be less then atLeast.");

        //
        Iterator<T> itr = items.iterator();
        int count = 0;


        for(T item : items) {
            if(predicate.evaluate(item)) count++;

            //Check if we can return early
            if((count == atLeast && atMost == Integer.MAX_VALUE ) || count > atMost) break;
        }
        return count >= atLeast && count <= atMost;
    }
}


