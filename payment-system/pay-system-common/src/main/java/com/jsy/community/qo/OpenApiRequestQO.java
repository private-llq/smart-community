package com.jsy.community.qo;

import com.gnete.api.GneteObject;
import com.gnete.openapi.internal.request.DefaultOpenApiRequest;

import java.util.Map;

/**
 * User: chenhf
 * Date: 2018/10/30
 * Time: 21:25
 */
public class OpenApiRequestQO<T> extends DefaultOpenApiRequest<T> {


    public OpenApiRequestQO(String bizConent){
        super(bizConent);
    }

    public OpenApiRequestQO(GneteObject bizObj) {
        super(bizObj);
    }


    public OpenApiRequestQO(Map<String,Object> bizObj){
        super(bizObj);
    }


}
