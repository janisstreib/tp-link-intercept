import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;

public class Intercept {
	private static final int CLIENT_COMMANDS = 29808;
	private static final int SERVER_COMMANDS = 29809;

	public static void main(String[] args) throws IOException {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					listen(CLIENT_COMMANDS);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					listen(SERVER_COMMANDS);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

	private static void listen(int port) throws SocketException, IOException {
		try (DatagramSocket transferSocket = new DatagramSocket(
				new InetSocketAddress(port))) {
			transferSocket.setReuseAddress(true);
			while (true) {
				byte[] crypt = new byte['ᝰ'];
				DatagramPacket localDatagramPacket = new DatagramPacket(crypt,
						crypt.length);
				transferSocket.receive(localDatagramPacket);
				System.out.println("\n\n===NEW PACKET ON PORT " + port
						+ " FROM " + localDatagramPacket.getAddress());
				crypt = Arrays.copyOf(crypt, localDatagramPacket.getLength());
				RC4.crypt(crypt);
				System.out.println("Decrypted data:");
				packet2Header(crypt);
				System.out.println(" RAW: " + bytesToHex(crypt));
			}
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

	// from decompiled com.tplink.smb.easySmartUtility.transfer.PacketHead
	// modified
	private static boolean packet2Header(byte[] paramArrayOfByte) {
		if (paramArrayOfByte.length < 32) {
			return false;
		}
		int i = 0;
		byte[] arrayOfByte1 = new byte[2];
		byte[] arrayOfByte2 = new byte[4];
		byte[] mac = new byte[6];
		System.out.println(" Version:" + paramArrayOfByte[(i++)]);
		System.out.println(" OPCODE:" + paramArrayOfByte[i++]);
		// setOpCode(paramArrayOfByte[(i++)]);
		System.arraycopy(paramArrayOfByte, i, mac, 0, mac.length);
		i += mac.length;
		System.out.println(" MAC:" + bytesToHex(mac));
		System.arraycopy(paramArrayOfByte, i, mac, 0, mac.length);
		i += mac.length;
		System.out.println(" HOST MAC:" + bytesToHex(mac));
		for (int k = 0; k < arrayOfByte1.length; k++) {
			arrayOfByte1[k] = paramArrayOfByte[(i++)];
		}
		// int j = TLV.byte2Short(arrayOfByte1);
		int j = byte2Short(arrayOfByte1);
		// if (j != sequenceNum) {
		// return false;
		// }
		System.out.println(" SEQUENCE NUMBER:" + j);
		for (int k = 0; k < arrayOfByte2.length; k++) {
			arrayOfByte2[k] = paramArrayOfByte[(i++)];
		}
		// setErrCode(TLV.byte2int(arrayOfByte2));
		System.out.println(" ERROR CODE (7 or 8 are somehow bad):"
				+ byte2int(arrayOfByte2));
		// if (((Page.pageType == Page.PageType.NORMAL_PAGE) && (this.errCode ==
		// 7))
		// || (this.errCode == 8)) {
		// throw new TransException(this.errCode);
		// }
		for (int k = 0; k < arrayOfByte1.length; k++) {
			arrayOfByte1[k] = paramArrayOfByte[(i++)];
		}
		System.out.println(" LENGTH:" + byte2Short(arrayOfByte1));
		for (int k = 0; k < arrayOfByte1.length; k++) {
			arrayOfByte1[k] = paramArrayOfByte[(i++)];
		}
		System.out.println(" FRAGMENT OFFSET:" + byte2Short(arrayOfByte1));
		i += 2;
		for (int k = 0; k < arrayOfByte1.length; k++) {
			arrayOfByte1[k] = paramArrayOfByte[(i++)];
		}
		System.out.println(" TOKEN ID:" + byte2Short(arrayOfByte1));
		for (int k = 0; k < arrayOfByte2.length; k++) {
			arrayOfByte2[k] = paramArrayOfByte[(i++)];
		}
		System.out.println(" CHECKSUM (apparently not implemented):"
				+ byte2int(arrayOfByte2));
		return true;
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