package com.jsy.community.utils.es;

/**
 * @author YuLF
 * @since 2021-02-02 16:06
 */
public enum RecordFlag{

    /**
     * 房屋租赁数据在ElasticSearch的标记字段
     */
    LEASE_HOUSE("LEASE_HOUSE"),

    /**
     * 商铺租赁数据在ElasticSearch的标记字段
     */
    LEASE_SHOP("LEASE_SHOP"),

    /**
     * 社区消息数据标记字段
     */
    INFORM("INFORM"),

    /**
     * 社区趣事标记字段
     */
    FUN("FUN");


    private final String value;

    RecordFlag(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.value;
    }

}
