package cz.kpartl.preprava.model;

import java.text.NumberFormat;
import java.util.Locale;


/**
 * @generated
 */
public class Objednavka implements java.io.Serializable {
	
	public static final int FAZE_OBJEDNANO = 0;
	public static final int FAZE_PREPRAVA_ZAHAJENA = 1;
	public static final int FAZE_PREPRAVA_UKONCENA = 2;
	public static final int FAZE_DOKLADY_KOMPLETNI = 3;
	public static final int FAZE_FAKTUROVANO = 4;
	//public static final int FAZE_VSE = 5;
	public static final int FAZE_UKONCENO = 6; //nemenit, zavisi na tom dotaz findNeukoncene
	
	
	private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.GERMANY);
	
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
	private Integer cislo_faktury_dopravce;

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
	private Objednavka pridruzena_objednavka;

	/**
	 * @generated
	 */
	private Long cislo_objednavky;

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
	
	public String getCenaFormated() {
		if(cena != null) {
			final String formatedCena =  currencyFormatter.format(cena);
			if(formatedCena.endsWith(" \u20ac")) return formatedCena.substring(0, formatedCena.length()- 2);
			else return formatedCena;
		}
		else return "";
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
	public Integer getCislo_faktury_dopravce() {
		return this.cislo_faktury_dopravce;
	}
	
	public String getCisloFakturyDopravceAsString()
	{
		if(cislo_faktury_dopravce == null) return "";
		else return String.valueOf(cislo_faktury_dopravce);
	}

	/**
	 * @generated
	 */
	public void setCislo_faktury_dopravce(Integer cislo_faktury_dopravce) {
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
				+ id + " cislo_objednavky=" + cislo_objednavky;
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

	/**
	 * @generated
	 */
	public Objednavka getPridruzena_objednavka() {
		return this.pridruzena_objednavka;
	}

	/**
	 * @generated
	 */
	public void setPridruzena_objednavka(Objednavka pridruzena_objednavka) {
		this.pridruzena_objednavka = pridruzena_objednavka;
	}

	/**
	 * @generated
	 */
	public Long getCislo_objednavky() {
		return this.cislo_objednavky;
	}

	/**
	 * @generated
	 */
	public void setCislo_objednavky(Long cislo_objednavky) {
		this.cislo_objednavky = cislo_objednavky;
	}
}