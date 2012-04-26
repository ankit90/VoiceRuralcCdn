import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class MultiThreadedServer implements Runnable{

    protected int          serverPort   = 2004;
    protected int		   workerType	= 0;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    protected String	   filename;

    public MultiThreadedServer(int port){//,int workertype){
        this.serverPort = port;
        //this.workerType = workertype;
    }

    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }
//            if (workerType == 0)
//            new Thread(
//                new WorkerRunnableDirect(
//                    clientSocket)
//            ).start();
//            else if (workerType == 1)
        	new Thread(
                    new WorkerRunnable(
                        clientSocket)//,serverSocket)//,filename)
                ).start();
        }
        System.out.println("Server Stopped.") ;
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }
    
    public static void main(String [] args){
    	MultiThreadedServer server = new MultiThreadedServer(2004);//,Integer.parseInt(args[1]));
    	new Thread(server).start();

            //try {
              //  Thread.sleep(20 * 1000);
            //} catch (InterruptedException e) {
                //e.printStackTrace();
            //}
    	//System.out.println("Stopping Server");
           // server.stop();
    }

}
