import net.jini.core.entry.*;

public class RY11QueueItem implements Entry{
    // Variables
    public String message;
    public String receiver;
    public String sender;
    
    // No arg contructor
    public RY11QueueItem (){
    }
    
    // Arg constructor
    public RY11QueueItem (String m){
	message = m;
    }

    // Full Arg Constructor
    public RY11QueueItem (String m, String recipient,String sName){
	message = m;
	receiver = recipient;
	sender = sName;
    }
    
    public RY11QueueItem (String mName, String sName){
    message = mName; 
    sender = sName;
    	
    }
    
 // Constructor on storing objects from the Message Sender Class
}
