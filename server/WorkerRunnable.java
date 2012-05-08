import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;

public class WorkerRunnable implements Runnable{
	DataInputStream is = null;
        DataOutputStream out = null;
	protected Socket connection = null;
        protected ServerSocket serverSocket=null;
        protected int filesize = 5242880;
	public WorkerRunnable(Socket clientSocket){
            this.connection = clientSocket;
	}
	  public void run(){
		  	int bytesRead;
                        int current = 0;
		    try{
                        System.out.println("Request Recived From Client !!");
                         is = new DataInputStream(connection.getInputStream());
                         out = new DataOutputStream(connection.getOutputStream());
                         out.flush();
                         String msg1 = null;
                         msg1 = is.readLine();
                         System.out.println("Control XML Message Received");
                         System.out.flush();
                         MessageParser mp = new MessageParser();
                         InputStream iss = new StringBufferInputStream(msg1);
			 Message msg = mp.runExample(iss);

		    	if (msg.getFunction().equalsIgnoreCase("upload"))
                        {
                            System.out.println("Control Message Type : Upload - Filename: "+msg.getTitle());
                            if (msg.getsize() == 0)
                            {
                                //out.writeBytes("Waiting to receive " + msg.getfileName() + " directly.\n");
                                out.flush();
                                lessThan5(msg.getfileName(),Long.parseLong(msg.getTitle()));
                            }
                            else if (msg.getsize() == 1)
                            {
                                out.writeBytes("Waiting to receive " + msg.getTitle() + " through USB keys.\n");
                                out.flush();
                            }
                            update(msg);
                        }
                        else if (msg.getFunction().equalsIgnoreCase("download"))
                        {
                            System.out.println("Control Message Type : Download - Filename: "+msg.getTitle());
                            download(msg);
                        }
                        else if (msg.getFunction().equalsIgnoreCase("comment"))
                        {
                            System.out.println("Control Message Type : Comment");
                            comment(msg);
                        }
                        else if (msg.getFunction().equalsIgnoreCase("voice"))
                        {
                            System.out.println("Control Message Type : Voice Comment");
                            voice(msg);
                        }
                        else if (msg.getFunction().equalsIgnoreCase("voice_download"))
                        {
                            System.out.println("Control Message Type : Voice Comment Download");
                            vd(msg);
                        }
                         is.close();
                         out.close();
                         connection.close();
		    }
                    catch(IOException ioException){
				ioException.printStackTrace();
			}
          }
	  
