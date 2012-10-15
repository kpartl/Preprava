package cz.kpartl.preprava.dao;


/**
 * @generated
 */
public class DopravceDAO {
	/**
	 * @generated
	 */
	public DopravceDAO() {
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
	public Long create(cz.kpartl.preprava.model.Dopravce object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().save(object);
		return object.getId();
	}

	/**
	 * @generated
	 */
	public cz.kpartl.preprava.model.Dopravce read(Long id) {
		if (id == null)
			throw new IllegalArgumentException("id");
		return (cz.kpartl.preprava.model.Dopravce) getSession().get(
				cz.kpartl.preprava.model.Dopravce.class, id);
	}

	/**
	 * @generated
	 */
	public void update(cz.kpartl.preprava.model.Dopravce object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().update(object);
	}

	/**
	 * @generated
	 */
	public void delete(cz.kpartl.preprava.model.Dopravce object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().delete(object);
	}

	/**
	 * @generated
	 */
	public String toString() {
		return "DopravceDAO";
	}

	/**
	 * @generated
	 */
	public java.util.List<cz.kpartl.preprava.model.Dopravce> findAll() {
		org.hibernate.Query query = getSession().createQuery(
				"from cz.kpartl.preprava.model.Dopravce d");
		return query.list();
	}

	/**
	 * @generated
	 */
	public cz.kpartl.preprava.model.Dopravce findByNazev(String nazev) {
		org.hibernate.Query query = getSession()
				.createQuery(
						"from cz.kpartl.preprava.model.Dopravce d where d.nazev = :nazev");
		query.setString("nazev", nazev);
		java.util.List list = query.list();
		if (list.size() == 1) {
			return (cz.kpartl.preprava.model.Dopravce) list.get(0);
		} else {
			return null;
		}
	}
}