package com.jsy.community.config.web;

public class AlipayConfig {
    public static String APPID = "2016110100783947";
    public static String RSA_PRIVATE_KEY = "MIIEwAIBADANBgkqhkiG9w0BAQEFAASCBKowggSmAgEAAoIBAQCsudPh1Xn/miQm2U3vd754/b0Z3WuvdIW/pDQwWSp8gAMkzqrhXzizKEuP63/I0RUXEZJDTcN6mF7+GyO8ik0eXX/hSW09m+shGiJZ7tFC8uHg2BcRgNA5wiHa8viqbWqTn9eB52Ei5oWznNLD6yIBRD2stKr36BiZ5IZxSf3ybjF66fvaGwVUzjW/fcspHa2LpqIo1BCqGnM1wtxpFsOH3kY4ilzAluwHELXQQELSUvu96C4BNq6e3tqth48dwWfrYxomW+8COumTnt5xrDtkUYsI95Rb8+yT9EkDuuyZ6OgDBMXAIQFKyXn0iMrAQw8pgfe0FFHiJkIs1J+ELqH3AgMBAAECggEBAI/ZIzDNTREmGj2QdGue2i6Bg19rHCe/bzQvWJd7avrM0dFxnLadSudKA8QUaROhrWLM63bJ6KbNBy+xAo4e0CMd00aYlDXfCG9FCJ7FWdnb1WogDPYyxTeVCgUCnT2yajQPxrcVL7yVJ3xyesVXqbZMUuDmhgx4aySfQahP6wJeiXtTf55eOddwhpzOgVFC6g0ElfOJdm8BRjlH93jYR7XycvmCetxDO8w436tjr//eNQZyVn5ua0JK9u35yasF62cId1JM2vrL00l5uEobuwebrm15I4rUw7XzxlhXnm4M7DI+Z+s1tt+NtrTTpHAVcjGyRPIUSVvxrB5BjfxLYIECgYEA8e1L3eRZtCJZ7sytOnyvfb1XHhR97pQK/4W5A6Whhd9HfKmpDMm336716p5BJ7V7BXjBuURJDU8p8TiI8TJ6a88wUbumzNRRrE0ZuFVEyR1aoI7eCjaOJgOS1XCroO+LvdAk2OVh+p6r8RFG/3gwUUPyGsgVxBRpXagpoboL0ucCgYEAtsYCjVG3Eot3WCpOS16uj31Xs2Uuqye3XHLw3yhIff/4P64vqI/S1ZAywKs8a9OWqv8BD13EEMne7o5DTeD/eTnN93BVzPKcm5oY/+UJ+LTDN/TSCPRyjxx0wW8Vt74MN6eT1l7k6he6ceSdloHBfnfhXnye34cHzoyFaTpN5nECgYEAtwW6m4ehhXFncA1QECs76HhRa+Q80T0WhPD5YxTu+YRNZLZFufLxNDv8tx46VHwrMZyFkk9ipLr7FA6dKzvw+ZapC2xXaCZIE33Qs29utmeKpGTw/fD/4Spa7zZp4TrYESVieuQuF5fqpFlZyPN4OkKY6kHslYaLn9sUg9FoGIkCgYEAqvlLxdrdALIKMCVTHFozvPKpqZ1LLLx4YDj9biOGvKdIGHnfBzWTKkg70dPdL5i85rSozzENig7tRgc6mo2RLluOeptRB+kj6sMaA7bDvy2JOL0CBXJ1/73E0nYRwNI9J4rRVsVqKjmMJaKQcAxtbtrBrdux2rO779GvsyHhYWECgYEAzrvVk+6BO/FSrL4IVqHsM9FYEyYIdKDMsHHKe5XzSkLLQiPsEnCyNrDwRmqePBlG6+8vN3ewDwUAsSgi2I4Sg+sxf/pQr2fG6t+dpE56ZApXuuomYhdV9dR+fKLbfTaNVRkwfmcIR0xHu20CPqXjQNy/zoP0cO6Vx2Un57FVscw=";
    public static String notify_url = "http://5h3cvg.natappfree.cc/api/v1/payment/AlipayPhoneH5/returnPay";
    public static String return_url = "http://isi3pz.natappfree.cc/#/";
    public static String URL = "https://openapi.alipaydev.com/gateway.do";
    public static String CHARSET = "UTF-8";
    public static String FORMAT = "json";
    public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoBdWoEJFRZIF4GlzDeQHY7cH+yXO+OeW20eQU/RTtMbEgbN1aJn/4pIqx6UgD1SCVJcUhXetlIBIKSncIqt5m/c+bsoZXZkiKmF8UPDu5Gxe58JyrFkdZgNpci4BeIgNNczj00UJ1JVBvJMKomjEKw8fNuZGRtoCdg1wHurUKLeV3YJTGbYTF8JuSXpqJ0Bjq0AR5GQjWFZUkx/p5zsKT7npTJeLCOUIYAWbDMivZbvTfydeYQBWbhzJAENuh5EQdRqR41gAPzeI+Pfe2rbZiCOCtaLnwD4Me3vRBBEqAO5synWwWwkXEhAHHC/4xqMGcDPHsllf5/pXxDp0O4wW4wIDAQAB";
    public static String log_path = "/log";
    public static String SIGNTYPE = "RSA2";


    public AlipayConfig() {
    }
}