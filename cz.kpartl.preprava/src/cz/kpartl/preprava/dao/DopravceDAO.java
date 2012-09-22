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
}