package mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import util.KeysCons;
import util.MailSender;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;


/**
 * @author Ahielg
 * @date 29/05/2016
 */
public class PahoMQTTClient implements MQTTClient {

    private static final ConnectionDetails DEFAULT_CONNECTION = new ConnectionDetails("tcp://localhost:1883", null, null);
    private MqttClient client;
    private boolean initialized = true;

    public PahoMQTTClient() {
        try {
            ConnectionDetails connectionDetails = loadConn();
            client = new MqttClient(connectionDetails.getUrl(), "gcal", new MemoryPersistence());
            client.connect(getMqttConnectOptions(connectionDetails));
        } catch (MqttException e) {
            sendMail(e);
            throw new RuntimeException("error while connecting");
        }
    }

    private MqttConnectOptions getMqttConnectOptions(ConnectionDetails connectionDetails) {
        MqttConnectOptions options = new MqttConnectOptions();
        if (connectionDetails.getPassword() != null) {
            options.setUserName(connectionDetails.getUsername());
            options.setPassword(connectionDetails.getPassword().toCharArray());
        }
        return options;
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

    private ConnectionDetails loadConn() {
        String firstFile = "/openhab/conf/services/mqtt.cfg";
        String secondFile = "/etc/openhab2/services/mqtt.cfg";

        try {
            FileInputStream inputStream;
            try {
                inputStream = new FileInputStream(firstFile);
            } catch (FileNotFoundException e) {
                System.out.println("File not found:" + firstFile + " tries with " + secondFile);
                inputStream = new FileInputStream(secondFile);
            }
            ///etc/openhab2
            Properties prop = new Properties();
            prop.load(inputStream);

            return new ConnectionDetails(prop.getProperty("broker.clientid", "tcp://localhost:1883"),
                    prop.getProperty("broker.user", null),
                    prop.getProperty("broker.pwd", null));
        } catch (Exception e) {
            System.out.println("File not found:" + secondFile);
            System.out.println("Using default connection");
            return DEFAULT_CONNECTION;
        }
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
    public void close() {
        try {
            initialized = false;
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
