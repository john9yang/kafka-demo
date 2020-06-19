package jhclass;

import ch.hsr.geohash.GeoHash;
import jhclass.model.Weather;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class ProducerHiveSample {
    private static String JDBC_DRIVER = "org.apache.hive.jdbc.HiveDriver";
    private static String CONNECTION_URL =
            "jdbc:hive2://sandbox-hdp.hortonworks.com:2181/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2";

    static{
        try {
            Class.forName(JDBC_DRIVER);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ProducerHiveSample sample = new ProducerHiveSample();
        sample.sendWeatherData();
    }

    public void sendWeatherData(){
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Weather weather = null;

        Map<String,Object> props = new HashMap<String,Object>();
        props.put("bootstrap.servers","sandbox-hdp.hortonworks.com:6667");
        props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer","jhclass.serd.JsonSerializer");
        props.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer","jhclass.serd.JsonDeserializer");
        props.put("zk.connect","sandbox-hdp.hortonworks.com:2181");

        String topic = "weather-hive-topic";

        Producer<String,Weather> producer = new KafkaProducer<String, Weather>(props);

        try{
            connection = DriverManager.getConnection(CONNECTION_URL,"hive","hive");
            ps = connection.prepareStatement("select * from weather");
            rs = ps.executeQuery();
            while ( rs.next() ){
                weather = new Weather();
                weather.setLng(rs.getDouble(1));
                weather.setLat(rs.getDouble(2));
                weather.setAvgTmpF(rs.getDouble(3));
                weather.setAvgTmpC(rs.getDouble(4));
                weather.setDate(rs.getString(5));
                if ( Double.valueOf(weather.getLat())!= null && Double.valueOf(weather.getLng())!= null ){
                    GeoHash hash = GeoHash.withBitPrecision(weather.getLat(),weather.getLng(),25);
                    weather.setHashGeo(hash.toBase32());
                }

                producer.send(new ProducerRecord<String, Weather>(topic,"weather",weather));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                if ( rs != null ){
                    rs.close();
                }

                if ( ps != null ){
                    ps.close();
                }

                if ( connection != null ){
                    connection.close();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

            producer.close();
        }
    }
}