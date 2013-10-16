package cz.kpartl.preprava.importer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVStrategy;
import org.eclipse.swt.custom.BusyIndicator;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kpartl.preprava.dao.DestinaceDAO;
import cz.kpartl.preprava.dao.DopravceDAO;
import cz.kpartl.preprava.model.Destinace;
import cz.kpartl.preprava.model.Dopravce;
import cz.kpartl.preprava.util.HibernateHelper;

public class DataImporter {

	public final static int STATUS_READY = 0;
	public final static int STATUS_WORKING = 1;
	final Logger logger = LoggerFactory.getLogger(this.getClass());

	String importedFilePath;
	DestinaceDAO destinaceDAO = new DestinaceDAO();
	DopravceDAO dopravceDAO = new DopravceDAO();
	
	HibernateHelper persistenceHelper = HibernateHelper.getInstance();

	int status = STATUS_READY;

	final ArrayList<String> messages = new ArrayList<String>();
	String message;

	public DataImporter(String importedFilePath) {
		super();
		this.importedFilePath = importedFilePath;
	}

	private String[][] importFile() {
		status = STATUS_WORKING;

		try {
			File file = new File(importedFilePath);
			if (!file.exists()) {
				message = "Soubor neexistuje: " + file.getAbsolutePath();
				messages.add(message);
				logger.error(message);
			}

			return (new CSVParser(new FileReader(file), new CSVStrategy(';',
					'"', '#'))).getAllValues();
		} catch (IOException e) {
			logger.error("Chyba pri cteni importovaneho souboru: ", e);
			messages.add("Pøi ètení importovaného souboru došlo k chybì, data nelze naimportovat");

			return null;
		}

	}

	public void importDestinace() {
		String[][] data = importFile();

		if (data == null)
			return;

		boolean prvniRadek = true;
		
		Transaction tx = persistenceHelper.getSession().beginTransaction();
		final int pocetDestinaciPred = destinaceDAO.findAll().size();
		int pocetChybnychDestinaci = 0;
		int pocetNovychDestinaci = 0;
		int pocetAktualizovanychDestinaci = 0;

		try {

			for (String[] radek : data) {
				if (prvniRadek) {
					prvniRadek = false;
					continue;
				}

				final Destinace destinace = getDestinace(radek);

				try {
					if (destinace != null) {
						Destinace existingDestinace = destinaceDAO
								.findByCislo(destinace.getCislo());
						if (existingDestinace == null) {
							destinaceDAO.create(destinace);
							pocetNovychDestinaci++;
						} else {
							existingDestinace
									.setKontakt(destinace.getKontakt() != null ? destinace.getKontakt():"");
							existingDestinace.setKontaktni_osoba(destinace
									.getKontaktni_osoba() != null ? destinace.getKontaktni_osoba():"");
							existingDestinace.setMesto(destinace.getMesto() != null ? destinace.getMesto():"");
							existingDestinace.setUlice(destinace.getUlice() != null ? destinace.getUlice():"");
							existingDestinace.setPSC(destinace.getPSC() != null ? destinace.getPSC():"");
							existingDestinace.setNazev(destinace.getNazev() != null ? destinace.getNazev():"");
							destinaceDAO.update(existingDestinace);
							pocetAktualizovanychDestinaci++;
							messages.add("Duplicitní nebo aktualizovaná destinace: " + destinace.toString());
						}
					} else {
						pocetChybnychDestinaci++;
					}

				} catch (Exception ex) {
					message = "Nelze vlozit destinaci " + destinace.toString();
					messages.add(message);
					logger.error(message, ex);
					pocetChybnychDestinaci++;
				}
			}

			tx.commit();
			int pocetDestinaciPo = destinaceDAO.findAll().size();
			logger.info("Pocet destinaci pred importem: " + pocetDestinaciPred);
			logger.info("Pocet destinaci po importu: " + pocetDestinaciPo);
			logger.info("Pocet chybnych destinaci: " + pocetChybnychDestinaci);

			messages.add("Poèet nových destinací: " + pocetNovychDestinaci);
			messages.add("Poèet aktualizovaných nebo duplicitních destinací: "
					+ pocetAktualizovanychDestinaci);
			messages.add("Poèet chybných destinací: " + pocetChybnychDestinaci);

		} catch (Exception e) {
			tx.rollback();
			logger.error("Chyba pri zapisu do db: ", e);
			messages.add("Pøi zápisu do databáze došlo k chybì, destinace nebyly naimportovány");
		}

		status = STATUS_READY;							
	}
	
