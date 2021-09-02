package com.jsy.community.qo.property;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Base64;

@Data
@ApiModel(value = "过车记录")
public class CarBeforeRecordQO implements Serializable {

   Integer code	;//	返回码	是
   String  msg	;//		返回描述	是
   Long  timestamp	;//		时间戳	是
   String    msgId	;//		消息id(原样返回请求消息id)	是
   String   data	;//		数据(注：下文所说响应接囗都只针对data字段的json数据结构)	否






//    String RecordId;     //记录流水	是
//    String CarNo;        //车牌号码	是
//    String PlateColor;    //车牌颜色	否
//    String CarType;       //车类 “固定”,”临停”,”免费”,”其它”	是
//    String InOutFlag;      //进出标志 0进;1,出	是
//    String VehicleLane;     //出入车道	是
//    String GateNo;        //道闸编号	是
//    String PassTime;      //通行时间 格式:yyyy-MM-dd HH:mm:ss	是
//    Base64 SmallImage;    //车牌小图	否
//    Base64 BigImage;      //车身全图	否
//    Boolean PayMoney;      //收费金额	是
}
