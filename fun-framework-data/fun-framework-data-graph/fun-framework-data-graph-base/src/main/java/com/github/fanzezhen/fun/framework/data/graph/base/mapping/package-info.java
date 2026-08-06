/**
 * 图结果映射包
 * <p>
 * 本包负责把驱动无关的中间表示映射为业务对象，是图模块的映射核心。
 * </p>
 * <p>主要组件：</p>
 * <ul>
 *   <li>{@link com.github.fanzezhen.fun.framework.data.graph.base.mapping.IGraphMapper} 映射引擎接口，可插拔</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.data.graph.base.mapping.DefaultGraphMapper} 默认反射实现</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.data.graph.base.mapping.GraphEntityMeta} 单个实体的全部映射元数据</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.data.graph.base.mapping.GraphClassCache} 元数据与标签反查缓存</li>
 * </ul>
 */
package com.github.fanzezhen.fun.framework.data.graph.base.mapping;
