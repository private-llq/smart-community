//package com.jsy.community.controller;
//
//import com.alipay.api.AlipayApiException;
//import com.alipay.api.AlipayClient;
//import com.alipay.api.DefaultAlipayClient;
//import com.alipay.api.request.AlipaySystemOauthTokenRequest;
//import com.alipay.api.response.AlipaySystemOauthTokenResponse;
//import me.zhyd.oauth.config.AuthConfig;
//import me.zhyd.oauth.model.AuthCallback;
//import me.zhyd.oauth.request.AuthAlipayRequest;
//import me.zhyd.oauth.request.AuthRequest;
//import me.zhyd.oauth.utils.AuthStateUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * @author chq459799974
// * @description TODO
// * @since 2021-01-07 11:17
// **/
//@RestController
//@RequestMapping("/oauth")
//public class RestAuthController {
//
//	@Value("${alipay.appid}")
//	private String appid;
//
//	@Value("${alipay.app-private-key}")
//	private String privateKey;
//
//	@Value("${alipay.public-key}")
//	private String publicKey;
//
//	@RequestMapping("/render")
//	public void renderAuth(HttpServletResponse response) throws IOException {
//		AuthRequest authRequest = getAuthRequest();
//		response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
//	}
//
//	@RequestMapping("/callback/alipay")
//	public String login(AuthCallback callback) {
//		AuthRequest authRequest = getAuthRequest();
////		String authorizeUrl = authRequest.authorize(AuthStateUtils.createState());
//		return "";
//	}
//
//	private AuthRequest getAuthRequest() {
//		return new AuthAlipayRequest(AuthConfig.builder()
//			.clientId(appid)
//			.clientSecret(privateKey)
//			.alipayPublicKey(publicKey)
//			.redirectUri("http://jsy.free.vipnps.vip/oauth/callback/alipay")
//			.build());
//	}
//
//	@RequestMapping("test")
//	public void test() {
//		AlipayClient alipayClient =  new DefaultAlipayClient( "https://openapi.alipay.com/gateway.do" , appid, privateKey,  "json" , "utf-8", publicKey,  "RSA2" );   //获得初始化的AlipayClient
//		AlipaySystemOauthTokenRequest request =  new  AlipaySystemOauthTokenRequest(); //创建API对应的request类
//		request.setGrantType( "authorization_code" );
//		request.setCode( "87840db5bc744108b7ff1dfdc19dRX15" );
//		AlipaySystemOauthTokenResponse response = null; //通过alipayClient调用API，获得对应的response类
//		try {
//			response = alipayClient.execute(request);
//		} catch (Exception e) {
//			System.out.println("调用异常");
//			e.printStackTrace();
//		}
//		System.out.print(response.getBody());
//	}
//
//}
