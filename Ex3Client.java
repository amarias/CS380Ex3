import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class Ex3Client {

	public static void main(String[] args) {
		try {
			Socket socket = new Socket("codebank.xyz", 38103);
			System.out.println("Connected to server.");

			BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
			PrintStream out = new PrintStream(socket.getOutputStream());

			int bytesSent = bis.read();
			System.out.println("Reading " + bytesSent + " bytes.");

			byte[] bytes = new byte[bytesSent];
			System.out.println("Data received: ");
			for (int i = 0; i < bytesSent; i++) {
				if (i % 10 == 0 && i != 0)
					System.out.println();
				bytes[i] = (byte) bis.read();
				System.out.print(String.format("%X", bytes[i]));
			}

			short checksum = checksum(bytes);
			System.out.print(String.format("\nChecksum calculated: 0x%X.\n", checksum));

			out.write(checksum & 0xFFFF0000);
			out.write(checksum & 0x0000FFFF);

			int response;
			if ((response = bis.read()) == 1)
				System.out.println("Response good.");
			else if (response == 0)
				System.out.println("Response not good.");

			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Disconnected from server.");
	}

	public static short checksum(byte[] b) {
		int sum = 0;
		int length = b.length;
		int i = 0;

		while (length > 1) {
			int s = ((b[i++] << 8) & 0xFF00) | (b[i++] & 0x00FF);
			sum += s;
			if ((sum & 0xFFFF0000) > 0) {
				// Not adding correctly
				sum &= 0xFFFF;
				sum++;
			}
			length -= 2;
		}

		// Left over byte, when an odd number is sent
		if (length > 0) {
			sum += (b[i] << 8 & 0xFF00);
			if ((sum & 0xFFFF0000) > 0) {
				// Not adding correctly
				sum &= 0xFFFF;
				sum++;
			}
		}
		return (short) ~(sum & 0xFFFF);
	}

}
