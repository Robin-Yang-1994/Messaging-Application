import net.jini.core.entry.*;

public class RY11QueueStatus implements Entry{
    // Variables
    public Integer nextMessage;
    public String contents;
    
    // No arg contructor
    public RY11QueueStatus (){
    }

    public RY11QueueStatus (int n){
	// set count to n
	nextMessage = new Integer(n);
	
    }

    public void writeMessage(){
	nextMessage = new Integer(nextMessage.intValue() + 1);
	
	//shows number of messages, increase by 1 upon a message is sent
	
	// used to increment message value by 1 but not used in gui to check how many message has been sent 
	
    }
    
}
