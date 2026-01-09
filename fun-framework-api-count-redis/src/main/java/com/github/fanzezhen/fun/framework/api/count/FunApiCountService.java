package com.github.fanzezhen.fun.framework.api.count;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.core.model.YApiModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author fanzezhen
 * @since 3.4.3.3
 */
@Slf4j
@Service
public class FunApiCountService {
    @Resource
    private StringRedisTemplate redisTemplate;
    @Value("${spring.application.name}")
    private String springApplicationName;

    public Map<String, LinkedHashMap<String, Integer>> map() {
        Map<String, LinkedHashMap<String, Integer>> result = new HashMap<>();
        String keyPrefix = FunApiCountAop.getKeyPrefix(springApplicationName);
        ScanOptions scanOptions = ScanOptions.scanOptions()
            .match(keyPrefix + "**") // 使用通配符匹配键
            .count(1000) // 每次扫描返回的数量
            .build();
        try (Cursor<String> cursor = redisTemplate.scan(scanOptions)) {
            while (cursor.hasNext()) {
                String key = cursor.next(); // 将字节数组转换为字符串
                if (key.startsWith("\"") && key.endsWith("\"")) {
                    key = key.substring(1, key.length() - 1);
                }
                Set<ZSetOperations.TypedTuple<String>> tupleSet = redisTemplate.opsForZSet().rangeByScoreWithScores(key, -1, Integer.MAX_VALUE);
                LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
                tupleSet.stream()
                    .filter(tuple -> tuple != null && tuple.getScore() != null)
                    .sorted(Comparator.comparingDouble(ZSetOperations.TypedTuple::getScore))
                    .forEach(tuple -> {
                        if (tuple.getScore() != null) {
                            map.put(tuple.getValue(), tuple.getScore().intValue());
                        }
                    });
                result.put(key.substring(keyPrefix.length()), map);
            }
        }
        return result;
    }

    public File exportExcel(Collection<YApiModel> yApiModelList) throws IOException {
        Map<String, LinkedHashMap<String, Integer>> apiMap = map();
        List<JSONObject> list = new ArrayList<>();
        for (YApiModel yApiModel : yApiModelList) {
            if (CollUtil.isNotEmpty(yApiModel.getList())) {
                for (YApiModel.Api api : yApiModel.getList()) {
                    String apiPath = api.getPath();
                    LinkedHashMap<String, Integer> map = apiMap.get(apiPath);
                    JSONObject row = new JSONObject(8, 1f, true)
                        .fluentPut("功能模块", yApiModel.getName())
                        .fluentPut("接口名", api.getTitle())
                        .fluentPut("接口", apiPath)
                        .fluentPut("是否接口无效", map == null)
                        .fluentPut("无效字段", null);
                    if (map != null) {
                        row.fluentPut("无效字段", map.entrySet().stream().filter(entry -> entry.getValue() <= 0)
                            .map(Map.Entry::getKey).collect(Collectors.joining(StrPool.COMMA)));
                    }
                    list.add(row);
                }
            }
        }
        File tempFile = File.createTempFile("接口统计", ".xlsx", FileUtil.mkdir(System.getProperty("java.io.tmpdir") + "接口统计"));
        ExcelUtil.getWriter(true).write(list).flush(tempFile);
        return tempFile;
    }

}
