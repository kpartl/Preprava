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
	private java.util.Date datum;

	/**
	 * @generated
	 */
	private String spec_zbozi;
	/**
	 * @generated
	 */
	private String adr;
	/**
	 * @generated
	 */
	private String preprav_podminky;
	/**
	 * @generated
	 */
	private String poznamka1;
	/**
	 * @generated
	 */
	private String poznamka2;
	/**
	 * @generated
	 */
	private String poznamka3;
	/**
	 * @generated
	 */
	private String poznamka4;
	/**
	 * @generated
	 */
	private String poznamka5;
	/**
	 * @generated
	 */
	private String objednavka1;
	/**
	 * @generated
	 */
	private String objednavka2;
	/**
	 * @generated
	 */
	private String objednavka3;
	/**
	 * @generated
	 */
	private String objednavka4;
	/**
	 * @generated
	 */
	private String objednavka5;
	/**
	 * @generated
	 */
	private String objednavka6;
	/**
	 * @generated
	 */
	private String objednavka7;
	/**
	 * @generated
	 */
	private String objednavka8;
	/**
	 * @generated
	 */
	private String dod_nazev;
	/**
	 * @generated
	 */
	private String dod_ulice;
	/**
	 * @generated
	 */
	private String dod_psc;
	/**
	 * @generated
	 */
	private String dod_mesto;
	/**
	 * @generated
	 */
	private String dod_dic;
	/**
	 * @generated
	 */
	private String dod_ic;
	/**
	 * @generated
	 */
	private String nakl_nazev;
	/**
	 * @generated
	 */
	private String nakl_ulice;
	/**
	 * @generated
	 */
	private String nakl_psc;
	/**
	 * @generated
	 */
	private String nakl_mesto;
	/**
	 * @generated
	 */
	private String nakl_kontakt_osoba;
	/**
	 * @generated
	 */
	private String nakl_kontakt;
	/**
	 * @generated
	 */
	private String vykl_nazev;
	/**
	 * @generated
	 */
	private String vykl_ulice;
	/**
	 * @generated
	 */
	private String vykl_psc;
	/**
	 * @generated
	 */
	private String vykl_mesto;
	/**
	 * @generated
	 */
	private String vykl_kontakt_osoba;
	/**
	 * @generated
	 */
	private String vykl_kontakt;

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
				+ id + " cislo_objednavky=" + cislo_objednavky + " datum="
				+ datum + " spec_zbozi=" + spec_zbozi + " adr=" + adr
				+ " preprav_podminky=" + preprav_podminky + " poznamka1="
				+ poznamka1 + " poznamka2=" + poznamka2 + " poznamka3="
				+ poznamka3 + " poznamka4=" + poznamka4 + " poznamka5="
				+ poznamka5 + " objednavka1=" + objednavka1 + " objednavka2="
				+ objednavka2 + " objednavka3=" + objednavka3 + " objednavka4="
				+ objednavka4 + " objednavka5=" + objednavka5 + " objednavka6="
				+ objednavka6 + " objednavka7=" + objednavka7 + " objednavka8="
				+ objednavka8 + " dod_nazev=" + dod_nazev + " dod_ulice="
				+ dod_ulice + " dod_psc=" + dod_psc + " dod_mesto=" + dod_mesto
				+ " dod_dic=" + dod_dic + " dod_ic=" + dod_ic + " nakl_nazev="
				+ nakl_nazev + " nakl_ulice=" + nakl_ulice + " nakl_psc="
				+ nakl_psc + " nakl_mesto=" + nakl_mesto
				+ " nakl_kontakt_osoba=" + nakl_kontakt_osoba
				+ " nakl_kontakt=" + nakl_kontakt + " vykl_nazev=" + vykl_nazev
				+ " vykl_ulice=" + vykl_ulice + " vykl_psc=" + vykl_psc
				+ " vykl_mesto=" + vykl_mesto + " vykl_kontakt_osoba="
				+ vykl_kontakt_osoba + " vykl_kontakt=" + vykl_kontakt;
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

	/**
	 * @generated
	 */
	public java.util.Date getDatum() {
		return this.datum;
	}

	/**
	 * @generated
	 */
	public void setDatum(java.util.Date datum) {
		this.datum = datum;
	}

	/**
	 * @generated
	 */
	public String getSpec_zbozi() {
		return this.spec_zbozi;
	}

	/**
	 * @generated
	 */
	public void setSpec_zbozi(String spec_zbozi) {
		this.spec_zbozi = spec_zbozi;
	}

	/**
	 * @generated
	 */
	public String getAdr() {
		return this.adr;
	}

	/**
	 * @generated
	 */
	public void setAdr(String adr) {
		this.adr = adr;
	}

	/**
	 * @generated
	 */
	public String getPreprav_podminky() {
		return this.preprav_podminky;
	}

	/**
	 * @generated
	 */
	public void setPreprav_podminky(String preprav_podminky) {
		this.preprav_podminky = preprav_podminky;
	}

	/**
	 * @generated
	 */
	public String getPoznamka1() {
		return this.poznamka1;
	}

	/**
	 * @generated
	 */
	public void setPoznamka1(String poznamka1) {
		this.poznamka1 = poznamka1;
	}

	/**
	 * @generated
	 */
	public String getPoznamka2() {
		return this.poznamka2;
	}

	/**
	 * @generated
	 */
	public void setPoznamka2(String poznamka2) {
		this.poznamka2 = poznamka2;
	}

	/**
	 * @generated
	 */
	public String getPoznamka3() {
		return this.poznamka3;
	}

	/**
	 * @generated
	 */
	public void setPoznamka3(String poznamka3) {
		this.poznamka3 = poznamka3;
	}

	/**
	 * @generated
	 */
	public String getPoznamka4() {
		return this.poznamka4;
	}

	/**
	 * @generated
	 */
	public void setPoznamka4(String poznamka4) {
		this.poznamka4 = poznamka4;
	}

	/**
	 * @generated
	 */
	public String getPoznamka5() {
		return this.poznamka5;
	}

	/**
	 * @generated
	 */
	public void setPoznamka5(String poznamka5) {
		this.poznamka5 = poznamka5;
	}

	/**
	 * @generated
	 */
	public String getObjednavka1() {
		return this.objednavka1;
	}

	/**
	 * @generated
	 */
	public void setObjednavka1(String objednavka1) {
		this.objednavka1 = objednavka1;
	}

	/**
	 * @generated
	 */
	public String getObjednavka2() {
		return this.objednavka2;
	}

	/**
	 * @generated
	 */
	public void setObjednavka2(String objednavka2) {
		this.objednavka2 = objednavka2;
	}

	/**
	 * @generated
	 */
	public String getObjednavka3() {
		return this.objednavka3;
	}

	/**
	 * @generated
	 */
	public void setObjednavka3(String objednavka3) {
		this.objednavka3 = objednavka3;
	}

	/**
	 * @generated
	 */
	public String getObjednavka4() {
		return this.objednavka4;
	}

	/**
	 * @generated
	 */
	public void setObjednavka4(String objednavka4) {
		this.objednavka4 = objednavka4;
	}

	/**
	 * @generated
	 */
	public String getObjednavka5() {
		return this.objednavka5;
	}

	/**
	 * @generated
	 */
	public void setObjednavka5(String objednavka5) {
		this.objednavka5 = objednavka5;
	}

	/**
	 * @generated
	 */
	public String getObjednavka6() {
		return this.objednavka6;
	}

	/**
	 * @generated
	 */
	public void setObjednavka6(String objednavka6) {
		this.objednavka6 = objednavka6;
	}

	/**
	 * @generated
	 */
	public String getObjednavka7() {
		return this.objednavka7;
	}

	/**
	 * @generated
	 */
	public void setObjednavka7(String objednavka7) {
		this.objednavka7 = objednavka7;
	}

	/**
	 * @generated
	 */
	public String getObjednavka8() {
		return this.objednavka8;
	}

	/**
	 * @generated
	 */
	public void setObjednavka8(String objednavka8) {
		this.objednavka8 = objednavka8;
	}

	/**
	 * @generated
	 */
	public String getDod_nazev() {
		return this.dod_nazev;
	}

	/**
	 * @generated
	 */
	public void setDod_nazev(String dod_nazev) {
		this.dod_nazev = dod_nazev;
	}

	/**
	 * @generated
	 */
	public String getDod_ulice() {
		return this.dod_ulice;
	}

	/**
	 * @generated
	 */
	public void setDod_ulice(String dod_ulice) {
		this.dod_ulice = dod_ulice;
	}

	/**
	 * @generated
	 */
	public String getDod_psc() {
		return this.dod_psc;
	}

	/**
	 * @generated
	 */
	public void setDod_psc(String dod_psc) {
		this.dod_psc = dod_psc;
	}

	/**
	 * @generated
	 */
	public String getDod_mesto() {
		return this.dod_mesto;
	}

	/**
	 * @generated
	 */
	public void setDod_mesto(String dod_mesto) {
		this.dod_mesto = dod_mesto;
	}

	/**
	 * @generated
	 */
	public String getDod_dic() {
		return this.dod_dic;
	}

	/**
	 * @generated
	 */
	public void setDod_dic(String dod_dic) {
		this.dod_dic = dod_dic;
	}

	/**
	 * @generated
	 */
	public String getDod_ic() {
		return this.dod_ic;
	}

	/**
	 * @generated
	 */
	public void setDod_ic(String dod_ic) {
		this.dod_ic = dod_ic;
	}

	/**
	 * @generated
	 */
	public String getNakl_nazev() {
		return this.nakl_nazev;
	}

	/**
	 * @generated
	 */
	public void setNakl_nazev(String nakl_nazev) {
		this.nakl_nazev = nakl_nazev;
	}

	/**
	 * @generated
	 */
	public String getNakl_ulice() {
		return this.nakl_ulice;
	}

	/**
	 * @generated
	 */
	public void setNakl_ulice(String nakl_ulice) {
		this.nakl_ulice = nakl_ulice;
	}

	/**
	 * @generated
	 */
	public String getNakl_psc() {
		return this.nakl_psc;
	}

	/**
	 * @generated
	 */
	public void setNakl_psc(String nakl_psc) {
		this.nakl_psc = nakl_psc;
	}

	/**
	 * @generated
	 */
	public String getNakl_mesto() {
		return this.nakl_mesto;
	}

	/**
	 * @generated
	 */
	public void setNakl_mesto(String nakl_mesto) {
		this.nakl_mesto = nakl_mesto;
	}

	/**
	 * @generated
	 */
	public String getNakl_kontakt_osoba() {
		return this.nakl_kontakt_osoba;
	}

	/**
	 * @generated
	 */
	public void setNakl_kontakt_osoba(String nakl_kontakt_osoba) {
		this.nakl_kontakt_osoba = nakl_kontakt_osoba;
	}

	/**
	 * @generated
	 */
	public String getNakl_kontakt() {
		return this.nakl_kontakt;
	}

	/**
	 * @generated
	 */
	public void setNakl_kontakt(String nakl_kontakt) {
		this.nakl_kontakt = nakl_kontakt;
	}

	/**
	 * @generated
	 */
	public String getVykl_nazev() {
		return this.vykl_nazev;
	}

	/**
	 * @generated
	 */
	public void setVykl_nazev(String vykl_nazev) {
		this.vykl_nazev = vykl_nazev;
	}

	/**
	 * @generated
	 */
	public String getVykl_ulice() {
		return this.vykl_ulice;
	}

	/**
	 * @generated
	 */
	public void setVykl_ulice(String vykl_ulice) {
		this.vykl_ulice = vykl_ulice;
	}

	/**
	 * @generated
	 */
	public String getVykl_psc() {
		return this.vykl_psc;
	}

	/**
	 * @generated
	 */
	public void setVykl_psc(String vykl_psc) {
		this.vykl_psc = vykl_psc;
	}

	/**
	 * @generated
	 */
	public String getVykl_mesto() {
		return this.vykl_mesto;
	}

	/**
	 * @generated
	 */
	public void setVykl_mesto(String vykl_mesto) {
		this.vykl_mesto = vykl_mesto;
	}

	/**
	 * @generated
	 */
	public String getVykl_kontakt_osoba() {
		return this.vykl_kontakt_osoba;
	}

	/**
	 * @generated
	 */
	public void setVykl_kontakt_osoba(String vykl_kontakt_osoba) {
		this.vykl_kontakt_osoba = vykl_kontakt_osoba;
	}

	/**
	 * @generated
	 */
	public String getVykl_kontakt() {
		return this.vykl_kontakt;
	}

	/**
	 * @generated
	 */
	public void setVykl_kontakt(String vykl_kontakt) {
		this.vykl_kontakt = vykl_kontakt;
	}
}