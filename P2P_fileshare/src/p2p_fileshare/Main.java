/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p2p_fileshare;
import java.util.*;
import java.io.*;

/**
 *
 * @author Adrian Ward-Manthey and Jeff Blanekship
 * 
 *C:\\Users\\Surface Book\\Desktop\\CCSU\\Spring 2017\\CS 490 Networking\\CS490-Group2-Final-Project\\P2P_fileshare\\src\\p2p_fileshare\\files
 * C:\Users\Surface Book\Desktop\Youtube Videos
 */

public class Main {
    public static void main(String[] args){
      Server MainServer = new Server();
      ArrayList<Song> song = new ArrayList<>();
      Peer p1 = new Peer();
      song = p1.getDirectory();
      System.out.println("PEER printDirectory Method");
      p1.printDirectory(song);
      //method to send this directory to server 
      //below is just a test to make sure that the server properly processes the arraylist and places
      //the values into a hashtable
       MainServer.processSongArray(song, MainServer.getTable() );
       System.out.println("SERVER printServerDirectory Method");
       MainServer.printServerDirectory(MainServer.getTable());
       //SO FAR SO GOOD!!!! NEED TO USE METHODS FROM RDT AND PACKET TO ACTUALLY SEND
       //ArrayList over UDP
      
      Peer p2 = new Peer();
      song = p1.getDirectory();
      p2.printDirectory(song);
      System.out.println("PEER2 printDirectory Method");
      p2.printDirectory(song);
      MainServer.processSongArray(song, MainServer.getTable() );
      System.out.println("SERVER printServerDirectory Method");
      MainServer.printServerDirectory(MainServer.getTable());
      //below is a test to make sure that the query works as intended...of course
      //in full application this will utilize RDT.java and Packet.java
      
      
     
    
    }
}