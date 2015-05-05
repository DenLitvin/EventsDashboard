package hello;

/**
 * Created by denisr on 5/5/2015.
 */

import com.citrix.queue.support.MultiMessageListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class AudioEventListener implements MultiMessageListener<String> {

    private final static Logger logger = LoggerFactory
            .getLogger(AudioEventListener.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ClientUpdater clientUpdater;


    @Override
    public void onMessages(List<String> messages) throws Exception {
        UUID uuid = UUID.randomUUID();
        try {
            logger.info("Events received. size=" + messages.size() + ", uuid=" + uuid);
            clientUpdater.update("test");
            logger.info("Events processed uuid=" + uuid);
        } catch (Exception ex) {
            logger.warn("Events failed. uuid=" + uuid);
            throw ex;
        }
    }


    public void setClientUpdater(ClientUpdater clientUpdater) {
        this.clientUpdater = clientUpdater;
    }
}
