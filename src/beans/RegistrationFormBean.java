package beans;


public class RegistrationFormBean implements java.io.Serializable {
	
	private Long 	id;
	private String 	eMail;
	private String 	userName;
	private String 	firstName;
	private String 	lastName;
	private String 	password; //TODO: Wird das ben�tigt, obwohl das Passwort nie angezeigt wird / werden kann?

	public RegistrationFormBean() {
	
	}

	public String geteMail() {
		return eMail;
	}

	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getId() {		
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}