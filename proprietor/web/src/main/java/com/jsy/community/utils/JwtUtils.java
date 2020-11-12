package com.jsy.community.utils;

import cn.hutool.core.date.DateUtil;
import com.jsy.community.vo.UserInfoVo;
import com.jsy.community.vo.UserLoginVo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * jwt工具类
 */
@ConfigurationProperties(prefix = "jsy.jwt")
@Component
@Data
public class JwtUtils {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private String secret;
	private long expire;
	private String header;
	
	/**
	 * 生成jwt token
	 */
	public UserLoginVo generateToken(UserInfoVo infoVo) {
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
		return new UserLoginVo(token, DateUtil.toLocalDateTime(expireDate), infoVo);
	}
	
	public Claims getClaimByToken(String token) {
		try {
			return Jwts.parser()
				.setSigningKey(secret)
				.parseClaimsJws(token)
				.getBody();
		} catch (Exception e) {
			logger.debug("validate is token error ", e);
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
}
