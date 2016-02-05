import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Very rough implementation using some of the decompiled source from the easy
 * smart configuration utility.
 */
public class Intercept {
    private static final int CLIENT_COMMANDS = 29808;
    private static final int SERVER_COMMANDS = 29809;
    private static final HashMap<Short, Type> TYPES = new HashMap<Short, Type>();
    private static int up = 0;

    static {
        TYPES.put((short) 512, new StringType("Username"));
        TYPES.put((short) 514, new StringType("Password"));
        TYPES.put((short) 7, new StringType("Device firmware version"));
        TYPES.put((short) 8, new StringType("Hardware version"));
        TYPES.put((short) 1, new StringType("Model (?)"));
        TYPES.put((short) 2, new StringType("Device description"));
        TYPES.put((short) 9, new BooleanType("DHCP"));
        TYPES.put((short) 17152, new RawType("Loop prevention"));
        TYPES.put((short) 6, new IPAddressType("Default gateway"));
        TYPES.put((short) 4, new IPAddressType("IP Address"));
        TYPES.put((short) 5, new IPAddressType("Subnet Mask"));
        TYPES.put((short) 4096, new RawType("Port setting"));
        TYPES.put((short) 4352, new RawType("IGMP Snooping"));
        TYPES.put((short) 16640, new RawType("Port Trunk/Mirror"));
        TYPES.put((short) 16384, new RawType("Port Statistics"));
        TYPES.put((short) 16896, new RawType("Cable Test"));
        TYPES.put((short) 8192, new RawType("MTU VLAN"));
        TYPES.put((short) 8448, new RawType("Port Based VLAN"));
        TYPES.put((short) 8704, new RawType("802.1 Q VLAN"));
        TYPES.put((short) 8706, new RawType("802.1Q PVID Setting"));
        TYPES.put((short) 773, new RawType("System reboot"));

    }

