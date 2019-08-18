package edu.yu.cs.com1320.project.Impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T> {

    private class SinglyLinkedListNode {
        T data;
        SinglyLinkedListNode next;
    }

    private SinglyLinkedListNode top;
    private int size;

    public StackImpl() {
        top= null;
        size = 0;
    }

    private boolean isEmpty() {return top == null;}

    public void push(T element) {
        SinglyLinkedListNode oldHead = top;
        top = new SinglyLinkedListNode();
        top.data = element;
        top.next = oldHead;
        size++;
    }


    public T pop() {
        if (this.isEmpty()) throw new IllegalArgumentException("Stack underflow. Stack is empty.");
        T data = top.data;
        top = top.next;
        size--;
        return data;
    }

    public  T peek() {
        if (this.isEmpty()) throw new IllegalArgumentException("Stack underflow. Stack is empty");
        return top.data;
    }

    public int size() {return size;}





}
