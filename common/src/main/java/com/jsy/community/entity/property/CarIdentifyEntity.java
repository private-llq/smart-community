package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@ApiModel("车禁模块-设备管理")
//@TableName("t_car_equipment_manage")
public class CarIdentifyEntity {
private  String type;//状态
private  Integer mode;//mode 协议模式，数字表示 模式 5 以上才有此字段
private  String plateNum;//plate_num 车牌号码，UTF8 编码
private  String plateColor;//plate_color 车牌底色，UTF8 编码
private boolean plateVal;// plate_val 虚假车牌信息，true 表示真牌，false 表示虚假车牌
private String confidence;//plate_num 车牌号码，UTF8 编码
private String carLogo;//plate_num 车牌号码，UTF8 编码
private  String carSubLogo;//车辆子品牌，UTF8 编码
private  String carColor;//车辆子品牌，UTF8 编码
private  String vehicleType;//vehicle_type 车辆类型，UTF8 编码
private  Long startTime;//start_time 车牌识别时间,1970/01/01 到现在的秒数目
private String parkId;//车场 ID，最大支持 60 个字符
private String camId;//车场 ID，最大支持 60 个字符
private String camIp;//车场 ID，最大支持 60 个字符
private  String vdcType;//出入口类型，in 表示入口，out 表示出口
private Boolean isWhitelist;//是否是白名单车辆，true 表示白名单，false 表示非白名
private String trigerType;//video 表示视频触发，hwtriger 表示地感触发，swtriger 表示软触发
private String picture;//全景图，BASE64 编码 为避免Http传输时URL编码意外
private  String closeupPic;//
private String interval;//车场 ID，最大支持 60 个字符

}
