package com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant;

/**
 * Elasticsearch 相关常量定义
 * <p>包含脚本类型、聚合脚本模板等常量，主要用于 Elasticsearch 的聚合查询操作</p>
 * <p>所有脚本均使用 Painless 脚本语言编写，这是 Elasticsearch 官方推荐的安全高效脚本语言</p>
 *
 * @author fanzezhen
 */
public interface ElasticsearchScriptConstant {

    // ==================== 脚本类型常量 ====================

    /**
     * Painless 脚本语言标识
     * <p>Painless 是 Elasticsearch 官方推荐的脚本语言，安全且性能高</p>
     * <p>特点：
     *   <ul>
     *     <li>语法简洁，类似 Java</li>
     *     <li>安全性高，有白名单机制</li>
     *     <li>性能优越，编译执行</li>
     *   </ul>
     * </p>
     */
    String PAINLESS = "painless";

    // ==================== 初始化脚本 ====================

    /**
     * Map 聚合初始化脚本
     * <p>用于初始化聚合状态，创建 groupMap 用于存储分组数据</p>
     * <p>执行阶段：Map 阶段开始时</p>
     */
    String INIT_SCRIPT = "state.groupMap = new HashMap();";

    // ==================== Map 阶段脚本模板 ====================

    /**
     * Map 聚合统计脚本模板（简单计数）
     * <p>用于统计字段值的出现次数，将结果存储在 state.statistics 中</p>
     * <p>占位符说明：
     *   <ul>
     *     <li>第 1 个 %s：字段名称</li>
     *     <li>第 2 个 %s：字段名称（用于获取文档值）</li>
     *   </ul>
     * </p>
     * <p>执行阶段：Map 阶段，对每个文档执行</p>
     * <p>适用场景：简单的字段值频次统计</p>
     */
    String MAP_SCRIPT_COUNT_TEMPLATE = """
        
                    if (doc['%s'].size() > 0) {
                      def value = doc['%s'].value;
                      state.statistics.put(value, state.statistics.getOrDefault(value, 0) + 1);
                    }
        """;

    /**
     * Map 聚合计数字典模板（带文档详情）
     * <p>用于统计字段值的出现次数，并保留每个分组的文档详情</p>
     * <p>占位符说明：
     *   <ul>
     *     <li>第 1 个 %s：分组字段名称</li>
     *     <li>第 2 个 %s：分组字段名称（用于获取值）</li>
     *     <li>第 3 个 %s：需要提取的文档字段处理逻辑</li>
     *     <li>第 4 个 %s：每个分组保留的最大文档数量</li>
     *     <li>第 5 个 %s：排序处理逻辑（可选）</li>
     *   </ul>
     * </p>
     * <p>数据结构：
     *   <pre>{@code
     *   state.groupMap = {
     *     "分组值": {
     *       "doc_count": 分组文档数，
     *       "hits": [文档列表]
     *     }
     *   }
     *   }</pre>
     * </p>
     * <p>执行阶段：Map 阶段</p>
     * <p>适用场景：需要查看每个分组的具体文档内容</p>
     */
    String MAP_SCRIPT_COUNT_DOC_TEMPLATE = """
        
                    if (doc['%s'].size() > 0) {
                      def key = doc['%s'].value;
                      def infoMap = state.groupMap.get(key);
                      def docData = new HashMap();
                      %s
                      if (infoMap == null) {
                        infoMap = new HashMap();
                        infoMap.put('doc_count', 1);
                        def docList = new ArrayList();
                        docList.add(docData);
                        infoMap.put('hits', docList);
                        state.groupMap.put(key, infoMap);
                      } else {
                        infoMap.put('doc_count', infoMap.get('doc_count') + 1);
                        def docList = infoMap.get('hits');
                        if (docList.size() < %s) {
                          docList.add(docData);
                        }
                        %s
                      }
                    }
        """;

    /**
     * Map 聚合命中记录排序脚本模板
     * <p>当文档列表超过限制时，用于插入新文档并触发排序</p>
     * <p>占位符说明：
     *   <ul>
     *     <li>%s：排序后的截取逻辑</li>
     *   </ul>
     * </p>
     * <p>执行阶段：Map 阶段，当 hits 列表满时</p>
     * <p>注意：此模板通常与 MAP_SCRIPT_COUNT_DOC_TEMPLATE 配合使用</p>
     */
    String MAP_SCRIPT_HIT_SORT_TEMPLATE = """
        
                     else {
                       docList.add(docData);
                       %s
                     }
        """;

