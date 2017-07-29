package sample.integration;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
@Configuration 
public class KafkaScope {
//	@Bean
//    @ServiceActivator(inputChannel = "correctChannel")
//    public KafkaProducerMessageHandler<String, String> handler() throws Exception{
//        return new KafkaProducerMessageHandler<String, String>(kafkaContext());
//    } 
//
//@Bean
//public KafkaProducerContext<String, String> kafkaContext () throws Exception{
//    KafkaProducerContext<String, String> ctx = new KafkaProducerContext<String, String>();
//    ctx.setProducerConfigurations(Collections.singletonMap("test", config()));
//    return ctx;
//}
//
//@Bean
//public ProducerConfiguration<String, String> config() throws Exception{
//    return new ProducerConfiguration<String, String>(metaData(), producer().getObject());
//}
//
//@Bean
//public Encoder<String> encoder(){
//    return new StringEncoder<String>();
//}
//
//@Bean
//public ProducerMetadata<String, String> metaData(){
//    ProducerMetadata<String, String> meta = new ProducerMetadata<String,String>("test");
//    meta.setValueClassType(String.class);
//    meta.setKeyClassType(String.class);
//    meta.setValueEncoder(encoder());
//    meta.setKeyEncoder(encoder());
//    return meta;
//}
//
//@Bean
//public ProducerFactoryBean<String, String> producer(){
//    return new ProducerFactoryBean<String,String>(metaData(), "localhost:9092");
//}
}
