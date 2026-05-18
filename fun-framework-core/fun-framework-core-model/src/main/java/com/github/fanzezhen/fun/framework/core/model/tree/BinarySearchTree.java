package com.github.fanzezhen.fun.framework.core.model.tree;

/**
 * 二叉搜索树实现
 * <p>
 * 提供插入和查询操作的基础实现。不支持删除操作和平衡调整，
 * 最坏情况下会退化为链表（时间复杂度 O(n)）。
 * </p>
 *
 * <p><b>限制：</b>不支持重复元素插入</p>
 *
 * @param <T> 数据类型
 */
@SuppressWarnings("unused")
public class BinarySearchTree<T extends Comparable<T>> {
    /**
     * 根节点
     */
    private BinaryNode<T> root;

    /**
     * 插入节点
     * <p>
     * 按二叉搜索树规则插入：小于当前节点的插入左子树，大于的插入右子树。
     * 不支持重复元素插入。
     * </p>
     *
     * @param data 待插入的数据
     * @return true 表示插入成功，false 表示数据为 null 或已存在
     */
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

    /**
     * 查询树中是否包含指定数据
     * <p>
     * 按二叉搜索树规则查找：小于当前节点向左查找，大于向右查找
     * </p>
     *
     * @param data 待查询的数据
     * @return true 表示包含该数据，false 表示不包含或数据为 null
     */
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
