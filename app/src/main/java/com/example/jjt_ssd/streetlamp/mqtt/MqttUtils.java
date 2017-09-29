package com.example.jjt_ssd.streetlamp.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttUtils {

    private static String host = "tcp://192.168.1.187:61613";
    private static String userName = "gkuser";
    private static String passWord = "gk8888";
    private static MqttClient client;
    private static MqttTopic topic;
    private static MqttMessage message;
    private static String publishClientId = "CallbackServer";
    private static String subscribeClientId = "CallbackClient";

    /**
     * 发布主题或指令信息
     *
     * @param topicParam 主题
     * @param msg        指令信息
     * @throws MqttException
     * @author jk97
     * 2017年8月23日
     */
    public static void publish(String topicParam, String msg,final MqttHandleListener listener) throws MqttException {
        // host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，  
        // MemoryPersistence设置clientid的保存形式，默认为以内存保存  
        client = new MqttClient(host, publishClientId, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，  
        // 这里设置为true表示每次连接到服务器都以新的身份连接  
        options.setCleanSession(true);
        // 设置连接的用户名  
        options.setUserName(userName);
        // 设置连接的密码  
        options.setPassword(passWord.toCharArray());
        // 设置超时时间 单位为秒  
        options.setConnectionTimeout(10);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制  
        options.setKeepAliveInterval(20);
        client.setCallback(new MqttCallback() {
            public void connectionLost(Throwable cause) {
                // //连接丢失后，一般在这里面进行重连    
                System.out.println("连接丢失----------"+cause);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken arg0) {
                //publish后会执行到这里
                System.out.println("publish后执行些代码，" + arg0.getMessageId());
            }

            @Override
            public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
                //subscribe后得到的消息会执行到这里面
                System.out.println("publish messageArrived");
                System.out.println(arg0);
                System.out.println(arg1.getPayload());
                listener.MqttSuccedListener(new String(arg1.getPayload()));
            }
        });
        topic = client.getTopic(topicParam);
        message = new MqttMessage();
        message.setQos(1);
        message.setRetained(true);
        System.out.println(message.isRetained() + "------ratained状态");
         
        /* //组装数据
         Date date = new Date();
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
         String dateString = sdf.format(date);
         JSONObject json = new JSONObject();//用JSONObject封装
         json.put("clientId", client.getClientId());
         json.put("time", dateString);
         json.put("lightOpen", 1);*/
        if (!"".equals(msg)) {
            message.setPayload(msg.getBytes());
        }
        if (!client.isConnected())
        client.connect(options);
        MqttDeliveryToken token = topic.publish(message);
        token.waitForCompletion();
        System.out.println(token.isComplete() + "========");
    }

    /**
     * 订阅
     *
     * @param topicParams 主题
     * @param qos         服务质量
     * @throws MqttException
     * @author jk97
     * 2017年8月23日
     */
    public static void subscribe(String[] topicParams, int[] qos, final MqttHandleListener listener) throws MqttException {
        //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，
        //MemoryPersistence设置clientid的保存形式，默认为以内存保存    
        client = new MqttClient(host, subscribeClientId, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，  
        //这里设置为true表示每次连接到服务器都以新的身份连接    
        options.setCleanSession(true);
        //设置连接的用户名   
        options.setUserName(userName);
        //设置连接的密码   
        options.setPassword(passWord.toCharArray());
        // 设置超时时间 单位为秒   
        options.setConnectionTimeout(10);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制    
        options.setKeepAliveInterval(20);
        //回调  
        client.setCallback(new MqttCallback() {
            public void connectionLost(Throwable cause) {
                // //连接丢失后，一般在这里面进行重连    
                System.out.println("connectionLost----------");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken arg0) {
                //publish后会执行到这里
                System.out.println("MqttReceive publish后会执行到这里");
                System.out.println(arg0.getMessageId());
            }

            @Override
            public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
                //subscribe后得到的消息会执行到这里面
                System.out.println("成功订阅主题：" + arg0);
                //System.out.println("收到来自主题为："+arg0+"的消息，以下是消息内容：");
                String data1 = new String(arg1.getPayload());
                System.out.println(data1);
                listener.MqttSuccedListener(data1);
                //在此作数据处理
                try {
//					MqttHandler.mqttSubscribeHandle(arg0, arg1);
                } catch (Exception e) {
                    String data = new String(arg1.getPayload());
                    System.out.println("接收到的数据为：" + data);
                    System.out.println("无法解析数据！");
                }

            }
        });
        //链接
        if (!client.isConnected())
        client.connect(options);
        //订阅
        client.subscribe(topicParams, qos);
        //client.unsubscribe("gk/dev/dev01/send");
        //client.close();
    }

    /**
     * 取消订阅
     *
     * @param topicParams 主题
     * @throws MqttException
     * @author jk97
     * 2017年8月23日
     */
    public static void unsubscribe(String[] topicParams) throws MqttException {
        //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，
        //MemoryPersistence设置clientid的保存形式，默认为以内存保存    
        client = new MqttClient(host, subscribeClientId, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，  
        //这里设置为true表示每次连接到服务器都以新的身份连接    
        options.setCleanSession(true);
        //设置连接的用户名   
        options.setUserName(userName);
        //设置连接的密码   
        options.setPassword(passWord.toCharArray());
        // 设置超时时间 单位为秒   
        options.setConnectionTimeout(10);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制    
        options.setKeepAliveInterval(20);
        //回调  
        client.setCallback(new MqttCallback() {
            public void connectionLost(Throwable cause) {
                // //连接丢失后，一般在这里面进行重连    
                System.out.println("connectionLost----------");

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken arg0) {
            }

            @Override
            public void messageArrived(String arg0, MqttMessage arg1) throws Exception {

            }
        });
        //链接  
        client.connect(options);
        //订阅  
        client.unsubscribe(topicParams);
    }

    /**
     * 连接
     *
     * @param topicParams 主题
     * @param qos         服务质量
     * @throws MqttException
     * @author jk97
     * 2017年8月24日
     */
    public static void connectClient(String[] topicParams, int[] qos) throws MqttException {
        //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，
        //MemoryPersistence设置clientid的保存形式，默认为以内存保存    
        client = new MqttClient(host, subscribeClientId, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，  
        //这里设置为true表示每次连接到服务器都以新的身份连接    
        options.setCleanSession(true);
        //设置连接的用户名   
        options.setUserName(userName);
        //设置连接的密码   
        options.setPassword(passWord.toCharArray());
        // 设置超时时间 单位为秒   
        options.setConnectionTimeout(10);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制    
        options.setKeepAliveInterval(20);
        //回调  
        client.setCallback(new MqttCallback() {
            public void connectionLost(Throwable cause) {
                // //连接丢失后，一般在这里面进行重连    
                System.out.println("connectionLost----------");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken arg0) {
                //publish后会执行到这里
                System.out.println("MqttReceive 3333333333333333333");
                System.out.println(arg0.getMessageId());
            }

            @Override
            public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
                //subscribe后得到的消息会执行到这里面
                System.out.println();
//				MqttHandler.mqttSubscribeHandle(arg0, arg1);

            }
        });
        //链接  
        client.connect(options);
    }

    public static void disconnect() {
        try {
            if (client != null) {
                client.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
