package com.jsy.community.qo.payment;



import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/*支付订单参数对象*/
@Data
public class AliOrderContentQO implements Serializable {
    private static final long serialVersionUID = 1L;
    private   String    productCode="QUICK_WAP_WAY";// 销售产品码 必填
    private    Long      communityId; //社区id
    private   String     carNumber;    //车牌号
    //@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 为了解析表单数据的（form/data)数据
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") //为了解析json数据
    private LocalDateTime beginTime;   //进入时间
    private  String   communityName ;   //社区名字
    private  String      time;          //停车时长
    private  BigDecimal  money;         //收费金额
    private  String    orderNum;       //订单号
}
