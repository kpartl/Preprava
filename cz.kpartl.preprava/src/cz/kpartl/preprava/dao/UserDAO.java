package cz.kpartl.preprava.dao;

import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import cz.kpartl.preprava.model.User;
import cz.kpartl.preprava.util.CryptoUtils;





/**
 * @generated
 */
/**
 * @author Karel Partl
 *
 */

public class UserDAO {
	
	 
	
	private static final Logger _logger = LoggerFactory
			.getLogger(UserDAO.class);
	 
	/**
	 * @generated
	 */
	public UserDAO() {
	}

	/**
	 * @generated
	 */
	private org.hibernate.Session getSession() {
		return cz.kpartl.preprava.util.HibernateHelper.getInstance()
				.getSession();
	}

	/**
	 * @generated
	 */
	public Long create(cz.kpartl.preprava.model.User object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().save(object);
		return object.getId();
	}

	/**
	 * @generated
	 */
	public cz.kpartl.preprava.model.User read(Long id) {
		if (id == null)
			throw new IllegalArgumentException("id");
		return (cz.kpartl.preprava.model.User) getSession().get(
				cz.kpartl.preprava.model.User.class, id);
	}

	/**
	 * @generated
	 */
	public void update(cz.kpartl.preprava.model.User object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().update(object);
	}

	/**
	 * @generated
	 */
	public void delete(cz.kpartl.preprava.model.User object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().delete(object);
	}

	/**
	 * @generated
	 */
	public cz.kpartl.preprava.model.User findByUsername(String username) {
		org.hibernate.Query query = getSession()
				.createQuery(
						"from cz.kpartl.preprava.model.User u where u.username = :username");
		query.setString("username", username);
		java.util.List list = query.list();
		if (list.size() == 1) {
			return (cz.kpartl.preprava.model.User) list.get(0);
		} else {
			return null;
		}
	}

	/**
	 * @generated
	 */
	public String toString() {
		return "UserDAO";
	}

	/**\
	 * 
	 * @param name
	 * @param password
	 * @return instance of User if given password is OK, else null
	 */
	public cz.kpartl.preprava.model.User login(String name, String password) {
		User user = findByUsername(name);
	      if(user == null)
	         return null;	      
	      	    
			if(password.equals(user.getPassword()))
			     return user;
			  else
			     return null;
		
	}
	
	
	/**
	 * @param password
	 * @return Hash of the password or empty string if any error has occured
	 */
	public static String encryptPassword(String password){
		try{
			return CryptoUtils.byteArrayToHexString(CryptoUtils.computeHash(password));
		} catch (NoSuchAlgorithmException e) {
			_logger.error("Cannot login user", e);
			return "";
		}
		
	}

	/**
	 * @generated
	 */
	public java.util.List<cz.kpartl.preprava.model.User> findAll() {
		org.hibernate.Query query = getSession().createQuery(
				"from cz.kpartl.preprava.model.User u");
		return query.list();
	}


	
	/*public int countAll() {
		return ( (Long) getSession().createQuery("select count (u.id) from cz.kpartl.preprava.model.User u").iterate().next()).intValue();		
	}*/
}