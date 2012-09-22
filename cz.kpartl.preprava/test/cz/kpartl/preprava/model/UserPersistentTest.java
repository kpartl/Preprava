package cz.kpartl.preprava.model;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cz.kpartl.preprava.dao.UserDAO;


/**
 * @generated
 */
public class UserPersistentTest extends junit.framework.TestCase {
	/**
	 * @generated
	 */
	private cz.kpartl.preprava.util.HibernateHelper persistenceHelper;
	
	private UserDAO userDAO = new UserDAO();
	
	static final String USER_NAME = "testUser";
	static final String USER_PASSWORD = "secret";
	
//	private Session session;

	/**
	 * @generated
	 */
	public UserPersistentTest() {
		persistenceHelper = cz.kpartl.preprava.util.HibernateHelper
				.getInstance();
	}

	
	private void initObjects() {
		
		org.hibernate.Transaction tx = persistenceHelper.getSession().beginTransaction();
		User user = new User();
		user.setUsername(USER_NAME);
		user.setPassword(userDAO.encryptPassword(USER_PASSWORD));
		userDAO.create(user);
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
		
	
	@Test
	public void testUniqUserName() throws Exception {	
		Throwable e = null;
		
		User user = new User();
		user.setUsername(USER_NAME);
		user.setPassword(userDAO.encryptPassword(USER_PASSWORD));
		
		Transaction tx = persistenceHelper.getSession().beginTransaction();
		
		Class<Throwable> t =  Throwable.class;
		try{			
			userDAO.create(user);
			tx.commit();
		}catch (Throwable ex){
			tx.rollback();
			e = ex;
		}
		
		assertTrue(e instanceof ConstraintViolationException);
	}

	/**
	 * @generated
	 */
	public void testFindByUsername() throws Exception {
	}

	/**
	 * @generated
	 */
	public void testLogin() throws Exception {
		User user = userDAO.login(USER_NAME, userDAO.encryptPassword(USER_PASSWORD));
	      assertNotNull(user);
	}
	
	@Test
	public void testLoginWrongPassword() throws Exception {
	      User user = userDAO.login(USER_NAME, "x");
	      assertNull(user);
	   }
	@Test
	public void testDeleteUser() throws Exception {
		Transaction tx = persistenceHelper.getSession().beginTransaction();
		User user = userDAO.findByUsername(USER_NAME);
		userDAO.delete(user);
		tx.commit();
		assertNull(userDAO.findByUsername(USER_NAME));
		
	}
	
	/**
	 * @generated
	 */
	public String toString() {
		return "UserPersistentTest";
	}


	
}