    /**
     * 文档列表排序脚本模板
     * <p>用于对分组内的文档列表进行排序，支持自定义排序字段</p>
     * <p>占位符说明：
     *   <ul>
     *     <li>第 1 个 %s：排序字段名称</li>
     *     <li>第 2 个 %s：排序字段名称</li>
     *     <li>第 3 个 %s：具体的比较逻辑（升序/降序）</li>
     *     <li>第 4 个 %s：截取的数量限制</li>
     *   </ul>
     * </p>
     * <p>排序规则：
     *   <ul>
     *     <li>空值排在最后</li>
     *     <li>数值类型使用 Double.compare 比较</li>
     *     <li>其他类型转为字符串比较</li>
     *   </ul>
     * </p>
     * <p>执行阶段：Map 阶段或 Reduce 阶段</p>
     *
     * @see #OBJECT_SORT_ASC_SCRIPT 升序比较脚本
     * @see #OBJECT_SORT_DESC_SCRIPT 降序比较脚本
     */
    String SCRIPT_HIT_SORT_TEMPLATE = """
                          docList.sort((a, b) -> {
                            def sortValue1 = a.get('%s');
                            def sortValue2 = b.get('%s');
                            if (sortValue1 == null && sortValue2 == null) return 0;
                            if (sortValue1 == null) return 1;
                            if (sortValue2 == null) return -1;
                            %s
                          });
                          infoMap.put('hits', docList.subList(0, %s));
    """;

    /**
     * 对象升序排序脚本
     * <p>用于 SCRIPT_HIT_SORT_TEMPLATE 的第 3 个占位符</p>
     * <p>排序规则：
     *   <ul>
     *     <li>数值类型：转为 Double 后比较</li>
     *     <li>其他类型：转为字符串后字典序比较</li>
     *   </ul>
     * </p>
     */
    String OBJECT_SORT_ASC_SCRIPT = """
                            if (sortValue1 instanceof Number && sortValue2 instanceof Number) {
                              return Double.compare(((Number) sortValue1).doubleValue(), ((Number) sortValue2).doubleValue());
                            } else {
                              return sortValue1.toString().compareTo(sortValue2.toString());
                            }
        """;

    /**
     * 对象降序排序脚本
     * <p>用于 SCRIPT_HIT_SORT_TEMPLATE 的第 3 个占位符</p>
     * <p>排序规则：
     *   <ul>
     *     <li>数值类型：转为 Double 后反向比较</li>
     *     <li>其他类型：转为字符串后反向字典序比较</li>
     *   </ul>
     * </p>
     */
    String OBJECT_SORT_DESC_SCRIPT = """
                            if (sortValue1 instanceof Number && sortValue2 instanceof Number) {
                              return Double.compare(((Number) sortValue2).doubleValue(), ((Number) sortValue1).doubleValue());
                            } else {
                              return sortValue2.toString().compareTo(sortValue1.toString());
                            }
        """;

    /**
     * Map 聚合默认文档信息提取脚本
     * <p>提取文档 ID 和源文档内容到 docData 中</p>
     * <p>无需占位符，直接使用</p>
     * <p>提取内容：
     *   <ul>
     *     <li>_id：文档唯一标识</li>
     *     <li>_source：完整的源文档内容</li>
     *   </ul>
     * </p>
     * <p>执行阶段：Map 阶段</p>
     */
    String MAP_SCRIPT_DEFAULT_DOC = """
                        docData.put('id', doc['_id'].value);
                        docData.putAll(params['_source']);
        """;

    /**
     * Map 聚合文档项提取脚本模板
     * <p>用于从文档中提取指定字段的值并放入 docData</p>
     * <p>占位符说明：
     *   <ul>
     *     <li>第 1 个 %s：目标字段名称（docData 中的 key）</li>
     *     <li>第 2 个 %s：源字段名称（doc 中的字段）</li>
     *   </ul>
     * </p>
     * <p>执行阶段：Map 阶段</p>
     * <p>使用示例：提取字段 "user_name"
     * <pre>{@code
     * String.format(MAP_SCRIPT_DOC_ITEM_TEMPLATE, "userName", "user_name")
     * 结果：docData.put('userName', doc['user_name'].value);
     * }</pre>
     * </p>
     */
    String MAP_SCRIPT_DOC_ITEM_TEMPLATE = """
                        docData.put('%s', doc['%s'].value);
        """;

    /**
     * Map 聚合结果合并脚本
     * <p>将 state.groupMap 中的数据转换为标准的返回格式</p>
     * <p>返回格式：
     *   <pre>{@code
     *   [
     *     {
     *       "key": "分组值",
     *       "doc_count": 文档数，
     *       "doc": {...}
     *     }
     *   ]
     *   }</pre>
     * </p>
     * <p>执行阶段：Map 阶段结束时</p>
     * <p>注意：此脚本在每个分片的 Map 阶段结束后执行</p>
     */
    String MAP_SCRIPT_COMBINE_SCRIPT = """
        
                    def resultList = [];
                    for (entry in state.groupMap.entrySet()) {
                      def item = new HashMap();
                      item.put('key', entry.key);
                      item.put('doc_count', entry.value.get('doc_count'));
                      item.put('doc', entry.value.get('doc'));
                      resultList.add(item);
                    }
                    return resultList;
        """;

