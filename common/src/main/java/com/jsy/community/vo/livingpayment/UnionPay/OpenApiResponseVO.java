package com.jsy.community.vo.livingpayment.UnionPay;

import com.gnete.openapi.internal.response.OpenApiResponse;

/**
 * User: chenhf
 * Date: 2018/11/2
 * Time: 10:23
 */
public class OpenApiResponseVO extends OpenApiResponse {
    private static final long serialVersionUID = -8350975509661157718L;
    private BizResponseContentVO response;

    public BizResponseContentVO getResponse() {
        return response;
    }

    public void setResponse(BizResponseContentVO response) {
        this.response = response;
    }
}
