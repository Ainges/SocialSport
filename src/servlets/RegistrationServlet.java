package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Enumeration;

import javax.sql.DataSource;

import beans.UserBean;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.SessionScoped;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utilities.HashPassword;

/**
 * @author Hubertus Seitz
 */
@WebServlet("/registrationServlet")
@SessionScoped
public class RegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Verbindung zur Datenbank deklarieren
	@Resource(lookup = "java:jboss/datasources/MySqlThidbDS")
	private DataSource ds;

	public RegistrationServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) // doGet darf hier nicht aufgerufen werden
			throws ServletException, IOException {

		response.sendError(HttpServletResponse.SC_FORBIDDEN);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Als erstes werden alle vorhergesehenen Paramter extrahiert.
		request.setCharacterEncoding("UTF-8");
		UserBean form = new UserBean(); // Erstellung der Bean
		HttpSession session = request.getSession();

		final Enumeration<String> formInputs = request.getParameterNames();
		final String eMail = request.getParameter("email");
		form.seteMail(eMail);
		final String userName = request.getParameter("userName");
		form.setUsername(userName);
		final String firstName = request.getParameter("firstName");
		form.setFirstName(firstName);
		final String lastName = request.getParameter("lastName");
		form.setLastName(lastName);
		
		//https://stackoverflow.com/questions/18257648/get-the-current-date-in-java-sql-date-format
		final java.sql.Date creationDate = new java.sql.Date(new java.util.Date().getTime());
		form.setCreationDate(creationDate);

		final String password = HashPassword.hashPassword(request.getParameter("password")); // Passwort als hash abspeichern
		boolean errorFound = false;

		session.setAttribute("form", form); // Bean in Session abspeichern

		// Überprüfung ob eines der übergebenen Paramter entweder NULL oder Leer ist.

		while (formInputs.hasMoreElements()) {
			String inputName = (String) formInputs.nextElement();
			String inputValue = request.getParameter(inputName);
			if (inputValue.isEmpty() || inputValue == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				errorFound = true;
			}
		}

		if (!errorFound)
		{

			createNewUser(eMail, userName, firstName, lastName, password, creationDate); 	// User in Datenbank schreiben
			form.setId(getUserId(form.getUsername()));										// generierte id aus Datenbank auslesen (damals war uns das mit den generated Keys noch nicht bewusst)   
			response.sendRedirect("html/registrationSuccsess.jsp");							// Redirect, da auf DB schreibend zugegriffen wird.
			
		}

	}




	public Long getUserId(String username) throws ServletException { // Funktion zum auslesen der id eines Users

		try (Connection con = ds.getConnection();
				PreparedStatement pstmt = con.prepareStatement("SELECT id FROM users WHERE username = ?")) {

			pstmt.setString(1, username); // id anhand des username holen
			
			Long id = 0L;
			
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) { 
				id = (rs.getLong("id"));
				}
				return id;
			}
		}

		catch (Exception ex) {
			throw new ServletException(ex.getMessage());
		}
	}
	
	public void createNewUser(String eMail, String userName, String firstName, String lastName, String password, Date creationDate ) throws ServletException { // Funktion zum anlegen eines Users
		try (Connection con = ds.getConnection();
				PreparedStatement pstmt = con.prepareStatement(
						"INSERT INTO users (email,username,firstName, lastname, pwd, creationDate) VALUES(?,?,?,?,?,?)")) {

			// Datenbank Operationen
			pstmt.setString(1, eMail);
			pstmt.setString(2, userName);
			pstmt.setString(3, firstName);
			pstmt.setString(4, lastName);
			pstmt.setString(5, password);
			pstmt.setString(6, creationDate.toString());
			pstmt.executeUpdate();
			

		} catch (Exception ex) {
			throw new ServletException(ex.getMessage());
		}
		
		
	}
	
	
}