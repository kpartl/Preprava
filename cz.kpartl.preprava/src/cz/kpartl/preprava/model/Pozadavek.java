package cz.kpartl.preprava.model;


/**
 * @generated
 */
public class Pozadavek implements java.io.Serializable {
	/**
	 * @generated
	 */
	private java.util.Date datum;
	/**
	 * @generated
	 */
	private String datum_nakladky;
	/**
	 * @generated
	 */
	private String datum_vykladky;
	/**
	 * @generated
	 */
	private String pocet_palet;
	/**
	 * @generated
	 */
	private Boolean je_termin_konecny;
	/**
	 * @generated
	 */
	private Boolean taxi;
	/**
	 * @generated
	 */
	private String poznamka;
	/**
	 * @generated
	 */
	private static final long serialVersionUID = -2022816842L;
	/**
	 * @generated
	 */
	private Long id;

	/**
	 * @generated
	 */
	private Zakaznik zakaznik;

	/**
	 * @generated
	 */
	private Dodavatel dodavatel;
	
	/**
	 * @generated
	 */
	private Objednavka objednavka;

	/**
	 * @generated
	 */
	private String celkova_hmotnost;
	/**
	 * @generated
	 */
	private User disponent;
	/**
	 * @generated
	 */
	private int attr;

	/**
	 * @generated
	 */
	public Pozadavek() {
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
	public String getDatum_nakladky() {
		return this.datum_nakladky;
	}

	/**
	 * @generated
	 */
	public void setDatum_nakladky(String datum_nakladky) {
		this.datum_nakladky = datum_nakladky;
	}

	/**
	 * @generated
	 */
	public String getDatum_vykladky() {
		return this.datum_vykladky;
	}

	/**
	 * @generated
	 */
	public void setDatum_vykladky(String datum_vykladky) {
		this.datum_vykladky = datum_vykladky;
	}

	/**
	 * @generated
	 */
	public String getPocet_palet() {
		return this.pocet_palet;
	}

	/**
	 * @generated
	 */
	public void setPocet_palet(String pocet_palet) {
		this.pocet_palet = pocet_palet;
	}

	/**
	 * @generated
	 */
	public Boolean getJe_termin_konecny() {
		return this.je_termin_konecny;
	}

	/**
	 * @generated
	 */
	public void setJe_termin_konecny(Boolean je_termin_konecny) {
		this.je_termin_konecny = je_termin_konecny;
	}

	/**
	 * @generated
	 */
	public Boolean getTaxi() {
		return this.taxi;
	}

	/**
	 * @generated
	 */
	public void setTaxi(Boolean taxi) {
		this.taxi = taxi;
	}

	/**
	 * @generated
	 */
	public String getPoznamka() {
		return this.poznamka;
	}

	/**
	 * @generated
	 */
	public void setPoznamka(String poznamka) {
		this.poznamka = poznamka;
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
		return "Pozadavek" + " datum=" + datum + " datum_nakladky="
				+ datum_nakladky + " datum_vykladky=" + datum_vykladky
				+ " pocet_palet=" + pocet_palet + " je_termin_konecny="
				+ je_termin_konecny + " taxi=" + taxi + " poznamka=" + poznamka
				+ " id=" + id + " celkova_hmotnost=" + celkova_hmotnost
				+ " attr=" + attr;
	}

	/**
	 * @generated
	 */
	public Zakaznik getZakaznik() {
		return this.zakaznik;
	}

	/**
	 * @generated
	 */
	public void setZakaznik(Zakaznik zakaznik) {
		this.zakaznik = zakaznik;
	}

	/**
	 * @generated
	 */
	public Dodavatel getDodavatel() {
		return this.dodavatel;
	}

	/**
	 * @generated
	 */
	public void setDodavatel(Dodavatel dodavatel) {
		this.dodavatel = dodavatel;
	}

	
	
	/**
	 * @generated
	 */
	public Objednavka getObjednavka() {
		return this.objednavka;
	}

	/**
	 * @generated
	 */
	public void setObjednavka(Objednavka objednavka) {
		this.objednavka = objednavka;
	}

	/**
	 * @generated
	 */
	public String getCelkova_hmotnost() {
		return this.celkova_hmotnost;
	}

	/**
	 * @generated
	 */
	public void setCelkova_hmotnost(String celkova_hmotnost) {
		this.celkova_hmotnost = celkova_hmotnost;
	}

	/**
	 * @generated
	 */
	public User getDisponent() {
		return this.disponent;
	}

	/**
	 * @generated
	 */
	public void setDisponent(User disponent) {
		this.disponent = disponent;
	}

	/**
	 * @generated
	 */
	public int getAttr() {
		return this.attr;
	}

	/**
	 * @generated
	 */
	public void setAttr(int attr) {
		this.attr = attr;
	}
}