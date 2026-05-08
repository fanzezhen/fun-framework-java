package com.github.fanzezhen.fun.framework.core.model.tree;

/**
 * 二叉搜索树实现
 * <p>
 * 提供插入和查询操作的基础实现。不支持删除操作和平衡调整，
 * 最坏情况下会退化为链表（时间复杂度O(n)）。
 * <p>
 * <b>限制：</b>不支持重复元素插入
 *
 */
@SuppressWarnings("unused")
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
