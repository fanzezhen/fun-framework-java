package com.github.fanzezhen.common.core.enums;


import com.github.fanzezhen.common.core.constant.CommonConstant;
import com.github.fanzezhen.common.core.constant.RegexConstant;

/**
 * @author zezhen.fan
 */
public class DateEnum {
    public enum DateRegexEnum {
        /**
         * 连字符
         */
        HYPHEN(CommonConstant.DEFAULT_DATE_PATTERN, RegexConstant.HYPHEN_DATE),
        /**
         * 极简
         */
        MINIMALISM(CommonConstant.MINIMALISM_DATE_DATE_PATTERN, RegexConstant.MINIMALISM_DATE),
        ;
        private String pattern;
        private String regex;

        DateRegexEnum(String pattern, String regex) {
            this.pattern = pattern;
            this.regex = regex;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public String getRegex() {
            return regex;
        }

        public void setRegex(String regex) {
            this.regex = regex;
        }
    }
}
