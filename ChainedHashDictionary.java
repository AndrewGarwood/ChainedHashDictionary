package datastructures.dictionaries;


import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @see IDictionary for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    private static final double DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD = 0.75;
    private static final int DEFAULT_INITIAL_CHAIN_COUNT = 101;
    private static final int DEFAULT_INITIAL_CHAIN_CAPACITY = 10;

    IDictionary<K, V>[] chains;

    private double lambda;
    private int bucketLength;
    private int tableSize;
    private int itemCount;

    public ChainedHashDictionary() {
        this(DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD, DEFAULT_INITIAL_CHAIN_COUNT, DEFAULT_INITIAL_CHAIN_CAPACITY);
    }

    public ChainedHashDictionary(double resizingLoadFactorThreshold, int initialChainCount, int chainInitialCapacity) {
        itemCount = 0;
        tableSize = initialChainCount;
        bucketLength = chainInitialCapacity;
        lambda = resizingLoadFactorThreshold;
        chains = makeArrayOfChains(initialChainCount);
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * `IDictionary<K, V>` objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int arraySize) {
        return (IDictionary<K, V>[]) new IDictionary[arraySize];
    }

    /**
     * @param key: they key one wants to get the hash of
     * @return is the hash code of the given key
     */
    private int getHash(K key) {
        if (Objects.equals(key, null)) {
            return 0;
        }
        return (Math.abs(key.hashCode())) % tableSize;
    }

    /**
     * @param key: the item who's value you want to find
     * @return is the value of the item you wish to find
     */
    @Override
    public V get(K key) {
        int hash = getHash(key);

        if (Objects.equals(chains[hash], null)) {
            throw new NoSuchKeyException();
        } else {
            return chains[hash].get(key);
        }
    }

    /**
     *
     * @param key: the given key the user wants to add to the dictionary
     * @param value: the value corresponding to the user's given key
     * @return if the key already exists in dictionary, return its value; otherwise return null
     */
    @Override
    public V put(K key, V value) {
        int hash = getHash(key);
        if (Objects.equals(chains[hash], null)) {
            chains[hash] = new ArrayDictionary<K, V>(bucketLength);
        }
        if (!chains[hash].containsKey(key)) {
            itemCount++;
        }
        if (((double) itemCount / tableSize) >= lambda) { // resize and rehash the table

            // next we make new buckets for chains
            IDictionary<K, V>[] oldChains = chains; // temp reference to chains

            tableSize = nextPrime(2 * tableSize); // resize table

            chains = makeArrayOfChains(tableSize); //make new buckets with new size

            Iterator<KVPair<K, V>> itr = new ChainedIterator<>(oldChains);
            while (itr.hasNext()) { // rehash and re put pairs in buckets
                KVPair<K, V> pair = itr.next();
                int newHash = getHash(pair.getKey());

                if (Objects.equals(chains[newHash], null)) {
                    chains[newHash] = new ArrayDictionary<K, V>(bucketLength);
                }
                chains[newHash].put(pair.getKey(), pair.getValue());
            }
            hash = getHash(key); // recalculates hash for new table.

        }
        if (Objects.equals(chains[hash], null)) {
            chains[hash] = new ArrayDictionary<K, V>(bucketLength);
        }
        return chains[hash].put(key, value);
    }

    /**
     *
     * @param num: the number one wants to find the next prime number of
     * @return is the closest prime number of the given number
     *         NOTE: nextPrime will always return the next prime GREATER than the original number given
     */
    private int nextPrime(int num) {
        num += 1; // increment num, now to check if num is prime
        for (int i = 2; i < num; i++) {
            if (num % i == 0) {
                num = nextPrime(num);
            } // else continue with for loop; if we've reached end of for loop, num is prime I think
        }
        return num;
    }


    /**
     *
     * @param key: the key for the value the user wishes to remove
     * @return is the corresponding value that was removed; if key is not in dictionary, return null
     *         Warning: values themselves can be null
     */
    @Override
    public V remove(K key) {
        int hash = getHash(key);
        if (Objects.equals(chains[hash], null)) {
            return null;
        } else {
            itemCount--;
            return chains[hash].remove(key);
        }
    }

    /**
     *
     * @param key: checks the dictionary to see if given the key exists
     * @return is true if key exists; false otherwise
     */
    @Override
    public boolean containsKey(K key) {
        int hash = getHash(key);
        if (Objects.equals(chains[hash], null)) {
            return false;
        }
        return chains[hash].containsKey(key);

    }

    /**
     *
     * @return is the size of the dictionary
     */
    @Override
    public int size() {
        return itemCount;
    }

    /**
     *
     * @return creates an iterator for the dictionary
     *         Warning: does not allow user to modify contents
     */
    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains);
    }

    /**
     *
     * @return is a string representation of the dictionary
     *
     */
    @Override
    public String toString() {
        return IDictionary.toString(this);
    }

    /*
    See the assignment webpage for tips and restrictions on implementing this iterator.
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        int bucketIteratorIndex;
        private Iterator<KVPair<K, V>> bucketIterator;

        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
            bucketIteratorIndex = 0;
            while (bucketIteratorIndex < chains.length && Objects.equals(chains[bucketIteratorIndex], null)) {
                bucketIteratorIndex++;
            }
            if (bucketIteratorIndex == chains.length) {
                bucketIterator = null;
            } else {
                bucketIterator = chains[bucketIteratorIndex].iterator();
            }
        }

        @Override
        public boolean hasNext() {
            if (!Objects.equals(bucketIterator, null)) {
                if (bucketIterator.hasNext()) {
                    return true;
                } else {
                    int temp = bucketIteratorIndex;
                    while (temp < chains.length - 1) {
                        temp++;
                        if (!Objects.equals(chains[temp], null)) {
                            if (chains[temp].size() > 0) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public KVPair<K, V> next() {
            if (!Objects.equals(bucketIterator, null) && bucketIterator.hasNext()) {
                return bucketIterator.next();
            } else {
                while (bucketIteratorIndex < chains.length - 1) {
                    bucketIteratorIndex++;
                    if (!Objects.equals(chains[bucketIteratorIndex], null)) {
                        if (chains[bucketIteratorIndex].size() > 0) {
                            bucketIterator = chains[bucketIteratorIndex].iterator();
                            return bucketIterator.next();
                        }
                    }
                }
                throw new NoSuchElementException();
            }
        }
    }
}
