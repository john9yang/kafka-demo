package jhclass;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConsumerSample {

    public static void main(String[] args) {
        String topic = "test-topic";

        Map<String,Object> props = new HashMap<String,Object>();
        props.put("bootstrap.servers","localhost:9092");
        props.put("group.id","testGroup1");
        props.put("enable.auto.commit","true");
        props.put("auto.commit.interval.ms","1000");
        props.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");

        Consumer<String,String> consumer = new KafkaConsumer<String,String>(props);
        consumer.subscribe(Arrays.asList(topic));

        while( true ){
            ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(100));
            for(ConsumerRecord<String,String> record : records){
                System.out.printf("partition = %d,offset = %d,key = %s,value = %s%n",record.partition(),record.offset(),record.key(),record.value());
            }
        }
    }
}