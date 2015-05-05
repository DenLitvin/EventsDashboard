package hello;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class ClientUpdater {

    @Autowired
    private SimpMessagingTemplate template;

    public void update(String greeting) {
        this.template.convertAndSend("/topic/events", new Greeting("Hello"+ greeting));
    }


}
