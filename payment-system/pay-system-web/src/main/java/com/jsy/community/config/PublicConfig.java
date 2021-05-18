package com.jsy.community.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.jsy.community.utils.AesUtil;
import com.jsy.community.utils.MyHttpClient;
import net.sf.json.JSONObject;
import okhttp3.HttpUrl;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

/**
 * @program: pay
 * @description:  微信支付工具类
 * @author: Hu
 * @create: 2021-01-21 16:56
 **/
public class PublicConfig {
    //请求网关
    private static final String url_prex = "https://api.mch.weixin.qq.com";
    //编码
    private static final String charset = "UTF-8";

    /**
     * 微信支付下单
     *
     * @param url                请求地址（只需传入域名之后的路由地址）
     * @param jsonStr            请求体 json字符串 此参数与微信官方文档一致
     * @param mercId             商户ID
     * @param serialNo          证书序列号
     * @param privateKeyFilePath 私钥的路径
     * @return 订单支付的参数
     * @throws Exception
     */
    public static String V3PayGet(String url, String jsonStr, String mercId, String serialNo, String privateKeyFilePath) throws Exception {
        String body = "";
        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(url_prex + url);
        //装填参数  charset
        StringEntity s = new StringEntity(jsonStr,charset);
        s.setContentType("application/json");
        s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
                "application/json"));
        //设置参数到请求对象中
        httpPost.setEntity(s);
       // String post = getToken("POST", HttpUrl.parse(url_prex + url), mercId, serial_no, privateKeyFilePath, jsonStr);
        //System.out.println(post);
        //设置header信息
        //指定报文头【Content-type】、【User-Agent】
        httpPost.setHeader("Content-type", "application/json");
        //httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        httpPost.setHeader("Accept", "application/json");
