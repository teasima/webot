package sample.integration.httpandtransformer;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@MessageEndpoint
public class ReplyEndpointOfInoubGateway {
	
	@Autowired
	private DirectChannel replyToInboundGateway;
	
	@ServiceActivator
	public Message<Map> m(Message<Map> inMessage ) throws Exception {
		GenericMessage<String> message = new GenericMessage<String>("fuck",inMessage.getHeaders());
		replyToInboundGateway.send(message);
		return inMessage;
	}
	
	
}
