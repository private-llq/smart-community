package com.jsy.community.callback;


public class FDSearch {
    
    ISAPI isapi = new ISAPI();
    
    /**
     * @return void
     * @Author 91李寻欢
     * @Description 查询剩余空间
     * @Date 2021/5/8 15:04
     * @Param [lUserID]
     **/
    public void getFaceLibSpace(int lUserID){
        isapi.Isapi(lUserID,"GET /ISAPI/Intelligent/FDLib/1/picture/surplusCapacity","");
    }
    
}
