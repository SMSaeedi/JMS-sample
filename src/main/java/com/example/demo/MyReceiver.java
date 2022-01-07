package com.example.demo;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;

import javax.jms.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Queue;

public class MyReceiver {

//    public static void main(String[] args) {
//        try {
//            //1) Create and start connection
//            Properties props = new Properties();
//            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
//            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:8080");
//            InitialContext ctx = new InitialContext(props);
////            InitialContext ctx = new InitialContext();
//            QueueConnectionFactory f = (QueueConnectionFactory) ctx.lookup("myQueueConnectionFactory");
//            QueueConnection con = f.createQueueConnection();
//            con.start();
//            //2) create Queue session
//            QueueSession ses = con.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
//            //3) get the Queue object
//            Queue t = (Queue) ctx.lookup("myQueue");
//            //4)create QueueReceiver
//            QueueReceiver receiver = ses.createReceiver((javax.jms.Queue) t);
//
//            //5) create listener object
//            MyListener listener = new MyListener();
//
//            //6) register the listener object with receiver
//            receiver.setMessageListener(listener);
//
//            System.out.println("Receiver1 is ready, waiting for messages...");
//            System.out.println("press Ctrl+c to shutdown...");
//            while (true) {
//                Thread.sleep(1000);
//            }
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//    }

    public static void main(String[] args) throws URISyntaxException, Exception {
        BrokerService broker = BrokerFactory.createBroker(new URI(
                "broker:(tcp://localhost:8080)"));
        broker.start();
        Connection connection = null;
        try {
            // Producer
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                    "tcp://localhost:8080");
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            Queue queue = (Queue) session.createQueue("customerQueue");
            String payload = "Important Task";
            Message msg = session.createTextMessage(payload);
            MessageProducer producer = session.createProducer((Destination) queue);
            System.out.println("Sending text '" + payload + "'");
            producer.send(msg);

            // Consumer
            MessageConsumer consumer = session.createConsumer((Destination) queue);
            connection.start();
            TextMessage textMsg = (TextMessage) consumer.receive();
            System.out.println(textMsg);
            System.out.println("Received: " + textMsg.getText());
            session.close();
        } finally {
            if (connection != null) {
                connection.close();
            }
            broker.stop();
        }
    }
}