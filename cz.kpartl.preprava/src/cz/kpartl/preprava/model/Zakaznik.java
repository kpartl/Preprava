package cz.kpartl.preprava.model;


/**
 * @generated
 */
public class Zakaznik implements java.io.Serializable {
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
	private String kontaktni_osoba;
	/**
	 * @generated
	 */
	private String kontakt;
	/**
	 * @generated
	 */
	private static final long serialVersionUID = 906799254L;
	/**
	 * @generated
	 */
	private Long id;

	/**
	 * @generated
	 */
	public Zakaznik() {
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
	public long getCislo() {
		return this.cislo;
	}

	/**
	 * @generated
	 */
	public void setCislo(long cislo) {
		this.cislo = cislo;
	}

	/**
	 * @generated
	 */
	public String getKontaktni_osoba() {
		return this.kontaktni_osoba;
	}

	/**
	 * @generated
	 */
	public void setKontaktni_osoba(String kontaktni_osoba) {
		this.kontaktni_osoba = kontaktni_osoba;
	}

	/**
	 * @generated
	 */
	public String getKontakt() {
		return this.kontakt;
	}

	/**
	 * @generated
	 */
	public void setKontakt(String kontakt) {
		this.kontakt = kontakt;
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
	public String toString() {
		return "Zakaznik" + " nazev=" + nazev + " cislo=" + cislo
				+ " kontaktni_osoba=" + kontaktni_osoba + " kontakt=" + kontakt
				+ " id=" + id;
	}
}