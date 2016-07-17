package cz.kpartl.preprava.exporter;

import java.io.File;
import java.io.FileWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kpartl.preprava.dao.ObjednavkaDAO;
import cz.kpartl.preprava.util.HibernateHelper;
import cz.kpartl.preprava.view.ObjednanoView;

public class DataExporter {
	
	public final static int STATUS_READY = 0;
	public final static int STATUS_WORKING = 1;
    // -2 is used to signal disabled, because it won't be confused with
    // an EOF signal (-1), and because \ufffe in UTF-16 would be
    // encoded as two chars (using surrogates) and thus there should never
    // be a collision with a real text char.

	public static char COMMENTS_DISABLED = (char) -2;

	public static char ESCAPE_DISABLED = (char) -2;
	final Logger logger = LoggerFactory.getLogger(this.getClass());

	ObjednavkaDAO objednavkaDAO = new ObjednavkaDAO();
	
	HibernateHelper persistenceHelper = HibernateHelper.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	int pocetVyexportovanychObj = -1; // kvuli hlavicce je o 1 mensi

	int status = STATUS_READY;

	final ArrayList<String> messages = new ArrayList<String>();
	String message;
	
	public void exportObjednavky(java.util.Date date, String exportFilePath) {
		if (exportFilePath.isEmpty()) {
			messages.add("Zadejte výstupní soubor");
			return;
		}
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		exportFile(sqlDate, exportFilePath);
	}
	
	public ArrayList<String> getMessages() {
		return messages;
	}

	private void exportFile(Date date, String exportFilePath) {
		status = STATUS_WORKING;
		try {
			File file = new File(exportFilePath);	
			final FileWriter fileWriter = new FileWriter(file);
			CSVPrinter csvPrinter = new CSVPrinter(fileWriter);
			csvPrinter.setStrategy(CSVStrategy.TDF_STRATEGY);
			for(List<String> objednavka: getObjednavky(date)) {
				for(String polozka: objednavka) {
					csvPrinter.print(polozka);
				}
			    csvPrinter.println();
			    pocetVyexportovanychObj++;
			}
		  fileWriter.flush();
		  fileWriter.close();
			
		} catch (Exception e) {
			logger.error("Chyba pri exportu do souboru: ", e);
			messages.add("Pøi exportu došlo k chybì, data nelze exportovat");			
		}
	}
	
	public int getPocetVyexportovanychObj() {
		return pocetVyexportovanychObj;
	}
	
	public static String notNullStr(Object text) {
		return text != null ? String.valueOf(text) : "";
	}
	private List<String> getHlavicka() {
		List<String> result = new ArrayList<String>();
		result.add("Èíslo objednávky");
		result.add("Èíslo pøidružené objednávky");
		result.add("Status");
		result.add("Dodavatel");
		result.add("Cena");
		result.add("Mìna");
		result.add("Zmìna termínu nakládky");
		result.add("Èíslo faktury");
		result.add("Datum objednávky");
		result.add("Datum nakládky");
		result.add("Hodina nakládky");
		result.add("Datum vykládky");
		result.add("Hodina vykládky");
		result.add("Odkud");
		result.add("Odkud kontakt. osoba");
		result.add("Odkud kontakt");
		result.add("Kam");
		result.add("Kam kontakt. osoba");
		result.add("Kam kontakt");
		result.add("Hmotnost");
		result.add("Poèet palet");
		result.add("Stohovatelné");
		result.add("Termín koneèný");
		result.add("Taxi");
		result.add("Zadavatel");
		result.add("Poznámka");
		result.add("Poznámka 1");
		result.add("Poznámka 2");
		result.add("Poznámka 3");
		result.add("Poznámka 4");
		result.add("Poznámka 5");
		return result;
	}
	
	private List<List<String>>getObjednavky(Date fromDate) {
		
		List<List<String>>result = new ArrayList<List<String>>();
		List<String>obj;
		result.add(getHlavicka());
		for(cz.kpartl.preprava.model.Objednavka objednavka: objednavkaDAO.findFromDate(fromDate)) {
			obj =  new ArrayList<String>();
			obj.add(notNullStr(objednavka.getCislo_objednavky()));
			if (objednavka.getPridruzena_objednavka() != null) {
				obj.add(notNullStr(objednavka.getPridruzena_objednavka().getCislo_objednavky()));
			} else {
				obj.add("");
			}
			obj.add(ObjednanoView.getFazeKey(objednavka.getFaze()));
			obj.add(notNullStr(objednavka.getDod_nazev()));
			obj.add(notNullStr(objednavka.getCena()));
			obj.add(notNullStr(objednavka.getMena()));
			obj.add(notNullStr(objednavka.getZmena_nakladky()));
			obj.add(notNullStr(objednavka.getCislo_faktury_dopravce()));
			obj.add(getDateAsString(objednavka.getDatum()));
			obj.add(notNullStr(objednavka.getPozadavek().getDatum_nakladky()));
			obj.add(notNullStr(objednavka.getPozadavek().getHodina_nakladky()));
			obj.add(notNullStr(objednavka.getPozadavek().getDatum_vykladky()));
			obj.add(notNullStr(objednavka.getPozadavek().getHodina_vykladky()));
			obj.add(notNullStr(objednavka.getNakl_nazev()));			
			obj.add(notNullStr(objednavka.getNakl_kontakt_osoba()));
			obj.add(notNullStr(objednavka.getNakl_kontakt()));
			obj.add(notNullStr(objednavka.getVykl_nazev()));
			obj.add(notNullStr(objednavka.getVykl_kontakt_osoba()));			
			obj.add(notNullStr(objednavka.getVykl_kontakt()));
			obj.add(notNullStr(objednavka.getPozadavek().getCelkova_hmotnost()));
			obj.add(notNullStr(objednavka.getPozadavek().getPocet_palet()));
			obj.add(objednavka.getPozadavek().getJe_stohovatelne() ? "ano" : "ne");
			obj.add(objednavka.getPozadavek().getJe_termin_konecny()? "ano" : "ne");
			obj.add(objednavka.getPozadavek().getTaxi()? "ano" : "ne");
			obj.add(notNullStr(objednavka.getPozadavek().getZadavatel().getUsername()));
			obj.add(notNullStr(objednavka.getPozadavek().getPoznamka()));
			obj.add(notNullStr(objednavka.getPoznamka1()));
			obj.add(notNullStr(objednavka.getPoznamka2()));
			obj.add(notNullStr(objednavka.getPoznamka3()));
			obj.add(notNullStr(objednavka.getPoznamka4()));
			obj.add(notNullStr(objednavka.getPoznamka5()));
			result.add(obj);
			
		};
		return result;
	}
	
	private String getDateAsString(java.util.Date date) {
		return date != null ? sdf.format(date) : "";
	}
	
	

}
