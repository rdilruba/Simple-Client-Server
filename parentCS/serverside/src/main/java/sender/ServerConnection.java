package sender;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Random;

public class ServerConnection {
	public static void main(String[] args) throws IOException{
		
		Selector selector = Selector.open();
		ServerSocketChannel channel = ServerSocketChannel.open();
		InetSocketAddress addres = new InetSocketAddress("localhost",9042);
		channel.bind(addres);
		channel.configureBlocking(false);
		int ops = channel.validOps();
		channel.register(selector, ops);
		Random r = new Random();
		while(true)
		{
			selector.select();
			log("i'm a server and i'm waiting for new connection and buffer select...");
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			while(it.hasNext())
			{
				SelectionKey key = it.next();
				if(key.isAcceptable()){
					SocketChannel clientChan = channel.accept();
					clientChan.configureBlocking(false);
					clientChan.register(selector, SelectionKey.OP_READ,r.nextInt(10));
					log("Connection Accepted: " + clientChan.getLocalAddress() + "\n");
					 
				}else if(key.isReadable()){
					SocketChannel clientChan = (SocketChannel) key.channel();
					ByteBuffer buffer = ByteBuffer.allocate(256);
					clientChan.read(buffer);
					String result = new String(buffer.array()).trim();
					log("Message recieved from Client: "+ result);
					if(result.contains("end")) {
						clientChan.close();
						continue;
					}
					int guess = Integer.parseInt(result);
					int number = (Integer) key.attachment();
					if (number==guess) {
						buffer = ByteBuffer.wrap(("\nCongratulations!").getBytes());
						clientChan.write(buffer);
						clientChan.close();
						
					}else if(number>guess) {
						buffer = ByteBuffer.wrap(("Auto generated value was "+number+". Try bigger than "+guess).getBytes());
						clientChan.write(buffer);
					}
					else if(number<guess) {
						buffer = ByteBuffer.wrap(("Auto generated value was "+number+". Try less than "+guess).getBytes());
						clientChan.write(buffer);
					}
				}
				
				it.remove();
			}
			
		}
		
	}
	private static void log(String message) {
		System.out.println(message);
	}

}
