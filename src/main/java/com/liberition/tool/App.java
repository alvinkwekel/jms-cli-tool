package com.liberition.tool;

import javax.jms.Connection;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

public class App {

  public static void main(String[] args) throws Exception {
    Options options = new Options();
    options.addOption("d", true, "destination name");
    options.addOption("n", true, "number of messages");
    options.addOption("u", false, "durable");
    options.addOption("f", true, "message file path");
    options.addOption("s", false, "sender");
    options.addOption("e", false, "receiver exception");
    options.addOption(null, "brokerUrl", true, "broker URL");
    options.addOption(null, "user", true, "user");
    options.addOption(null, "password", true, "password");

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);

    String destinationName = cmd.getOptionValue("d", "queue:TEST");
    Integer messageCount = Integer.parseInt(cmd.getOptionValue("n", "10"));
    Boolean durable = cmd.hasOption("u");
    Boolean sender = cmd.hasOption("s");
    String messageFilePath = cmd.getOptionValue("f");
    String brokerUrl = cmd.getOptionValue("brokerUrl", "localhost:62616");
    String user = cmd.getOptionValue("user", "admin");
    String password = cmd.getOptionValue("password", "admin");
    
    RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
    redeliveryPolicy.setMaximumRedeliveries(1);
    redeliveryPolicy.setInitialRedeliveryDelay(1);
    redeliveryPolicy.setRedeliveryDelay(1);

    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
    connectionFactory.setBrokerURL("tcp://" + brokerUrl);
    connectionFactory.setUserName(user);
    connectionFactory.setPassword(password);
    connectionFactory.setRedeliveryPolicy(redeliveryPolicy);
    Connection connection = connectionFactory.createConnection();
    connection.setClientID("client1");

    if (sender) {
      Sender.send(destinationName, messageFilePath, connection);
    } else
      Receiver.receive(destinationName, messageCount, durable, connection);
  }
}
