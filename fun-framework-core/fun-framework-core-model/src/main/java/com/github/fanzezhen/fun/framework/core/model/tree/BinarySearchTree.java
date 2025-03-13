package com.github.fanzezhen.fun.framework.core.model.tree;

/**
 * @author fanzezhen
 */
public class BinarySearchTree<T extends Comparable<T>> {
    private BinaryNode<T> root;

    public boolean insert(T data) {
        if (data == null) {
            return false;
        }
        if (root == null) {
            root = new BinaryNode<>(data);
        }
        BinaryNode<T> cur = root;
        while (true) {
            if (data.compareTo(cur.data) < 0) {
                if (cur.left == null) {
                    cur.left = new BinaryNode<>(data);
                    return true;
                }
                cur = cur.left;
            } else if (data.compareTo(cur.data) > 0) {
                if (cur.right == null) {
                    cur.right = new BinaryNode<>(data);
                    return true;
                }
                cur = cur.right;
            } else {
                return false;
            }
        }
    }

    public boolean contains(T data) {
        if (root == null || data == null) {
            return false;
        }
        BinaryNode<T> cur = root;
        while (true) {
            if (data.compareTo(cur.data) < 0) {
                cur = cur.left;
            } else if (data.compareTo(cur.data) > 0) {
                cur = cur.right;
            } else {
                return true;
            }
            if (cur == null) {
                return false;
            }
        }
    }
}
