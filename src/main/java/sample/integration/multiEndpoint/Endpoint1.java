package sample.integration.multiEndpoint;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@MessageEndpoint
public class Endpoint1 {
	
	@ServiceActivator
	public String m(Map input) throws Exception {
		 System.out.println("endpoint1 with map");
		 ObjectMapper om  = new ObjectMapper();
		 return om.writeValueAsString(input) ;
	}
	
	public static void main(String[] args) throws Exception {
		Endpoint1  e = new Endpoint1();
		Map m = new HashMap();
		m.put("a", "value");
		System.out.println(e.m(m)); 
	}
}
