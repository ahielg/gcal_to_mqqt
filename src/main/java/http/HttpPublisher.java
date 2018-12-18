package http;

import mqtt.MQTTClient;

/**
 * @author Ahielg
 * @date 2018-12-18
 */
public class HttpPublisher implements MQTTClient {

    private final ItemComm itemComm;

    public HttpPublisher(String url, String user, String pass) {
        itemComm = new ItemComm(url, user, pass);
    }

    public HttpPublisher(String url) {
        itemComm = new ItemComm(url);
    }

    public HttpPublisher() {
        this.itemComm = new ItemComm("http://localhost:8080");
    }

    @Override
    public void publish(String topic, String value) {
        itemComm.updateState(toItemName(topic), value);
    }

    @Override
    public void close() {
    }

    private String toItemName(String mqttName) {
        return mqttName.substring(mqttName.lastIndexOf('/') + 1);
    }
}
