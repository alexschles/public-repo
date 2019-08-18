package edu.yu.cs.com1320.project.Impl;

import edu.yu.cs.com1320.project.Trie;

import java.util.*;

public  class TrieImpl<Value> implements Trie<Value> {
    private static final int alphabetSize = 256; // extended ASCII
    private Node root; // root of trie
    private Comparator<Value> trieComparator;

    public static class Node<Value> { ;
        protected Node[] links = new Node[alphabetSize];
        protected List<Value> valArrayList;

        private Node(){
            valArrayList = new ArrayList<>();
            for (int i = 0; i < alphabetSize; i++){
                links[i] = null;
            }
        }

    }

    public TrieImpl(Comparator<Value> comparator) {
        trieComparator = comparator;
    }



    @Override
    public List<Value> getAllSorted(String key) {
        List<Value> x = this.get(this.root, key, 0);
        if (x == null)
        {
            return new ArrayList<>();
        }
        x.sort(trieComparator);

        return x;
    }


    private List<Value> get(Node x, String key, int d) {
        //link was null - return null, indicating a miss
        if (x == null)
        {
            return null;
        }
        //we've reached the last node in the key,
        //return the node
        if (d == key.length())
        {
            return x.valArrayList;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        return this.get(x.links[c], key, d + 1);
    }

    @Override
    public void put(String key, Value val) {
        //deleteAll the value from this key
        if (val == null)
        {
            this.deleteAll(key);
        }
        else
        {
            this.root = put(this.root, key, val, 0);
        }
    }

    private Node put(Node x, String key, Value val, int d) {
        //create a new node
        if (x == null)
        {
            x = new Node();
        }
        //we've reached the last node in the key,
        //set the value for the key and return the node
        if (d == key.length())
        {
            if(!(x.valArrayList.contains(val))) {
                x.valArrayList.add(val);
            }
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        x.links[c] = this.put(x.links[c], key, val, d + 1);
        return x;
    }

    @Override
    public void deleteAll(String key)
    {
        this.root = deleteAll(this.root, key, 0);
    }

    private Node deleteAll(Node x, String key, int d) {
        if (x == null)
        {
            return null;
        }
        //we're at the node to del - set the val to null
        if (d == key.length())
        {
            x.valArrayList.clear();
        }
        //continue down the trie to the target node
        else
        {
            char c = key.charAt(d);
            x.links[c] = this.deleteAll(x.links[c], key, d + 1);
        }
        //this node has a val – do nothing, return the node
        if (!(x.valArrayList.isEmpty()))
        {
            return x;
        }
        //remove subtrie rooted at x if it is completely empty
        for (int c = 0; c <alphabetSize; c++)
        {
            if (x.links[c] != null)
            {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }

    public void delete(String key, Value val) { this.root = delete(this.root, key, val, 0);}

    private Node delete(Node x, String key, Value val, int d) {
        if (x == null) {
            return null;
        }
        //we're at the node to del - set the val to null
        if (d == key.length()) {
            x.valArrayList.remove(val);
        }
        //continue down the trie to the target node
        else {
            char c = key.charAt(d);
            x.links[c] = this.delete(x.links[c], key, val, d + 1);
        }
        //this node has a val – do nothing, return the node
        if (!(x.valArrayList.isEmpty())) {
            return x;
        }
        //remove subtrie rooted at x if it is completely empty
        for (int c = 0; c < alphabetSize; c++) {
            if (x.links[c] != null) {
                return x; //not empty
            }

        }
        return null;
    }


}



