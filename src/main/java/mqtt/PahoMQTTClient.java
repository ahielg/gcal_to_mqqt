package mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import util.KeysCons;
import util.MailSender;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * @author Ahielg
 * @date 29/05/2016
 */
public class PahoMQTTClient implements MQTTClient {

    private MqttClient client;
    private boolean initialized = true;

    public PahoMQTTClient() {
        try {
            client = new MqttClient("tcp://localhost:1883", "gcal", new MemoryPersistence());
            client.connect();
        } catch (MqttException e) {
            sendMail(e);
            throw new RuntimeException("error while connecting");
        }
    }

    private static void sendMail(Throwable throwable) {
        try {

            MailSender.sendMail(KeysCons.PASSWORD, KeysCons.TO, "בעיה עם עדכוני חגים", getStackTrace(throwable));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    @Override
    public void publish(String topic, String value) {
        if (!initialized) {
            throw new IllegalStateException("MqttClient is closed");
        }
        try {
            client.publish(topic, new MqttMessage(value.getBytes()));
        } catch (MqttException e) {
            e.printStackTrace();
            sendMail(e);

        }
    }

    @Override
    public void close() throws IOException {
        try {
            initialized = false;
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