    public static void main(String[] args) throws IOException {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    DatagramSocket out = new DatagramSocket(CLIENT_COMMANDS);
                    while (up != 1 || !out.isBound()) {
                        Thread.sleep(500);
                    }
                    TPTools.scanForDevices(out);
                    listen(out, System.err);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    DatagramSocket in = new DatagramSocket(SERVER_COMMANDS);
                    listen(in, System.out);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private static void listen(DatagramSocket transferSocket, PrintStream out)
            throws SocketException, IOException {
        transferSocket.setReuseAddress(true);
        while (true) {
            byte[] crypt = new byte['ᝰ'];
            DatagramPacket localDatagramPacket = new DatagramPacket(crypt,
                    crypt.length);
            up++;
            transferSocket.receive(localDatagramPacket);
            out.println("\n\n===NEW PACKET ON PORT "
                    + localDatagramPacket.getPort() + " FROM "
                    + localDatagramPacket.getAddress());
            crypt = Arrays.copyOf(crypt, localDatagramPacket.getLength());
            RC4.crypt(crypt);
            out.println("Decrypted data:");
            int offset = packet2Header(crypt, out);
            out.println("  BODY:");
            readBody(crypt, offset, out);
            out.println("  RAW: " + bytesToHex(crypt));
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static void readBody(byte[] data, int startOffset, PrintStream out) {
        int i = startOffset;
        while (i < data.length) {
            out.println(" -START TLV-");
            byte[] shortBuf = Arrays.copyOfRange(data, i, i + 2);
            i += 2;
            short type = byte2Short(shortBuf);
            Type pType = TYPES.get(type);
            out.println("  TYPE:" + type + " (" + pType == null ? "Unknown"
                    : pType + ")");
            shortBuf = Arrays.copyOfRange(data, i, i + 2);
            i += 2;
            short length = byte2Short(shortBuf);
            out.println("  LENGTH:" + length);

            shortBuf = Arrays.copyOfRange(data, i, i + length);
            i += length;
            out.println("  BODY:"
                    + bytesToHex(shortBuf)
                    + " ("
                    + (pType == null ? new String(shortBuf).trim()
                            + "::unknown" : pType.makeHumanRadable(shortBuf)
                            .trim() + "::" + pType.getClass().getName()) + ")");
        }
    }

    // from decompiled com.tplink.smb.easySmartUtility.transfer.PacketHead
    // modified
    private static int packet2Header(byte[] paramArrayOfByte, PrintStream out) {
        if (paramArrayOfByte.length < 32) {
            return 0;
        }
        int i = 0;
        byte[] arrayOfByte1 = new byte[2];
        byte[] arrayOfByte2 = new byte[4];
        byte[] mac = new byte[6];
        out.println(" Version:" + paramArrayOfByte[(i++)]);
        out.println(" OPCODE:" + paramArrayOfByte[i++]);
        // setOpCode(paramArrayOfByte[(i++)]);
        System.arraycopy(paramArrayOfByte, i, mac, 0, mac.length);
        i += mac.length;
        out.println(" MAC:" + bytesToHex(mac));
        System.arraycopy(paramArrayOfByte, i, mac, 0, mac.length);
        i += mac.length;
        out.println(" HOST MAC:" + bytesToHex(mac));
        for (int k = 0; k < arrayOfByte1.length; k++) {
            arrayOfByte1[k] = paramArrayOfByte[(i++)];
        }
        // int j = TLV.byte2Short(arrayOfByte1);
        int j = byte2Short(arrayOfByte1);
        // if (j != sequenceNum) {
        // return false;
        // }
        out.println(" SEQUENCE NUMBER:" + j);
        for (int k = 0; k < arrayOfByte2.length; k++) {
            arrayOfByte2[k] = paramArrayOfByte[(i++)];
        }
        // setErrCode(TLV.byte2int(arrayOfByte2));
        out.println(" ERROR CODE (7 or 8 are somehow bad):"
                + byte2int(arrayOfByte2));
        // if (((Page.pageType == Page.PageType.NORMAL_PAGE) && (this.errCode ==
        // 7))
        // || (this.errCode == 8)) {
        // throw new TransException(this.errCode);
        // }
        for (int k = 0; k < arrayOfByte1.length; k++) {
            arrayOfByte1[k] = paramArrayOfByte[(i++)];
        }
        out.println(" LENGTH:" + byte2Short(arrayOfByte1));
        for (int k = 0; k < arrayOfByte1.length; k++) {
            arrayOfByte1[k] = paramArrayOfByte[(i++)];
        }
        out.println(" FRAGMENT OFFSET:" + byte2Short(arrayOfByte1));
        i += 2;
        for (int k = 0; k < arrayOfByte1.length; k++) {
            arrayOfByte1[k] = paramArrayOfByte[(i++)];
        }
        out.println(" TOKEN ID:" + byte2Short(arrayOfByte1));
        for (int k = 0; k < arrayOfByte2.length; k++) {
            arrayOfByte2[k] = paramArrayOfByte[(i++)];
        }
        out.println(" CHECKSUM (apparently not implemented):"
                + byte2int(arrayOfByte2));

        return i;
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    // from decompiled com.tplink.smb.easySmartUtility.transfer.RC4
    public static class RC4 {
        private static int[] s_box = new int['Ā'];
        private static boolean isInit = false;

        private static void keyInit() {
            if (isInit) {
                return;
            }
            String key = "Ei2HNryt8ysSdRRI54XNQHBEbOIRqNjQgYxsTmuW3srSVRVFyLh8mwvhBLPFQph3ecDMLnDtjDUdrUwt7oTsJuYl72hXESNiD6jFIQCtQN1unsmn3JXjeYwGJ55pqTkVyN2OOm3vekF6G1LM4t3kiiG4lGwbxG4CG1s5Sli7gcINFBOLXQnPpsQNWDmPbOm74mE7eyR3L7tk8tUhI17FLKm11hrrd1ck74bMw3VYSK3X5RrDgXelewMU6o1tJ3iX";
            byte[] arrayOfByte = new byte['Ā'];
            int i = 0;
            for (int j = 0; j < 256; j++) {
                s_box[j] = j;
                arrayOfByte[j] = ((byte) key.charAt(j % key.length()));
            }
            for (int j = 0; j < 256; j++) {
                i = (i + s_box[j] + arrayOfByte[j]) % 256;
                int k = s_box[j];
                s_box[j] = s_box[i];
                s_box[i] = k;
            }
            isInit = true;
        }

        public static byte[] crypt(byte[] paramArrayOfByte, int paramInt) {
            if (paramInt > paramArrayOfByte.length) {
                return null;
            }
            keyInit();
            int i = 0;
            int j = 0;
            int k = 0;
            int[] arrayOfInt = s_box.clone();
            for (int m = 0; m < paramInt; m++) {
                i = (i + 1) % 256;
                j = (j + arrayOfInt[i]) % 256;
                int n = arrayOfInt[i];
                arrayOfInt[i] = arrayOfInt[j];
                arrayOfInt[j] = n;
                k = (arrayOfInt[i] + arrayOfInt[j]) % 256;
                int tmp95_93 = m;
                paramArrayOfByte[tmp95_93] = ((byte) (paramArrayOfByte[tmp95_93] ^ arrayOfInt[k]));
            }
            return paramArrayOfByte;
        }

        public static byte[] crypt(byte[] paramArrayOfByte) {
            return crypt(paramArrayOfByte, paramArrayOfByte.length);
        }
    }

    // from decompiled TLV
    public static short byte2Short(byte[] paramArrayOfByte) {
        if (paramArrayOfByte.length != 2) {
            return 0;
        }
        return (short) (paramArrayOfByte[0] << 8 & 0xFF00 | paramArrayOfByte[1] & 0xFF);
    }

    // from decompiled TLV
    public static int byte2int(byte[] paramArrayOfByte) {
        if (paramArrayOfByte.length != 4) {
            return 0;
        }
        return paramArrayOfByte[0] << 24 | paramArrayOfByte[1] << 24 >>> 8
                | paramArrayOfByte[2] << 8 & 0xFF00 | paramArrayOfByte[3]
                & 0xFF;
    }
}