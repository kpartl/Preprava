package cz.kpartl.preprava.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.hibernate.Transaction;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kpartl.preprava.dao.DestinaceDAO;
import cz.kpartl.preprava.dao.DopravceDAO;
import cz.kpartl.preprava.dao.ObjednavkaDAO;
import cz.kpartl.preprava.dao.PozadavekDAO;
import cz.kpartl.preprava.dao.UserDAO;
import cz.kpartl.preprava.model.Destinace;
import cz.kpartl.preprava.model.Dopravce;
import cz.kpartl.preprava.model.Objednavka;
import cz.kpartl.preprava.model.Pozadavek;
import cz.kpartl.preprava.model.User;

public class DatabaseFiller {
	final static Logger logger = LoggerFactory.getLogger(DatabaseFiller.class);
	final static Session session = HibernateHelper.getInstance().getSession();
	
	final static DestinaceDAO destinaceDAO = new DestinaceDAO();
	final static DopravceDAO dopravceDAO = new DopravceDAO();
	final static UserDAO userDAO = new UserDAO();
	final static PozadavekDAO pozadavekDAO = new PozadavekDAO();
	final static ObjednavkaDAO objednavkaDAO = new ObjednavkaDAO();
	static User user;
	
	
	final static int destinaceCount = 20;
	final static int dopravceCount = 20;
	final static int pozadavkyCount = 100;
	final static int objednavkyCount = 1000;
	
	public static void main(String[] args){
		user = userDAO.findByUsername("admin");
		if(user == null)user = createUser();
		fillDestinace(destinaceCount);
		fillDopravce(dopravceCount);
		fillPozadavky(pozadavkyCount);
		fillObjednavky(objednavkyCount);
		session.flush();
		session.close();
		
		test();
		
	};
	
	private static User createUser() {
		User user = new User();
		user.setAdministrator(true);
		user.setUsername("admin");
		user.setPassword(userDAO.encryptPassword("admin"));
		user.setId(userDAO.create(user));
		
		return user;
	}
	
	private static void fillDestinace(int count){
		logger.info("fillDestinace");
		Destinace d = new Destinace();
		Transaction tx = session.beginTransaction();
		for(int i = 0;i<=count;i++){			
			d.setCislo(i);
			d.setKontakt("Kontakt "+i);
			d.setKontaktni_osoba("Kontaktni osoba " + i);
			d.setMesto("Mesto " + i);
			d.setNazev("Nazev " + i);
			d.setPSC("PSC" + i);
			d.setUlice("Ulice " + i);
			
			destinaceDAO.create(d);
			
		}
		tx.commit();
	}
	
	private static void fillDopravce(int count){
		logger.info("fillDopravce");
		Dopravce d = new Dopravce();
		Transaction tx = session.beginTransaction();
		for(int i = 0;i<=count;i++){
			d.setNazev("Dopravce "+i);
			dopravceDAO.create(d);
		}
		tx.commit();
	}
	
	private static void fillPozadavky(int count){
		logger.info("fillPozadavky");
		Destinace destinace_do;
		Destinace destinace_z;
		
		boolean bool=true;
		Transaction tx = session.beginTransaction();
		for(int i = 0;i<=count;i++){
			
			pozadavekDAO.create(createPozadavek(i, user));
			
		}
		tx.commit();
	}
	
	private static Pozadavek createPozadavek(int i, User user) {
		Pozadavek p =new Pozadavek();
		p.setCelkova_hmotnost("Hmotnost " +i);
		p.setDatum(new Date(System.currentTimeMillis()));
		p.setDatum_nakladky("Datum nakladky " + i);
		p.setDatum_vykladky("Datum vykladky " + i);
		
		p.setDestinace_do(destinaceDAO.findByCislo(new Random( ).nextInt(destinaceCount)));
		
		p.setDestinace_z(destinaceDAO.findByCislo(new Random().nextInt(destinaceCount)));
		
		p.setHodina_nakladky("Hodina nakladky " +i);
		p.setJe_termin_konecny(true);
		
		p.setTaxi(false);
		p.setPoznamka("poznamka " + i);
		p.setZadavatel(user);
		
		p.setId(pozadavekDAO.create(p));
		
		return p;
	}
	
	private static void fillObjednavky(int count){
		logger.info("fillObjednavky");
		Objednavka  o = new Objednavka(); 
		List<Dopravce> dopravci = dopravceDAO.findAll();
		
		Transaction tx = session.beginTransaction();
		
		for(int i = 0;i<=count;i++){
			o.setCena( BigDecimal.valueOf(new Double(i)));
			o.setCislo_faktury_dopravce(i);			
			o.setDopravce(dopravci.get(new Random().nextInt(dopravceCount)));
			o.setFaze(new Random().nextInt(7));
			o.setMena("CZK");
			Pozadavek pozadavek= createPozadavek(i, user);
			pozadavek.setId(pozadavekDAO.create(pozadavek));
			o.setPozadavek(pozadavek);
			o.setPuvodni_termin_nakladky("termin " + i);
			o.setZmena_nakladky("zmena nakladky "+ i);
			objednavkaDAO.create(o);
		}
		
		tx.commit();
	}
	
	private static void test(){
		logger.info("Pocet destinaci: " + destinaceDAO.findAll().size());
		logger.info("Pocet dopravcu: " + dopravceDAO.findAll().size());
		logger.info("Pocet pozadavku: " + pozadavekDAO.findAll().size());
		
		System.out.println("Pocet pozadavku: " + pozadavekDAO.findAll().size());
		System.out.println("Pocet destinaci: " + destinaceDAO.findAll().size());
		System.out.println("Pocet dopravcu: " + dopravceDAO.findAll().size());
		
		
	}

}
