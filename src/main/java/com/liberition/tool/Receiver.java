package com.liberition.tool;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

public class Receiver {
  protected static void receive(String destinationName, Integer maxMessageCount, Boolean durable, Connection connection) throws JMSException, InterruptedException {

    Queue queue = null;
    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

    String[] destinationNameParts = destinationName.split(":");
    MessageConsumer consumer;
    if (destinationNameParts[0].equalsIgnoreCase("topic")) {
      Topic topic = session.createTopic(destinationNameParts[1]);
      if (durable) {
        consumer = session.createDurableSubscriber(topic, "subscriber1");
      } else {
        consumer = session.createConsumer(topic);
      }
    } else {
      queue = session.createQueue(destinationNameParts[1]);
      consumer = session.createConsumer(queue);
    }

    //consumer.setMessageListener(new Listener("consumer1"));
    connection.start();
    
    Integer messageCount = 0;
    while (messageCount < maxMessageCount) {
      Message message = consumer.receive();
      if (message instanceof TextMessage) {
        TextMessage textMessage = (TextMessage) message;
        String text = textMessage.getText();
        System.out.println("Received: " + text + " on " + message.getJMSDestination());
      } else {
        System.out.println("Received: " + message + " on " + message.getJMSDestination());
      }
      messageCount++;
    }
    consumer.close();
    session.close();
    connection.close();

  }
}

/*class Listener implements MessageListener {

  private String consumerName;

  public Listener(String consumerName) {
    this.consumerName = consumerName;
  }

  @Override
  public void onMessage(Message message) {
    try {
      if (message instanceof TextMessage) {
        TextMessage textMessage = (TextMessage) message;
        String text = textMessage.getText();
        System.out.println("Received: " + text + " on " + message.getJMSDestination());
      } else {
        System.out.println("Received: " + message + " on " + message.getJMSDestination());
      }

      // message.acknowledge();
    } catch (JMSException e) {
      e.printStackTrace();
    }
    throw new RuntimeException("horrible");
  }
}*/