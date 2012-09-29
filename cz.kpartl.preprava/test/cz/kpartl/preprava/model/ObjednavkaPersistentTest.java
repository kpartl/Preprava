package cz.kpartl.preprava.model;

import java.util.Date;

import org.junit.Test;

import cz.kpartl.preprava.dao.ObjednavkaDAO;
import cz.kpartl.preprava.dao.PozadavekDAO;


/**
 * @generated
 */
public class ObjednavkaPersistentTest extends junit.framework.TestCase {
	
	private PozadavekDAO pozadavekDAO = new PozadavekDAO();
	private ObjednavkaDAO objednavkaDAO = new ObjednavkaDAO();
	/**
	 * @generated
	 */
	private cz.kpartl.preprava.util.HibernateHelper persistenceHelper;

	/**
	 * @generated
	 */
	public ObjednavkaPersistentTest() {
		persistenceHelper = cz.kpartl.preprava.util.HibernateHelper
				.getInstance();
	}

	/**
	 * @generated
	 */
	private void initObjects() {
		org.hibernate.Session session = persistenceHelper.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();		
		
		tx.commit();
		persistenceHelper.closeSession();
	}

	/**
	 * @generated
	 */
	protected void tearDown() throws Exception {
		if (persistenceHelper != null) {
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
	public void test1() throws Exception {
	}
	
	@Test
	public void testObjednavku() throws Exception {
		
	}

	/**
	 * @generated
	 */
	public String toString() {
		return "ObjednavkaPersistentTest";
	}
}