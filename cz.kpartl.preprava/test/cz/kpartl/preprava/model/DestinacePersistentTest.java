package cz.kpartl.preprava.model;

import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;

import cz.kpartl.preprava.dao.DestinaceDAO;


/**
 * @generated
 */
public class DestinacePersistentTest extends junit.framework.TestCase {
	/**
	 * @generated
	 */
	private cz.kpartl.preprava.util.HibernateHelper persistenceHelper;
	
	private DestinaceDAO destinaceDAO = new DestinaceDAO();
	
	private static final int ZAKAZNIK_CISLO = 1234567890;
	private static final String ZAKAZNIK_NAZEV = "PENTIAC GROUP";
	

	private void initObjects() {
		org.hibernate.Session session = persistenceHelper.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		Destinace zakaznik = new Destinace();
		zakaznik.setCislo(ZAKAZNIK_CISLO);
		zakaznik.setNazev(ZAKAZNIK_NAZEV);
		
		destinaceDAO.create(zakaznik);
		tx.commit();		
	}

	
	protected void tearDown() throws Exception {
		if (persistenceHelper != null) {
			
			persistenceHelper.closeSession();
			
			persistenceHelper.close();
		}
		super.tearDown();
	}

	/**
	 * @generated
	 */
	protected void setUp() throws Exception {
		super.setUp();
		initObjects();
	}

	/**
	 * @generated
	 */
	public void testUniqueCislo() throws Exception {
		Throwable e = null;
		
		Destinace zakaznik = new Destinace();
		zakaznik.setCislo(ZAKAZNIK_CISLO);
		
		Transaction tx = persistenceHelper.getSession().beginTransaction();
		
		Class<Throwable> t =  Throwable.class;
		try{
			destinaceDAO.create(zakaznik);
			tx.commit();
		}catch (Throwable ex){
			e = ex;
			tx.rollback();
		}
		
		assertTrue(e instanceof ConstraintViolationException);
	}

	/**
	 * @generated
	 */
	public void testFindByCislo() throws Exception {
		Destinace zakaznik = destinaceDAO.findByCislo(ZAKAZNIK_CISLO);
		assertTrue(zakaznik != null);
	}

	/**
	 * @generated
	 */
	public String toString() {
		return "DestinacePersistentTest";
	}


	/**
	 * @generated
	 */
	public DestinacePersistentTest() {
		persistenceHelper = cz.kpartl.preprava.util.HibernateHelper
				.getInstance();
	}
}