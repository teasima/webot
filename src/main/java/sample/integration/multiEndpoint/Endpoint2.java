package sample.integration.multiEndpoint;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@MessageEndpoint
public class Endpoint2 {
	
	@ServiceActivator
	public Map hello(String input) throws Exception {
		 ObjectMapper om  = new ObjectMapper();
		 return om.readValue(input, Map.class);
	}

}
