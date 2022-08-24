package usyd.mingyi.animalcare.utils;

public enum ReturnCode {
    /**操作成功**/
    RC200(200,"success"),
    /**操作失败**/
    RC999(999,"fail");


    /**自定义状态码**/
    private final int code;
    /**自定义描述**/
    private final String message;

    ReturnCode(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
