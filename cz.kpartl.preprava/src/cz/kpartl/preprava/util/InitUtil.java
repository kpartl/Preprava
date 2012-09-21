package cz.kpartl.preprava.util;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;

import cz.kpartl.preprava.dao.DAOFactory;
import cz.kpartl.preprava.dao.UserDAO;
import cz.kpartl.preprava.model.User;

@Creatable
public class InitUtil {
	
	public static final String LOGIN = "admin";
	public static final String PASSWORD = "istrator";
		
	
	@Inject
	private static UserDAO userDAO;		
	
	public static void initDBData(){
								
		User adminUser = new User();
		adminUser.setUsername(LOGIN);
		adminUser.setPassword(userDAO.encryptPassword(PASSWORD));
		userDAO.create(adminUser);
	}

}
