package com.jsy.community.utils.ios;
/**
 * @Description:
 * @author: Hu
 * @since: 2021/5/31 15:45
 * @Param:
 * @return:
 */
public class AppleKeyVo {

    /**
     * 加密算法
     */
    private String kty;

    /**
     * 密钥id
     */
    private String kid;

    /**
     * 用处
     */
    private String use;

    /**
     * 算法
     */
    private String alg;

    /**
     * 公钥参数
     */
    private String n;

    /**
     * 公钥参数
     */
    private String e;

    public String getKty() {
        return kty;
    }

    public void setKty(String kty) {
        this.kty = kty;
    }

    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getAlg() {
        return alg;
    }

    public void setAlg(String alg) {
        this.alg = alg;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    @Override
    public String toString() {
        return "AppleKeyVo{" +
                "kty='" + kty + '\'' +
                ", kid='" + kid + '\'' +
                ", use='" + use + '\'' +
                ", alg='" + alg + '\'' +
                ", n='" + n + '\'' +
                ", e='" + e + '\'' +
                '}';
    }
}