//        httpPost.setHeader("Authorization",
//                "WECHATPAY2-SHA256-RSA2048 " + post);
        //执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = MyHttpClient.createHttpClient().execute(httpPost);

        //获取结果实体
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, charset);
        }
        EntityUtils.consume(entity);
        //释放链接
        if (response!=null){
            response.close();
        }
        switch (url) {
            case "/v3/pay/transactions/app"://返回APP支付所需的参数
                return JSONObject.fromObject(body).getString("prepay_id");
            case "/v3/pay/transactions/jsapi"://返回JSAPI支付所需的参数
                return JSONObject.fromObject(body).getString("prepay_id");
            case "/v3/pay/transactions/native"://返回native的请求地址
                return JSONObject.fromObject(body).getString("code_url");
            case "/v3/pay/transactions/h5"://返回h5支付的链接
                return JSONObject.fromObject(body).getString("h5_url");
        }
        return null;
    }


    /**
     * 微信调起支付参数
     * 返回参数如有不理解 请访问微信官方文档
     * https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter4_1_4.shtml
     *
     * @param prepayId           微信下单返回的prepay_id
     * @param appId              应用ID(appid)
     * @param privateKeyFilePath 私钥的地址
     * @return 当前调起支付所需的参数
     * @throws Exception
     */
    public static JSONObject WxTuneUp(String prepayId, String appId, String privateKeyFilePath) throws Exception {
        String time = System.currentTimeMillis() / 1000 + "";
        String nonceStr = UUID.randomUUID().toString().replace("-", "");
//        String packageStr = "prepay_id=" + prepayId;
        ArrayList<String> list = new ArrayList<>();
        list.add(appId);
        list.add(time);
        list.add(nonceStr);
        list.add(prepayId);
        //加载签名
        String packageSign = sign(buildSignMessage(list).getBytes(), privateKeyFilePath);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appid", appId);
        jsonObject.put("partnerid", WechatConfig.MCH_ID);
        jsonObject.put("prepayid", prepayId);
        jsonObject.put("package", "Sign=WXPay");
        //jsonObject.put("packages", packageStr);
        jsonObject.put("noncestr", nonceStr);
        jsonObject.put("timestamp", time);
//        jsonObject.put("sign_type","HMAC-SHA256");
        jsonObject.put("sign", packageSign);
        return jsonObject;
    }


    /**
     * 处理微信异步回调
     *
     * @param request
     * @param response
     * @param privateKey APIv3 32的秘钥
     */
    public static Map<String, String> notify(HttpServletRequest request, HttpServletResponse response, String privateKey) throws Exception {
        Map<String, String> map = new HashMap<>(12);
        String result = readData(request);
        // 需要通过证书序列号查找对应的证书，verifyNotify 中有验证证书的序列号
        String plainText = verifyNotify(result, privateKey);
        if (StrUtil.isNotEmpty(plainText)) {
            map.put("code", "SUCCESS");
            map.put("message", "SUCCESS");
        } else {
            map.put("code", "ERROR");
            map.put("message", "签名错误");
        }

        String out_trade_no = JSONObject.fromObject(plainText).getString("out_trade_no");
        String transaction_id = JSONObject.fromObject(plainText).getString("transaction_id");
        String attach = JSONObject.fromObject(plainText).getString("attach");
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("out_trade_no",out_trade_no);
        hashMap.put("attach",attach);
        hashMap.put("transaction_id",transaction_id);
        return hashMap;
    }


    /**
     * 生成组装请求头
     *
     * @param method             请求方式
     * @param url                请求地址
     * @param mercId             商户ID
     * @param serialNo          证书序列号
     * @param privateKeyFilePath 私钥路径
     * @param body               请求体
     * @return 组装请求的数据
     * @throws Exception
     */
    public static String getToken(String method, HttpUrl url, String mercId, String serialNo, String privateKeyFilePath, String body) throws Exception {
        String nonceStr = UUID.randomUUID().toString().replace("-", "");
        long timestamp = System.currentTimeMillis() / 1000;
        String message = buildMessage(method, url, timestamp, nonceStr, body);
        String signature = sign(message.getBytes("UTF-8"), privateKeyFilePath);
        return "mchid=\"" + mercId + "\","
                + "serial_no=\"" + serialNo + "\","
                + "nonce_str=\"" + nonceStr + "\","
                + "timestamp=\"" + timestamp + "\","
                + "signature=\"" + signature + "\"";
    }

    /**
     * 生成签名
     *
     * @param message            请求体
     * @param privateKeyFilePath 私钥的路径
     * @return 生成base64位签名信息
     * @throws Exception
     */
    public static String sign(byte[] message, String privateKeyFilePath) throws Exception {
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(getPrivateKey(privateKeyFilePath));
        sign.update(message);
        return Base64.getEncoder().encodeToString(sign.sign());
    }




    /**
     * 组装签名加载
     *
     * @param method    请求方式
     * @param url       请求地址
     * @param timestamp 请求时间
     * @param nonceStr  请求随机字符串
     * @param body      请求体
     * @return 组装的字符串
     */
    public static String buildMessage(String method, HttpUrl url, long timestamp, String nonceStr, String body) {
        String canonicalUrl = url.encodedPath();
        if (url.encodedQuery() != null) {
            canonicalUrl += "?" + url.encodedQuery();
        }
        return method + "\n"
                + canonicalUrl + "\n"
                + timestamp + "\n"
                + nonceStr + "\n"
                + body + "\n";
    }

    /**
     * 获取私钥。
     *
     * @param filename 私钥文件路径  (required)
     * @return 私钥对象
     */
    public static PrivateKey getPrivateKey(String filename) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filename)), "UTF-8");
        try {
            String privateKey = content.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(
                    new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("当前Java环境不支持RSA", e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("无效的密钥格式");
        }
    }

    /**
     * 构造签名串
     *
     * @param signMessage 待签名的参数
     * @return 构造后带待签名串
     */
    public static String buildSignMessage(ArrayList<String> signMessage) {
        if (signMessage == null || signMessage.size() <= 0) {
            return null;
        }
        StringBuilder sbf = new StringBuilder();
        for (String str : signMessage) {
            sbf.append(str).append("\n");

        }
        return sbf.toString();
    }


    /**
     * v3 支付异步通知验证签名
     *
     * @param body 异步通知密文
     * @param key  api 密钥
     * @return 异步通知明文
     * @throws Exception 异常信息
     */
    public static String verifyNotify(String body, String key) throws Exception {
        // 获取平台证书序列号
        cn.hutool.json.JSONObject resultObject = JSONUtil.parseObj(body);
        cn.hutool.json.JSONObject resource = resultObject.getJSONObject("resource");
        String cipherText = resource.getStr("ciphertext");
        String nonceStr = resource.getStr("nonce");
        String associatedData = resource.getStr("associated_data");
        AesUtil aesUtil = new AesUtil(key.getBytes(StandardCharsets.UTF_8));
        // 密文解密
        return aesUtil.decryptToString(
                associatedData.getBytes(StandardCharsets.UTF_8),
                nonceStr.getBytes(StandardCharsets.UTF_8),
                cipherText
        );
    }


    /**
     * 处理返回对象
     *
     * @param request
     * @return
     */
    public static String readData(HttpServletRequest request) {
        BufferedReader br = null;
        try {
            StringBuilder result = new StringBuilder();
            br = request.getReader();
            for (String line; (line = br.readLine()) != null; ) {
                if (result.length() > 0) {
                    result.append("\n");
                }
                result.append(line);
            }
            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * @Description: 关闭订单避免重复支付
     * @author: Hu
     * @since: 2021/1/27 11:39
     * @Param:
     * @return:
     */
    public static void closeOrder(String order) throws Exception {

        String postfix="/v3/pay/transactions/out-trade-no/"+order+"/close";
        //请求URL
        HttpPost httpPost = new HttpPost(url_prex+postfix);
        //请求body参数
        String reqdata ="{\"mchid\": \""+WechatConfig.MCH_ID+"\"}";
        StringEntity entity = new StringEntity(reqdata);
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");


        //完成签名并执行请求
        CloseableHttpResponse response = MyHttpClient.createHttpClient().execute(httpPost);
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                System.out.println("success,return body = " + EntityUtils.toString(response.getEntity()));
            } else if (statusCode == 204) {
                System.out.println("success");
            } else {
                System.out.println("failed,resp code = " + statusCode+ ",return body = " + EntityUtils.toString(response.getEntity()));
                throw new IOException("request failed");
            }
        } finally {
            response.close();
        }
    }


    /**
     * @Description: 查询订单
     * @author: Hu
     * @since: 2021/1/27 17:37
     * @Param:
     * @return:
     */
    public static void QueryOrder(String order) throws Exception {

        //请求URL
        URIBuilder uriBuilder = new URIBuilder("https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/"+order+"");
        uriBuilder.setParameter("mchid", WechatConfig.MCH_ID);

        //完成签名并执行请求
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.addHeader("Accept", "application/json");
        CloseableHttpResponse response = MyHttpClient.createHttpClient().execute(httpGet);

        try {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                System.out.println("success,return body = " + EntityUtils.toString(response.getEntity()));
            } else if (statusCode == 204) {
                System.out.println("success");
            } else {
                System.out.println("failed,resp code = " + statusCode+ ",return body = " + EntityUtils.toString(response.getEntity()));
                throw new IOException("request failed");
            }
        } finally {
            response.close();
        }
    }

    /**
     * Description:MD5工具生成token
     * @param value
     * @return
     */
    public static String getMD5Value(String value){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] md5ValueByteArray = messageDigest.digest(value.getBytes());
            BigInteger bigInteger = new BigInteger(1 , md5ValueByteArray);
            return bigInteger.toString(16).toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 生成签名
     * @param map
     * @return
     */
    public static String getSignToken(Map<String, Object> map,String apikey) {
        String result = "";
        try {
            List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(map.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, Object>>() {

                @Override
                public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });
            // 构造签名键值对的格式
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Object> item : infoIds) {
                if (item.getKey() != null || item.getKey() != "") {
                    String key = item.getKey();
                    Object val = item.getValue();
                    if (!(val == "" || val == null)) {
                        sb.append(key + "=" + val + "&");
                    }
                }
            }
            sb.append("key="+apikey+"");
            result = sb.toString();
            //进行MD5加密
            result = getMD5Value(result);
        } catch (Exception e) {
            return null;
        }

        return result;
    }

    public static String getXml(Map<String,Object> map){
        StringBuilder builder = new StringBuilder();
        builder.append("<xml>");
            List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(map.entrySet());
            // 构造签名键值对的格式
            for (Map.Entry<String, Object> item : infoIds) {
                if (item.getKey() != null || item.getKey() != "") {
                    String key = item.getKey();
                    Object val = item.getValue();
                    builder.append("<"+key+">");
                    builder.append(val);
                    builder.append("</"+key+">");
                }
            }
            builder.append("</xml>");
        return builder.toString();
    }


}
