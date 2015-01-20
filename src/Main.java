import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/1/20.
 */
public class Main
{
    public static volatile int messageCounter = 0;
    public static volatile int clientCounter = 0;
    public static volatile HashMap<String,Integer> receivedCounter = new HashMap<String, Integer>();
    public static ArrayList<QuoteSubscriber> quoteSubscribers = new ArrayList<QuoteSubscriber>();
    public static ArrayList<Thread> quoteSubscriberThreads = new ArrayList<Thread>();
    public static int clientNumber = 300;
    public static int totalMessageNumber = 200000;
    public static void main(String[] args)
    {
        for (int i = 0; i < clientNumber; i++) {
            Thread quoteSubscriberThread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    QuoteSubscriber quoteSubscriber = new QuoteSubscriber();
                    quoteSubscriber.subscribe();
                    quoteSubscribers.add(quoteSubscriber);
                }
            });
            quoteSubscriberThreads.add(quoteSubscriberThread);
            quoteSubscriberThread.start();
        }

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true) {
                    if(messageCounter >= totalMessageNumber) {
                        for (int i = 0; i < quoteSubscribers.size(); i++) {
                            QuoteSubscriber quoteSubscriber = quoteSubscribers.get(i);
                            quoteSubscriber.disconnect();
                        }

                        for (int i = 0; i < quoteSubscriberThreads.size(); i++) {
                            Thread thread = quoteSubscriberThreads.get(i);
                            thread.interrupt();
                        }

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        int totalCount = 0;
                        int size = 0;
                        for (String mapKey : Main.receivedCounter.keySet()) {
                            int count = Main.receivedCounter.get(mapKey);

                            totalCount += count;
                            size++;
                        }
                        System.out.println("Total Count : " + totalCount);
                        int idealCount = size * clientNumber;
                        System.out.println("Ideal Message Size : " + idealCount);
                        System.out.println("loss : " + (idealCount - totalCount) + " messages");
                        break;
                    }
                }
                System.exit(0);
            }
        }).start();
    }
}
