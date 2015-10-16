package cz.kpartl.preprava.view;

import java.text.SimpleDateFormat;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import cz.kpartl.preprava.model.Objednavka;
import cz.kpartl.preprava.util.EventConstants;

public class ObjednavkaDetailView extends PozadavekDetailView {

	protected Objednavka objednavka;

	protected Label cisloObjednavky;
	protected Label faze;
	protected Label cena;
	protected Label mena;
	protected Label dopravce;
	protected Label zmenaTerminuNakladky;
	protected Label cisloFakturyDopravce;
	protected Label pridruzenaObjednavka;

	@Inject
	public ObjednavkaDetailView(Composite parent, Shell parentShell,
			IEclipseContext context, IEventBroker eventBroker) {
		super(parent, parentShell, context, eventBroker);

	}

	public static final String ID = "cz.kpartl.preprava.part.objednavkaDetailPart";

	@Override
	public void createPartControl(Composite parent) {

		createBoldLabel(parent, "Èíslo objednávky: ");
		cisloObjednavky = new Label(parent, SWT.NONE);

		createBoldLabel(parent, "Fáze objednávky: ");
		faze = new Label(parent, SWT.NONE);

		createBoldLabel(parent, "Dopravce: ");
		dopravce = new Label(parent, SWT.NONE);

		createBoldLabel(parent, "Èíslo faktury dopravce: ");
		cisloFakturyDopravce = new Label(parent, SWT.NONE);

		createBoldLabel(parent, "Cena dopravy: ");
		cena = new Label(parent, SWT.NONE);

		createBoldLabel(parent, "Mìna: ");
		mena = new Label(parent, SWT.NONE);

		createBoldLabel(parent, "Zmìna termínu nakládky: ");
		zmenaTerminuNakladky = new Label(parent, SWT.NONE);

		createBoldLabel(parent, "Pøidružená objednávka: ");
		pridruzenaObjednavka = new Label(parent, SWT.NONE);

		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);

