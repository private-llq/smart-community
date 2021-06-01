package com.jsy.community.utils.ios;

/**
 * 苹果用户token中信息
 *
 * @version : 1.0.0
 * @date :   2020/7/17 15:03
 */
public class AppleTokenVo {

    /**
     * 签发机构网址
     */
    private String iss;

    /**
     * bundle id
     */
    private String aud;

    /**
     * 过期时间戳
     */
    private Long exp;

    /**
     * 签发时间
     */
    private Long iat;

    /**
     * user id
     */
    private String sub;

    /**
     * 客户端发出请求时携带的随机串，用于对照
     */
    private String nonce;

    /**
     * 邮箱
     */
    private String email;

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public String getAud() {
        return aud;
    }

    public void setAud(String aud) {
        this.aud = aud;
    }

    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }

    public Long getIat() {
        return iat;
    }

    public void setIat(Long iat) {
        this.iat = iat;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "AppleTokenVo{" +
                "iss='" + iss + '\'' +
                ", aud='" + aud + '\'' +
                ", exp=" + exp +
                ", iat=" + iat +
                ", sub='" + sub + '\'' +
                ", nonce='" + nonce + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
