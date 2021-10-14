package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayFundTransUniTransferRequest;
import com.alipay.api.response.AlipayFundTransUniTransferResponse;
import com.jsy.community.api.IPayConfigureService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.api.UserAccountWithdrawalService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PayConfigureEntity;
import com.jsy.community.utils.AESOperator;
import com.jsy.community.utils.AlipayUtils;
import com.jsy.community.vo.WithdrawalResulrVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Value;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.util.*;

@Slf4j
@DubboService(version = Const.version, group = Const.group_payment)
public class UserAccountWithdrawalServiceImpl implements UserAccountWithdrawalService {

    @Value("${wechat.app_id}")
    private String appId;
    @Value("${wechat.mch_id}")
    private String mchId;
    @Value("${wechat.private_key}")
    private String privateKey;
    @Value("${wechat.spbill_create_ip}")
    private String spbillCreateIp;
    @Value("${wechat.apiclient_cret}")
    private String fileUrl;
    //转账请求接口地址
    public static final String PAY_URL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
    /**
     * UTF-8编码
     */
    public static final String UTF8 = "UTF-8";
    public static final String[] HEX_DIGITS = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

//**************************************************************  微信提现 start  ********************************************************************

    private static String uuidSimple() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private static String MD5Encode(String origin, String charsetName) {
        String resultString = null;
        try {
            resultString = origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (StringUtils.isBlank(charsetName)) {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            } else {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetName)));
            }
        } catch (Exception ignored) {

        }
        return resultString;
    }

    private static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (byte value : b) {
            resultSb.append(byteToHexString(value));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n += 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return HEX_DIGITS[d1] + HEX_DIGITS[d2];
    }

    /**
     * 生成签名字符串
     *
     * @param params
     * @return
     */
    private String signStr(Map<String, String> params) {
        String[] keys = params.keySet().toArray(new String[]{});
        Arrays.sort(keys);
        StringBuilder str = new StringBuilder();
        for (String s : keys) {
            str.append(s).append("=").append(params.get(s)).append("&");
        }
        //签名步骤二：加上key
        str.append("key=").append(privateKey);
        String s = str.toString();
        log.info("按照顺序排列的sign未加密值：" + s);
        //步骤三：加密并大写
        return MD5Encode(s, "utf-8").toUpperCase();
    }

    /**
     * 将需要传递给微信的参数转成xml格式
     *
     * @param parameters
     * @return
     */
    private static String assembParamToXml(Map<String, String> parameters) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        Set<String> es = parameters.keySet();
        List<Object> list = new ArrayList<>(es);
        Object[] ary = list.toArray();
        Arrays.sort(ary);
        list = Arrays.asList(ary);
        for (Object o : list) {
            String key = (String) o;
            String val = parameters.get(key);
            if ("attach".equalsIgnoreCase(key) || "body".equalsIgnoreCase(key) || "sign".equalsIgnoreCase(key)) {
                sb.append("<").append(key).append(">").append("<![CDATA[").append(val).append("]]></").append(key).append(">");
            } else {
                sb.append("<").append(key).append(">").append(val).append("</").append(key).append(">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * 发起调用
     *
     * @param paramStr
     * @return
     * @throws Exception
     */
    private String getInSsl(String paramStr) throws Exception {
        String text;
        // 指定读取证书格式为PKCS12
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        // 读取本机存放的PKCS12证书文件
        try (FileInputStream instream = new FileInputStream(fileUrl)) {
            // 指定PKCS12的密码(商户ID)
            keyStore.load(instream, mchId.toCharArray());
        }
        SSLContext sslcontext = org.apache.http.conn.ssl.SSLContexts.custom().loadKeyMaterial(keyStore, mchId.toCharArray()).build();
        // 指定TLS版本
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, null, null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        // 设置httpclient的SSLSocketFactory
        try (CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build()) {
            HttpPost post = new HttpPost(PAY_URL);
            StringEntity s = new StringEntity(paramStr, "utf-8");
            if (StringUtils.isBlank("application/xml")) {
                s.setContentType("application/xml");
            }
            s.setContentType("application/xml");
            post.setEntity(s);
            HttpResponse res = httpclient.execute(post);
            HttpEntity entity = res.getEntity();
            text = EntityUtils.toString(entity, "utf-8");
            log.info("text：" + text);
        }
        return text;
    }

    /**
     * 获取子结点的xml
     *
     * @param children
     * @return String
     */
    private static String getChildrenText(List children) {
        StringBuilder sb = new StringBuilder();
        if (!children.isEmpty()) {
            Iterator it = children.iterator();
            while (it.hasNext()) {
                Element e = (Element) it.next();
                String name = e.getName();
                String value = e.getTextNormalize();
                List list = e.getChildren();
                sb.append("<" + name + ">");
                if (!list.isEmpty()) {
                    sb.append(getChildrenText(list));
                }
                sb.append(value);
                sb.append("</").append(name).append(">");
            }
        }
        return sb.toString();
    }

    /**
     * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
     *
     * @param strxml
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    private static Map parseXMLToMap(String strxml) throws JDOMException, IOException {
        strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"" + UTF8 + "\"");
        if ("".equals(strxml)) {
            return null;
        }
        Map m = new HashMap();
        InputStream in = new ByteArrayInputStream(strxml.getBytes(StandardCharsets.UTF_8));
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(in);
        Element root = doc.getRootElement();
        List list = root.getChildren();
        for (Object o : list) {
            Element e = (Element) o;
            String k = e.getName();
            String v;
            List children = e.getChildren();
            if (children.isEmpty()) {
                v = e.getTextNormalize();
            } else {
                v = getChildrenText(children);
            }
            m.put(k, v);
        }
        //关闭流
        in.close();
        return m;
    }

    private Object[] buildParam(String serialNumber, String openid, String amount) {
        Map<String, String> params = new HashMap<>();
        params.put("mch_appid", appId);//商户账号appid
        params.put("mchid", mchId);//商户号
        params.put("nonce_str", uuidSimple());//随机字符串
        params.put("partner_trade_no", serialNumber);//商户订单号,即为提现流水号
        params.put("openid", openid);//用户openid
        params.put("check_name", "NO_CHECK");//校验用户姓名选项
        params.put("amount", amount);//金额
        params.put("desc", "用户微信提现");//付款备注
        params.put("spbill_create_ip", spbillCreateIp);//Ip地址
        params.put("sign", signStr(params));//签名
        String paramStr = assembParamToXml(params);
        boolean success = true;
        String resXml = "";
        try {
            resXml = getInSsl(paramStr);
        } catch (Exception ex) {
            success = false;
            log.error("发起微信提现调用异常 /n {}", ex);
        }
        Object[] result = new Object[2];
        result[0] = success;
        result[1] = resXml;
        return result;
    }

    /**
     * 调用微信提现（商家账户转账至个人账户）
     * ◆ 当返回错误码为“SYSTEMERROR”时，请不要更换商户订单号，一定要使用原商户订单号重试，否则可能造成重复支付等资金风险。
     * ◆ XML具有可扩展性，因此返回参数可能会有新增，而且顺序可能不完全遵循此文档规范，如果在解析回包的时候发生错误，请商户务必不要换单重试，
     * 请商户联系客服确认付款情况。如果有新回包字段，会更新到此API文档中。
     * ◆ 因为错误代码字段err_code的值后续可能会增加，所以商户如果遇到回包返回新的错误码，请商户务必不要换单重试，请商户联系客服确认付款情况。如果有新的错误码，会更新到此API文档中。
     * ◆ 错误代码描述字段err_code_des只供人工定位问题时做参考，系统实现时请不要依赖这个字段来做自动化处理。
     * 文档地址：https://pay.weixin.qq.com/wiki/doc/api/tools/mch_pay.php?chapter=14_2
     *
     * @param serialNumber 转账订单号，唯一
     * @param openid       收款方openid
     * @param amount       转账金额（分）
     * @return true：成功；false：失败
     */
    @Override
    public WithdrawalResulrVO weiXinWithdrawal(String serialNumber, String openid, String amount) {
        log.info("进入微信提现接口，参数：转账订单号 - {},收款方openid - {},转账金额（分） - {}", serialNumber, openid, amount);
        log.info("商家信息：appId : {},mchId : {},privateKey : {},spbillCreateIp : {},fileUrl : {}", appId, mchId, privateKey, spbillCreateIp, fileUrl);
        //开始构建参数，发送请求
        Object[] result = buildParam(serialNumber, openid, amount);
        //解析返回结果
        String resXml = (String) result[1];
        boolean success = (Boolean) result[0];
        if (success) {
            Map<String, String> map = new HashMap<>();
            try {
                map = parseXMLToMap(resXml);
            } catch (Exception e) {
                log.error("parseXMLToMap error：{}", e);
            }
            String returnCode = map.get("return_code");
            if (returnCode.equalsIgnoreCase("FAIL")) {
                //支付失败
                log.error("提现失败：{}", map.get("return_msg"));
                return new WithdrawalResulrVO("-1", map.get("return_msg"), false);
            } else if (returnCode.equalsIgnoreCase("SUCCESS")) {
                if (map.get("err_code") != null) {
                    //支付失败
                    log.error("提现失败：{}", map.get("err_code_des"));
                    return new WithdrawalResulrVO("-1", map.get("err_code_des"), false, map.get("err_code"));
                } else if (map.get("result_code").equalsIgnoreCase(
                        "SUCCESS")) {
                    //支付成功  paymentNo：微信付款单号  payment_time：付款成功时间
                    String paymentNo = map.get("payment_no");
                    String paymentTime = map.get("payment_time");
                    log.info("提现转账成功，微信付款单号 {} ，付款成功时间 {}", paymentNo, paymentTime);
                    return new WithdrawalResulrVO("0", "提现成功,微信付款单号：" + paymentNo + ",付款成功时间：" + paymentTime + "。", true);
                }
            }
        }
        return new WithdrawalResulrVO("-1", resXml, false);
    }

//**************************************************************  微信提现 end  ********************************************************************

//**************************************************************  支付宝提现 start  ********************************************************************

    private static final String ALIPAY_SERVER_URL = "https://openapi.alipay.com/gateway.do";
    private static final String ALIPAY_FORMAT = "json";
    private static final String ALIPAY_CHARSET = "UTF-8";
    private static final String ALIPAY_SIGN_TYPE = "RSA2";

    @DubboReference(version = Const.version, group = Const.group_property)
    private IPayConfigureService iPayConfigureService;

    private CertAlipayRequest initRequest() throws Exception {
        PayConfigureEntity companyConfig = iPayConfigureService.getCompanyConfig(1L);
        if (companyConfig == null) {
            throw new ProprietorException("没有支付宝配置");
        }
        CertAlipayRequest certAlipayRequest = new CertAlipayRequest();
        certAlipayRequest.setServerUrl(ALIPAY_SERVER_URL);  //gateway:支付宝网关（固定）https://openapi.alipay.com/gateway.do
        certAlipayRequest.setFormat(ALIPAY_FORMAT);  //参数返回格式，只支持 json 格式
        certAlipayRequest.setCharset(ALIPAY_CHARSET);  //请求和签名使用的字符编码格式，支持 GBK和 UTF-8
        certAlipayRequest.setSignType(ALIPAY_SIGN_TYPE);  //商户生成签名字符串所使用的签名算法类型，目前支持 RSA2 和 RSA，推荐商家使用 RSA2。
        certAlipayRequest.setAppId(AESOperator.decrypt(companyConfig.getAppId()));  //APPID 即创建应用后生成,详情见创建应用并获取 APPID
        certAlipayRequest.setPrivateKey(AESOperator.decrypt(companyConfig.getPrivateKey()));  //开发者应用私钥，由开发者自己生成
        //设置应用公钥证书路径
        String decrypt = AESOperator.decrypt(companyConfig.getCertPath());
        certAlipayRequest.setCertContent(AlipayUtils.getPrivateKey(decrypt));
        //设置支付宝公钥证书路径
        String decrypt1 = AESOperator.decrypt(companyConfig.getAlipayPublicCertPath());
        certAlipayRequest.setAlipayPublicCertContent(AlipayUtils.getPrivateKey(decrypt1));
        //设置支付宝根证书路径
        String decrypt2 = AESOperator.decrypt(companyConfig.getRootCertPath());
        certAlipayRequest.setRootCertContent(AlipayUtils.getPrivateKey(decrypt2));
        return certAlipayRequest;
    }

    /**
     * 说明：单笔转账到支付宝账户
     * 文档地址：https://opendocs.alipay.com/open/02byuo
     *
     * @param serialNumber 商家侧唯一订单号
     * @param amount       订单总金额，单位为元，精确到小数点后两位
     * @param realName     收款方信息->真实姓名
     * @param identity     收款方信息->参与方的唯一标识
     * @param identityType 收款方信息->参与方的标识类型，目前支持如下类型：
     *                     1、ALIPAY_USER_ID：支付宝账号对应的支付宝唯一用户号。以2088开头的16位纯数字组成。
     *                     2、ALIPAY_LOGON_ID：支付宝登录号，支持邮箱和手机号格式。
     */
    @Override
    public WithdrawalResulrVO zhiFuBaoWithdrawal(String serialNumber, String amount, String realName, String identity, String identityType) {
        AlipayClient alipayClient;
        try {
            alipayClient = new DefaultAlipayClient(initRequest());
        } catch (Exception e) {
            log.error("获取alipay连接异常，{}", e);
            return new WithdrawalResulrVO("-1", "获取alipay连接异常。", false);
        }
        AlipayFundTransUniTransferRequest request = new AlipayFundTransUniTransferRequest();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("out_biz_no", serialNumber);
        paramMap.put("trans_amount", amount);
        paramMap.put("product_code", "TRANS_ACCOUNT_NO_PWD");
        paramMap.put("biz_scene", "DIRECT_TRANSFER");
        paramMap.put("order_title", "e到家钱包提现");
        Map<String, Object> payeeInfo = new HashMap<>();
        payeeInfo.put("identity", identity);
        payeeInfo.put("identity_type", identityType);
        payeeInfo.put("name", realName);
        paramMap.put("payee_info", payeeInfo);
        paramMap.put("remark", "e到家钱包提现");
        String bizContent = JSONObject.toJSONString(paramMap);
        request.setBizContent(bizContent);
        AlipayFundTransUniTransferResponse response;
        try {
            log.info("转账发送---,参数为:{}", request.getBizContent());
            response = alipayClient.certificateExecute(request);
            if (response.isSuccess()) {
                log.info("支付宝转账API调用成功。");
                if ("10000".equals(response.getCode())) {
                    log.info("转账成功,支付宝返回参数:{}", JSONObject.toJSONString(response));
                    return new WithdrawalResulrVO("0", "转账成功", true);
                } else {
                    log.error("转账失败,支付宝返回参数:{}", JSONObject.toJSONString(response));
                    return new WithdrawalResulrVO(response.getCode(), response.getSubMsg(), false, response.getSubCode());
                }
            } else {
                log.error("支付宝转账API调用失败：{}", JSONObject.toJSONString(response));
                return new WithdrawalResulrVO(response.getCode(), "转账失败,调用支付宝转账API失败", false, response.getSubCode());
            }
        } catch (AlipayApiException e) {
            log.error("调用支付宝转账API异常：{}", e);
            return new WithdrawalResulrVO("-1", "转账失败,调用支付宝转账API异常", false);
        }
    }

//**************************************************************  支付宝提现 end  ********************************************************************
}
