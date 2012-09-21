package cz.kpartl.preprava.model;


/**
 * @generated
 */
public class Dodavatel implements java.io.Serializable {
	/**
	 * @generated
	 */
	private static final long serialVersionUID = 158177189L;
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
	private long cislo;
	/**
	 * @generated
	 */
	private String hodina_nakladky;

	/**
	 * @generated
	 */
	public Dodavatel() {
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
	String getNazev() {
		return this.nazev;
	}

	/**
	 * @generated
	 */
	void setNazev(String nazev) {
		this.nazev = nazev;
	}

	/**
	 * @generated
	 */
	long getCislo() {
		return this.cislo;
	}

	/**
	 * @generated
	 */
	void setCislo(long cislo) {
		this.cislo = cislo;
	}

	/**
	 * @generated
	 */
	String getHodina_nakladky() {
		return this.hodina_nakladky;
	}

	/**
	 * @generated
	 */
	void setHodina_nakladky(String hodina_nakladky) {
		this.hodina_nakladky = hodina_nakladky;
	}

	/**
	 * @generated
	 */
	public String toString() {
		return "Dodavatel" + " id=" + id + " nazev=" + nazev + " cislo="
				+ cislo + " hodina_nakladky=" + hodina_nakladky;
	}
}