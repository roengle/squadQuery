package query;

import response.A2SInfoResponse;
import response.A2SRulesResponse;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class Query {
    private final DatagramSocket socket;
    private final String address;
    private final Integer port;

    public Query(String address, Integer port) throws IOException{
        this.socket = new DatagramSocket();
        this.address = address;
        this.port = port;
    }

    public A2SInfoResponse queryInfo() throws IOException {
        InetAddress address = InetAddress.getByName(this.address);

        String payload = "Source Engine Query\0";

        ByteBuffer buffer = ByteBuffer.allocate(5 + payload.getBytes(StandardCharsets.UTF_8).length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.put((byte)0xFF);
        buffer.put((byte)0xFF);
        buffer.put((byte)0xFF);
        buffer.put((byte)0xFF);
        buffer.put((byte)'T');
        buffer.put(payload.getBytes(StandardCharsets.UTF_8));

        send(this.socket, address, this.port, buffer.array());

        DatagramPacket receivingPacket = receive(socket);
        byte[] receivedData = receivingPacket.getData();
        if(receivedData[4] == (byte)0x41){
            buffer = ByteBuffer.allocate(buffer.capacity() + 4);
            buffer.put((byte) 0xFF);
            buffer.put((byte) 0xFF);
            buffer.put((byte) 0xFF);
            buffer.put((byte) 0xFF);
            buffer.put((byte) 'T');
            buffer.put(payload.getBytes(StandardCharsets.UTF_8));
            for (int i = 5; i <= 8; i++) {
                buffer.put(receivedData[i]);
            }
            send(this.socket, address, this.port, buffer.array());

            receivedData = receive(this.socket).getData();

            return A2SInfoResponse.from(receivedData);
        }else{
            return A2SInfoResponse.from(receivedData);
        }
    }

    public A2SRulesResponse queryRules() throws IOException {
        InetAddress address = InetAddress.getByName(this.address);

        ByteBuffer buffer = ByteBuffer.allocate(9);
        buffer.put((byte)0xFF);
        buffer.put((byte)0xFF);
        buffer.put((byte)0xFF);
        buffer.put((byte)0xFF);
        buffer.put((byte)0x56);
        buffer.put((byte)0xFF);
        buffer.put((byte)0xFF);
        buffer.put((byte)0xFF);
        buffer.put((byte)0xFF);

        send(socket, address, port, buffer.array());

        DatagramPacket receivingPacket = receive(socket);
        byte[] receivedData = receivingPacket.getData();
        if(receivedData[4] == (byte)0x41){
            buffer = ByteBuffer.allocate(buffer.capacity());
            buffer.put((byte) 0xFF);
            buffer.put((byte) 0xFF);
            buffer.put((byte) 0xFF);
            buffer.put((byte) 0xFF);
            buffer.put((byte)0x56);
            for(int i = 5; i < 9; i++){
                buffer.put(receivedData[i]);
            }

            send(this.socket, address, this.port, buffer.array());
            receivedData = receive(this.socket).getData();

            return A2SRulesResponse.from(receivedData);
        }else{
            return A2SRulesResponse.from(receivedData);
        }
    }

    private static void send(DatagramSocket socket, InetAddress address, Integer port, byte[] payload) throws IOException {
        DatagramPacket sendPacket = new DatagramPacket(payload, payload.length, address, port);
        socket.send(sendPacket);
    }

    private static DatagramPacket receive(DatagramSocket socket) throws IOException {
        byte[] receivingBuffer = new byte[1400];

        DatagramPacket receivingPacket = new DatagramPacket(receivingBuffer, receivingBuffer.length);

        socket.receive(receivingPacket);

        return receivingPacket;
    }

}
