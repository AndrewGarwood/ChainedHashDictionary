package datastructures.dictionaries;


import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import static java.util.Arrays.copyOf;

/**
 * @see IDictionary
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.

    Note: The field below intentionally omits the "private" keyword. By leaving off a specific
    access modifier like "public" or "private" it becomes package-private, which means anything in
    the same package can access it. Since our tests are in the same package, they will be able
    to test this property directly.
     */
    public Pair<K, V>[] pairs;

    private int size;
    // You may add extra fields or helper methods though!

    public static final int DEFAULT_INITIAL_CAPACITY = 100;
    /**
     * Standard constructor for ArrayDictionary.
     */
    public ArrayDictionary() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public ArrayDictionary(int initialCapacity) {
        pairs = makeArrayOfPairs(initialCapacity);
        size = 0;
    }

    /**
     * This method will return a new, empty array of the given size that can contain `Pair<K, V>`
     * objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        /*
        It turns out that creating arrays of generic objects in Java is complicated due to something
        known as "type erasure."

        We've given you this helper method to help simplify this part of your assignment. Use this
        helper method as appropriate when implementing the rest of this class.

        You are not required to understand how this method works, what type erasure is, or how
         arrays and generics interact. Do not modify this method in any way.
        */
        return (Pair<K, V>[]) (new Pair[arraySize]);
    }

    /**
     * @param key: the item who's index the user wishes to find
     * @return is the index of the given key
     * @throws NoSuchKeyException: throws new NoSuchKeyException if the key does not exist in the dictionary
     */
    private int findIndexOfKey(K key) {
        if (!this.containsKey(key)) {
            throw new NoSuchKeyException();
        }

        int index = 0;
        while (!Objects.equals(key, pairs[index].key)) {
            index++;
        }
        return index;
    }

    /**
     * @param key: the item who's value you want to find
     * @return is the value of the item you wish to find
     */
    @Override
    public V get(K key) {
        return pairs[findIndexOfKey(key)].value;
    }

    /**
     *
     * @param key: the given key the user wants to add to the dictionary
     * @param value: the value corresponding to the user's given key
     * @return if the key already exists in dictionary, return its value; otherwise return null
     */
    @Override
    public V put(K key, V value) {
        int index = 0;
        while (index < size && !Objects.equals(key, pairs[index].key)) {
            index++;
        }
        if (index < size && Objects.equals(key, pairs[index].key)) {
            V oldValue = pairs[index].value;
            pairs[index].value = value;
            return oldValue;
        } else { //it is a new, unique key
            if (size == pairs.length) {
                pairs = copyOf(pairs, 2 * pairs.length);
            }
            pairs[size] = new Pair(key, value);
            size++;
            return null;
        }


    }

    /**
     *
     * @param key: the key for the value the user wishes to remove
     * @return is the corresponding value that was removed; if key is not in dictionary, return null
     *         Warning: values themselves can be null
     */
    @Override
    public V remove(K key) {

        int index = 0;
        while (index < size && !Objects.equals(key, pairs[index].key)) {
            index++;
        }

        if (index < size && Objects.equals(key, pairs[index].key)) {
            V deletedValue = this.get(key);
            pairs[index] = pairs[size - 1];
            pairs[size - 1] = null;
            size--; // accounts for removal at end of array too
            return deletedValue;
        } else { // Dictionary does not contain given key
            return null;
        }
    }

    /**
     *
     * @param key: checks the dictionary to see if given the key exists
     * @return is true if key exists; false otherwise
     */
    @Override
    public boolean containsKey(K key) {
        for (int i = 0; i < size; i++) {
            if (java.util.Objects.equals(key, pairs[i].key)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return is the size of the dictionary
     */
    @Override
    public int size() {
        return size;
    }

    /**
     *
     * @return creates an iterator for the dictionary
     *         Warning: does not allow user to modify contents
     */
    @Override
    public Iterator<KVPair<K, V>> iterator() {
        return new ArrayDictionaryIterator<>(pairs);
    }

    /**
     *
     * @return is a string representation of the dictionary
     *
     */
    @Override
    public String toString() {
        // return super.toString();

        /*
        After you've implemented the iterator, comment out the line above and uncomment the line
        below to get a better string representation for objects in assertion errors and in the
        debugger.
        */

        return IDictionary.toString(this);
    }

    private static class Pair<K, V> {
        private K key;
        private V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("%s=%s", this.key, this.value);
        }
    }

    private static class ArrayDictionaryIterator<K, V> implements Iterator<KVPair<K, V>> {
        // You'll need to add some fields
        private Pair<K, V> next;
        private int index;
        private Pair<K, V>[] pairArray;

        public ArrayDictionaryIterator(Pair<K, V>[] pairs) {
            index = 0;
            pairArray = pairs;
            if (pairs.length > 0) {
                next = pairs[index];
            } else {
                next = null;
            }

        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public KVPair<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            KVPair<K, V> result = new KVPair(next.key, next.value);
            index++;
            if (index < pairArray.length) { // if valid index
                next = pairArray[index];
            } else { // if we've reached the end, no longer valid index
                next = null;
            }
            return result;
        }
    }
}
