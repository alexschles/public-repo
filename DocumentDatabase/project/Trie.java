package edu.yu.cs.com1320.project;

import java.util.List;

public interface Trie<Value>
{
    /**
     * get all matches for the given key, sorted in descending order.
     * Search is CASE INSENSITIVE.
     * @param key
     * @return
     */
    List<Value> getAllSorted(String key);

    /**
     * add the given value at the given key
     * @param key
     * @param val
     */
    void put(String key, Value val);

    /**
     * delete ALL matches for the given key
     * @param key
     */
    void deleteAll(String key);

    /**
     * delete ONLY the given value from the given key. Leave all other values.
     * @param key
     * @param val
     */
    void delete(String key, Value val);
}