    // ==================== Reduce 阶段脚本模板 ====================

    /**
     * Reduce 聚合脚本模板（带文档详情，可排序）
     * <p>用于合并多个分片的统计结果，支持文档详情保留和排序</p>
     * <p>占位符说明：
     *   <ul>
     *     <li>第 1 个 %s：每个分组初始保留的文档数量限制</li>
     *     <li>第 2 个 %s：合并后的排序逻辑</li>
     *     <li>第 3 个 %s：最终文档数量判断阈值</li>
     *     <li>第 4 个 %s：最终返回的文档数量限制</li>
     *     <li>第 5 个 %s：分组列表的排序逻辑（可选）</li>
     *     <li>第 6 个 %s：分组列表的数量限制（可选）</li>
     *   </ul>
     * </p>
     * <p>执行阶段：Reduce 阶段，合并所有分片的结果</p>
     * <p>特点：
     *   <ul>
     *     <li>保留每个分组的详细文档列表</li>
     *     <li>支持自定义排序</li>
     *     <li>自动截取 Top N</li>
     *   </ul>
     * </p>
     *
     * @see #REDUCE_SCRIPT_SORT_ASC_TEMPLATE 按 doc_count 升序
     * @see #REDUCE_SCRIPT_SORT_DESC_TEMPLATE 按 doc_count 降序
     * @see #REDUCE_SCRIPT_LIMIT_TEMPLATE 数量限制脚本
     */
    String REDUCE_SCRIPT_TEMPLATE = """
        
                        def finalMap = new HashMap();
                        for (state in states) {
                          if (state != null) {
                            for (entry in state.entrySet()) {
                              def infoMap = finalMap.get(entry.key);
                              if (infoMap == null) {
                                infoMap = new HashMap();
                                infoMap.put('key', entry.key);
                                infoMap.put('doc_count', entry.value.get('doc_count'));
                                def docList = new ArrayList();
                                def sourceList = entry.value.get('hits');
                                def limit = Math.min(sourceList.size(), %s);
                                for(def i=0; i<limit; i++){
                                    docList.add(sourceList.get(i));
                                }
                                infoMap.put('hits', docList);
                                finalMap.put(entry.key, infoMap);
                              } else {
                                infoMap.put('doc_count', infoMap.get('doc_count') + entry.value.get('doc_count'));
                                def targetList = infoMap.get('hits');
                                def sourceList = entry.value.get('hits');
                                def docList = new ArrayList();
                                for(def i=0; i<sourceList.size(); i++){
                                    docList.add(sourceList.get(i));
                                }
                                for(def i=0; i<targetList.size(); i++){
                                    docList.add(targetList.get(i));
                                }
                                %s
                                if (docList.size() > %s) {
                                  infoMap.put('hits', docList.subList(0, %s));
                                }
                              }
                            }
                          }
                        }
                        def resultList = [];
                        for (entry in finalMap.entrySet()) {
                          resultList.add(entry.value);
                        }
                        %s
                        %s
                        return resultList;
        """;

    /**
     * Reduce 阶段按 doc_count 升序排序脚本
     * <p>用于 REDUCE_SCRIPT_TEMPLATE 的第 5 个占位符</p>
     * <p>排序规则：doc_count 小的在前</p>
     */
    String REDUCE_SCRIPT_SORT_ASC_TEMPLATE = "resultList.sort((a, b) -> a.doc_count - b.doc_count);";

    /**
     * Reduce 阶段按 doc_count 降序排序脚本
     * <p>用于 REDUCE_SCRIPT_TEMPLATE 的第 5 个占位符</p>
     * <p>排序规则：doc_count 大的在前</p>
     */
    String REDUCE_SCRIPT_SORT_DESC_TEMPLATE = "resultList.sort((a, b) -> b.doc_count - a.doc_count);";

    /**
     * Reduce 阶段结果数量限制脚本模板
     * <p>用于限制最终返回的结果数量</p>
     * <p>占位符说明：
     *   <ul>
     *     <li>第 1 个 %s：最大允许的数量</li>
     *     <li>第 2 个 %s：实际截取的数量（通常与第 1 个相同）</li>
     *   </ul>
     * </p>
     * <p>用于 REDUCE_SCRIPT_TEMPLATE 的第 6 个占位符</p>
     */
    String REDUCE_SCRIPT_LIMIT_TEMPLATE = """
            if (resultList.size() > %s) {
              resultList = resultList.subList(0, %s);
            }
        """;

