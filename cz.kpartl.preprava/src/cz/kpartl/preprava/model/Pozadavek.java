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
	private Objednavka objednavka;

	/**
	 * @generated
	 */
	private String celkova_hmotnost;
	/**
	 * @generated
	 */
	private int attr;

	/**
	 * @generated
	 */
	private String hodina_nakladky;

	/**
	 * @generated
	 */
	private Destinace destinace_z;
	/**
	 * @generated
	 */
	private Destinace destinace_do;

	/**
	 * @generated
	 */
	private User zadavatel;

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
				+ " attr=" + attr + " hodina_nakladky=" + hodina_nakladky;
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
	public int getAttr() {
		return this.attr;
	}

	/**
	 * @generated
	 */
	public void setAttr(int attr) {
		this.attr = attr;
	}

	/**
	 * @generated
	 */
	public String getHodina_nakladky() {
		return this.hodina_nakladky;
	}

	/**
	 * @generated
	 */
	public void setHodina_nakladky(String hodina_nakladky) {
		this.hodina_nakladky = hodina_nakladky;
	}

	/**
	 * @generated
	 */
	public Destinace getDestinace_z() {
		return this.destinace_z;
	}

	/**
	 * @generated
	 */
	public void setDestinace_z(Destinace destinace_z) {
		this.destinace_z = destinace_z;
	}

	/**
	 * @generated
	 */
	public Destinace getDestinace_do() {
		return this.destinace_do;
	}

	/**
	 * @generated
	 */
	public void setDestinace_do(Destinace destinace_do) {
		this.destinace_do = destinace_do;
	}

	/**
	 * @generated
	 */
	public User getZadavatel() {
		return this.zadavatel;
	}

	/**
	 * @generated
	 */
	public void setZadavatel(User zadavatel) {
		this.zadavatel = zadavatel;
	}
}