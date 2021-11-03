package ru.fdoke.direct.chatter;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class MediatorApp {

    public static void main(String[] args) throws IOException {
        String ip = "0.0.0.0";
        if (args.length > 0) {
            ip = args[0];
        }


        DatagramSocket discussionServer = new DatagramSocket(new InetSocketAddress(ip, 12222));
        DatagramPacket client1Packet = new DatagramPacket(new byte[1024], 1024);
        System.out.println("Started for waiting");

        discussionServer.receive(client1Packet);
        InetAddress client1Host = client1Packet.getAddress();
        int client1Port = client1Packet.getPort();
        System.out.println("Know client1 [" + client1Host + " " + client1Port + "]");

        DatagramPacket client2Packet = new DatagramPacket(new byte[1024], 1024);
        discussionServer.receive(client2Packet);
        InetAddress client2Host = client2Packet.getAddress();
        int client2Port = client2Packet.getPort();
        System.out.println("Know client 2 [" + client2Host + " " + client2Port + "]");

        byte[] msg1 = (client2Host.getHostAddress() + ":" + client2Port).getBytes(StandardCharsets.UTF_8);
        discussionServer.send(new DatagramPacket(msg1, msg1.length, client1Host, client1Port));

        byte[] msg2 = (client1Host.getHostAddress() + ":" + client1Port).getBytes(StandardCharsets.UTF_8);
        discussionServer.send(new DatagramPacket(msg2, msg2.length, client2Host, client2Port));

        System.out.println("Sent data to clients");

        discussionServer.close();
    }
}