		final Label separator = new Label(parent, SWT.SEPARATOR
				| SWT.HORIZONTAL);
		final GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL);
		gridData.horizontalSpan = 4;
		separator.setLayoutData(gridData);

		//super.createPartControl(parent);
		createBoldLabel(parent, "Datum požadavku: ");
		datum = new Label(parent, SWT.NONE);

		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);

		createBoldLabel(parent, "Požadované datum nakládky: ");

		datumNakladkylabel = new Label(parent, SWT.NONE);
		createBoldLabel(parent, "Požadované datum vykládky: ");
		datumVykladkylabel = new Label(parent, SWT.NONE);
		createBoldLabel(parent, "Výchozí destinace: ");
		odkud = new Label(parent, SWT.NONE);
		createBoldLabel(parent, "Kontaktní osoba a kontakt:");
		odkud_kontakt = new Label(parent, SWT.NONE);
		createBoldLabel(parent, "Cílová destinace: ");
		kam = new Label(parent, SWT.NONE);
		createBoldLabel(parent, "Kontaktní osoba a kontakt:");
		kam_kontakt = new Label(parent, SWT.NONE);
		createBoldLabel(parent, "Celková hmotnost zásilky v kg: ");
		hmotnost = new Label(parent, SWT.NONE);
		createBoldLabel(parent, "Poèet EUR palet: ");
		palet = new Label(parent, SWT.NONE);
		createBoldLabel(parent, "Palety jsou stohovatelné: ");
		stohovatelne = new Label(parent, SWT.NONE);
		createBoldLabel(parent, "Je termín nakládky koneèný?: ");
		termin_konecny = new Label(parent, SWT.NONE);
		createBoldLabel(parent, "TAXI?: ");
		taxi = new Label(parent, SWT.NONE);
		createBoldLabel(parent, "Hodina nakládky u dodavatele: ");
		hodina_nakladky = new Label(parent, SWT.NONE);
		createBoldLabel(parent, "Hodina vykládky: ");
		hodina_vykladky = new Label(parent, SWT.NONE);
		createBoldLabel(parent, "Poznámka: ");
		poznamka = new Label(parent, SWT.NONE);
		createBoldLabel(parent, "Zadavatel: ");
		zadavatel = new Label(parent, SWT.NONE);

		// GridData gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false,
		// false);
		// datumNakladkylabel.setLayoutData(gridData);
		// datumVykladkylabel.setLayoutData(gridData);

	

	}

	@Override
	public void setFocus() {
		super.setFocus();

	}

	@Override
	protected void fillData() {
		if (objednavka == null) {
			cisloObjednavky.setText("");
			faze.setText("");
			dopravce.setText("");
			zmenaTerminuNakladky.setText("");
			cena.setText("");
			mena.setText("");
			cisloFakturyDopravce.setText("");
			pridruzenaObjednavka.setText("");

			super.fillData();
			return;
		}

		cisloObjednavky
				.setText(String.valueOf(objednavka.getCislo_objednavky()));

		faze.setText(ObjednanoView.getFazeKey(objednavka.getFaze()));
		dopravce.setText(notNullStr(objednavka.getDod_nazev()));
		cena.setText(objednavka.getCenaFormated());
		zmenaTerminuNakladky.setText(objednavka.getZmena_nakladky());
		cisloFakturyDopravce.setText(objednavka
				.getCisloFakturyDopravceAsString());

		mena.setText(objednavka.getMena());
		if (objednavka.getPridruzena_objednavka() != null)
			pridruzenaObjednavka.setText(String.valueOf(objednavka
					.getPridruzena_objednavka().getCislo_objednavky()));
		else
			pridruzenaObjednavka.setText("");
		
		
		
		odkud.setText(notNullStr(objednavka.getNakl_nazev()));
		odkud_kontakt.setText(getSpojenyString(objednavka.getNakl_kontakt_osoba(), objednavka.getNakl_kontakt()));

		kam.setText(notNullStr(objednavka.getVykl_nazev()));
		kam_kontakt.setText(getSpojenyString(objednavka.getVykl_kontakt_osoba(), objednavka.getVykl_kontakt()));
		
		// pozadavek
		datum.setText(new SimpleDateFormat("dd.MM.yyyy").format(pozadavek
				.getDatum()));
		datumNakladkylabel.setText(pozadavek.getDatum_nakladky());
		datumVykladkylabel.setText(pozadavek.getDatum_vykladky());

				
		hmotnost.setText(pozadavek.getCelkova_hmotnost());
		palet.setText(pozadavek.getPocet_palet());
		stohovatelne.setText(pozadavek.getJe_stohovatelne() ?  "ANO" : "NE");
		termin_konecny.setText(pozadavek.getJe_termin_konecny() ? "ANO" : "NE");
		taxi.setText(pozadavek.getTaxi() ? "ANO" : "NE");
		hodina_nakladky.setText(pozadavek.getHodina_nakladky());
		hodina_vykladky.setText(pozadavek.getHodina_vykladky());
		poznamka.setText(pozadavek.getPoznamka());
		zadavatel.setText(pozadavek.getZadavatel().getUsername());

		datumNakladkylabel.getParent().layout();
	}
	
	
	

	@Inject
	@Optional
	void selectionChanged(
			@UIEventTopic(EventConstants.OBJEDNAVKA_SELECTION_CHANGED) Objednavka o) {
		if (isBeingDisposed)
			return;
		this.objednavka = o;
		this.pozadavek = o.getPozadavek();
		fillData();

	}

	@Inject
	@Optional
	void selectionChangedToEmpty(
			@UIEventTopic(EventConstants.EMPTY_OBJEDNAVKA_SEND) String s) {
		if (isBeingDisposed)
			return;
		if (s.equals(EventConstants.EMPTY_OBJEDNAVKA_SEND)) {
			this.objednavka = null;
			this.pozadavek = null;
			fillData();
		}
	}
	
	public static String getSpojenyString(String nazev, String cislo){
		if (nazev == null) nazev = "";
		if (cislo == null) 
			return nazev;
		else
			return nazev.concat(" (").concat(String.valueOf(cislo).concat(")"));
	}
	
	public static String notNullStr(String text) {
		return text != null ? text : "";
	}

}
