package mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import util.CryptoUtil;
import util.KeysCons;
import util.MailSender;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import static util.KeysCons.MQTT_PASS;
import static util.KeysCons.MQTT_USER;


/**
 * @author Ahielg
 * Date 29/05/2016
 */
public class PahoMQTTClient implements MQTTClient {

    private final MqttClient client;
    private boolean initialized = true;

    public PahoMQTTClient() {
        try {
            ConnectionDetails connectionDetails = loadConn();
            System.out.println("Connecting to MQTT broker: " + connectionDetails.getUrl());
            client = new MqttClient(connectionDetails.getUrl(), "gcal", new MemoryPersistence());
            client.connect(getMqttConnectOptions(connectionDetails));
            System.out.println("Connected successfully");
        } catch (MqttException e) {
            System.err.println("Failed to connect to MQTT broker");
            e.printStackTrace();
            sendMail(e);
            throw new RuntimeException("error while connecting");
        }
    }

    private static ConnectionDetails defaultConnection() {
        try {
            return new ConnectionDetailsBuilder()
                    .setUrl("tcp://192.168.5.55:1883")
                    .setUsername(MQTT_USER)
                    .setPassword(new CryptoUtil().decrypt(MQTT_PASS))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("error while connecting", e);
        }
    }

    private static void sendMail(Throwable throwable) {
        try {

            MailSender.sendMail(KeysCons.PASSWORD, KeysCons.TO, "שגיאה בלוח העברי שלי", getStackTrace(throwable));
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

    private MqttConnectOptions getMqttConnectOptions(ConnectionDetails connectionDetails) {
        MqttConnectOptions options = new MqttConnectOptions();
        if (connectionDetails.getPassword() != null) {
            options.setUserName(connectionDetails.getUsername());
            options.setPassword(connectionDetails.getPassword().toCharArray());
        }
        return options;
    }

    private ConnectionDetails loadConn() {
        String firstFile = "/openhab/conf/services/mqtt.cfg";
        String secondFile = "/etc/openhab2/services/mqtt.cfg";

        try {
            FileInputStream inputStream = openFile(firstFile);
            if (inputStream == null) {
                inputStream = openFile(secondFile);
            }

            if (inputStream == null) {
                System.out.println("Configuration files not found. Using default connection.");
                return defaultConnection();
            }

            Properties prop = new Properties();
            prop.load(inputStream);
            inputStream.close();

            return new ConnectionDetailsBuilder()
                    .setUrl(prop.getProperty("broker.url", "tcp://192.168.5.55:1883"))
                    .setUsername(prop.getProperty("broker.user", MQTT_USER))
                    .setPassword(prop.getProperty("broker.pwd", new CryptoUtil().decrypt(MQTT_PASS)))
                    .build();
        } catch (Exception e) {
            System.out.println("Error loading configuration, using default connection. Error: " + e.getMessage());
            return defaultConnection();
        }
    }


    private FileInputStream openFile(String filename) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filename);
            System.out.println("Using Configuration file:" + filename);
        } catch (FileNotFoundException e) {
            System.out.println("File not found:" + filename);
        }
        return inputStream;
    }


    @Override
    public void publish(String topic, String value) {
        if (!initialized) {
            throw new IllegalStateException("MqttClient is closed");
        }
        try {
            if (value == null) {
                value = "";
            }
            System.out.println("Publishing to " + topic + ": " + value);
            client.publish(topic, new MqttMessage(value.getBytes()));
        } catch (MqttException e) {
            System.err.println("Failed to publish to " + topic);
            e.printStackTrace();
            sendMail(e);
        }
    }

    @Override
    public void close() {
        try {
            initialized = false;
            System.out.println("Disconnecting from MQTT broker...");
            client.disconnect(1000); // Wait up to 1 second for work to complete
            client.close();
            System.out.println("Disconnected.");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
