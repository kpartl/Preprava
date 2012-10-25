package cz.kpartl.preprava.view;

import java.text.SimpleDateFormat;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;

import cz.kpartl.preprava.dialog.NovyPozadavekDialog;
import cz.kpartl.preprava.model.Destinace;
import cz.kpartl.preprava.model.Pozadavek;
import cz.kpartl.preprava.util.EventConstants;

public class PozadavekDetailView extends ViewPart {

	public static final String ID = "cz.kpartl.preprava.part.pozadavekDetailPart";

	protected IEventBroker eventBroker;
	protected Pozadavek pozadavek;
	protected Composite parent;

	protected Label datum;
	protected Label datumNakladkylabel;
	protected Label datumVykladkylabel;
	protected Label odkud;
	protected Label kam;
	protected Label hmotnost;
	protected Label palet;
	protected Label termin_konecny;
	protected Label taxi;
	protected Label odkud_kontakt;
	protected Label kam_kontakt;
	protected Label hodina_nakladky;
	protected Label poznamka;
	protected Label zadavatel;
	protected Shell shell;

	protected Font boldFont;

	@Inject
	Shell parentShell;

	@Inject
	public PozadavekDetailView(Composite parent,
			@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell,
			IEclipseContext context, IEventBroker eventBroker) {
		this.parent = parent;
		this.eventBroker = eventBroker;
		this.shell = shell;
		this.pozadavek = null;

		GridLayout layout = new GridLayout(4, false);
		layout.horizontalSpacing = 30;
		parent.setLayout(layout);
		
		createPartControl(parent);
		

	}

	protected Label createBoldLabel(Composite parent, String text) {
		if (boldFont == null) {
			final FontData[] fd = parent.getDisplay().getSystemFont()
					.getFontData();
			fd[0].setStyle(SWT.BOLD);
			boldFont = new Font(
					parent.getDisplay().getSystemFont().getDevice(), fd[0]);
		}

		final Label result = new Label(parent, SWT.NONE);
		result.setFont(boldFont);
		result.setText(text);

		return result;
	}

	@Override
	public void createPartControl(Composite parent) {
		
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
		createBoldLabel(parent, "Celková hmotnost zásilky: ");
		hmotnost = new Label(parent, SWT.NONE);
		createBoldLabel(parent, "Poèet palet: ");
		palet = new Label(parent, SWT.NONE);
		createBoldLabel(parent, "Je termín nakládky koneèný?: ");
		termin_konecny = new Label(parent, SWT.NONE);
		createBoldLabel(parent, "TAXI?: ");
		taxi = new Label(parent, SWT.NONE);
		createBoldLabel(parent, "Hodina nakládky u dodavatele: ");
		hodina_nakladky = new Label(parent, SWT.NONE);
		createBoldLabel(parent, "Poznámka: ");
		poznamka = new Label(parent, SWT.NONE);
		createBoldLabel(parent, "Zadavatel: ");
		zadavatel = new Label(parent, SWT.NONE);

		// GridData gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false,
		// false);
		// datumNakladkylabel.setLayoutData(gridData);
		// datumVykladkylabel.setLayoutData(gridData);

		fillData();

	}

	protected void fillData() {
		if (pozadavek == null){
			datum.setText("");
			odkud.setText("");
			kam.setText("");
			odkud_kontakt.setText("");
			kam_kontakt.setText("");
			hmotnost.setText("");
			palet.setText("");
			termin_konecny.setText("");
			datum.setText("");
			datumNakladkylabel.setText("");
			datumVykladkylabel.setText("");
			taxi.setText("");
			hodina_nakladky.setText("");
			poznamka.setText("");
			zadavatel.setText("");
			
			return;
		}

		datum.setText(new SimpleDateFormat("dd.MM.yyyy").format(pozadavek
				.getDatum()));
		datumNakladkylabel.setText(pozadavek.getDatum_nakladky());
		datumVykladkylabel.setText(pozadavek.getDatum_vykladky());

		odkud.setText(pozadavek.getDestinaceZNazevACislo());
		odkud_kontakt.setText(pozadavek.getDestinaceZKontaktAOsobu());

		kam.setText(pozadavek.getDestinaceDoNazevACislo());
		kam_kontakt.setText(pozadavek.getDestinaceDoKontaktAOsobu());
		hmotnost.setText(pozadavek.getCelkova_hmotnost());
		palet.setText(pozadavek.getPocet_palet());
		String terminString = "NE";
		if (pozadavek.getJe_termin_konecny())
			terminString = "ANO";
		termin_konecny.setText(terminString);
		taxi.setText(pozadavek.getTaxi() ? "ANO" : "NE");
		hodina_nakladky.setText(pozadavek.getHodina_nakladky());
		poznamka.setText(pozadavek.getPoznamka());
		zadavatel.setText(pozadavek.getZadavatel().getUsername());

		datumNakladkylabel.getParent().layout();
	}

	@Override
	public void setFocus() {
		parent.setFocus();
	}

	@Inject
	@Optional
	void selectionChanged(
			@UIEventTopic(EventConstants.POZADAVEK_SELECTION_CHANGED) Pozadavek p) {
		this.pozadavek = p;
		fillData();

	}
	
	@Inject
	@Optional
	void selectionChangedToEmpty(
			@UIEventTopic(EventConstants.EMPTY_POZADAVEK_SEND) String s) {
		this.pozadavek = null;
		fillData();

	}

	@Inject
	@Optional	 
	protected void destroyMe(@UIEventTopic(EventConstants.DISPOSE_DETAIL) String s){		
		this.dispose();
	}
	
	@PreDestroy
	protected void onDestroy() {
		if (boldFont != null)
			boldFont.dispose();
	}

}
