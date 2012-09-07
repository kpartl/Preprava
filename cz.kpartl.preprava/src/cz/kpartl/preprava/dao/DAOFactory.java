package cz.kpartl.preprava.dao;

import javax.inject.Inject;

public class DAOFactory {
	
	public static final String CONTEXT_NAME = "DAOFACTORY";
	
	@Inject
	private UserDAO userDAO;
	
	public UserDAO getUserDAO(){
		if (userDAO == null) userDAO = new UserDAO();
		return userDAO;
	}

}
