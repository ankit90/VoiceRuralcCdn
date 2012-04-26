  import java.io.*;
import java.net.*;
 class Requester{
	Socket requestSocket;
	DataOutputStream out;
 	DataInputStream in;
 	String message;
	Requester(){}
	void run()
	{
		try{
			//1. creating a socket to connect to the server
			requestSocket = new Socket("localhost", 2004);
			System.out.println("Connected  in port 2004");
			//2. get Input and Output streams
			out = new DataOutputStream(requestSocket.getOutputStream());
                        out.flush();
                        
			BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
			//3: Communicating with the server
				try{
					message =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><Message><size>1</size><fileName>name</fileName><Title>til</Title><Desc>hi ah</Desc>"+
					           "<Tags>da</Tags><Time_stamp>fff</Time_stamp>"+
					           "<Conference_stamp>dddd</Conference_stamp></Message></root>";
					sendMessage(message);
                                        out.close();
                                        //requestSocket = new Socket("localhost", 2004);
                                        in = new DataInputStream(requestSocket.getInputStream());
                                        String msg = null;
                                        System.out.println("hahaha");
                                        while((msg = in.readLine())!=null)
                                        {
                                            System.out.println(msg);
                                        }
                                        
				}
				catch(Exception classNot){
                                    classNot.printStackTrace();
					System.err.println("data received in unknown format");
				}
		}
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//4: Closing connection
			try{
				//out.close();
                                in.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	void sendMessage(String msg)
	{
		try{
			out.writeBytes(msg);
			out.flush();
			//System.out.println("client>" + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	public static void main(String args[])
	{
		Requester client = new Requester();
		client.run();
	}
}

