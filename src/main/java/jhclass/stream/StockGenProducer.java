package jhclass.stream;

import jhclass.model.Trade;
import jhclass.serde.JsonDeserializer;
import jhclass.serde.JsonSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.streams.StreamsConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;


public class StockGenProducer {

    public static KafkaProducer<String, Trade> producer = null;

    public static void main(String[] args) throws Exception {

        System.out.println("Press CTRL-C to stop generating data");

        // add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Shutting Down");
                if (producer != null)
                    producer.close();
            }
        });

        JsonSerializer<Trade> tradeSerializer = new JsonSerializer<>();
        JsonDeserializer<Trade> tradeDeserializer = new JsonDeserializer<>();

        // Configuring producer
//        Properties props;
//        if (args.length==1)
//            props = LoadConfigs.loadConfig(args[0]);
//        else
//            props = LoadConfigs.loadConfig();

        Properties props = new Properties();
//        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stockgenproducer");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,Constants.BROKER);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", tradeSerializer.getClass().getName());
        //props.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        //props.put("value.deserializer",tradeDeserializer.getClass().getName());
        props.put("zk.connect","192.168.56.101:2181");
        props.put("auto.create.topics.enable","false");

        // Starting producer
        producer = new KafkaProducer<>(props);

        // initialize
        Random random = new Random();
        long iter = 0;

        Map<String, Integer> prices = new HashMap<>();

        for (String ticker : Constants.TICKERS)
            prices.put(ticker, Constants.START_PRICE);

        // Start generating events, stop when CTRL-C

        while (true) {
            iter++;
            for (String ticker : Constants.TICKERS) {
                double log = random.nextGaussian() * 0.25 + 1; // random var from lognormal dist with stddev = 0.25 and mean=1
                int size = random.nextInt(100);
                int price = prices.get(ticker);

                // flunctuate price sometimes
                if (iter % 10 == 0) {
                    price = price + random.nextInt(Constants.MAX_PRICE_CHANGE * 2) - Constants.MAX_PRICE_CHANGE;
                    prices.put(ticker, price);
                }

                Trade trade = new Trade("ASK",ticker,(price+log),size);
                // Note that we are using ticker as the key - so all asks for same stock will be in same partition
                ProducerRecord<String, Trade> record = new ProducerRecord<>(Constants.STOCK_TOPIC, ticker, trade);

                System.out.println("send:"+record);
                producer.send(record, (RecordMetadata r, Exception e) -> {
                    if (e != null) {
                        System.out.println("Error producing events");
                        e.printStackTrace();
                    }
                });
                //producer.send(record);

                // Sleep a bit, otherwise it is frying my machine
                Thread.sleep(Constants.DELAY);
            }
        }
    }
}