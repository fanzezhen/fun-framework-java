package com.github.fanzezhen.fun.framework.core.model.tree;

/**
 * 二叉树节点
 * <p>
 * package访问权限允许BinarySearchTree直接访问内部结构，提升性能
 */
public class BinaryNode<T extends Comparable<T>> {
    protected T data;
    protected BinaryNode<T> left;
    protected BinaryNode<T> right;

    public BinaryNode(T data){
        this.data = data;
    }
}
