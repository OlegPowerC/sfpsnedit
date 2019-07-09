/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ja1;
import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.*;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 *
 * @author Oleg
 */
public class Ja1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        ByteBuffer BB = ByteBuffer.allocate(256);
        int readed_bytes = 0;
        byte BB_as_array[];
        byte md5_src_str[];
        byte skey1[];
        short skey12[] = {0x4A,0xF8,0x67,0x16,0xED,0x1E,0x2F,0x34,0x7C,0xA1,0x3C,0x99,0x78,0xAD,0x8C,0xA0};
        short skey11[] = {0x8D,0xDA,0xE6,0xA4,0x6E,0xC9,0xDE,0xF6,0x10,0x0B,0xF1,0x85,0x05,0x9C,0x3D,0xAB};
        
        byte SN[] = {32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32};
        
        skey1 = new byte[16];
       for(short a = 0; a < 16; a++)
       {
           skey1[a] = (byte) skey11[a];
       }
        
        md5_src_str = new byte[49];
        if(args.length < 2)
        {
            System.out.println("Error: need filename and SN"); 
        }
        else
        {
            
            
            System.out.println("OK Start to calc " + args[0]);
            FileChannel FC = new FileInputStream(args[0]).getChannel();
            readed_bytes = FC.read(BB);
            System.out.println("Readed");
            BB_as_array = BB.array();
            byte sum22 = 0;
            byte SN2[] = args[1].getBytes();
            System.arraycopy(SN2, 0, SN, 0, SN2.length);
            
            System.arraycopy(SN, 0, BB_as_array, 68, 16);
            for(char a = 64; a < 95; a++)
            {
                sum22 += BB_as_array[a];
                
            }
            BB_as_array[95] = sum22;
            
            File scoreFile = new File("out.bin");
            if(!scoreFile.exists()) {
                scoreFile.createNewFile();
                } 
            FileOutputStream oFile = new FileOutputStream(scoreFile, false);      
            
            System.out.println("Get Vendor ID");
            System.arraycopy(BB_as_array, 98, md5_src_str, 0, 1);
            System.out.println("Get Vendor name");
            System.arraycopy(BB_as_array, 20, md5_src_str, 1, 16);
            System.out.println("Get SN");
            System.arraycopy(BB_as_array, 68, md5_src_str, 17, 16);
            System.out.println("Copy Key");
            System.arraycopy(skey1, 0, md5_src_str, 33, 16);
            System.out.println("Calc MD5");
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(md5_src_str);
            byte[] mdig = m.digest();
 
            //Копируем MD5 в файл
            System.arraycopy(mdig, 0, BB_as_array, 99, 16);
                              
            System.out.println("OK ");
            
            Checksum checksum = new CRC32();
            checksum.reset();

            checksum.update(BB_as_array, 96, 28);
            long CRM = checksum.getValue();
            byte CRC_B[] = new byte[4];
            CRC_B[0] = (byte) (CRM);
   
            CRC_B[1] = (byte) (CRM >> 8);
      
            CRC_B[2] = (byte) (CRM >> 16);
         
            CRC_B[3] = (byte) (CRM >> 24);
            
            System.arraycopy(CRC_B, 0, BB_as_array,124, 4);
           
            oFile.write(BB_as_array);
            oFile.flush();
            oFile.close();
            System.out.println("OK " );
    }
        
    }
}
