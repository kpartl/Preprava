package cz.kpartl.preprava.model;


/**
 * @generated
 */
public class Dopravce implements java.io.Serializable {
	/**
	 * @generated
	 */
	private static final long serialVersionUID = 917935527L;
	/**
	 * @generated
	 */
	private Long id;
	/**
	 * @generated
	 */
	private String nazev;

	/**
	 * @generated
	 */
	public Dopravce() {
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
	public String getNazev() {
		return this.nazev;
	}

	/**
	 * @generated
	 */
	public void setNazev(String nazev) {
		this.nazev = nazev;
	}

	/**
	 * @generated
	 */
	public String toString() {
		return "Dopravce" + " id=" + id + " nazev=" + nazev;
	}
}