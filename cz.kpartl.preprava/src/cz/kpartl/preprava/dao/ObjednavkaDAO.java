package cz.kpartl.preprava.dao;


/**
 * @generated
 */
public class ObjednavkaDAO {
	/**
	 * @generated
	 */
	public ObjednavkaDAO() {
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
	public Long create(cz.kpartl.preprava.model.Objednavka object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().save(object);
		return object.getId();
	}

	/**
	 * @generated
	 */
	public cz.kpartl.preprava.model.Objednavka read(Long id) {
		if (id == null)
			throw new IllegalArgumentException("id");
		return (cz.kpartl.preprava.model.Objednavka) getSession().get(
				cz.kpartl.preprava.model.Objednavka.class, id);
	}

	/**
	 * @generated
	 */
	public void update(cz.kpartl.preprava.model.Objednavka object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().update(object);
	}

	/**
	 * @generated
	 */
	public void delete(cz.kpartl.preprava.model.Objednavka object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().delete(object);
	}

	/**
	 * @generated
	 */
	public String toString() {
		return "ObjednavkaDAO";
	}

	/**
	 * @generated
	 */
	public java.util.List<cz.kpartl.preprava.model.Objednavka> findByFaze(
			int faze) {
				org.hibernate.Query query = getSession()
						.createQuery(
								"from cz.kpartl.preprava.model.Objednavka o where o.faze = :faze");
				query.setInteger("faze", faze);
				return query.list();
			}

	/**
	 * @generated
	 */
	public java.util.List<cz.kpartl.preprava.model.Objednavka> findNeukoncene() {
		org.hibernate.Query query = getSession().createQuery(
				"from cz.kpartl.preprava.model.Objednavka o where o.faze != 6");
		return query.list();
	}
}