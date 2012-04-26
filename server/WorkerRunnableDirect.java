import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**

 */
public class WorkerRunnableDirect implements Runnable{

//    protected Socket clientSocket = null;
//    protected String serverText   = null;
//
//    public WorkerRunnable(Socket clientSocket, String serverText) {
//        this.clientSocket = clientSocket;
//        this.serverText   = serverText;
//    }
//
//    public void run() {
//        try {
//            InputStream input  = clientSocket.getInputStream();
//            OutputStream output = clientSocket.getOutputStream();
//            long time = System.currentTimeMillis();
//            output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " +
//                    this.serverText + " - " +
//                    time +
//                    "").getBytes());
//            output.close();
//            input.close();
//            System.out.println("Request processed: " + time);
//        } catch (IOException e) {
//            //report exception somewhere.
//            e.printStackTrace();
//        }
//    }
	
	protected Socket connection = null;
	DataOutputStream out;
	DataInputStream in;
	String message;
	public WorkerRunnableDirect(Socket clientSocket) {
      this.connection = clientSocket;
  }
	public void run(){
		try{
			System.out.println("Connection received from " + connection.getInetAddress().getHostName());
			out = new DataOutputStream(connection.getOutputStream());
			out.flush();
			in = new DataInputStream(connection.getInputStream());
			sendMessage("Connection successful");
			
				try{
					message = (String)in.readUTF();
				    sendMessage("Received :"+message);
					System.out.println("client>" + message);
				}
				catch(Exception e){
					System.out.println(e.getMessage());
				}
			
            
			out.close();
			in.close();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		
	}

	void sendMessage(String msg)
	{
		try{
			out.writeUTF(msg);
			out.flush();
			System.out.println("server>" + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
}