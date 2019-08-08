package reciever;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;

public class ClientConnection {

	private static int start = 0;
	private static int end = 10;

	public static void main(String[] args) throws IOException, InterruptedException {

		InetSocketAddress address = new InetSocketAddress("localhost",9042);
		SocketChannel channel = SocketChannel.open(address);
		log("Connecting to server on port 9042");
		ByteBuffer buffer = null;
		sendNumber(buffer,channel);
		Thread.sleep(2000);
		if(channel.isOpen())
			sendNumber(buffer,channel);
		Thread.sleep(2000);
		if(channel.isOpen())
			sendNumber(buffer,channel);
		Thread.sleep(2000);
		byte[] message = new String("end").getBytes();
		buffer = ByteBuffer.wrap(message);
		if(channel.isOpen()) {
			channel.write(buffer);
			log("End of my guesses ");
			buffer.clear();
			channel.close();
		}
	}

	private static void log(String message){
		System.out.println(message);
	}
	private static void sendNumber(ByteBuffer buffer, SocketChannel channel) throws IOException
	{
		Random r = new Random();
		int number = r.nextInt(end-start)+start;
		byte[] message = new String(number+"").getBytes();
		buffer = ByteBuffer.wrap(message);
		channel.write(buffer);
		log("Sending number : " + number);
		buffer.clear();
		buffer = ByteBuffer.allocate(256);
		channel.read(buffer);
		String result = new String(buffer.array()).trim();
		log("Message recieved from Server: "+ result);
		if(result.contains("Congratulations")) {
			message = new String("end").getBytes();
			buffer = ByteBuffer.wrap(message);
			channel.write(buffer);
			log("End of my guesses ");
			channel.close();
		}else if(result.contains("less")) {
			end = number; 
		}
		else if(result.contains("bigger")) {
			start = number + 1;
		}

	}


}
