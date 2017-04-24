import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.ByteBuffer;
import java.util.Arrays;
public class Ex3Client{

	public static void main(String[] args)throws Exception{
		try(Socket socket = new Socket("codebank.xyz", 38103)){
			System.out.println("Connect to Server.");
			InputStream fromServer = socket.getInputStream();
			OutputStream toServer = socket.getOutputStream();
			byte[] numberOfByte = new byte[1];
			fromServer.read(numberOfByte);
			System.out.println("Reading "+numberOfByte+" bytes.");
			byte[] message = readBytesFromServer(fromServer, Byte.toUnsignedInt(numberOfByte[0]));
			//System.out.println(Arrays.toString(message));
			System.out.println("Data received: ");
			printBytesInHex(message);
			long sum = checksum(message, message.length);
			System.out.print("Checksum Calculated:");
			ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
			byteBuffer.putLong(sum);
			//convert 8byte to 2 bytes array
			byte[] b = changeByteSize(byteBuffer.array(), 2);
			printBytesInHex(b);
			// send the checksum to server
			toServer.write(b);
			// read respond from server
			fromServer.read(numberOfByte);
			if(numberOfByte[0]==1){
				System.out.println("Response Good");
			}
			else{
				System.out.println("Response Bad");
			}

		}
	}
	//convert byte array size to a given array size
	public static byte[] changeByteSize(byte[] bytesArray, int length){
		int index = bytesArray.length -1;
		int stop = index -length;
		byte[] b = new byte[length];
		int counter = length-1;
		while(index != stop ){
			b[counter] = bytesArray[index];
			index--;
			counter --;
		}
		return b;
	}

	//read and return byte array received from server
	public static byte[] readBytesFromServer(InputStream inputStream, int number)throws Exception{
		int index = 0;
		byte[] receivedBytes = new byte[number];
		while(index < number){
			byte[] oneByte = new byte[1];
			inputStream.read(oneByte);
			receivedBytes[index] = oneByte[0];
			index++;
		}
		return receivedBytes;
	}

	//funtion that prints the byte array into hex format string. Maximum byte
	//per line is 10 with a 2 space indentation at the beginning of each line.
	public static void printBytesInHex(byte[] bytes){
		for(int i = 0; i<bytes.length; i++){
			if(i%10 == 0 && i != 0){
				System.out.println();
				System.out.print("  ");
			}
			if(i == 0){
				System.out.print("  ");
			}
			System.out.print(String.format("%02X", bytes[i]));
		}
		System.out.println();
	}
	//return checksum in long
	public static long checksum(byte[] message, int length) {
	    int i = 0;
	    long sum = 0;
	    while (length > 0) {
	        sum += (message[i]&0xff) << 8;
	        i++;
	        length--;
	        if ((length)==0) break;
	        sum += (message[i++]&0xff);
	        length--;
	    }
	    return (~((sum & 0xFFFF)+(sum >> 16)))&0xFFFF;
	}
}