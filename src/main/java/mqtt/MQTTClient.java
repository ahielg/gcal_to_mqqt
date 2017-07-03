package mqtt;

import java.io.Closeable;

/**
 * @author Ahielg
 * @date 29/05/2016
 */
public interface MQTTClient extends Closeable {
    void publish(String topic, String value);
}
