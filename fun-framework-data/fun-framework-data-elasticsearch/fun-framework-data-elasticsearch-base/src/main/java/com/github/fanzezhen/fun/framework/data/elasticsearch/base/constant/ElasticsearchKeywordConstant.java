package com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant;

/**
 * Elasticsearch 相关常量定义
 * <p>包含脚本类型、聚合脚本模板等常量，主要用于 Elasticsearch 的聚合查询操作</p>
 * <p>所有脚本均使用 Painless 脚本语言编写，这是 Elasticsearch 官方推荐的安全高效脚本语言</p>
 *
 * @author fanzezhen
 */
public interface ElasticsearchKeywordConstant{
    String PREFIX_GROUP_COUNT = "group_count_";
    
}
