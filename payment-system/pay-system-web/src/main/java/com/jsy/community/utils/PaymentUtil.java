package com.jsy.community.utils;
/**
 * @Description: 暂时没用
 * @author: Hu
 * @since: 2021/2/23 17:49
 * @Param:
 * @return:
 */
public  class PaymentUtil {

    public static String getPayment(Integer type){
        String value;
        switch (type){
            case 1: value= "充值提现";break;
            case 2: value= "商城购物";break;
            case 3: value= "生活缴费";break;
            case 4: value= "物业管理";break;
            case 5: value= "房屋租金";break;
            case 6: value= "红包";break;
            default:  value= null;
        }
        return value;
    }

}
