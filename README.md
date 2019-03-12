mvn clean package

./jms-cli-tool.sh -s -d queue:TEST -f /path/to/message.xml --brokerUrl localhost:61616

./jms-cli-tool.sh -c -d queue:TEST --brokerUrl tcp://talendtest.brg.local:61616 --sourceBrokerUrl tcp://talendqa01.brg.local:61616