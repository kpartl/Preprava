package cz.kpartl.preprava.dao;


/**
 * @generated
 */
public class DodavatelDAO {
	/**
	 * @generated
	 */
	public DodavatelDAO() {
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
	public Long create(cz.kpartl.preprava.model.Dodavatel object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().save(object);
		return object.getId();
	}

	/**
	 * @generated
	 */
	public cz.kpartl.preprava.model.Dodavatel read(Long id) {
		if (id == null)
			throw new IllegalArgumentException("id");
		return (cz.kpartl.preprava.model.Dodavatel) getSession().get(
				cz.kpartl.preprava.model.Dodavatel.class, id);
	}

	/**
	 * @generated
	 */
	public void update(cz.kpartl.preprava.model.Dodavatel object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().update(object);
	}

	/**
	 * @generated
	 */
	public void delete(cz.kpartl.preprava.model.Dodavatel object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().delete(object);
	}

	/**
	 * @generated
	 */
	public cz.kpartl.preprava.model.Dodavatel findByCislo(long cislo) {
		org.hibernate.Query query = getSession()
				.createQuery(
						"from cz.kpartl.preprava.model.Dodavatel d where d.cislo = :cislo");
		query.setParameter("cislo", cislo);
		java.util.List list = query.list();
		if (list.size() == 1) {
			return (cz.kpartl.preprava.model.Dodavatel) list.get(0);
		} else {
			return null;
		}
	}

	/**
	 * @generated
	 */
	public String toString() {
		return "DodavatelDAO";
	}
}