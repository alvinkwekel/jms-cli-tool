package com.liberition.tool;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.IntStream;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

public class Sender {

    protected static void send(String destinationName, String messageFilePath, String headerString, Connection connection, Integer messageCount) throws Exception {

        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        String[] destinationNameParts = destinationName.split(":");
        MessageProducer producer;
        if (destinationNameParts.length < 2) {
            Queue queue = session.createQueue(destinationNameParts[0]);
            producer = session.createProducer(queue);
        } else if (destinationNameParts[0].equalsIgnoreCase("topic")) {
            Topic topic = session.createTopic(destinationNameParts[1]);
            producer = session.createProducer(topic);
        } else {
            Queue queue = session.createQueue(destinationNameParts[1]);
            producer = session.createProducer(queue);
        }

        List<String> lines = Files.readAllLines(Paths.get(messageFilePath));
        String content = String.join("\n", lines);

        Message message = session.createTextMessage(content);

        if (headerString != null) {
            Map<String, String> headers = new HashMap();
            for (String nameValuePairString : headerString.split(",")) {
                String[] nameValuePair = nameValuePairString.split("=");
                headers.put(nameValuePair[0], nameValuePair[1]);
            }

            headers.forEach((k, v) -> {
                try {
                    message.setStringProperty(k, v);
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        IntStream.range(0, messageCount).forEach($ -> {
            try {
                producer.send(message);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        });

        producer.close();
        session.close();
        connection.close();
    }
}
