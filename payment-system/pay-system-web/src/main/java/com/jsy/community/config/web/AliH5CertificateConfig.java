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
        certAlipayRequest.setPrivateKey("MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCo/urQd+BXzpOKhindIRQkSiTiKn7bLpwTkCE8sLVEJJvNQcyZN7f20AYXn8PjSuWq1FRwoY5OYFBKruvItyuGnZYAq1VjS6OVCvURDv2IGb5Aq6f3/qFrhItNvW/iYf35wueRl0Jt8t12eqFHNMEN/XjGYWRHclUmVI6pAiRYV4UobsV8u54ivSfNq1Bh2n3OSVUjljw3vgvE2iPrYgwJDhTqMiBJRfMoXyDGf0emGsxobkzfDlM9ONS/TkOVVO10HW/OxMwzqd62rBxKvOEmhvRP6YndkOUX9NBYNfRyWMKkr4DFMRGcc7EnBrjLu38Nmr58Sy0n0rMqJZfB3M6TAgMBAAECggEAVOlo0IA7D8Ny7JpGtJ3nQeijKaRSgwyjTU0Q9i6PlYKTagmSpLWTUIbrWLlksnKyfSgcmU8DcqwAC9ZiEELAsu3BLeroh3C866lKw1x11QmiR1o4lmmxb5V0bQYvDl9QPktJESUgMqBh2CL7oAyIp9/g7ESJH0D6Ob0oxRCXdG8L3z9jxhyFq8WXpMIvB6VV6jlbVt/veG9DmMWIclb1y75Zr9CC51SdkSwc/KpXm3ThPWAaI/CllA1jzvjsOCtrH39qehh80JlZ4H75pr1cjfrn5kg6SUbpf8uj1mZXQir6P2/j9h9AOz5l26wHctmdiTMbEqBHXwbUTJfIAw3hcQKBgQDbXDuH8MJcI/dhUiRtluO8mVjgPdLLmSTfGCSqiN766nLB8r4CuaQuZLhtgXCs6kDxkXjpRrn5A70oT7DDl526/QdKzN8ImOLDGau76QTlKaXG0DFrlQprEnbKTT3KkUE3QAdJEy/bDLWEJnOjdox/XJ7KNLv4Uy1AWavvOJzXywKBgQDFOR24kjrL/pt04XcDLpC+ZVsDvaFeyEtCnQrreUOioiuMsoJwHB4GrSudxIuW+vl7qikNlG+s0jGn0LPraGu01UaYHhZgoxZTBZ0W+KAlYiDnlUi9PZPZpcwcjpJGt80CUD+ceTg2rJybBgxsd9NMQwr03VwVI+VdxBsOUY47WQKBgC5AIiUldDVtN+mDIj8hA19ujbiqEFnxLj8Dnid+uFT7Pbr67WkxrxZBEkm0cwdiVNMaMv4sdWW1jhqDwudafl1AoFr7G0N6yMUOEpGKLggLYfKrTuA6CnpM6FL7Oj4Npkx56Y9UxlBPO65jVRpwEmuAIdLMj51qHC5nOcM0ko1FAoGAQ82uFR1Yz3cZ9OXqp5JjusFXuAKCrvZHQYCCF5BaG1iLhlR8mZRhEkSIwTWoE+D20ngeoweMGSsuXRZYNK9yj/neyj9VzCVlcGd2qUYSsKd6zoM5Pa4k3Wets4ekbGGWPpxeMipOlCVltbovnT7YytgltSdBGBd+LG1WK+cH8gkCgYA9nTPbTALOKv2pEuAWBafqAg34Yr2xaoiEys22awEwoJACoAjCcsBOLp0+IR8677TKmtYhhcYWydH4U6YSFffYsJ1jw6cDNNYQ/tA4EPaj6yfO/c2s8OhmdQ4v28f19awtRRDwB5BWb7Ry7se8+equegqB77Is8jgfO4TdPpO9+g==");
        certAlipayRequest.setFormat("json");
        //设置字符集
        certAlipayRequest.setCharset("utf-8");
        //设置签名类型
        certAlipayRequest.setSignType("RSA2");
        //设置应用公钥证书路径
        certAlipayRequest.setCertContent("C:\\alipay\\appCertPublicKey_2016110100783947.crt");
        //设置支付宝公钥证书路径
        certAlipayRequest.setAlipayPublicCertContent("C:\\alipay\\alipayCertPublicKey_RSA2.crt");
        //设置支付宝根证书路径
        certAlipayRequest.setRootCertContent("C:\\alipay\\alipayRootCert.crt");
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
