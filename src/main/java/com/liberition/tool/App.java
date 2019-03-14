package com.liberition.tool;

import javax.jms.Connection;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.commons.lang3.RandomStringUtils;
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
        options.addOption("h", true, "headers");

        options.addOption("s", false, "sender");
        options.addOption("c", false, "copy");
        options.addOption("r", false, "receiver");

        options.addOption("e", false, "receiver exception");
        options.addOption(null, "brokerUrl", true, "broker URL");
        options.addOption(null, "user", true, "user");
        options.addOption(null, "password", true, "password");
        options.addOption(null, "sourceBrokerUrl", true, "broker URL");
        options.addOption(null, "sourceUser", true, "user");
        options.addOption(null, "sourcePassword", true, "password");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String destinationName = cmd.getOptionValue("d", "queue:TEST");
        Integer messageCount = Integer.parseInt(cmd.getOptionValue("n", "1"));
        Boolean durable = cmd.hasOption("u");

        Boolean receiver = cmd.hasOption("r");
        Boolean sender = cmd.hasOption("s");
        Boolean copy = cmd.hasOption("c");

        String messageFilePath = cmd.getOptionValue("f");
        String headers = cmd.getOptionValue("h");
        String brokerUrl = cmd.getOptionValue("brokerUrl", "tcp://localhost:61616");
        String user = cmd.getOptionValue("user", "admin");
        String password = cmd.getOptionValue("password", "admin");
        String sourceBrokerUrl = cmd.getOptionValue("sourceBrokerUrl", "tcp://localhost2:61616");
        String sourceUser = cmd.getOptionValue("sourceUser", user);
        String sourcePassword = cmd.getOptionValue("sourcePassword", password);

        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(1);
        redeliveryPolicy.setInitialRedeliveryDelay(1);
        redeliveryPolicy.setRedeliveryDelay(1);

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(brokerUrl);
        connectionFactory.setUserName(user);
        connectionFactory.setPassword(password);
        connectionFactory.setRedeliveryPolicy(redeliveryPolicy);
        Connection connection = connectionFactory.createConnection();
        connection.setClientID(RandomStringUtils.randomAlphabetic(5));

        if (sender) {
            Sender.send(destinationName, messageFilePath, headers, connection, messageCount);
        } else if (copy) {
            ActiveMQConnectionFactory sourceConnectionFactory = new ActiveMQConnectionFactory();
            sourceConnectionFactory.setBrokerURL(sourceBrokerUrl);
            sourceConnectionFactory.setUserName(sourceUser);
            sourceConnectionFactory.setPassword(sourcePassword);
            sourceConnectionFactory.setRedeliveryPolicy(redeliveryPolicy);
            Connection sourceConnection = sourceConnectionFactory.createConnection();
            sourceConnection.setClientID(RandomStringUtils.randomAlphabetic(5));
            Copy.copy(destinationName, durable, sourceConnection, connection);
        } else
            Receiver.receive(destinationName, messageCount, durable, connection);
    }
}
