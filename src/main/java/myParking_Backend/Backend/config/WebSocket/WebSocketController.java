package myParking_Backend.Backend.config.WebSocket;

import java.util.*;

import myParking_Backend.Backend.Parking.ResponceEntities.ResponceCities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketController {


    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    //  Αυτό θα το καλεί ο backend όταν θέλει να στείλει δεδομένα στο frontend
    public void broadcastCities(List<ResponceCities> cities) {
             messagingTemplate.convertAndSend("/topic/cities", cities);
    }

    public void broadcastPopularCities(List<ResponceCities> cities){
        messagingTemplate.convertAndSend("/topic/popularcities");
    }
}
