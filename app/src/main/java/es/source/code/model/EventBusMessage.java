package es.source.code.model;

/**
 * 实现EventBus的类
 */
public class EventBusMessage {

    //传递的消息
    private String message;

    public EventBusMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

}
