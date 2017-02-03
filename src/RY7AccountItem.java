import java.security.KeyStore.Entry;

public class RY7AccountItem implements Entry, net.jini.core.entry.Entry {
	
	public String username;
	public String password;
	public Integer login;
	
		public RY7AccountItem(){
			
		}
	
		public RY7AccountItem(String username){
			
			this.username = username;
		}
		
		public RY7AccountItem (String u, String p){
			
			username = u;
			password = p;
			login = 0;
		
		}

}
