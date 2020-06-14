package com.hongjun.response;

/**
 * @author hongjun500
 * @date 2020/6/13 23:55
 * Created with 2019.3.2.IntelliJ IDEA
 * Description:通用返回对象---正确信息
 */
public class CommonReturnType {
    /**
     * 对应返回处理结果(成功与否)
     * success
     * fail
     */
    private String status;
    /**
     * 若status=success,则对应data内返回前端需要的json数据
     * 若status=success,则对应data内使用通用的错误码格式
     */
    private Object data;

    /**
     * 定义一个通用的创建方法
     * @param object
     * @return
     */
    public static CommonReturnType create(Object object){
        return CommonReturnType.create(object,"success");
    }

    public static CommonReturnType create(Object object, String status){
        CommonReturnType type = new CommonReturnType();
        type.setData(object);
        type.setStatus(status);
        return type;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
