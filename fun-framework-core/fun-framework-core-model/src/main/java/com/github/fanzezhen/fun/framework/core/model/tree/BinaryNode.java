package com.github.fanzezhen.fun.framework.core.model.tree;

/**
 * 二叉树节点
 * <p>
 * 用于构建二叉树结构，包含数据域和左右子节点指针。
 * package 访问权限允许 BinarySearchTree 直接访问内部结构，提升性能。
 * </p>
 *
 * @param <T> 节点数据类型
 */
public class BinaryNode<T extends Comparable<T>> {
    /**
     * 节点数据
     */
    protected T data;

    /**
     * 左子节点
     */
    protected BinaryNode<T> left;

    /**
     * 右子节点
     */
    protected BinaryNode<T> right;

    /**
     * 构造二叉树节点
     *
     * @param data 节点数据
     */
    public BinaryNode(T data){
        this.data = data;
    }
}