    /**
     * Reduce 聚合脚本模板（简单计数，不排序）
     * <p>用于合并多个分片的统计结果，仅返回计数，不包含文档详情</p>
     * <p>占位符说明：
     *   <ul>
     *     <li>第 1 个 %s：最大返回数量</li>
     *     <li>第 2 个 %s：实际截取数量</li>
     *   </ul>
     * </p>
     * <p>返回格式：
     *   <pre>{@code
     *   [
     *     {"key": "分组值", "doc_count": 数量}
     *   ]
     *   }</pre>
     * </p>
     * <p>执行阶段：Reduce 阶段</p>
     * <p>特点：
     *   <ul>
     *     <li>只统计数量，无文档详情</li>
     *     <li>不进行排序</li>
     *     <li>性能最优</li>
     *   </ul>
     * </p>
     *
     * @see #REDUCE_SCRIPT_ASCENDING_TEMPLATE 升序版本
     * @see #REDUCE_SCRIPT_DESCENDING_TEMPLATE 降序版本
     */
    String REDUCE_SCRIPT_UNSORTED_TEMPLATE = """
        
            HashMap map = new HashMap();
            for (state in states) {
              if (state != null) {
                for (entry in state.entrySet()) {
                  map.put(entry.key, map.getOrDefault(entry.key, 0) + entry.value);
                }
              }
            }
            def resultList = [];
            for (entry in map.entrySet()) {
              resultList.add(['key': entry.key, 'doc_count': entry.value]);
            }
            if (resultList.size() > %s) {
              return resultList.subList(0, %s);
            } else {
              return resultList;
            }
        """;

    /**
     * Reduce 聚合脚本模板（按 doc_count 升序排序）
     * <p>用于合并多个分片的统计结果，按文档数从小到大排序</p>
     * <p>占位符说明：
     *   <ul>
     *     <li>第 1 个 %s：最大返回数量</li>
     *     <li>第 2 个 %s：实际截取数量</li>
     *   </ul>
     * </p>
     * <p>执行阶段：Reduce 阶段</p>
     * <p>特点：
     *   <ul>
     *     <li>只统计数量，无文档详情</li>
     *     <li>按 doc_count 升序排序</li>
     *     <li>自动截取前 N 条</li>
     *   </ul>
     * </p>
     * <p>适用场景：找出出现次数最少的分组</p>
     *
     * @see #REDUCE_SCRIPT_UNSORTED_TEMPLATE 不排序版本
     * @see #REDUCE_SCRIPT_DESCENDING_TEMPLATE 降序版本
     */
    String REDUCE_SCRIPT_ASCENDING_TEMPLATE = """
        
            HashMap map = new HashMap();
            for (state in states) {
              if (state != null) {
                for (entry in state.entrySet()) {
                  map.put(entry.key, map.getOrDefault(entry.key, 0) + entry.value);
                }
              }
            }
            def resultList = [];
            for (entry in map.entrySet()) {
              resultList.add(['key': entry.key, 'doc_count': entry.value]);
            }
            resultList.sort((a, b) -> a.doc_count - b.doc_count);
            if (resultList.size() > %s) {
              return resultList.subList(0, %s);
            } else {
              return resultList;
            }
        """;

    /**
     * Reduce 聚合脚本模板（按 doc_count 降序排序）
     * <p>用于合并多个分片的统计结果，按文档数从大到小排序</p>
     * <p>占位符说明：
     *   <ul>
     *     <li>第 1 个 %s：最大返回数量</li>
     *     <li>第 2 个 %s：实际截取数量</li>
     *   </ul>
     * </p>
     * <p>执行阶段：Reduce 阶段</p>
     * <p>特点：
     *   <ul>
     *     <li>只统计数量，无文档详情</li>
     *     <li>按 doc_count 降序排序</li>
     *     <li>自动截取前 N 条</li>
     *   </ul>
     * </p>
     * <p>适用场景：Top N 统计，如热门商品、高频用户等</p>
     *
     * @see #REDUCE_SCRIPT_UNSORTED_TEMPLATE 不排序版本
     * @see #REDUCE_SCRIPT_ASCENDING_TEMPLATE 升序版本
     */
    String REDUCE_SCRIPT_DESCENDING_TEMPLATE = """
        
            HashMap map = new HashMap();
            for (state in states) {
              if (state != null) {
                for (entry in state.entrySet()) {
                  map.put(entry.key, map.getOrDefault(entry.key, 0) + entry.value);
                }
              }
            }
            def resultList = [];
            for (entry in map.entrySet()) {
              resultList.add(['key': entry.key, 'doc_count': entry.value]);
            }
            resultList.sort((a, b) -> b.doc_count - a.doc_count);
            if (resultList.size() > %s) {
              return resultList.subList(0, %s);
            } else {
              return resultList;
            }
        """;
}
