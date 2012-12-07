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
	private String ulice;
	/**
	 * @generated
	 */
	private String mesto;
	/**
	 * @generated
	 */
	private String sap_cislo;
	/**
	 * @generated
	 */
	private String kontaktni_osoba;
	/**
	 * @generated
	 */
	private String kontakt_ostatni;

	/**
	 * @generated
	 */
	private String kontaktni_telefon;

	/**
	 * @generated
	 */
	private String psc;

	/**
	 * @generated
	 */
	private String ic;
	/**
	 * @generated
	 */
	private String dic;

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
		return "Dopravce" + " id=" + id + " nazev=" + nazev + " ulice=" + ulice
				+ " mesto=" + mesto + " ic=" + ic + " dic=" + dic
				+ " sap_cislo=" + sap_cislo + " kontaktni_osoba="
				+ kontaktni_osoba + " kontaktni_telefon=" + kontaktni_telefon
				+ " kontakt_ostatni=" + kontakt_ostatni + " psc=" + psc;
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
	public String getSap_cislo() {
		return this.sap_cislo;
	}

	/**
	 * @generated
	 */
	public void setSap_cislo(String sap_cislo) {
		this.sap_cislo = sap_cislo;
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
	public String getKontakt_ostatni() {
		return this.kontakt_ostatni;
	}

	/**
	 * @generated
	 */
	public void setKontakt_ostatni(String kontakt_ostatni) {
		this.kontakt_ostatni = kontakt_ostatni;
	}

	/**
	 * @generated
	 */
	public String getKontaktni_telefon() {
		return this.kontaktni_telefon;
	}

	/**
	 * @generated
	 */
	public void setKontaktni_telefon(String kontaktni_telefon) {
		this.kontaktni_telefon = kontaktni_telefon;
	}

	/**
	 * @generated
	 */
	public String getPsc() {
		return this.psc;
	}

	/**
	 * @generated
	 */
	public void setPsc(String psc) {
		this.psc = psc;
	}

	/**
	 * @generated
	 */
	public String getIc() {
		return this.ic;
	}

	/**
	 * @generated
	 */
	public void setIc(String ic) {
		this.ic = ic;
	}

	/**
	 * @generated
	 */
	public String getDic() {
		return this.dic;
	}

	/**
	 * @generated
	 */
	public void setDic(String dic) {
		this.dic = dic;
	}
}