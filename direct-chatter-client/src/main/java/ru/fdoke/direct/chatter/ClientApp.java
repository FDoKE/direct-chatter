package ru.fdoke.direct.chatter;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientApp {
    static final Pattern IP_PORT = Pattern.compile("(\\d+.\\d+.\\d+.\\d+):(\\d+).*");

    public static void main(String[] args) throws IOException {
        String ip = args[0];

        DatagramSocket punchConnection = new DatagramSocket();
        punchConnection.setReuseAddress(true);
        punchConnection.send(new DatagramPacket(new byte[1], 1, new InetSocketAddress(ip, 12222)));
        DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
        punchConnection.receive(packet);

        String data = new String(packet.getData(), StandardCharsets.UTF_8);
        System.out.println(data);

        Matcher matcher = IP_PORT.matcher(data);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Could not find address");
        }

        String remoteIp = matcher.group(1);
        int remotePort = Integer.parseInt(matcher.group(2));
        System.out.println("Other client: " + remoteIp + ":" + remotePort);

        int localPort = punchConnection.getLocalPort();
        System.out.println("Binding to: " + localPort);
        punchConnection.close();

        DatagramSocket conn = new DatagramSocket(localPort);
        conn.setReuseAddress(true);
        conn.setSoTimeout(500);
        InetAddress byName = InetAddress.getByName(remoteIp);

        while (!Thread.currentThread().isInterrupted()) {
            try {
                byte[] msgBytes = "Ping".getBytes(StandardCharsets.UTF_8);
                DatagramPacket datagramPacket = new DatagramPacket(msgBytes, msgBytes.length, byName, remotePort);
                conn.send(datagramPacket);
                DatagramPacket datagramPacket2 = new DatagramPacket(new byte[1024], 1024);
                conn.receive(datagramPacket2);
                System.out.println("Connection established");
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (Thread.interrupted()) {
            System.exit(1);
        }

        new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    DatagramPacket datagramPacket2 = new DatagramPacket(new byte[1024], 1024);
                    conn.receive(datagramPacket2);
                    String content = new String(datagramPacket2.getData(), StandardCharsets.UTF_8);
                    System.out.println("Received: " + content.substring(0, content.indexOf('\u0000')));
                } catch (Exception ignored) {
                }
            }
        }).start();

        BufferedReader bufferedInputStream = new BufferedReader(new InputStreamReader(System.in));
        while (!Thread.interrupted()) {
            String msg = bufferedInputStream.readLine();
            byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
            DatagramPacket datagramPacket = new DatagramPacket(msgBytes, msgBytes.length, byName, remotePort);
            conn.send(datagramPacket);
        }
    }
}
