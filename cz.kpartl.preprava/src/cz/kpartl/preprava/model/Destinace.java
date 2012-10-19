package cz.kpartl.preprava.model;


/**
 * @generated
 */
public class Destinace implements java.io.Serializable {
	/**
	 * @generated
	 */
	private String nazev;
	/**
	 * @generated
	 */
	private int cislo;
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
	private static final long serialVersionUID = 869913401L;
	/**
	 * @generated
	 */
	private Long id;

	/**
	 * @generated
	 */
	private String ulice;
	/**
	 * @generated
	 */
	private String mesto;
	/**
	 * @generated
	 */
	private Integer PSC;

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
	public int getCislo() {
		return this.cislo;
	}

	/**
	 * @generated
	 */
	public void setCislo(int cislo) {
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
		return "Destinace" + " nazev=" + nazev + " cislo=" + cislo
				+ " kontaktni_osoba=" + kontaktni_osoba + " kontakt=" + kontakt
				+ " id=" + id + " ulice=" + ulice + " mesto=" + mesto + " PSC="
				+ PSC;
	}

	/**
	 * @generated
	 */
	public Destinace() {
	}

	/**
	 * @generated
	 */
	public String getUlice() {
		return this.ulice;
	}

	/**
	 * @generated
	 */
	public void setUlice(String ulice) {
		this.ulice = ulice;
	}

	/**
	 * @generated
	 */
	public String getMesto() {
		return this.mesto;
	}

	/**
	 * @generated
	 */
	public void setMesto(String mesto) {
		this.mesto = mesto;
	}

	/**
	 * @generated
	 */
	public Integer getPSC() {
		return this.PSC;
	}

	/**
	 * @generated
	 */
	public void setPSC(Integer PSC) {
		this.PSC = PSC;
	}
	
	public String getNazevACislo(){
		return getNazev().concat("(").concat(String.valueOf(getCislo()).concat(")"));
	}
	
	public String getKontaktniOsobuAKontakt(){
		return getKontaktni_osoba().concat(" (").concat(getKontakt()).concat(")");
	}
}