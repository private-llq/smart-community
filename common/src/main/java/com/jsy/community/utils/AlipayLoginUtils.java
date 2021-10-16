package com.jsy.community.utils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @program: com.jsy.community
 * @description: 支付宝登录工具类
 * @author: Hu
 * @create: 2021-10-13 14:02
 **/
@Slf4j
@Component
public class AlipayLoginUtils {

    /**
     * 会员信息请求类
     */
    public static AlipayUserInfoShareRequest alipayUserInfoShareRequest =  new  AlipayUserInfoShareRequest();

    //初始化证书请求类
    public static CertAlipayRequest getCert() throws Exception {
        //支付宝证书方式请求
        CertAlipayRequest certAlipayRequest = new CertAlipayRequest();
        //设置网关地址
        certAlipayRequest.setServerUrl("https://openapi.alipay.com/gateway.do");
        //设置应用Id
        certAlipayRequest.setAppId("2021002119679359");
        //设置应用私钥
        certAlipayRequest.setPrivateKey("MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCB3eUTPKZwI9ySgN4xHNRpxdN1npXkHLfacxqbaHKxxLPeebKf15u68nLnwkEgsSOYIbffy/newdCv8nWzLthkSrI8s/9LXQ4BLH3DKzi1tiwoBE49gsJscGBUkTa/XeJmzpn1EVViE5pCPPjRP6E9DXC/XTXI/hW75QG4RM9l0unOoqDju9iQqKs/dzRqChJL+w3krDvWdnkAp9ODh2YLigAT6bOBl/u18rYVHoG9jCP539bIKYPYPR65YFv2qPODFZPd0CjNCNWHEW0iFSompGqfdICJ7bJDLEalSEY2ldMEo+0Lhd8xwnh3rjkCEMjlphKC8JLDTzUn/ZpxGRpVAgMBAAECggEAEzci6vrBYbxcqay5s6ihYVktFa02Xn9FUORRHoeQ2O7S8mstW+tKFppDGDms60pqitsKWunSefxRhYcplS6sRAKtZr/3WU5WgSP1I8ikZnajB/TDIuTTIbpq9nhaEmyw0CdnrlQ3PwDJhV2CL2vrzp3LffUrvSajTp7u5zX/PgCGMRgZaHsIBKbXX75FotJn6tpVaCOr07imoqMtMOxO+TVe2wuEKiMm74goPmUzWIFY3SaO8Ro2fzW2bjAES/AcYLw9zVPn0JjjWsltbAznxYC66FCG/nhcG5lFdr9sZW0Y9txyOKXHiMZZ34KSm+1zZb5+1ZT9s6ufjnFt9Ni4TQKBgQC6eDzUzw01mkpIV5iZPDllcObhyRbd0Zh1NEfhfsqR4VxPV66TBy/eFS2ARW+VrpH5wJJLAL5gEaB9C1NJvV/00v25xr4i0232Vs9FAJirNw1Xsw4u8l+3mqBrfJkKWcKs+1cUH9dHenjortLZKYFCzw2cLAVx4R9SdksICV3AFwKBgQCySolRdtoT4hjmrb8MWlnG4Rdi4AXv+UqdW+qF6IC+yyf1u9M81z8xpcHqh8+croUf1eVlWYL2iqbv4Qs8n4wzYttoh2+o+zJh2awlnDX8gnCLrOw8wzXyUS3RY/SOZIijQJIeWZW4GBKNs+I1dftVHHxN/5eXdFrVCyrQHvSwcwKBgGNpFK0zkUxBbFay9HTFKahOD5jRtvIc+pWJgMTT7rTlG2xlR3m0/Cz+x1o6Kmn3PnWS16tKwzO+Ufw4HHgUkOKZ1ZDERruUUmxhDXExBzNIT0GxAN/AcY0Vz25eZ8yf2+yStnLRItlFjs4l3dzOhs4SSqQ2x9RVe3hf/lJTg/qTAoGAE564NcrKfN2ot9nu6EEZGBW1KSBWBu58E40F5e6MHHLm1tfwiwV04tXG3TRM2IUsCTDUqa6MBu2DKWqufeFg4FbEpmAhHYtnI/V8SDdEiaEhGX3SEW26BgyA6kYBp5nQn4Z/je911mhvwkBFaHSvT9Juq3axC+22ATPVZknBy5kCgYBVZjVl4mqJqgvvEPo12dwBQqBjoCwoT3xongYDnMzso7GYL2oRL6o43Utp+wxdE7OY2S+54rja6u9BVDNW9Rv+GGohbfRr6LQFytEjdAoCgoxjSGI5v+Ox6MF7sK72TVum5lnUWRjV1Bf4t3s9UuNjYuiLoxXbDJJNFG2yIv1zDA==");
        //设置请求格式，固定值json
        certAlipayRequest.setFormat("json");
        //设置字符集
        certAlipayRequest.setCharset("utf-8");
        //设置签名类型
        certAlipayRequest.setSignType("RSA2");
		if(System.getProperty("os.name").startsWith("Win")){
			//设置应用公钥证书路径
			certAlipayRequest.setCertPath("D:/ali_cert/appCertPublicKey_2021002119679359.crt");
			//设置支付宝公钥证书路径
			certAlipayRequest.setAlipayPublicCertPath("D:/ali_cert/alipayCertPublicKey_RSA2.crt");
			//设置支付宝根证书路径
			certAlipayRequest.setRootCertPath("D:/ali_cert/alipayRootCert.crt");
		}else{
        //设置应用公钥证书路径
        certAlipayRequest.setCertPath("/mnt/db/smart-community/cert/ali_cert/appCertPublicKey_2021002119679359.crt");
        //设置支付宝公钥证书路径
        certAlipayRequest.setAlipayPublicCertPath("/mnt/db/smart-community/cert/ali_cert/alipayCertPublicKey_RSA2.crt");
        //设置支付宝根证书路径
        certAlipayRequest.setRootCertPath("/mnt/db/smart-community/cert/ali_cert/alipayRootCert.crt");
		}
        return certAlipayRequest;
    }

    //获取证书请求客户端
    public static AlipayClient getDefaultCertClient(){
        AlipayClient alipayClient = null;
        try {
            alipayClient = new DefaultAlipayClient(getCert());
        } catch (AlipayApiException e) {
            System.out.println("支付宝证书请求客户端构建失败");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alipayClient;
    }
}
