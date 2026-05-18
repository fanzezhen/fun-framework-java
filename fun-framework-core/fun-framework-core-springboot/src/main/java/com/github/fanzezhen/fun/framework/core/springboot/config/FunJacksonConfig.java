package com.github.fanzezhen.fun.framework.core.springboot.config;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Jackson 序列化配置类
 * <p>
 * 配置 ObjectMapper 的序列化和反序列化行为，主要功能包括：
 * <ul>
 *   <li>LocalDateTime、LocalDate、LocalTime 序列化为时间戳</li>
 *   <li>支持多种日期时间格式的反序列化</li>
 *   <li>忽略未知属性和无效子类型</li>
 * </ul>
 * </p>
 */
@Configuration
public class FunJacksonConfig {

    /**
     * 配置 Jackson ObjectMapper 实例
     * <p>
     * 配置包括：
     * <ul>
     *   <li>LocalDateTime/LocalDate 序列化为毫秒时间戳</li>
     *   <li>LocalTime 序列化为 HH:mm:ss 格式</li>
     *   <li>支持时间戳、ISO-8601、中文日期等多种格式的反序列化</li>
     *   <li>忽略未知属性和无效子类型</li>
     * </ul>
     * </p>
     *
     * @return 配置好的 ObjectMapper 实例
     */
    @Bean
    @ConditionalOnMissingBean
    ObjectMapper objectMapper() {
        String timestampErrMsg = "入参不是时间戳";
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule timeModule = new JavaTimeModule();
        timeModule.addSerializer(LocalDateTime.class, new JsonSerializer<>() {
            @Override
            public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                if (value != null) {
                    long timestamp = LocalDateTimeUtil.toEpochMilli(value);
                    gen.writeNumber(timestamp);
                }
            }
        });
        timeModule.addSerializer(LocalDate.class, new JsonSerializer<>() {
            @Override
            public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                if (value != null) {
                    long timestamp = LocalDateTimeUtil.toEpochMilli(value);
                    gen.writeNumber(timestamp);
                }
            }
        });
        timeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));
        timeModule.addDeserializer(LocalDateTime.class, new JsonDeserializer<>() {
            @Override
            public LocalDateTime deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException {
                long timestamp = p.getValueAsLong();
                if (timestamp > 0) {
                    return LocalDateTimeUtil.of(timestamp, ZoneId.systemDefault());
                }
                throw new IOException(timestampErrMsg);
            }
        });
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.UTC_MS_PATTERN)));
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.UTC_PATTERN)));
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.UTC_WITH_ZONE_OFFSET_PATTERN)));
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.UTC_MS_WITH_ZONE_OFFSET_PATTERN)));
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.CHINESE_DATE_TIME_PATTERN)));
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.PURE_DATETIME_PATTERN)));
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.CHINESE_DATE_PATTERN)));
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.PURE_DATE_PATTERN)));
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_MONTH_PATTERN)));
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.SIMPLE_MONTH_PATTERN)));
        timeModule.addDeserializer(LocalDate.class, new JsonDeserializer<>() {
            @Override
            public LocalDate deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException {
                long timestamp = p.getValueAsLong();
                if (timestamp > 0) {
                    return LocalDateTimeUtil.of(timestamp, ZoneId.systemDefault()).toLocalDate();
                }
                throw new IOException(timestampErrMsg);
            }
        });
        timeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DatePattern.UTC_MS_PATTERN)));
        timeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        timeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        timeModule.addDeserializer(LocalTime.class, new JsonDeserializer<>() {
            @Override
            public LocalTime deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException {
                long timestamp = p.getValueAsLong();
                if (timestamp > 0) {
                    return LocalDateTimeUtil.of(timestamp, ZoneId.systemDefault()).toLocalTime();
                }
                throw new IOException(timestampErrMsg);
            }
        });
        timeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.UTC_MS_PATTERN)));
        timeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));
        timeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        objectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        objectMapper.registerModule(timeModule).registerModule(new ParameterNamesModule()).registerModules(ObjectMapper.findModules());
        return objectMapper;
    }
}
