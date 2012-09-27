package cz.kpartl.preprava.dao;


/**
 * @generated
 */
public class PozadavekDAO {
	/**
	 * @generated
	 */
	public PozadavekDAO() {
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
	public Long create(cz.kpartl.preprava.model.Pozadavek object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().save(object);
		return object.getId();
	}

	/**
	 * @generated
	 */
	public cz.kpartl.preprava.model.Pozadavek read(Long id) {
		if (id == null)
			throw new IllegalArgumentException("id");
		return (cz.kpartl.preprava.model.Pozadavek) getSession().get(
				cz.kpartl.preprava.model.Pozadavek.class, id);
	}

	/**
	 * @generated
	 */
	public void update(cz.kpartl.preprava.model.Pozadavek object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().update(object);
	}

	/**
	 * @generated
	 */
	public void delete(cz.kpartl.preprava.model.Pozadavek object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().delete(object);
	}

	/**
	 * @generated
	 */
	public String toString() {
		return "PozadavekDAO";
	}

	/**
	 * @generated
	 */
	public java.util.List<cz.kpartl.preprava.model.Pozadavek> findAll() {
		org.hibernate.Query query = getSession().createQuery(
				"from cz.kpartl.preprava.model.Pozadavek p");
		return query.list();
	}

	/**
	 * @generated
	 */
	public java.util.List<cz.kpartl.preprava.model.Pozadavek> findNeobjednane() {
		org.hibernate.Query query = getSession()
				.createQuery(
						"from cz.kpartl.preprava.model.Pozadavek p where ( p.objednavka is null OR p.objednavka is empty )");
		return query.list();
	}
}