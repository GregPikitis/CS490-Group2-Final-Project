/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p2p_fileshare;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Scanner;
import javax.swing.JFileChooser;
/**
 *
 * @author Jeff Blankenship and Adrian Ward-Manthey
 * This class will allow contain functionality to allow the user to inform and update
 * It will also allow the user to 
 * 
 * USE IP.isReachable() to check and see if we can reach the IP before establishing a TCP connection
 */
public class Peer {
    
    public JFileChooser chooser = new JFileChooser();
    public File folder = null;
    public String centralServerIP = "";
    Hashtable<String, Song> Ltab = new Hashtable<>();
  
    // There's not real logging in, it mostly just checks for a response from the entered IP, using
    // the HTTP req/res system for a proof of concept.
    public void setIP() throws IOException, InterruptedException{
      System.out.print("Enter the IP to look for the Server: ");
      Scanner scan = new Scanner(System.in);
      String tryIP = scan.nextLine();
      
      //Try login attempt...basically a fancy ping
      String code = "L";    // I for inform and update
      String phrase = "L";  // I for inform and update (yes it's the same.  The response are the ones that are different than the code.
      InetAddress LocalIP = InetAddress.getLocalHost(); 
      String IPaddress = LocalIP.getHostAddress();
      String version = "1"; // because we only have one version!
      String payload = "Payload not used";  //this is just a filler
      HTTP login = new HTTP(code, phrase, IPaddress, version, payload);
      
      RDT.transmit( tryIP, Globals.MSG_PORT, login.asString());
      
      HTTP response = new HTTP("200","O","someIP","1","some payload");
      //RDT.listen (needs to be coded)...  get the HTTP response and if check to 200:Okay
      if ( response.getCode().equals("200") ) {
        System.out.println("Login succes, IP is working.");
        centralServerIP = tryIP;
        } else {
          centralServerIP = "";
          System.out.print("Login failed, that IP is not working.");
      }
      
      
    }
    
    public void chooseFolder(){
          // JFileChooser chooser = new JFileChooser();
              //FileNameExtensionFilter filter = new FileNameExtensionFilter(
              //    "mp3 files", "mp3");
              //chooser.setFileFilter(filter);
          chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
          int returnVal = chooser.showOpenDialog(null);
          //chooser.setCurrentDirectory(new java.io.File("."));
          if(returnVal == JFileChooser.APPROVE_OPTION) {
            //folder = chooser.getSelectedFile().getName());
            folder =  chooser.getSelectedFile();
            System.out.println("Selected folder: " + folder.getName() );
            // folder = chooser.getSelectedFile().getName());
          }
    }
    
    public String getDirectory() throws UnknownHostException{
      String songList= "";
      File[] listOfFiles = folder.listFiles();
      int j=0;
      for(File file : listOfFiles)
       if(file.isFile() && file.getName().endsWith(".mp3"))
           songList+= file.getName() + "\t" + file.length() + "\t" + InetAddress.getLocalHost().getHostAddress() + "\n"; 
      return songList;
    }
    
    //Add Hashtable to peer...modify to search table in peer instance later
    //Returns User Query as a string to be returned for transmit to Server
    
