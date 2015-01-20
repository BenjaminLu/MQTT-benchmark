import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class QuoteSubscriber implements MqttCallback
{
    MqttClient client;
    public static Object lock = new Object();
    MemoryPersistence memoryPersistence = new MemoryPersistence();
    public QuoteSubscriber()
    {
    }

    public void subscribe()
    {
        try {
            String randomID = String.valueOf(Math.random());
            client = new MqttClient("tcp://140.120.15.86:1883", randomID,memoryPersistence);
            client.connect();
            client.setCallback(this);
            client.subscribe("tick");
            Main.clientCounter++;
            System.out.println(Main.clientCounter);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect()
    {
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause)
    {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception
    {
        synchronized (lock) {
            if(topic.equals("tick")) {
                System.out.println(message);
                Main.messageCounter++;
                System.out.println(Main.messageCounter);

                String[] detail = message.toString().split(", ");
                String key = detail[0] + detail[3];
                if(Main.receivedCounter.containsKey(key)) {
                    int count = Main.receivedCounter.get(key);
                    count++;
                    Main.receivedCounter.put(key,count);
                } else {
                    Main.receivedCounter.put(key,1);
                }
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token)
    {

    }

}