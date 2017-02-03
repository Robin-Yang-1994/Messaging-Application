import net.jini.space.*;
import net.jini.core.lease.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;

import javax.swing.*;
import net.jini.core.transaction.*;
import net.jini.core.transaction.server.*;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.core.event.*;
import net.jini.space.JavaSpace;

import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import javax.swing.border.LineBorder;



public class MessageSender extends JFrame implements RemoteEventListener{
    private JavaSpace05 space;
    private JPanel jPanel1, jPanel2;
    private JPanel jPanel1_1;
    private String userName;
    private JLabel messageLabel, messageNameLabel;
    private JTextField messageIn, messageNumberOut, toNameIn;
    private JButton sendMessageButton;
    private TransactionManager mgr;
    private RemoteEventListener theStub;
    private JCheckBox notificationOnOff;
    private JComboBox userList;
    private JButton btnReplyMessage;
    private JButton clearmesssageButton;
    private JList displayMessage,displayMessage1;
    private JButton viewSavedMessage;
    private JButton saveMessageButton;
    private JButton btnLogOff;
    
    public MessageSender(String username) {
    	setResizable(true);
    	setRootPaneCheckingEnabled(false);
    	setSize(new Dimension(1500, 900));
    	setBackground(Color.ORANGE);
    	
	space = (JavaSpace05)SpaceUtils.getSpace(); // get java space manager
	if (space == null){
	    System.err.println("Failed to find the javaspace");
	    System.exit(1);
	}
	
	 mgr = SpaceUtils.getManager(); //  get transactions manager
     if (mgr == null){ 
         System.err.println("Failed to find the transaction manager");
         System.exit(1);
     }
     
     Exporter myDefaultExporter =  
    		    new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
    					  new BasicILFactory(), false, true);
    		try {
    		    theStub = (RemoteEventListener) myDefaultExporter.export(this);
    		    
    		    RY11QueueStatus template = new RY11QueueStatus();
    		  
    		    space.notify(template, null, this.theStub, Lease.FOREVER, null); // default java notify method from java space
    		    																// calls the notify method	
    		} catch (Exception e) {
    		    e.printStackTrace();
    		}
	

