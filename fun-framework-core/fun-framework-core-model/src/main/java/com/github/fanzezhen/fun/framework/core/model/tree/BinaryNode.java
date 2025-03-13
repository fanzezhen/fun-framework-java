package com.github.fanzezhen.fun.framework.core.model.tree;

/**
 * @author fanzezhen
 */
public class BinaryNode<T extends Comparable<T>> {
    protected T data;
    protected BinaryNode<T> left;
    protected BinaryNode<T> right;

    public BinaryNode(T data){
        this.data = data;
    }
}
