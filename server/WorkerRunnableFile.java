import java.net.*;
import java.io.*;

public class WorkerRunnableFile implements Runnable{
//  public static void main (String [] args ) throws IOException {
//    // create socket
//    ServerSocket servsock = new ServerSocket(1326);
//  }
	
	protected Socket connection = null;
	protected String filename = null;
    protected int filesize = 6022386; // filesize temporary hardcoded to 5MB
	public WorkerRunnableFile(Socket clientSocket,String filename) {
      this.connection = clientSocket;
      this.filename = filename;
	}
  public void run(){
	    try{
	//      System.out.println("Waiting...");
	//
	//      Socket sock = servsock.accept();
	//      System.out.println("Accepted connection : " + sock);
	
	      // sendfile    
			long start = System.currentTimeMillis();
		    int bytesRead;
		    int current = 0;
		    // localhost for testing
		    
		
		    // receive file
		    byte [] mybytearray  = new byte [filesize];
		    InputStream is = connection.getInputStream();
		    FileOutputStream fos = new FileOutputStream(filename);
		    BufferedOutputStream bos = new BufferedOutputStream(fos);
		    bytesRead = is.read(mybytearray,0,mybytearray.length);
		    current = bytesRead;
		    do {
		       bytesRead =
		          is.read(mybytearray, current, (mybytearray.length-current));
		       if(bytesRead >= 0) current += bytesRead;
		    } while(bytesRead > -1);
		    bos.write(mybytearray, 0 , current);
		    bos.flush();
		    long end = System.currentTimeMillis();
		    System.out.println(end-start);
		    bos.close();
		    connection.close();
	    }catch(IOException ioException){
			ioException.printStackTrace();
		}
    }
}

