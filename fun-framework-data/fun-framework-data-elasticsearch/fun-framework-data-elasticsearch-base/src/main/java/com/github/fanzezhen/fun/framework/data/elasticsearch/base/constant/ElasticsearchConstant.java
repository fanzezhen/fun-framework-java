package com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant;

/**
 * Elasticsearch 相关常量定义
 * 包含脚本类型、聚合脚本模板等常量
 */
public interface ElasticsearchConstant {
    /**
     * Painless 脚本语言标识
     * Painless 是 Elasticsearch 官方推荐的脚本语言，安全且性能高
     */
    String PAINLESS = "painless";

    /**
     * Map 聚合脚本模板
     * 用于统计字段值的出现次数
     * 第一个占位符为字段名，第二个占位符为字段名（用于获取文档值）
     * 将统计结果存储在 state.statistics 中
     */
    String MAP_SCRIPT_TEMPLATE = """
        
                    if (doc['%s'].size() > 0) {
                      def value = doc['%s'].value;
                      state.statistics.put(value, state.statistics.getOrDefault(value, 0) + 1);
                    }
        """;

    /**
     * Reduce 聚合脚本模板（不排序）
     * 用于合并多个分片的统计结果，返回前 N 条数据
     * 占位符分别为：限制返回的数量、限制返回的数量
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
     * 用于合并多个分片的统计结果，按文档数从小到大排序，返回前 N 条数据
     * 占位符分别为：限制返回的数量、限制返回的数量
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
     * 用于合并多个分片的统计结果，按文档数从大到小排序，返回前 N 条数据
     * 占位符分别为：限制返回的数量、限制返回的数量
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
