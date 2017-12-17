/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharefile;
import java.net.*;
import java.io.*;
import java.sql.ResultSet;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import sharefile.jdbc;
/**
 *
 * @author JUHI AGRAWAL
 */

public class SendFile extends Thread {
    
    Socket sock;
    String ip;
    String loc;
    String recname;
    FileInputStream finp;
    DataOutputStream dout;
    
    public SendFile(String i,String l,String u){
        ip=i;
        loc=l;
        recname=u;
    }
    
    public void run(){
        
        try{
            //send file ka code
            sock = new Socket(ip,4444);
            
            dout = new   DataOutputStream(sock.getOutputStream());
            dout.writeUTF("recieve file "+loc);
            dout.flush();
            
            File f = new File(loc);
            
            finp = new FileInputStream(f);
            int count;
            byte[] bytes = new byte[16 * 1024];
            while ((count = finp.read(bytes)) > 0) {
                dout.write(bytes, 0, count);
            }
            System.out.println("File sent");

            finp.close();   
        
            String filename=loc.substring(loc.lastIndexOf(File.separator)+1);
            String type=filename.substring(filename.lastIndexOf(".")+1);
            long size = f.length();
            System.out.println(filename+type+size);
            jdbc j =new jdbc();
            String query="Insert into `fileinfo`(`name`,`type`,`size`) values('"+filename+"','"+type+"','"+size+"')";
            System.out.println(query);
            j.execute(query);
            query="Select `id` from `fileinfo` where `name` like '"+filename+"' and `size`='"+size+"'";
            ResultSet []rset1= new ResultSet[10];
            j.search(query, rset1);
            int fileid=0;
            while(rset1[0].next()){
                 fileid=rset1[0].getInt("id");
            }

            String dt=java.time.LocalDate.now().toString();
            query="INSERT INTO `send`(`fileid`, `rec_name`, `date`) VALUES ('"+fileid+"','"+recname+"','"+dt+"')";
            System.out.println("sec query "+query);
        }
        catch(Exception e)
        {}
        finally{
            try {
                sock.close();
            } catch (IOException ex) {
                Logger.getLogger(SendFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        
        
    }
}
