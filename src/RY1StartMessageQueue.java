import net.jini.space.*;
import net.jini.core.lease.*;

public class RY1StartMessageQueue{
    public static void main(String args[]){
	JavaSpace space = SpaceUtils.getSpace();
	if (space == null){
	    System.err.println("Failed to find the javaspace");
	    System.exit(1);
	}

	try {
	    RY11QueueStatus qs = new RY11QueueStatus(0);
	    space.write( qs, null, Lease.FOREVER);
	} catch ( Exception e) {
	    e.printStackTrace();
	}
    
   }
}
