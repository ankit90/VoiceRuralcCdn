import java.net.*;
import java.io.*;

class FileClient{
  public static void main (String [] args ) throws IOException {
    
    Socket sock = new Socket("127.0.0.1",1326);
    System.out.println("Connecting...");
      File myFile = new File ("client.java");
      byte [] mybytearray  = new byte [(int)myFile.length()];
      FileInputStream fis = new FileInputStream(myFile);
      BufferedInputStream bis = new BufferedInputStream(fis);
      bis.read(mybytearray,0,mybytearray.length);
      OutputStream os = sock.getOutputStream();
      System.out.println("Sending...");
      os.write(mybytearray,0,mybytearray.length);
      System.out.println("Sent...");
      os.flush();
      sock.close();
  }
}


