import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.Enumeration;

public class TPTools {
    public static void scanForDevices(DatagramSocket sock) throws IOException {
        // sock.setBroadcast(true);
        Enumeration<NetworkInterface> interfaces = NetworkInterface
                .getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.isLoopback()) {
                continue; // Don't want to broadcast to the loopback interface
            }
            for (InterfaceAddress interfaceAddress : networkInterface
                    .getInterfaceAddresses()) {
                InetAddress broadcast = interfaceAddress.getBroadcast();
                if (broadcast == null) {
                    continue;
                }
                ByteBuffer bb = ByteBuffer.allocate(36);
                // version
                bb.put((byte) 1);
                // opcode
                bb.put((byte) 0);
                // dest. mac
                for (int i = 0; i < 6; i++) {
                    bb.put((byte) 0);
                }
                // host mac
                byte[] mac = networkInterface.getHardwareAddress();
                for (int i = 0; i < mac.length; i++) {
                    bb.put(mac[i]);
                }
                // seq. number
                bb.putShort((short) 142);
                // error code
                bb.putInt(0);
                // length
                bb.putShort((short) 36);
                // fragment offset
                bb.putInt(0);
                // token id
                bb.putShort((short) 0); // In short: It's the answer

                // checksum
                bb.putInt(0);
                // body: -1 type
                bb.putShort((short) -1);
                byte[] buf = bb.array();
                buf = Intercept.RC4.crypt(buf);
                DatagramPacket pack = new DatagramPacket(buf, buf.length,
                        InetAddress.getByName("255.255.255.255"),
                        sock.getLocalPort());
                sock.send(pack);
            }
        }

    }
}
