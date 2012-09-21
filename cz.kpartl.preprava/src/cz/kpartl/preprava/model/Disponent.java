package cz.kpartl.preprava.model;


/**
 * @generated
 */
public class Disponent implements java.io.Serializable {
	/**
	 * @generated
	 */
	private static final long serialVersionUID = -857751129L;
	/**
	 * @generated
	 */
	private Long id;
	/**
	 * @generated
	 */
	private String jmeno;

	/**
	 * @generated
	 */
	public Disponent() {
	}

	/**
	 * @generated
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * @generated
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @generated
	 */
	String getJmeno() {
		return this.jmeno;
	}

	/**
	 * @generated
	 */
	void setJmeno(String jmeno) {
		this.jmeno = jmeno;
	}

	/**
	 * @generated
	 */
	public String toString() {
		return "Disponent" + " id=" + id + " jmeno=" + jmeno;
	}
}