package com.github.fanzezhen.fun.framework.core.data;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.github.fanzezhen.fun.framework.core.data.util.StrFileUtil;
import com.github.fanzezhen.fun.framework.core.model.file.FileContentInfo;
import com.github.fanzezhen.fun.framework.core.model.file.FileLineMatchRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Disabled
class StrFileUtilTest {

    @Test
    @Disabled("手动使用")
    void testTrimLines() {
        Assertions.assertDoesNotThrow(() -> StrFileUtil.trimLines("压测\\name.csv", "\""));
    }

    @Test
    @Disabled("手动使用")
    void testCollectFileData() {
        Pattern pattern = Pattern.compile("orc2es_([^.]+)\\.json");
        UnaryOperator<String> aliasNameOperator = name -> {
            String aliasName = null;
            Matcher matcher = pattern.matcher(name);
            if (matcher.find()) {
                aliasName = matcher.group(1);
            }
            return aliasName;
        };
        String dir = "logs-elasticsearch";
        Collection<FileContentInfo<DataXJobResult>> fileContentInfos =
            StrFileUtil.collectFileData(dir, getLineMatchRuleList(), aliasNameOperator);
        fileContentInfos = fileContentInfos.stream()
            .collect(Collectors.groupingBy(
                FileContentInfo::getAliasName,
                LinkedHashMap::new, // 保持插入顺序
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> {
                        for (FileContentInfo<DataXJobResult> fileContentInfo : list) {
                            try {
                                if (Long.parseLong(fileContentInfo.getData().getRec()) > 0) {
                                    return fileContentInfo;
                                }
                            } catch (Exception ignored) {
                            }
                        }
                        return list.get(0);
                    }
                )
            )).values();
        List<List<String>> rows = new ArrayList<>(fileContentInfos
            .stream()
            .map(fileContentInfo -> List.of(
                fileContentInfo.getFilename(),
                CharSequenceUtil.nullToEmpty(fileContentInfo.getAliasName()),
                CharSequenceUtil.nullToEmpty(fileContentInfo.getData().getFlow()),
                CharSequenceUtil.nullToEmpty(fileContentInfo.getData().getRec()))
            ).toList());
        rows.clear();
        Map<String, FileContentInfo<DataXJobResult>> fileContentInfoMap = fileContentInfos.stream().collect(Collectors.toMap(FileContentInfo::getAliasName, Function.identity()));
        for (String index : FileUtil.readUtf8Lines("indexes.txt")) {
            FileContentInfo<DataXJobResult> fileContentInfo = fileContentInfoMap.getOrDefault(index, FileContentInfo.empty());
            rows.add(List.of(
                fileContentInfo.getFilename(),
                CharSequenceUtil.nullToEmpty(fileContentInfo.getAliasName()),
                CharSequenceUtil.nullToEmpty(fileContentInfo.getData().getFlow()),
                CharSequenceUtil.nullToEmpty(fileContentInfo.getData().getRec())));
        }


        File tempFile = FileUtil.createTempFile(".xlsx", true);
        try (ExcelWriter excelWriter = ExcelUtil.getWriter(true)) {
            excelWriter.write(rows).flush(tempFile);
        }
        log.info("文件地址 {}", tempFile.getAbsolutePath());
        Assertions.assertTrue(FileUtil.isNotEmpty(tempFile));
    }

    private List<FileLineMatchRule<DataXJobResult>> getLineMatchRuleList() {
        FileLineMatchRule<DataXJobResult> flowLineMatchRule = getFlowLineMatchRule();
        FileLineMatchRule<DataXJobResult> recLineMatchRule = new FileLineMatchRule<>();
        recLineMatchRule.setName("记录写入速度");
        recLineMatchRule.setPredicate((line, data) -> (data == null || data.getRec() == null) && line.startsWith("记录写入速度"));
        recLineMatchRule.setExtractFunction((line, data) -> {
            String[] strings = line.split(StrPool.COLON);
            if (strings.length > 1) {
                String rec = strings[1].trim();
                if (!rec.startsWith("0")) {
                    if (data == null) {
                        data = new DataXJobResult();
                    }
                    rec = rec.replace("rec/s", CharSequenceUtil.EMPTY);
                    data.setRec(rec);
                }
            }
            return data;
        });
        return new ArrayList<>() {{
            add(flowLineMatchRule);
            add(recLineMatchRule);
        }};
    }

    private static FileLineMatchRule<DataXJobResult> getFlowLineMatchRule() {
        FileLineMatchRule<DataXJobResult> flowLineMatchRule = new FileLineMatchRule<>();
        flowLineMatchRule.setName("任务平均流量");
        flowLineMatchRule.setPredicate((line, data) -> (data == null || data.getFlow() == null) && line.startsWith("任务平均流量"));
        flowLineMatchRule.setExtractFunction((line, data) -> {
            String[] strings = line.split(StrPool.COLON);
            if (strings.length > 1) {
                String flow = strings[1].trim();
                if (!flow.startsWith("0")) {
                    if (data == null) {
                        data = new DataXJobResult();
                    }
                    data.setFlow(flow);
                }
            }
            return data;
        });
        return flowLineMatchRule;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    static class DataXJobResult {
        /**
         * 任务平均流量
         */
        String flow;
        /**
         * 记录写入速度
         */
        String rec;
    }
}
