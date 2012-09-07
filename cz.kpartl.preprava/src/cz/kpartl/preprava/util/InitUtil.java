package cz.kpartl.preprava.util;

import cz.kpartl.preprava.dao.DAOFactory;
import cz.kpartl.preprava.dao.UserDAO;
import cz.kpartl.preprava.model.User;

public class InitUtil {
	
	public static void initDBData(){
		DAOFactory daoFactory = new DAOFactory();
		UserDAO userDAO = daoFactory.getUserDAO();
		
		User adminUser = new User();
		adminUser.setUsername("admin");
		adminUser.setPassword(userDAO.encryptPassword("istrator"));
		userDAO.create(adminUser);
	}

}