	  public void lessThan5(String fileName,long size){

              File f = new File(fileName);
              long d = 0;
              if (f.exists())
                  d = f.length();
              int bytesRead;
		    int current = 0;
		    byte [] mybytearray;
                    long loop = (size-d)/filesize;
                    long rem = (size-d)%filesize;
		    try{
                        long time = System.currentTimeMillis();
                        out.writeBytes(d+"\n");
                        long t=0;
                        FileOutputStream fos;
                        if (f.exists())
                            fos = new FileOutputStream(fileName,true);
                        else
                            fos = new FileOutputStream(fileName);
                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        while(t<loop){
                            System.out.println(t);
                            mybytearray  = new byte [filesize];
                            bytesRead = is.read(mybytearray,0,filesize);
			    current = bytesRead;
			    do {
			      //System.out.println("neww");
			      if(current>=filesize)
			        break;
			       bytesRead =
			          is.read(mybytearray, current, (mybytearray.length-current));
			       if(bytesRead >= 0) current += bytesRead;
			      // System.out.println("dsd");
			    } while(bytesRead > -1);
                            System.out.println("hhhhhhh");
			    bos.write(mybytearray, 0 , current);
			    bos.flush();
                            t++;
                        }
                        if(rem!=0){
                            System.out.println(rem);
                            mybytearray  = new byte [filesize];
                            bytesRead = is.read(mybytearray,0,filesize);
			    current = bytesRead;
			    do {
			      //System.out.println("neww");
			      if(current>=size-t*filesize-d)
			        break;
			       bytesRead =
			          is.read(mybytearray, current, (mybytearray.length-current));
			       if(bytesRead >= 0) current += bytesRead;
			    } while(bytesRead > -1);
                            bos.write(mybytearray, 0 , current);
			    bos.flush();
                        }
                            out.writeBytes("File " + fileName +" received succesfully.\n");
                            out.flush();
                            bos.close();
                            long time1 = System.currentTimeMillis();
                            System.out.println("Time taken for Upload ...... + "+(time1-time)+" msec");
		    }catch(IOException ioException){
				ioException.printStackTrace();
			}


	  }

          
          public void comment(Message msg) throws IOException{
                Connection con = null;
                Statement st = null;
                ResultSet rs = null;

                String url = "jdbc:mysql://localhost:3306/innobo";
                String user = "root";
                String password = "sadgurugyan";

                try {
                    con = DriverManager.getConnection(url, user, password);
                    st = con.createStatement();
                    
                    rs = st.executeQuery("Select comments from VoiceRuralCDN where title =\""+msg.getTitle()+"\"");
                    String newcomment ="";
                   if(rs.next()) {
                       String oldcomment = rs.getString(1);
                       if (!oldcomment.equals("default"))
                       newcomment = oldcomment + "~"+msg.getDesc();
                       else newcomment = msg.getDesc();
                    }
                    System.out.println(newcomment);
                    String query = "UPDATE VoiceRuralCDN SET comments = \""+newcomment+"\" WHERE title =\""+msg.getTitle()+"\"";
                    st.executeUpdate(query);
                } catch (SQLException ex) {
                    Logger lgr = Logger.getLogger(WorkerRunnable.class.getName());
                    lgr.log(Level.SEVERE, ex.getMessage(), ex);

                } finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                        if (st != null) {
                            st.close();
                        }
                        if (con != null) {
                            con.close();
                        }

                    } catch (SQLException ex) {
                        Logger lgr = Logger.getLogger(WorkerRunnable.class.getName());
                        lgr.log(Level.WARNING, ex.getMessage(), ex);
                    }
                }
            }

          public void download(Message msg){
              try{
                    long time = System.currentTimeMillis();
                    File myFile = new File (msg.getTitle());
                    long downloaded_size = Long.parseLong(msg.getDesc());
                    long totalsize = myFile.length();
                    out.writeBytes(totalsize+"\n");
                    boolean flag=is.readBoolean();
                    if(flag==true){
		    long loop = (totalsize-downloaded_size)/(long)filesize;
		    long rem = (totalsize-downloaded_size)%(long)filesize;
		    FileInputStream fis = new FileInputStream(myFile);
		    BufferedInputStream bis = new BufferedInputStream(fis);
		    long l = downloaded_size/(long)filesize;
		    long r = downloaded_size%(long)filesize;
		    long t1=0;
		    while(t1<l){
		    	byte [] my  = new byte [filesize];
			    bis.read(my,0,my.length);
			    t1++;
		    }
		    if(r != 0){
		    	byte [] my  = new byte [(int)(downloaded_size-t1*filesize)];
			    bis.read(my,0,my.length);
		    }
		    long t=0;
		    while(t<loop){
		    	byte [] mybytearray  = new byte [filesize];
		    	bis.read(mybytearray,0,mybytearray.length);
		        out.write(mybytearray,0,mybytearray.length);
		        out.flush();
		        t++;
		    }
		    if(rem != 0){
		    	byte [] mybytearray  = new byte [(int)(totalsize-downloaded_size-loop*filesize)];
		    	bis.read(mybytearray,0,mybytearray.length);
		        out.write(mybytearray,0,mybytearray.length);
		        out.flush();
		    }
                    }
                    long time1= System.currentTimeMillis();
                    System.out.println("Time taken for download ...... + "+(time1-time)+" msec");

              }
              catch(IOException ioException){
                  ioException.printStackTrace();
                  }
          }

          public void update(Message msg){
              Connection con = null;
                Statement st = null;
                ResultSet rs = null;

                String url = "jdbc:mysql://localhost:3306/innobo";
                String user = "root";
                String password = "sadgurugyan";

                try {
                    con = DriverManager.getConnection(url, user, password);
                    st = con.createStatement();
                    String q="INSERT INTO "+"VoiceRuralCDN VALUES('"
                             +msg.getfileName()+"','"+msg.getDesc() +"','"
                             +msg.getTags()+"','default','"+msg.getConference_stamp()
                             +"','default')";
                    st.executeUpdate(q);
                } catch (SQLException ex) {
                    Logger lgr = Logger.getLogger(WorkerRunnable.class.getName());
                    lgr.log(Level.SEVERE, ex.getMessage(), ex);

                } finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                        if (st != null) {
                            st.close();
                        }
                        if (con != null) {
                            con.close();
                        }

                    } catch (SQLException ex) {
                        Logger lgr = Logger.getLogger(WorkerRunnable.class.getName());
                        lgr.log(Level.WARNING, ex.getMessage(), ex);
                    }
                }
          }

          public void voice(Message msg){
                    int bytesRead;
		    int current = 0;
		    byte [] mybytearray  = new byte [filesize];
                    int size = Integer.parseInt(msg.getTags());
		    try{
			    FileOutputStream fos = new FileOutputStream(msg.getDesc());
			    BufferedOutputStream bos = new BufferedOutputStream(fos);
			    bytesRead = is.read(mybytearray,0,filesize);
			    current = bytesRead;
			    do {
			      if(current>=size)
			        break;
			       bytesRead =
			          is.read(mybytearray, current, (mybytearray.length-current));
			       if(bytesRead >= 0) current += bytesRead;
			    } while(bytesRead > -1);
			    bos.write(mybytearray, 0 , current);
			    bos.flush();
                            out.writeBytes("Comment " + msg.getDesc() +" received succesfully.\n");
                            out.flush();
                            bos.close();
		    }catch(IOException ioException){
				ioException.printStackTrace();
			}

                Connection con = null;
                Statement st = null;
                ResultSet rs = null;

                String url = "jdbc:mysql://localhost:3306/innobo";
                String user = "root";
                String password = "sadgurugyan";

                try {
                    con = DriverManager.getConnection(url, user, password);
                    st = con.createStatement();

                    rs = st.executeQuery("Select audio_comments from VoiceRuralCDN where title =\""+msg.getTitle()+"\"");
                    String newcomment ="";
                   if(rs.next()) {
                       String oldcomment = rs.getString(1);
                       if (!oldcomment.equals("default"))
                       newcomment = oldcomment + "~"+msg.getDesc();
                       else newcomment = msg.getDesc();
                    }
                    System.out.println(newcomment);
                    String query = "UPDATE VoiceRuralCDN SET audio_comments = \""+newcomment+"\" WHERE title =\""+msg.getTitle()+"\"";
                    st.executeUpdate(query);
                } catch (SQLException ex) {
                    Logger lgr = Logger.getLogger(WorkerRunnable.class.getName());
                    lgr.log(Level.SEVERE, ex.getMessage(), ex);

                } finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                        if (st != null) {
                            st.close();
                        }
                        if (con != null) {
                            con.close();
                        }

                    } catch (SQLException ex) {
                        Logger lgr = Logger.getLogger(WorkerRunnable.class.getName());
                        lgr.log(Level.WARNING, ex.getMessage(), ex);
                    }
                }
          }

          public void vd(Message msg){
              try{
                    System.out.println(msg.getTitle());
                    File myFile = new File (msg.getTitle());
                    out.writeBytes((int)myFile.length() + "\n");
                    out.flush();
                    if ((int)myFile.length() <= filesize){
                            byte [] mybytearray  = new byte [(int)myFile.length()];
                            FileInputStream fis = new FileInputStream(myFile);
                            BufferedInputStream bis = new BufferedInputStream(fis);
                            bis.read(mybytearray,0,mybytearray.length);
                            out.write(mybytearray,0,mybytearray.length);
                            //dataOutputStream.writeBytes("\n");
                            //textIn.setText("Sent...");
                            out.flush();
                            System.out.println(is.readLine());
                        }
                    }
                    catch(IOException ioException){
				ioException.printStackTrace();
			}
          }
}


