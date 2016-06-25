package com.drawers;

import com.drawers.dao.ChatConstant;
import com.drawers.dao.MqttChatMessage;
import com.drawers.dao.packets.MqttChat;
import com.drawers.dao.packets.MqttProviderManager;
import com.drawers.dao.packets.listeners.NewMessageListener;
import org.drawers.bot.listener.ConnectionStateListener;
import org.drawers.bot.mqtt.DrawersBot;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class EchoBot
{
    // Obtain client id and password from https://broker.sandwitch.in/botAdmin
    static final private String clientId = "REPLACE_ME";
    static final private String password = "REPLACE_ME";
    static DrawersBot bot;
    public static void main( String[] args )
    {
        bot = new DrawersBot(clientId, password, new ConnectionStateListener() {
            @Override
            public void onConnected() {
                System.out.println("Connected to server");
            }

            @Override
            public void onConnectionLost() {
                System.out.println("Disconnected from server");
            }
        });

        // subscribe for text message
        MqttProviderManager.getInstanceFor(bot).addMessageListener(new NewMessageListener(){
            @Override
            public void receiveMessage(MqttChatMessage message) {
                // here you will receive message, operate on it and reply
                MqttChat mqttChat = new MqttChat(message.senderUid, UUID.randomUUID().toString(),
                        message.message, false, ChatConstant.ChatType.TEXT, clientId);
                mqttChat.sendStanza(bot);
            }

            @Override
            public void acknowledgeStanza(MqttChatMessage message) {
                // this is called when message is send successfully to the mqtt server.

            }
        });

        // Start the bot
        bot.start();

        // Block the main thread
        try {
            bot.getExecutorService().awaitTermination(10000000000l, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
