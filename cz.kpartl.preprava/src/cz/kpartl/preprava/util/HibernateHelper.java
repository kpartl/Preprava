package cz.kpartl.preprava.util;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @generated
 */
public class HibernateHelper {
	
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * @generated
	 */
	private static HibernateHelper singleton = new HibernateHelper();
	/**
	 * @generated
	 */
	private org.hibernate.SessionFactory factory;
	/**
	 * @generated
	 */
	private ThreadLocal currentSession = new ThreadLocal();

	/**
	 * @generated
	 */
	private HibernateHelper() throws org.hibernate.HibernateException {
	}

	/**
	 * @generated
	 */
	public static void main(String[] args) throws Exception {
		String sqlFile = null;
		if (args.length > 0) {
			sqlFile = args[0];
		}
		boolean print = (sqlFile == null);
		boolean export = (sqlFile == null);
		org.hibernate.cfg.Configuration config = getInstance()
				.getConfiguration();
		org.hibernate.tool.hbm2ddl.SchemaExport exporter = new org.hibernate.tool.hbm2ddl.SchemaExport(
				config);
		if (sqlFile != null) {
			exporter.setOutputFile(sqlFile);
		}
		exporter.create(print, export);
	}

	/**
	 * @generated
	 */
	public static HibernateHelper getInstance() {
		return singleton;
	}

	/**
	 * @generated
	 */
	public synchronized org.hibernate.SessionFactory getFactory()
			throws org.hibernate.HibernateException {
				if (factory == null) {
					org.hibernate.cfg.Configuration config = getConfiguration();
					factory = config.buildSessionFactory();
				}
				return factory;
			}

	/**
	 * @generated
	 */
	public synchronized void close() throws org.hibernate.HibernateException {
		closeSession();
		if (factory != null) {
			factory.close();
			factory = null;
		}
	}

	/**
	 * @generated
	 */
	public org.hibernate.cfg.Configuration getConfiguration()
			throws org.hibernate.MappingException {
				org.hibernate.cfg.Configuration config = new org.hibernate.cfg.Configuration();
				config.addClass(cz.kpartl.preprava.model.User.class);
				config.addClass(cz.kpartl.preprava.model.Pozadavek.class);
				config.addClass(cz.kpartl.preprava.model.Destinace.class);
				config.addClass(cz.kpartl.preprava.model.Objednavka.class);
				config.addClass(cz.kpartl.preprava.model.Dopravce.class);
				return config;
			}

	/**
	 * @generated
	 */
	public org.hibernate.Session openSession()
			throws org.hibernate.HibernateException {
				org.hibernate.Session session = getFactory().openSession();
				session.setCacheMode(CacheMode.IGNORE); //to jsem pridal ja
				session.setFlushMode(FlushMode.ALWAYS);
				session.connection();
				return session;
			}

	/**
	 * @generated
	 */
	public org.hibernate.Session getSession()
			throws org.hibernate.HibernateException {
				org.hibernate.Session session = (org.hibernate.Session) currentSession
						.get();
				
				if (session == null || !session.isOpen()) {
					session = openSession();
					currentSession.set(session);
				}
				
				session.clear(); //to jsem pridal ja
				return session;
			}

	/**
	 * @generated
	 */
	public void closeSession() throws org.hibernate.HibernateException {
		org.hibernate.Session session = (org.hibernate.Session) currentSession
				.get();
		if (session != null && session.isOpen()) {
			session.close();
		}
		currentSession.set(null);
	}

	/**
	 * @generated
	 */
	public void save(Object object) throws org.hibernate.HibernateException {
		getSession().save(object);
	}

	/**
	 * @generated
	 */
	public void delete(Object object) throws org.hibernate.HibernateException {
		getSession().delete(object);
	}

	/**
	 * @generated
	 */
	public String toString() {
		return "HibernateHelper";
	}
	
	public Transaction beginTransaction() {
		return getSession().beginTransaction();
	}
	
}