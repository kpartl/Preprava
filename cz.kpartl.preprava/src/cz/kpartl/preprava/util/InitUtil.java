package cz.kpartl.preprava.util;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;

import cz.kpartl.preprava.dao.PozadavekDAO;
import cz.kpartl.preprava.dao.UserDAO;
import cz.kpartl.preprava.dao.DestinaceDAO;
import cz.kpartl.preprava.model.Pozadavek;
import cz.kpartl.preprava.model.User;
import cz.kpartl.preprava.model.Destinace;

import java.text.SimpleDateFormat;

@Creatable
public class InitUtil {
	
	public static final String LOGIN = "admin";
	public static final String PASSWORD = "istrator";
		
	
	@Inject
	private static UserDAO userDAO;	
	
	@Inject
	private static DestinaceDAO destinaceDAO;
	
	@Inject
	private static PozadavekDAO pozadavekDAO;
	
	public static void initDBData(){
								
		User adminUser = new User();
		adminUser.setUsername(LOGIN);
		adminUser.setPassword(UserDAO.encryptPassword(PASSWORD));
		adminUser.setAdministrator(true);
		
		userDAO.create(adminUser);
		
		Destinace destinace = new Destinace();
		destinace.setCislo(123);
		destinace.setKontaktni_osoba("Èistiè Viktor");
		destinace.setKontakt("cistic.viktor@bezstarosti.cz");
		destinace.setNazev("Bez starosti");
		
		destinace.setId(destinaceDAO.create(destinace));
		
		Destinace destinace2 = new Destinace();
		destinace2.setCislo(309613);
		destinace2.setKontaktni_osoba("Claudia Elvia Lopez Luna");
		destinace2.setKontakt("+52 444826 1232");
		destinace2.setNazev("Valeo Mexico");
		
		destinace2.setId(destinaceDAO.create(destinace2));
		
		
		Pozadavek pozadavek = new Pozadavek();
		pozadavek.setDatum(new Date(System.currentTimeMillis()));
		pozadavek.setDatum_nakladky("11.12.2012");
		pozadavek.setDatum_vykladky("23.12.2012");
		pozadavek.setJe_termin_konecny(true);
		pozadavek.setPocet_palet("100+3");
		pozadavek.setPoznamka("Protøepat, nemíchat");
		pozadavek.setCelkova_hmotnost("500 kg");
		pozadavek.setDestinace_z(destinace);
		pozadavek.setDestinace_do(destinace2);
		pozadavek.setTaxi(false);
		pozadavek.setHodina_nakladky("kolem obìda, nejlépe ve 12:00");
		pozadavek.setZadavatel(adminUser);
		
		
		pozadavek.setId(pozadavekDAO.create(pozadavek));
		
		pozadavek = new Pozadavek();
		pozadavek.setDatum(new Date(System.currentTimeMillis()+123456));
		pozadavek.setDatum_nakladky("13.12.2012");
		pozadavek.setDatum_vykladky("22.12.2012");
		pozadavek.setJe_termin_konecny(false);
		pozadavek.setPocet_palet("100+3");
		pozadavek.setPoznamka("Nechte to bejt");
		pozadavek.setDestinace_z(destinace2);
		pozadavek.setDestinace_do(destinace);
		pozadavek.setTaxi(true);
		
		pozadavek.setId(pozadavekDAO.create(pozadavek));
		
		
	}

}