    public void informAndUpdate(Hash serv) throws UnknownHostException, IOException, InterruptedException{
      if (centralServerIP.equals("") || (folder==null) ) {
        System.out.println("Please set Central Server IP and sharing folder first.");
      }else{
        System.out.println("Informing Server...");
        //DO WE NEED THIS TEMP???
        String temp = getDirectory();
        //Build the HTTP request   public HTTP(String code, String phrase, String IPaddress, String version, String payload){
        String code = "I";    // I for inform and update
        String phrase = "I";  // I for inform and update (yes it's the same.  The response are the ones that are different than the code.
        InetAddress LocalIP = InetAddress.getLocalHost(); 
        String IPaddress = LocalIP.getHostAddress();
        String version = "1"; // because we only have one version!
        String payload = getDirectory();  //
        //CLEAR OUT LOCAL TABLE
        Ltab.clear();
        
        HTTP inform = new HTTP(code, phrase, IPaddress, version, payload);
        RDT.transmit( centralServerIP, Globals.MSG_PORT, inform.asString() );
        Song.processSongString(payload, Ltab);

        //rdt.transmit request
        //wait for a response
        //after response use song to Song.processSongString(response, Mtab) to add the string to hashtable
        //serv.processSongString(temp, serv.getTable());
        
        System.out.println(temp);
        Song.processSongString(temp,Ltab );
        Song.processSongString(temp, serv.getTable());
        //System.out.println(temp);
        //System.out.println("This is HASHTABLE in Server");
        //Song.printDirectory(serv.getTable());
        System.out.println("This is HASHTABLE in Peer");
        Song.printDirectory(Ltab);
        } //end else
    }
    
    
    public void exit() throws UnknownHostException, IOException, InterruptedException{
      if (centralServerIP.equals("") || (folder==null) ) {
        System.out.println("Please set Central Server IP and sharing folder first.");
      }else{
        System.out.println("Informing Server...");
        String temp = getDirectory();
        //Build the HTTP request   public HTTP(String code, String phrase, String IPaddress, String version, String payload){
        String code = "E";    // E for exit
        String phrase = "E";  // E for exit (repeated for emphasis, of course)
        InetAddress LocalIP = InetAddress.getLocalHost(); 
        String IPaddress = LocalIP.getHostAddress();
        String version = "1"; // because we only have one version!
        String payload = getDirectory();  //
        HTTP request = new HTTP(code, phrase, IPaddress, version, payload);
        RDT.transmit( centralServerIP, Globals.MSG_PORT, request.asString() );

        //rdt.transmit request
        //wait for a response
        //serv.processSongString(temp, serv.getTable());
        
        //System.out.println(temp);
        //Song.processSongString(temp,Ltab );
        //Song.processSongString(temp, serv.getTable());
        //System.out.println(temp);
        //System.out.println("This is HASHTABLE in Server");
        //Song.printDirectory(serv.getTable());
        System.out.println("Central Server has been notified to remove files.  Have a peachy day.");
        //Song.printDirectory(Ltab);
        } //end else
    }
    
    
    
    
    public String makeQuery(){
         System.out.print("Please enter file name that you would like to query: ");
         String q;
         Scanner query = new Scanner(System.in);
         q = query.nextLine();
         q = q.toLowerCase().replace(" ", "");
         return q;
    }
    
    
    //This Method will handle the bulk of userInteraction with the Server
    public void userInteraction(Hash serv) throws UnknownHostException, IOException, InterruptedException{
    Scanner in = new Scanner(System.in);
    String input = "I";
    
    while(!input.equals("E")){
      String temp;
      System.out.println("\nWelcome to the network!!!!");
      System.out.println("S: Set Central Server IP");
      System.out.println("F: Select sharing folder");
      System.out.println("I: Inform and Update");
      System.out.println("Q: Query for content");
      System.out.println("R: Request Content");
      System.out.println("E: Exit Network");
      System.out.print("Enter desired operation: ");
      input = in.nextLine().toUpperCase();
      
        if(input.equals("I")){
          informAndUpdate(serv);
        }
        else if(input.equals("Q")){
            String query = makeQuery();
            //RDT transmit query 
            serv.processQuery(serv.getTable(), query);
        }
        else if(input.equals("R")){
           System.out.println("Handling Request-Not Yet But Eventually");
        }
        else if(input.equals("F")){
          chooseFolder();
        } 
        else if(input.equals("S")){
          setIP();
        }
        else if(input.equals("E")){
          exit();
        }else
            System.out.println("Please Enter a valid input:");
      }
    System.out.println("Exiting Network and Deleting Corresponding Entries in Server");
    in.close();
  } 
    
}