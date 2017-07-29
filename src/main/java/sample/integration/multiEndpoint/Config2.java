package sample.integration.multiEndpoint;

import java.io.File;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.DefaultFileNameGenerator;
import org.springframework.integration.file.FileNameGenerator;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.integration.transformer.ObjectToStringTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import sample.integration.httpandtransformer.OAMessage;

@Component
public class Config2 {
	
	 

	@Bean
	public DirectChannel httpRequest3(){
		return new DirectChannel();
	}
	

	@Bean
	public DirectChannel httpRequest4(){
		return new DirectChannel();
	}
	

	@Bean
	public DirectChannel httpRequest5(){
		return new DirectChannel();
	}
	
	
	@Bean
	public DirectChannel invocationChannel(){
		return new DirectChannel();
	}
	
	@Bean
	public DirectChannel invocationChannel2(){
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow inbound() {
		return IntegrationFlows.from(Http.inboundGateway("/multiEndpoint").requestMapping(m -> m.methods(HttpMethod.POST))
				.requestPayloadType(Map.class)).channel(httpRequest3()).handle(new Endpoint1()).channel(httpRequest4()).handle(new Endpoint2()).transform(new ObjectToJsonTransformer()).handle(fileWriter()).get();
	}
	
//	@Bean
//	public IntegrationFlow inbound2() {
//		return IntegrationFlows.from("invocationChannel").transform(new ObjectToStringTransformer() ).channel(invocationChannel2()).handle(fileWriter()).get();
//	}

	
	@Transformer(inputChannel = "httpRequest2", outputChannel = "invocationChannel")
	public OAMessage buildRequest2(Message<String> msg) {
		OAMessage oam = new OAMessage();
		oam.setConent(msg.getPayload());
		return oam;
	}
	
//	public customObjectMapper(){ 
//		return ObjectMapperFactory.getMapper();
//	}
	
	@Bean
	public FileWritingMessageHandler fileWriter() {
		FileWritingMessageHandler writer = new FileWritingMessageHandler(
				new File("./"));
		writer.setExpectReply(false);
		writer.setFileNameGenerator(new FileNameGenerator() {
            @Override
            public String generateFileName(Message<?> message) {
                    return "ss.txt";
            }
        });
		
		return writer;
	}
}
