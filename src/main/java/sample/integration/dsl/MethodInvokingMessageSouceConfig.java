package sample.integration.dsl;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowDefinition;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.endpoint.MethodInvokingMessageSource;

@Configuration
public class MethodInvokingMessageSouceConfig {
	
	@Bean
	public MessageSource <?> integrationMessageSouce(){
		MethodInvokingMessageSource source = new MethodInvokingMessageSource();
		source.setObject(new AtomicInteger());
		source.setMethodName("getAndIncrement");
		return source;
	}
	
	@Bean
	 public DirectChannel inputChannelMethodInvokingMessageSouceConfig() {
        return new DirectChannel(); 
    }
	
	@Bean
	public IntegrationFlow aMethodInvokingMessageSouceConfigd(){
		return   IntegrationFlows.from(integrationMessageSouce(),c->c.poller(Pollers.fixedRate(1000))).
				channel(inputChannelMethodInvokingMessageSouceConfig() ).
				filter(((Integer p)->p >0)).
				transform(Print::toString).
				channel(MessageChannels.queue(2)).
				
				//handle(System.out::println).

				get();
	}

	static class Print{
		public static String toString(Object o){
			System.out.println(o.toString());
			return o.toString();
		}
	}
}
