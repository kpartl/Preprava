package cz.kpartl.preprava.dao;


/**
 * @generated
 */
public class DestinaceDAO {
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
	public Long create(cz.kpartl.preprava.model.Destinace object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().save(object);
		return object.getId();
	}

	/**
	 * @generated
	 */
	public cz.kpartl.preprava.model.Destinace read(Long id) {
		if (id == null)
			throw new IllegalArgumentException("id");
		return (cz.kpartl.preprava.model.Destinace) getSession().get(
				cz.kpartl.preprava.model.Destinace.class, id);
	}

	/**
	 * @generated
	 */
	public void update(cz.kpartl.preprava.model.Destinace object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().update(object);
	}

	/**
	 * @generated
	 */
	public void delete(cz.kpartl.preprava.model.Destinace object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().delete(object);
	}

	/**
	 * @generated
	 */
	public java.util.List<cz.kpartl.preprava.model.Destinace> findByNazev(String nazev) {
		org.hibernate.Query query = getSession()
				.createQuery(
						"from cz.kpartl.preprava.model.Destinace z where z.nazev = :nazev");
		query.setString("nazev", nazev);
		return query.list();
	}

	/**
	 * @generated
	 */
	public cz.kpartl.preprava.model.Destinace findByCislo(int cislo) {
		org.hibernate.Query query = getSession()
				.createQuery(
						"from cz.kpartl.preprava.model.Destinace z where z.cislo = :cislo");
		query.setInteger("cislo", cislo);
		java.util.List list = query.list();
		if (list.size() == 1) {
			return (cz.kpartl.preprava.model.Destinace) list.get(0);
		} else {
			return null;
		}
	}

	/**
	 * @generated
	 */
	public String toString() {
		return "DestinaceDAO";
	}

	/**
	 * @generated
	 */
	public DestinaceDAO() {
	}

	/**
	 * @generated
	 */
	public java.util.List<cz.kpartl.preprava.model.Destinace> findAll() {
		org.hibernate.Query query = getSession().createQuery(
				"from cz.kpartl.preprava.model.Destinace d");
		return query.list();
	}
}