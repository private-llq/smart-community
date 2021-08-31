package com.jsy.community.util;

/**
 * 租赁 模块的帮助类  用于自定义一些静态调用方法
 *
 * @author YuLF
 * @since 2021-01-12 15:41
 */
public class HouseHelper {

    /**
     * 通过传进来的房屋类型Code 转换成中文 如 040202 会被转换成 4室2厅2卫
     *
     * @param houseTypeCode 存储的房屋类型code
     * @return 返回几室几厅
     * @author YuLF
     * @since 2021/1/12 15:07
     */
    public static String parseHouseType(String houseTypeCode) {
        //只要该数据是从数据库取出 那么一定是一个6位数的数字
        if (houseTypeCode == null) {
            return "其他类型";
        }
        //别墅code
        String villaCode = "000000";
        if (villaCode.equals(houseTypeCode)) {
            return "别墅";
        }
        return divisionStr(0, 2, houseTypeCode) + "室" + divisionStr(2, 4, houseTypeCode) + "厅" + divisionStr(4, 6, houseTypeCode) + "卫";
    }

    /**
     * 传入 010203  根据 起始位 结束为 获得正确的数字
     * 如 起始位 0 结束为 2
     * 被截取后的字符串为 01 经过验证该字符串第一位 如果等于0 则返回1 否则返回该字符串
     *
     * @param start 起始位
     * @param end   结束位
     * @param str   字符串
     * @return 返回处理后的字符串
     * @author YuLF
     * @since 2021/1/12 15:07
     */
    private static String divisionStr(int start, int end, String str) {
        String room = str.substring(start, end);
        int roomNum = Integer.parseInt(String.valueOf(room.charAt(0)));
        if (roomNum == 0) {
            return str.substring(start + 1, end);
        } else {
            return room;
        }
    }

}
