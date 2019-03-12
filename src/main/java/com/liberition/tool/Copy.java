package com.liberition.tool;

import org.apache.commons.lang3.RandomStringUtils;
import sun.security.krb5.internal.crypto.Des;

import javax.jms.*;

public class Copy {
    protected static void copy(String destinationName, Boolean durable, Connection sourceConnection, Connection destinationConnection) throws JMSException, InterruptedException {

        Integer maxMessageCount = 1000;
        Queue queue = null;
        Topic topic = null;
        Session sourceSession = sourceConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        String subscriberId = RandomStringUtils.randomAlphabetic(5);

        String[] destinationNameParts = destinationName.split(":");
        MessageConsumer consumer;
        if (destinationNameParts[0].equalsIgnoreCase("topic")) {
            topic = sourceSession.createTopic(destinationNameParts[1]);
            if (durable) {
                consumer = sourceSession.createDurableSubscriber(topic, subscriberId);
            } else {
                consumer = sourceSession.createConsumer(topic);
            }
        } else {
            queue = sourceSession.createQueue(destinationNameParts[1]);
            consumer = sourceSession.createConsumer(queue);
        }

        Session destinationSession = destinationConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageProducer producer = destinationSession.createProducer(topic != null ? topic : queue);

        destinationConnection.start();
        sourceConnection.start();

        Integer messageCount = 0;
        while (messageCount < maxMessageCount) {
            Message message = consumer.receive();
            System.out.println("Copying message: " + message + " from " + message.getJMSDestination());
            producer.send(message);
            messageCount++;
        }

        consumer.close();
        producer.close();
        sourceSession.close();
        destinationSession.close();
        sourceConnection.close();
        destinationConnection.close();
    }
}