
import net.jini.space.*;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.*;
import net.jini.core.transaction.TransactionException;
import java.awt.*;
import java.rmi.RemoteException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;



public class RY5UserAccount extends JFrame {
    private JavaSpace space;
    private JPanel jPanel1;
    private JLabel usernameLB;
    private JLabel passwordLB;
    private JTextField usernameFD;
    private JPasswordField passwordFD;
    private JLabel errorMsg;
    private JButton registerBTN;
    private JButton loginBTN;
   

    public RY5UserAccount() {   

        space = SpaceUtils.getSpace(); // find and get space 
        if (space == null){
            System.err.println("Failed to find the javaspace");
            System.exit(1);
        }
        userInterface();
    }
    
    public void userInterface() { // gui code

        setSize(551, 300);

        jPanel1 = new JPanel();
        jPanel1.setBackground(Color.ORANGE);
        jPanel1.setBorder(new EmptyBorder(200, 100, 200, 100));
        setContentPane(jPanel1);
        jPanel1.setLayout(null);

        usernameLB = new JLabel();
        usernameLB.setBounds(159, 40, 117, 25);
        usernameLB.setText("Username:");
        usernameLB.setFont(new Font("Arial Black", Font.PLAIN, 16));
        jPanel1.add(usernameLB);

        usernameFD= new JTextField();
        usernameFD.setBounds(307, 42, 125, 25);
        jPanel1.add(usernameFD);

        passwordLB = new JLabel();
        passwordLB.setBounds(159, 103, 117, 25);
        passwordLB.setText("Password:");
        passwordLB.setFont(new Font("Arial Black", Font.PLAIN, 16));
        jPanel1.add(passwordLB);

        passwordFD = new JPasswordField();
        passwordFD.setBounds(307, 100, 125, 31);
        passwordFD.setText("");
        jPanel1.add(passwordFD);

        registerBTN = new JButton("Register");
        registerBTN.setBounds(159, 175, 117, 25);
        registerBTN.setBackground(Color.LIGHT_GRAY);
        registerBTN.setForeground(Color.DARK_GRAY);
        registerBTN.setFont(new Font("Calibri", Font.BOLD, 16));
        registerBTN.addActionListener (new java.awt.event.ActionListener () {
            public void actionPerformed (java.awt.event.ActionEvent evt) {
                    try {
						NewUser (evt);
					} catch (RemoteException | UnusableEntryException | TransactionException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            }
        }  );
        jPanel1.add(registerBTN);

        loginBTN = new JButton("Login");
        loginBTN.setBounds(315, 175, 117, 25);
        loginBTN.setBackground(Color.LIGHT_GRAY);
        loginBTN.setForeground(Color.DARK_GRAY);
        loginBTN.setFont(new Font("Calibri", Font.BOLD, 16));
        loginBTN.addActionListener (new java.awt.event.ActionListener () {
            public void actionPerformed (java.awt.event.ActionEvent evt) {
                Login(evt);
            }
        }  );   
        jPanel1.add(loginBTN);

        errorMsg = new JLabel();
        errorMsg.setBounds(47, 187, 492, 64);
        errorMsg.setVisible(false);
        jPanel1.add(errorMsg);
    }
  
    // method to create new user
    public void NewUser(java.awt.event.ActionEvent evt) throws RemoteException, UnusableEntryException, TransactionException, InterruptedException {

        String username = usernameFD.getText();
        String password = new String(passwordFD.getPassword());   

        if (username.isEmpty() || password.isEmpty()) { // check value inside both input box for value validation
            errorMsg.setText("Please enter both username and password");
            errorMsg.setVisible(true);           
            System.out.print("Please enter both username and password");
        }	
        
        // checking user input for both fields, error message if one field is empty
       
       
        else if (username!=null & password !=null)
       
        {		// if user exist display message that user already exist in the space
                RY7AccountItem template = new RY7AccountItem();
                template.username = username;		
                if (space.readIfExists(template,null,Long.MAX_VALUE)!= null) { // read if user data exist by checking space if this value is not null
                    JOptionPane.showMessageDialog(jPanel1, "Username exist", username, JOptionPane.ERROR_MESSAGE);
                    System.out.println("User already exist");
                }
                else {
                	// add user to space
                	RY7AccountItem template1 = new RY7AccountItem(username,password);
                    space.write(template1, null, Lease.FOREVER); // write user information template to space
                    JOptionPane.showMessageDialog(jPanel1, "User has been added", username, JOptionPane.INFORMATION_MESSAGE);
                    MessageSender method = new MessageSender(username); // run the message sender method which will be added user account
                    System.out.println("User has been added");
                    dispose(); // close registration
                }
        }
        }
               
   
    // login method
    public void Login(java.awt.event.ActionEvent evt) {

        String username = usernameFD.getText();
        String password = new String(passwordFD.getPassword());   

        if (username.isEmpty() || password.isEmpty()) { // check value inside both input box for value validation
            errorMsg.setText("Please enter username and password");
            errorMsg.setVisible(true);
            System.out.print("Please enter username and password");
        }	
        // checking user input for both fields, error message if one field is empty

        else {
            try {
            	RY7AccountItem template = new RY7AccountItem(); // create new template for storing user name and password
                template.username = username;
                template.password = password;   

                RY7AccountItem user = (RY7AccountItem) space.takeIfExists(template,null,Long.MAX_VALUE); // take space user name or password if exist 

                if (user != null) {       

                    if (user.login == 1) { // reset user login value
                        user.login = 0;
                    } // using 0 and 1 to define true or false and 0 is the value initialised in the account item class  
                        else if (user.login == 0){ // using boolean to check if user is in the entry for login
                        			user.login = 1;
                        MessageSender MessMethod = new MessageSender(username); // launch method for message sender, login user
                        space.write(user,null,Long.MAX_VALUE);
                        dispose();   
                    }
                    
//                    else {
//                        MessageSender messMethod = new MessageSender(username);  
//                        dispose();
//                    }

                }
                else { // else it means user name or password does not exist in space
                    System.out.print("Username/Password doesn't match"); // error message because credentials does not match
                    errorMsg.setText("Username/Password doesn't match");
                    errorMsg.setVisible(true);
                }
            }  catch ( Exception e) {
                e.printStackTrace();
            }}
        }
    
   
    public static void main(String[] args) { // main method
              
    	RY5UserAccount newGUI = new RY5UserAccount(); // new login/register 
        newGUI.setVisible(true);
        newGUI.setLocationRelativeTo(null); // set gui to centre             
    }

}

