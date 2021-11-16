package com.jsy.community.vo.unionpay;

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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("OpenApiResponseVO{");
        sb.append("response=").append(response.toString());
        sb.append('}');
        return sb.toString();
    }
}
