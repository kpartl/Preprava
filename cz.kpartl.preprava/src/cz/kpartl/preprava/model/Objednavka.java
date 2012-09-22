package cz.kpartl.preprava.model;


/**
 * @generated
 */
public class Objednavka implements java.io.Serializable {
	/**
	 * @generated
	 */
	private static final long serialVersionUID = 1087054822L;
	/**
	 * @generated
	 */
	private Long id;
	/**
	 * @generated
	 */
	private int cislo;
	/**
	 * @generated
	 */
	private java.math.BigDecimal cena;
	/**
	 * @generated
	 */
	private String mena;
	/**
	 * @generated
	 */
	private String zmena_nakladky;
	/**
	 * @generated
	 */
	private String puvodni_termin_nakladky;
	/**
	 * @generated
	 */
	private int cislo_faktury_dopravce;

	/**
	 * @generated
	 */
	private Pozadavek pozadavek;

	/**
	 * @generated
	 */
	private Dopravce dopravce;

	/**
	 * @generated
	 */
	public Objednavka() {
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
	public java.math.BigDecimal getCena() {
		return this.cena;
	}

	/**
	 * @generated
	 */
	public void setCena(java.math.BigDecimal cena) {
		this.cena = cena;
	}

	/**
	 * @generated
	 */
	public String getMena() {
		return this.mena;
	}

	/**
	 * @generated
	 */
	public void setMena(String mena) {
		this.mena = mena;
	}

	/**
	 * @generated
	 */
	public String getZmena_nakladky() {
		return this.zmena_nakladky;
	}

	/**
	 * @generated
	 */
	public void setZmena_nakladky(String zmena_nakladky) {
		this.zmena_nakladky = zmena_nakladky;
	}

	/**
	 * @generated
	 */
	public String getPuvodni_termin_nakladky() {
		return this.puvodni_termin_nakladky;
	}

	/**
	 * @generated
	 */
	public void setPuvodni_termin_nakladky(String puvodni_termin_nakladky) {
		this.puvodni_termin_nakladky = puvodni_termin_nakladky;
	}

	/**
	 * @generated
	 */
	public int getCislo_faktury_dopravce() {
		return this.cislo_faktury_dopravce;
	}

	/**
	 * @generated
	 */
	public void setCislo_faktury_dopravce(int cislo_faktury_dopravce) {
		this.cislo_faktury_dopravce = cislo_faktury_dopravce;
	}

	/**
	 * @generated
	 */
	public String toString() {
		return "Objednavka" + " id=" + id + " cislo=" + cislo + " cena=" + cena
				+ " mena=" + mena + " zmena_nakladky=" + zmena_nakladky
				+ " puvodni_termin_nakladky=" + puvodni_termin_nakladky
				+ " cislo_faktury_dopravce=" + cislo_faktury_dopravce;
	}

	/**
	 * @generated
	 */
	public Pozadavek getPozadavek() {
		return this.pozadavek;
	}

	/**
	 * @generated
	 */
	public void setPozadavek(Pozadavek pozadavek) {
		this.pozadavek = pozadavek;
	}

	/**
	 * @generated
	 */
	public Dopravce getDopravce() {
		return this.dopravce;
	}

	/**
	 * @generated
	 */
	public void setDopravce(Dopravce dopravce) {
		this.dopravce = dopravce;
	}
}