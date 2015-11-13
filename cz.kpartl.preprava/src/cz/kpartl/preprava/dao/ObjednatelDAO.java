package cz.kpartl.preprava.dao;


/**
 * @generated
 */
public class ObjednatelDAO {
	/**
	 * @generated
	 */
	public ObjednatelDAO() {
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
	public Long create(cz.kpartl.preprava.model.Objednatel object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().save(object);
		return object.getId();
	}

	/**
	 * @generated
	 */
	public cz.kpartl.preprava.model.Objednatel read(Long id) {
		if (id == null)
			throw new IllegalArgumentException("id");
		return (cz.kpartl.preprava.model.Objednatel) getSession().get(
				cz.kpartl.preprava.model.Objednatel.class, id);
	}

	/**
	 * @generated
	 */
	public void update(cz.kpartl.preprava.model.Objednatel object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().update(object);
	}

	/**
	 * @generated
	 */
	public void delete(cz.kpartl.preprava.model.Objednatel object) {
		if (object == null)
			throw new IllegalArgumentException("object");
		getSession().delete(object);
	}

	/**
	 * @generated
	 */
	public String toString() {
		return "ObjednatelDAO";
	}
}