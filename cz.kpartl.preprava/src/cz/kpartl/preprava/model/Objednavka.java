package cz.kpartl.preprava.model;


/**
 * @generated
 */
public class Objednavka implements java.io.Serializable {
	
	public static final int FAZE_OBJEDNANO = 1;
	public static final int FAZE_PREPRAVA_ZAHAJENA = 2;
	public static final int FAZE_PREPRAVA_UKONCENA = 3;
	public static final int FAZE_DOKLADY_KOMPLETNI = 4;
	public static final int FAZE_FAKTUROVANO = 5;
	public static final int FAZE_UKONCENO = 6;
	
	/**
	 * @generated
	 */
	private static final long serialVersionUID = 1087054822L;
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
	private int faze;

	/**
	 * @generated
	 */
	private Long id;

	/**
	 * @generated
	 */
	public Objednavka() {
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
		return "Objednavka" + " faze=" + faze + " cena=" + cena + " mena="
				+ mena + " zmena_nakladky=" + zmena_nakladky
				+ " puvodni_termin_nakladky=" + puvodni_termin_nakladky
				+ " cislo_faktury_dopravce=" + cislo_faktury_dopravce + " id="
				+ id;
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

	/**
	 * @generated
	 */
	public int getFaze() {
		return this.faze;
	}

	/**
	 * @generated
	 */
	public void setFaze(int faze) {
		this.faze = faze;
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
}