	userName = username;	// username of the application (username object)
	displayMessage = new JList(); // message list inside the jlist box
	getContentPane().add(displayMessage, BorderLayout.CENTER);
	displayMessage.setPreferredSize(new Dimension(300, 300)); // set size for chat box
	userInterface (); // GUI
	pack ();
	setVisible(true); // show GUI
	setLocationRelativeTo(null); //centre the jframe
	takeAllMessages(); // run method to get all message as the user login
	
    }
    
    private void userInterface () { // gui code
	setTitle ("Username: " + userName);
	addWindowListener (new java.awt.event.WindowAdapter () {
	    public void windowClosing (java.awt.event.WindowEvent evt) {
		System.exit(0);
	    }
	}   );
	
	Container cp = getContentPane();
	cp.setLayout (new BorderLayout ());

	jPanel1 = new JPanel();
	jPanel1.setLayout(new BorderLayout(0, 0));
	
	cp.add(jPanel1,BorderLayout.EAST);
		
	DefaultListModel listmodel;
	listmodel = new DefaultListModel();
	
	displayMessage = new JList();
	displayMessage.setBorder(new LineBorder(new Color(0, 0, 0)));
	displayMessage.setBackground(new Color(211, 211, 211));
	getContentPane().add(displayMessage, BorderLayout.CENTER);
	displayMessage.setPreferredSize(new Dimension(300, 300));
	displayMessage.setModel(listmodel);
	
	
	DefaultListModel listmodel1;
	listmodel1 = new DefaultListModel();
	
	displayMessage1 = new JList();
	displayMessage1.setBorder(new LineBorder(new Color(0, 0, 0)));
	displayMessage1.setBackground(new Color(211, 211, 211));
	getContentPane().add(displayMessage1, BorderLayout.EAST);
	displayMessage1.setPreferredSize(new Dimension(300, 300));
	displayMessage1.setModel(listmodel1);
	
	jPanel1_1 = new JPanel ();
	jPanel1_1.setBackground(Color.ORANGE);
	jPanel1_1.setLayout (new FlowLayout ());
	
	userList = new JComboBox();
	jPanel1_1.add(userList);
	
	btnReplyMessage = new JButton("Reply Message");
	btnReplyMessage.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			replyMessage(evt);
		}
	});
	jPanel1_1.add(btnReplyMessage);

	messageLabel = new JLabel ();
	messageLabel.setFont(new Font("Arial Black", Font.PLAIN, 12));
	messageLabel.setText ("Enter Message: ");
	jPanel1_1.add (messageLabel);

	messageIn = new JTextField (12);
	messageIn.setBackground(Color.LIGHT_GRAY);
	messageIn.setText ("");
	jPanel1_1.add (messageIn);

	messageNameLabel = new JLabel ();
	messageNameLabel.setFont(new Font("Arial Black", Font.PLAIN, 12));
	messageNameLabel.setText ("To: ");
	jPanel1_1.add (messageNameLabel);

	toNameIn = new JTextField (12);
	toNameIn.setBackground(Color.LIGHT_GRAY);
	toNameIn.setText ("");
	jPanel1_1.add (toNameIn);

	cp.add (jPanel1_1, "North");
	
	btnLogOff = new JButton("Log Off");
	btnLogOff.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	});
	btnLogOff.setBackground(Color.LIGHT_GRAY);
	jPanel1_1.add(btnLogOff);

	jPanel2 = new JPanel ();
	jPanel2.setBackground(Color.ORANGE);
	jPanel2.setLayout (new FlowLayout ());
	
	sendMessageButton = new JButton();
	sendMessageButton.setBackground(Color.LIGHT_GRAY);
	sendMessageButton.setText("Send Message");
        sendMessageButton.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent e) {
            	writeMessage(e);
            }
	}  );
	
	saveMessageButton = new JButton("Save Message");
	saveMessageButton.setBackground(Color.LIGHT_GRAY);
	saveMessageButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			saveMessage(evt);
		}
	});
	
	notificationOnOff = new JCheckBox("Notification On/Off");
	notificationOnOff.setBackground(Color.LIGHT_GRAY);
	jPanel2.add(notificationOnOff);
	jPanel2.add(saveMessageButton);
	notificationOnOff.setEnabled(true);

	viewSavedMessage = new JButton("View Saved Message");
	viewSavedMessage.setBackground(Color.LIGHT_GRAY);
	viewSavedMessage.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			viewSavedMessage(evt);
		}
	});
	jPanel2.add(viewSavedMessage);
	jPanel2.add(sendMessageButton);

	cp.add (jPanel2, "South");

	clearmesssageButton = new JButton("Clear");
	clearmesssageButton.setBackground(Color.LIGHT_GRAY);
	clearmesssageButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			DefaultListModel listmodel = (DefaultListModel) displayMessage.getModel();
			int selectedText = displayMessage.getSelectedIndex();
			if(selectedText != -1){
				listmodel.remove(selectedText);
			}
		}
	});
	jPanel2.add(clearmesssageButton);
	
    }
    
    
    public void writeMessage(java.awt.event.ActionEvent evt){ // create template and send message using transactions
    	
    	  String nameBox = toNameIn.getText(); // get text from the user name and password input box 
          String messageBox = messageIn.getText();   
    	
    	if (nameBox.isEmpty()|| messageBox.isEmpty()) { // check value inside these input boxes before running the try method
    		JOptionPane eMsg = new JOptionPane();
    		JOptionPane.showMessageDialog(jPanel1, "Enter username and message");
    		System.out.println("Enter username and message");
    	}
    		else{
    	try {
    		
            Transaction.Created trc = null;
            try {
                 trc = TransactionFactory.create(mgr, 3000); // create new transaction
            } catch (Exception e) {
                 System.out.println("Could not create transaction " + e);;
            }

            Transaction txn = trc.transaction;
    		
    	    RY11QueueStatus qsTemplate = new RY11QueueStatus(); // create new template
    	    RY11QueueStatus qStatus = (RY11QueueStatus)space.take(qsTemplate,txn, Long.MAX_VALUE); // take template, with transactions

    	    String message = messageIn.getText(); //String value
    	    String receiver = toNameIn.getText(); //String value
    	    String sender = userName; //String value test
    	    
    	    RY11QueueItem newMessage = new RY11QueueItem(message, receiver, sender); // add to template
    	    
    	    space.write(newMessage, txn, 1000 * 60 * 5); // transactions used for writing message to space

    	    qStatus.writeMessage();
    	    space.write( qStatus, txn, Lease.FOREVER); // write to space
    	    txn.commit(); // committing the transaction
    	    System.out.println("Transactions is Working");
    	}  catch ( Exception e) {
    	    e.printStackTrace();
    	    System.out.println("Transaction failed " + e);
    	}
        }}
    
    public void replyMessage(java.awt.event.ActionEvent evt){
    	DefaultListModel listmodel = (DefaultListModel) displayMessage.getModel();
		String selectedText = displayMessage.getSelectedValue().toString(); // get value from the string
		String [] textName = selectedText.split(":"); // split message according to string
		
		String getName = textName[1].trim(); // get the first split element and set the name input according to the picked up object
		toNameIn.setText(getName); // set text according to element
		
    }
    
    public void takeAllMessages(){
    	
    	Transaction.Created trc = null; // transactions for getting all messages
        try {
             trc = TransactionFactory.create(mgr, 3000); // create new transaction
        } catch (Exception e) {
             System.out.println("Could not create transaction " + e);;
        }
        
       Transaction txn = trc.transaction;
    	
    	Collection<RY11QueueItem> templates = new ArrayList<RY11QueueItem>(); // change templates in queue item into array list

    	RY11QueueItem template = new RY11QueueItem();
    	template.receiver = userName; 
		templates.add(template);

		try { // same as view saved message method to get iterated value from while loop
			
			Collection<?> results = space.take(templates, txn, 100, Long.MAX_VALUE); // take collection from space

			Iterator<?> i = results.iterator();
			while (i.hasNext()) { // find next message through while loop
				RY11QueueItem s = (RY11QueueItem) i.next();
				//System.out.println(s.contents);
				
				DefaultListModel listmodel;
    			listmodel = (DefaultListModel) displayMessage.getModel();
    			
    			listmodel.addElement("From" + ":" + s.sender + " : " + s.message + "\n" ); // display message in the list
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

    }
    
    public void saveMessage(java.awt.event.ActionEvent evt){
    	 try
    	 {
    		 String message = (String) displayMessage.getSelectedValue(); // gets the selected value from the list and the user name
    		 
    		 String name = userName;
    		 
    		 RY11QueueItem savedTemplate = new RY11QueueItem(message, name); // save the message templates
    		 
    		 try{
    			 space.write(savedTemplate, null, Lease.FOREVER); // writes to add message to the space according to user
    			 
    			 System.out.println("Message Saved"); // display message when message is saved to space 
    		 }  catch ( Exception e) {
    	    	    e.printStackTrace();
    		 }}
    	 catch ( Exception e) {
	    	    e.printStackTrace();
    	 }
    	
    }
    
    public void viewSavedMessage(java.awt.event.ActionEvent evt){
    	
    	DefaultListModel listmodel; // creating a list model for the jlist message box
    	listmodel = (DefaultListModel) displayMessage1.getModel(); // get the list model
    	
    	try {
    		
            Transaction.Created trc = null; // try creating transactions
            try {
                 trc = TransactionFactory.create(mgr, 3000);
            } catch (Exception e) {
                 System.out.println("Could not create transaction " + e);;
            }
            
           Transaction txn = trc.transaction; // new transaction
           
           System.out.println("Transactions is Working");
           
           Collection<RY11QueueItem> template = new ArrayList<RY11QueueItem>(); // change queue item value into collection of template values to array of collection templates
           																	
           RY11QueueItem displayMessage = new RY11QueueItem();					
           
           displayMessage.sender = userName; // define username
           
           String mess = displayMessage.message; // define message
           
           template.add(displayMessage); // add the selected message to the collection template
           
           Collection<?> storedMessage = space.take(template, txn, 100,Long.MAX_VALUE); //take it from the space onto template
           
           Iterator<?> i = storedMessage.iterator(); // create new iterator object 
           
           while (i.hasNext()){ // while loop to iterate through the list to retrieve saved collection of messages
        	   
        	   RY11QueueItem saved = (RY11QueueItem) i.next(); // create saved to be the message it has found
   			   listmodel.addElement(saved.message+ "\n" ); // add these messages to the list
   			   displayMessage1.setModel(listmodel);
   			   
   			   space.write(saved, txn, Long.MAX_VALUE); // write it back to the space to view the message again in a later time
           } } catch ( Exception e) {
   			e.printStackTrace();
           }
    }
        	   
    public void notify(RemoteEvent evt) {
    	
    	// default notify message using java notify initiated above
    	
    	if(notificationOnOff.isSelected() == false){ // if the button has not been selected, do the following, otherwise run 'else' statement
    		   
    		System.out.println("Notify Enabled"); // display message notify on
    		
    		RY11QueueItem template = new RY11QueueItem();
    		template.receiver = userName; // sender name
        	
        	 try {
        			
        		RY11QueueItem nextMessage = (RY11QueueItem)space.take(template,null,Long.MAX_VALUE); // take message from space
        			
        			String nextSendMessage = nextMessage.message; // new message object
        			String nextSenderName = nextMessage.sender; // new sender name object
        			
        			DefaultListModel listmodel;
        			listmodel = (DefaultListModel) displayMessage.getModel(); // create new model for adding element to list
        			
        			listmodel.addElement("From" + ":" + nextSenderName + " : " + nextSendMessage + "\n" );
        			
        			displayMessage.setModel(listmodel);
        			userList.addItem(nextSenderName);
        			System.out.println("You have received a message from:" + " "+ nextSenderName);
        		    }  catch ( Exception e) {
        			e.printStackTrace();
            }
    	}
    		else {
    			// same method as above but without notify message
    			System.out.println("Notify Disabled"); // display message notify off
    			
    			try{
    	    	
    	    	RY11QueueItem template = new RY11QueueItem();
    			template.receiver = userName;
    			RY11QueueItem nextMessage = (RY11QueueItem)space.take(template,null,Long.MAX_VALUE); // take message from space
    			
    			String nextSendMessage = nextMessage.message; // new message object
    			String nextSenderName = nextMessage.sender; // new sender name object
    			
    			
    			DefaultListModel listmodel;
    			listmodel = (DefaultListModel) displayMessage.getModel(); // create new model for adding element to list
    			
    			listmodel.addElement("From" + ":" + nextSenderName + " : " + nextSendMessage + "\n" );
    			}catch ( Exception e) {
        			e.printStackTrace();
        	    	}
    		}
    		}
    
    

    
    public static void main(java.lang.String[] args) throws RemoteException {
    if (System.getSecurityManager() == null) // security manager
    	    System.setSecurityManager(new RMISecurityManager());

	if (args.length != 1){
	    System.out.println("Usage:Error");
	    System.exit(0);
	} else {
	    new MessageSender(args[0]); // method for starting new class
	    
	}
    }
}
