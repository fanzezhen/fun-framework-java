package com.github.fanzezhen.common.core.enums;

/**
 * @author zezhen.fan
 */
public class CommonEnum {

    /**
     * 是否删除枚举类
     */
    public enum DeleteFlagEnum {
        /**
         * 未删除
         */
        NO(0, "未删除"),
        /**
         * 已删除
         */
        YES(1, "已删除");

        private final int code;
        private final String desc;

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        DeleteFlagEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public static String getColumn() {
            return "del_flag";
        }
    }

    /**
     * 是否启用
     */
    public enum StatusEnum {
        /**
         * 启用
         */
        VALID(0, "启用"),
        /**
         * 禁用
         */
        INVALID(1, "禁用");

        private final int code;
        private final String desc;

        StatusEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public static StatusEnum queryStatusEnumByCode(int code) {
            for (StatusEnum statusEnum : values()) {
                if (statusEnum.code == code) {
                    return statusEnum;
                }
            }
            return null;
        }
    }

    public enum ExitFlagEnum {
        /**
         * 在职的
         */
        INCUMBENT(0, "在职的"),
        /**
         * 离职的
         */
        OUTGOING(1, "离职的");

        private final int code;
        private final String desc;

        ExitFlagEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    public enum PermissionTypeEnum {
        /**
         * 只读权限
         */
        OPERATE(0, "只读权限"),
        /**
         * 操作权限
         */
        MANAGE(1, "操作权限"),
        ;

        private final int code;
        private final String desc;

        PermissionTypeEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * @author fanzezhen
     * 附件类型枚举类
     */
    public enum AttachmentTypeEnum {
        /**
         * 图片
         */
        PICTURE(0, "图片"),
        /**
         * 文件
         */
        FILE(1, "文件");

        private int code;
        private String desc;

        AttachmentTypeEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    public enum SexEnum {
        /**
         * 女
         */
        WOMAN(0, "女"),
        /**
         * 男
         */
        MAN(1, "男"),
        /**
         * 未知
         */
        UNKNOWN(2, "未知"),
        /**
         * 未说明
         */
        UNSPECIFIED(3, "未说明");

        private final int code;
        private final String desc;

        SexEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * @author fanzezhen
     * 错误码以及错误描述
     */
    public enum ErrorMsgEnum {
        /**
         * 没有登录
         */
        NOT_LOGIN("100001", "没有登录!"),
        /**
         * 登录被踢出
         */
        LOGIN_KICK_AWAY("100002", "登录被踢出！"),
        /**
         * 无操作权限
         */
        NO_OPERATION_PERMISSION("100003", "无操作权限！"),
        /**
         * 编码异常
         */
        ENCODE_ERROR("100005", "编码异常！");

        private String code;
        private String desc;

        ErrorMsgEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * @author fanzezhen
     * 是否匿名
     */
    public enum AnonymousEnum {
        /**
         * 匿名
         */
        ANONYMOUS_YES(0, "匿名"),
        /**
         * 非匿名
         */
        ANONYMOUS_NO(1, "非匿名");

        private Integer code;
        private String desc;

        AnonymousEnum(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

}
