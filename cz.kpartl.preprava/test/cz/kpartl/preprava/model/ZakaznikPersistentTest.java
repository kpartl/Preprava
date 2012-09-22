package cz.kpartl.preprava.model;

import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;

import cz.kpartl.preprava.dao.ZakaznikDAO;


/**
 * @generated
 */
public class ZakaznikPersistentTest extends junit.framework.TestCase {
	/**
	 * @generated
	 */
	private cz.kpartl.preprava.util.HibernateHelper persistenceHelper;
	
	private ZakaznikDAO zakaznikDAO = new ZakaznikDAO();
	
	private static final int ZAKAZNIK_CISLO = 1234567890;
	private static final String ZAKAZNIK_NAZEV = "PENTIAC GROUP";
	

	/**
	 * @generated
	 */
	public ZakaznikPersistentTest() {
		persistenceHelper = cz.kpartl.preprava.util.HibernateHelper
				.getInstance();
	}

	
	private void initObjects() {
		org.hibernate.Session session = persistenceHelper.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		Zakaznik zakaznik = new Zakaznik();
		zakaznik.setCislo(ZAKAZNIK_CISLO);
		zakaznik.setNazev(ZAKAZNIK_NAZEV);
		
		zakaznikDAO.create(zakaznik);
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
	@Test
	public void testUniqueCislo() throws Exception {
		Throwable e = null;
		
		Zakaznik zakaznik = new Zakaznik();
		zakaznik.setCislo(ZAKAZNIK_CISLO);
		
		Transaction tx = persistenceHelper.getSession().beginTransaction();
		
		Class<Throwable> t =  Throwable.class;
		try{
			zakaznikDAO.create(zakaznik);
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
	@Test
	public void testFindByCislo() throws Exception {
		Zakaznik zakaznik = zakaznikDAO.findByCislo(ZAKAZNIK_CISLO);
		assertTrue(zakaznik != null);
	}

	/**
	 * @generated
	 */
	public String toString() {
		return "ZakaznikPersistentTest";
	}
}