	public void importDopravce() {
		String[][] data = importFile();

		if (data == null)
			return;

		boolean prvniRadek = true;
		
		Transaction tx = persistenceHelper.getSession().beginTransaction();
		final int pocetDopravcuPred = dopravceDAO.findAll().size();
		int pocetChybnychDopravcu = 0;
		int pocetNovychDopravcu = 0;
		int pocetAktualizovanychDopravcu = 0;

		try {

			for (String[] radek : data) {
				if (prvniRadek) {
					prvniRadek = false;
					continue;
				}

				final Dopravce dopravce = getDopravce(radek);

				try {
					if (dopravce != null) {
						Dopravce existingDopravce = dopravceDAO
								.findBySAPCislo(dopravce.getSap_cislo());
						if (existingDopravce == null) {
							dopravceDAO.create(dopravce);
							pocetNovychDopravcu++;
						} else {
							existingDopravce
									.setKontakt_ostatni(dopravce.getKontakt_ostatni() != null ? dopravce.getKontakt_ostatni():"");
							existingDopravce.setKontaktni_osoba(dopravce
									.getKontaktni_osoba()!= null ? dopravce.getKontaktni_osoba():"");
							existingDopravce.setKontaktni_telefon(dopravce
									.getKontaktni_telefon()!= null ? dopravce.getKontaktni_telefon():"");
							existingDopravce.setMesto(dopravce.getMesto()!= null ? dopravce.getMesto():"");
							existingDopravce.setUlice(dopravce.getUlice()!= null ? dopravce.getUlice():"");
							existingDopravce.setPsc(dopravce.getPsc()!= null ? dopravce.getPsc():"");
							existingDopravce.setNazev(dopravce.getNazev()!= null ? dopravce.getNazev():"");
							dopravceDAO.update(existingDopravce);
							pocetAktualizovanychDopravcu++;
							messages.add("Duplicitní nebo aktualizovaní dopravci: " + dopravce.toString());
						}
					} else {
						pocetChybnychDopravcu++;
					}

				} catch (Exception ex) {
					message = "Nelze vlozit dopravce " + dopravce.toString();
					messages.add(message);
					logger.error(message, ex);
					pocetChybnychDopravcu++;
				}
			}

			tx.commit();
			int pocetDopravcuPo = dopravceDAO.findAll().size();
			logger.info("Pocet dopravcu pred importem: " + pocetDopravcuPred);
			logger.info("Pocet dopravcu po importu: " + pocetDopravcuPo);
			logger.info("Pocet chybnych dopravcu: " + pocetChybnychDopravcu);

			messages.add("Poèet nových dopravcù: " + pocetNovychDopravcu);
			messages.add("Poèet aktualizovaných nebo duplicitních dopravcù: "
					+ pocetAktualizovanychDopravcu);
			messages.add("Poèet chybných dopravcù: " + pocetChybnychDopravcu);

		} catch (Exception e) {
			tx.rollback();
			logger.error("Chyba pri zapisu do db: ", e);
			messages.add("Pøi zápisu do databáze došlo k chybì, dopravci nebyly naimportovány");
		}

		status = STATUS_READY;
			
						

	}

	public ArrayList<String> getMessages() {
		return messages;
	}

	public static void main(String[] args) {
		DataImporter importer = new DataImporter("c:\\tmp\\destinace.csv ");
		importer.importDestinace();
	}

	private Destinace getDestinace(String[] radek) {
		final Destinace destinace = new Destinace();

		try {
			String nazev = radek[1];
			destinace.setNazev(nazev);
			destinace.setCislo(Integer.parseInt(radek[0]));
			destinace.setUlice(radek[2]);
			destinace.setMesto(radek[3]);
			final String psc = radek[4];
			// psc = psc.replaceAll(" ", "");
			destinace.setPSC(psc);
			destinace.setKontaktni_osoba(radek.length >=6 ? radek[5]:"");
			destinace.setKontakt(radek.length >=7 ? radek[6]:"");

		} catch (Exception e) {
			messages.add("Destinace nema spravny format: " + radekToString(radek));
			logger.error("Destinace nema spravny format: " + radekToString(radek), e);
			return null;
		}

		return destinace;
	}
	
	private Dopravce getDopravce(String[] radek) {
		final Dopravce dopravce = new Dopravce();

		try {
			String nazev = radek[1];
			dopravce.setNazev(nazev);
			dopravce.setSap_cislo(radek[0]);
			dopravce.setUlice(radek[2]);
			dopravce.setMesto(radek[3]);					
			dopravce.setPsc(radek[4]);
			dopravce.setDic(radek[5]);
			dopravce.setIc(radek[6]);
			dopravce.setKontaktni_osoba(radek.length >=8 ? radek[7]:"");
			dopravce.setKontaktni_telefon(radek.length >=9 ? radek[8]:"");
			dopravce.setKontakt_ostatni(radek.length >=10 ?radek[9]:"");

		} catch (Exception e) {
			messages.add("Dopravce nema spravny format: " + radekToString(radek));
			logger.error("Dopravce nema spravny format: " + radekToString(radek), e);
			return null;
		}

		return dopravce;
	}
	
	private String radekToString(String[] radek){
		StringBuilder result = new StringBuilder();
		for(String pole:radek){
			result.append(pole).append(";");
		}
		
		return result.toString();
	}

	public int getStatus() {
		return status;
	}

}
