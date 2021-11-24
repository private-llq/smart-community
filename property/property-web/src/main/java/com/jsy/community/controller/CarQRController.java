package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICaQRService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarQREntity;
import com.jsy.community.util.QRCodeGenerator;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.UrltoBatyUtils;
import com.jsy.community.utils.UserUtils;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.UUID;

@ApiJSYController
@RequestMapping("/CarQR")
@RestController
@Api(tags = "车辆支付")
public class CarQRController {
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICaQRService iCaQRService;

    @Value("${qr}")
    private String URL;
    private static final String BUCKET_NAME = "qrcode";
    private static final Integer WIDTH = 400;
    private static final Integer HEIGHT = 400;

//    public static void main(String[] args) throws Exception {
//        String text = "http://192.168.12.188:8080/#/?CommunityID=11L";
//        int width = 400;
//        int height = 400;
//        byte[] bytes = QRCodeGenerator.generateQRCode(text, width, height);
//        InputStream inputStream = new ByteArrayInputStream(bytes);
//        MultipartFile file = new MockMultipartFile(ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream);
//        String s = MinioUtils.uploadNameByCarJPG(file,BUCKET_NAME,UUID.randomUUID()+".jpg");
//        System.out.println(s);
//        System.out.println("ok");
//    }
    @ApiOperation("下载社区停车二维码")
    @RequestMapping(value = "/uploadQRCode", method = RequestMethod.POST)
    @Permit("community:property:CarQR:uploadQRCode")
    public void uploadQRCode(HttpServletResponse response) throws Exception {
        Long communityId = UserUtils.getAdminCommunityId();
        CarQREntity carQREntity =  iCaQRService.findOne(communityId);
        if (carQREntity!=null){
            ServletOutputStream out = response.getOutputStream();
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(UUID.randomUUID()+".jpg","ISO8859-1"));
            //将url地址转换为byte数组
            byte[] date = UrltoBatyUtils.getDate(carQREntity.getPath());
            //数组写到respone
            out.write(date);
            out.flush();
            out.close();
        }else {
            String text = URL+communityId;
            System.out.println("新增二维码");
            byte[] bytes = QRCodeGenerator.generateQRCode(text, WIDTH, HEIGHT);
            ServletOutputStream out = response.getOutputStream();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            MultipartFile file = new MockMultipartFile(ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream);
            UUID fileName = UUID.randomUUID();
            String path = MinioUtils.uploadNameByCarJPG(file,BUCKET_NAME, fileName+".jpg");
            boolean b = iCaQRService.addQRCode(path,communityId);
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName+".jpg","ISO8859-1" ));
            out.write(bytes);
            out.flush();
            out.close();
            System.out.println(path);
        }
       // return CommonResult.ok("下载成功");
    }
}
