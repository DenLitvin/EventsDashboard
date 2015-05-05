package hello;

import com.citrix.queue.support.PollingMessageListenerContainer;
import com.citrix.queue.support.QueueServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic");
		config.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/hello").withSockJS();
	}


    @Bean
    public ClientUpdater getClientUpdater() {
        return new ClientUpdater();
    }

    @Bean
    public RestTemplate getRestTemplate(){
        RestTemplate template = new RestTemplate();
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(15000);
        template.setRequestFactory(factory);
        return template;
    }

    @Bean
    public QueueServiceImpl getQueueService() {
        QueueServiceImpl queueService = new QueueServiceImpl();
        queueService.setRestTemplate(getRestTemplate());
        List<HttpMessageConverter<?>> list = new ArrayList<>();
        list.add(new StringHttpMessageConverter());
        queueService.setMessageConverters(list);
        return queueService;
    }

    @Bean
    public PollingMessageListenerContainer getEventListenerContainer(){
        PollingMessageListenerContainer container = new PollingMessageListenerContainer();
        container.setQueueName("audio.bridge.event+BEC");
        container.setQueueService(getQueueService());
        container.setResponseType(java.lang.String.class);
        container.setLogLevel("INFO");
        container.setTtl(3000l);
        AudioEventListener listener = new AudioEventListener();
        listener.setClientUpdater(getClientUpdater());
        container.setMultiMessageListener(listener);
        container.setServiceAddresses("queue1rc1svc.qai.expertcity.com:80");
        container.setNumTasks(1);
        container.setTaskPeriod(3000);
        container.setTransactional(false);
        ScheduledExecutorFactoryBean executor = new ScheduledExecutorFactoryBean();
        executor.setPoolSize(10);
        executor.setContinueScheduledExecutionAfterException(true);
        container.setExecutor(new ScheduledThreadPoolExecutor(10));
        container.setMaxItems(100);
        return container;

    }

}