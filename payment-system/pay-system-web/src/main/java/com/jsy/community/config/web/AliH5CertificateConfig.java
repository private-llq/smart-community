package com.jsy.community.config.web;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.jsy.community.constant.ConstClasses;

public class AliH5CertificateConfig {
    //初始化证书请求类
    public static CertAlipayRequest getCert() throws Exception {
        //支付宝证书方式请求
        CertAlipayRequest certAlipayRequest = new CertAlipayRequest();
        //设置网关地址
        certAlipayRequest.setServerUrl("https://openapi.alipaydev.com/gateway.do");
        //设置应用Id
        certAlipayRequest.setAppId("2016110100783947");
        //设置应用私钥
        certAlipayRequest.setPrivateKey("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCcNJVHqlT4Aie1zP5K2cx1BbotSph7A6/NIiaEBhy0FUAEZNKdMGbRjqr7kW9Nm65Ejn/KzKg1/x18p6lKCuGR4nN4awLzV+bC3s/ZlCflfW02pjlLvghbJ6T4CVT569ID9SYQB0iG4vp2YqLBbYTqYDU1JpVbKXy6KcFwCvpe9nQDunxM5MNkUW0xkt4Un2POhve6Fdrr/4FO5rwebvj9Q+2TyOHW3mmcEdFHt534zbICtxJUkHP6ctVZx+u7iqpUvxBfMEGSzGg0gzo76GFwTOHLq3rcpf1DWwEuryYgx1qVo6GWXnIsgtT2aNSVkceyUNmsgk/qlAccN35AsUBFAgMBAAECggEBAISdI8tW8ZIoijwPUCiN5cXH2frPpMpzI9J81sQpSD8e912Dl0c6K2BnxgOTwjHaJMYxUI8E1x4Ye5kz4kZpHH41CaFHAJdFnjT84usum/x6Urkdv/Ds4b052BV4HrubGI6hlvHurQVPOCI/tTuQVlKA4UtBeXWOsESAZKhRuaPaLR7ViqH0s8akXKrhv677UYEm2jmIqeOAzlZ1aO6KQcjP/NB9xU0yHSis+5Qty4MQb/m3lUgM/lVSx28XdNRMCbX3HIfEcmTdDWbmhNpA6dx0Lo5596/vIlBubAA80hlz1KGTGbSAnQhs0vY9j+Z+mXuucrYnJIO7HwtvgHfeBqECgYEA/XHV+NpJZ4RoQApcelGk4oURLg5Y9br6mRtWq/wfHk8V3q25sECsAPu+ngjrza9sdvUl0Gcr89wQN4tuEQuVv+61M9fag2U9h8uVFGLrPf4T5hiQyi+wXwUiuJuytuo8Th2bD+LM9cYrQocs8Lj+WVIYaE8KSeWt6m5fttI7HGkCgYEAncfDhFQseU+S9IRqMNVa82kDQqVAN7YUWCwcMlRdxo876e5qtdcvdTAul73VyR07iwA1N6QWNg7f2saTXkQMR6h17fZ15i7ZVbXUITvYI/zHk1SA4ll0gX4qwRnsS/68o4xipLSs+sXqo7KPLHr4f7A/XsmqBBBzBE32fxBZOX0CgYAr5oQBzw3X+H92WFUcevfspNANAj82kWTHEI06zTk2rsxC/iSj+w/QO8sQ5MqznidTk7MxHi3m17+XX3d5TGBsFpUcnc8j4219EC6KkXfeDRy1RpwN6aTLUMBq6c1TEc/QWCB75/VIvULRC0kliWXFYwsHRmmp0zcE3ImHyXKjwQKBgFReUWodKHrl1gryE2Zxb+TgrmGgw6oGrvJHA/zVqB9Y0AzS9DSwECx1yzRQylR0UTaOseZC062sXn851+t8L6TRq93HAFxJiOWQCRKMUZNMw9tAQHrIosfrkykWl4g7ettpYgX4ptahpkpKXMzIJMECmaAx7bumoBoZJknjkBJxAoGAYS6K+qjr9VEfFhNnu0GcFyXBXkUdfEi2iczQi40p1ZtoP6RLi9y49cEOQgbF8s6qtn1EW00Ce10kK+SLYBpTzooHe2+FCFlfgxVcnuzoqFUVIQTlmbhmwNC878toEQyRVjRBrOtcV381ee13Thg4FYuIcIdyh2m1PdEZt2f4MNQ=");
        certAlipayRequest.setFormat("json");
        //设置字符集
        certAlipayRequest.setCharset("utf-8");
        //设置签名类型
        certAlipayRequest.setSignType("RSA2");
        //设置应用公钥证书路径
        certAlipayRequest.setCertContent("C:\\Users\\Administrator\\Desktop\\pay\\appCertPublicKey_2016110100783947.crt");
        //设置支付宝公钥证书路径
        certAlipayRequest.setAlipayPublicCertContent("C:\\Users\\Administrator\\Desktop\\pay\\alipayCertPublicKey_RSA2.crt");
        //设置支付宝根证书路径
        certAlipayRequest.setRootCertContent("C:\\Users\\Administrator\\Desktop\\pay\\alipayRootCert.crt");
        return certAlipayRequest;
    }

    //获取证书请求客户端
    public static AlipayClient getDefaultCertClient() {
        AlipayClient alipayClient = null;
        try {
            alipayClient = new DefaultAlipayClient(getCert());
        } catch (AlipayApiException e) {
            System.out.println(e.getMessage());
            System.out.println("支付宝证书请求客户端构建失败");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alipayClient;
    }
}
