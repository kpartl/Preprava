package cz.kpartl.preprava.util;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;

import cz.kpartl.preprava.dao.DAOFactory;
import cz.kpartl.preprava.dao.PozadavekDAO;
import cz.kpartl.preprava.dao.UserDAO;
import cz.kpartl.preprava.model.Pozadavek;
import cz.kpartl.preprava.model.User;
import java.text.SimpleDateFormat;

@Creatable
public class InitUtil {
	
	public static final String LOGIN = "admin";
	public static final String PASSWORD = "istrator";
		
	
	@Inject
	private static UserDAO userDAO;	
	
	@Inject
	private static PozadavekDAO pozadavekDAO;
	
	public static void initDBData(){
								
		User adminUser = new User();
		adminUser.setUsername(LOGIN);
		adminUser.setPassword(userDAO.encryptPassword(PASSWORD));
		userDAO.create(adminUser);
		
		Pozadavek pozadavek = new Pozadavek();
		pozadavek.setDatum(new Date(System.currentTimeMillis()));
		pozadavek.setDatum_nakladky("11.12.2012");
		pozadavek.setDatum_vykladky("23.12.2012");
		pozadavek.setJe_termin_konecny(true);
		pozadavek.setPocet_palet("100+3");
		pozadavek.setPoznamka("Protøepat, nemíchat");
		
		pozadavek.setId(pozadavekDAO.create(pozadavek));
		
		pozadavek = new Pozadavek();
		pozadavek.setDatum(new Date(System.currentTimeMillis()+123456));
		pozadavek.setDatum_nakladky("13.12.2012");
		pozadavek.setDatum_vykladky("22.12.2012");
		pozadavek.setJe_termin_konecny(false);
		pozadavek.setPocet_palet("100+3");
		pozadavek.setPoznamka("Nechte to bejt");
		
		pozadavek.setId(pozadavekDAO.create(pozadavek));
		
		
	}

}
