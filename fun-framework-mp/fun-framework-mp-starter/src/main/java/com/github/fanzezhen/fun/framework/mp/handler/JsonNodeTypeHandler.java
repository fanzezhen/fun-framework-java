package com.github.fanzezhen.fun.framework.mp.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import jakarta.annotation.Resource;

@Slf4j
@MappedTypes(JsonNode.class)
@MappedJdbcTypes(JdbcType.JAVA_OBJECT)
public class JsonNodeTypeHandler extends AbstractJsonTypeHandler<JsonNode> {
    @Resource
    private ObjectMapper objectMapper;

    public JsonNodeTypeHandler() {
        super(JsonNode.class);
    }

    @Override
    @SneakyThrows
    protected JsonNode parse(String jsonStr) {
        return objectMapper.readTree(jsonStr);
    }

    @Override
    protected String toJson(JsonNode obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }
}
