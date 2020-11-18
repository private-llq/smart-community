package com.jsy.community.utils;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.jsy.community.intercepter.AuthorizationInterceptor;
import com.jsy.community.vo.UserAuthVo;
import com.jsy.community.vo.UserInfoVo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * jwt工具类
 */
@ConfigurationProperties(prefix = "jsy.jwt")
@Component
@Data
@Slf4j
@ConditionalOnProperty(value = "jsy.module.name", havingValue = "web")
public class JwtUtils {
	
	private String secret;
	private long expire;
	private String header;
	
	/**
	 * 生成jwt token
	 */
	public UserAuthVo generateToken(UserInfoVo infoVo) {
		Date nowDate = new Date();
		//过期时间
		Date expireDate = new Date(nowDate.getTime() + expire * 1000);
		
		String token = Jwts.builder()
			.setHeaderParam("typ", "JWT")
			.setSubject(infoVo.getId().toString())
			.setIssuedAt(nowDate)
			.setExpiration(expireDate)
			.signWith(SignatureAlgorithm.HS512, secret)
			.compact();
		return new UserAuthVo(token, LocalDateTimeUtil.of(expireDate), infoVo);
	}
	
	public Claims getClaimByToken(String token) {
		try {
			return Jwts.parser()
				.setSigningKey(secret)
				.parseClaimsJws(token)
				.getBody();
		} catch (Exception e) {
			log.error("validate is token error ", e);
			return null;
		}
	}
	
	/**
	 * token是否过期
	 *
	 * @return true：过期
	 */
	public boolean isTokenExpired(Date expiration) {
		return expiration.before(new Date());
	}
	
	@Nullable
	public static Long getUserId() {
		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
			.getRequest();
		return (Long) request.getAttribute(AuthorizationInterceptor.USER_KEY);
	}
}
