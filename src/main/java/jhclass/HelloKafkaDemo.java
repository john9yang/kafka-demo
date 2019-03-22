package jhclass;

import jhclass.bean.HelloKafka;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HelloKafkaDemo {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-kafka.xml");
        HelloKafka helloKafka = (HelloKafka) context.getBean("hello");
        helloKafka.sayHi();
    }
}
