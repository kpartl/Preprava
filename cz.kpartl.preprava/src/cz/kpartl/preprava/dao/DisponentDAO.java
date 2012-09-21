package cz.kpartl.preprava.dao;


/**
 * @generated
 */
public class DisponentDAO {
	/**
	 * @generated
	 */
	public DisponentDAO() {
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
	public Long create(cz.kpartl.preprava.model.Disponent object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().save(object);
		return object.getId();
	}

	/**
	 * @generated
	 */
	public cz.kpartl.preprava.model.Disponent read(Long id) {
		if (id == null)
			throw new IllegalArgumentException("id");
		return (cz.kpartl.preprava.model.Disponent) getSession().get(
				cz.kpartl.preprava.model.Disponent.class, id);
	}

	/**
	 * @generated
	 */
	public void update(cz.kpartl.preprava.model.Disponent object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().update(object);
	}

	/**
	 * @generated
	 */
	public void delete(cz.kpartl.preprava.model.Disponent object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().delete(object);
	}

	/**
	 * @generated
	 */
	public cz.kpartl.preprava.model.Disponent findByJmeno(String jmeno) {
		org.hibernate.Query query = getSession()
				.createQuery(
						"from cz.kpartl.preprava.model.Disponent d where d.jmeno = :jmeno");
		query.setString("jmeno", jmeno);
		java.util.List list = query.list();
		if (list.size() == 1) {
			return (cz.kpartl.preprava.model.Disponent) list.get(0);
		} else {
			return null;
		}
	}

	/**
	 * @generated
	 */
	public String toString() {
		return "DisponentDAO";
	}
}