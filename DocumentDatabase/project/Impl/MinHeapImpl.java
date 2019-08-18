package edu.yu.cs.com1320.project.Impl;
import edu.yu.cs.com1320.project.MinHeap;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

//import com.oracle.tools.packager.IOUtils;


public class MinHeapImpl<E extends Comparable> extends MinHeap<E>  {
    protected E[] elements;
    protected int count;
    protected Map<E,Integer> elementsToArrayIndex; //used to store the index in the elements array
    protected int initialCapacity;
    protected int maxCapacity;


    public MinHeapImpl() {
        this.count =  0;
        this.maxCapacity = 50;
        this.initialCapacity = 10;
        this.elements = (E[]) new Comparable[initialCapacity + 1];
       // for (int i = 0; i < (initialCapacity + 1); i++) {
          //  this.elements[i] = new Comparable<E>();
       // }
        this.elementsToArrayIndex = new HashMap<>();
    }



    @Override
    public void reHeapify(Comparable element) {
        if (element != null) {
            if (!(elementsToArrayIndex.isEmpty()) && elementsToArrayIndex.get(element) != null) {
                int i = this.elementsToArrayIndex.get(element);
                this.upHeap(i);
                this.downHeap(i);
            }
        }
    }

    @Override
    protected int getArrayIndex(Comparable element) {
        if (elementsToArrayIndex.get(element) == null) return -1;
        else return elementsToArrayIndex.get(element);
    }

    public int size() {
        return this.count;
    }

    // double size of heap array
    protected void doubleArraySize() {
        assert maxCapacity > this.count;
        E[] temp = (E[]) new Object[maxCapacity];
        for (int i = 0; i < this.count; i++) {
            temp[i] = this.elements[i];
        }
        this.elements = temp;
    }


    protected  boolean isEmpty()
    {
        //return this.count == 0;
        return this.elementsToArrayIndex.isEmpty();
    }
    /**
     * is elements[i] > elements[j]?
     */
    protected  boolean isGreater(int i, int j) {
        if (this.elements[i] != null && this.elements[j] != null) {
            return this.elements[i].compareTo(this.elements[j]) > 0;
        }
        return true;
    }

    /**
     * swap the values stored at elements[i] and elements[j]
     */
    protected  void swap(int i, int j)
    {
        E temp = this.elements[i];
        this.elements[i] = this.elements[j];
        this.elements[j] = temp;

        this.elementsToArrayIndex.replace(this.elements[i], i, j );
        this.elementsToArrayIndex.replace(this.elements[j], j, i);
    }

    /**
     *while the key at index k is less than its
     *parent's key, swap its contents with its parent’s
     */
    protected  void upHeap(int k)
    {
        while (k > 1 && this.isGreater(k / 2, k))
        {
            this.swap(k, k / 2);
            k = k / 2;
        }
    }

    /**
     * move an element down the heap until it is less than
     * both its children or is at the bottom of the heap
     */
    protected  void downHeap(int k)
    {
        while (2 * k <= this.count)
        {
            //identify which of the 2 children are smaller
            int j = 2 * k;
            if (j < this.count && this.isGreater(j, j + 1))
            {
                j++;
            }
            //if the current value is < the smaller child, we're done
            if (!this.isGreater(k, j))
            {
                break;
            }
            //if not, swap and continue testing
            this.swap(k, j);
            k = j;
        }
    }

    public void insert(E x)
    {
        // double size of array if necessary
        if (this.count >= this.elements.length - 1)
        {
            this.doubleArraySize();
        }
        //add x to the bottom of the heap
        if(!(elementsToArrayIndex.containsKey(x))) {
            this.elements[++this.count] = x;

            this.elementsToArrayIndex.put(x, this.count);
            //percolate it up to maintain heap order property
            this.upHeap(this.count);
        }

    }

    public E removeMin()
    {
        if (isEmpty())
        {
            throw new NoSuchElementException("Heap is empty");
        }
        E min = this.elements[1];
        //swap root with last, decrement count
        this.swap(1, this.count--);
        //move new root down as needed
        this.downHeap(1);
        this.elements[this.count + 1] = null;//null it to prepare for GC
        this.elementsToArrayIndex.remove(min);
        return min;
    }
}
