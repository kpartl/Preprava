package cz.kpartl.preprava.dialog;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hibernate.Transaction;
import org.hibernate.property.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kpartl.preprava.dao.DestinaceDAO;
import cz.kpartl.preprava.dao.ObjednatelDAO;
import cz.kpartl.preprava.dao.ObjednavkaDAO;
import cz.kpartl.preprava.dao.PozadavekDAO;
import cz.kpartl.preprava.model.Destinace;
import cz.kpartl.preprava.model.Objednatel;
import cz.kpartl.preprava.model.Objednavka;
import cz.kpartl.preprava.util.EventConstants;
import cz.kpartl.preprava.util.HibernateHelper;
import cz.kpartl.preprava.util.OtherUtils;
import cz.kpartl.preprava.util.PrintHelper;

public class FormularDialog extends TitleAreaDialog {

	final Font nr12B = new org.eclipse.swt.graphics.Font(null,
			"Times New Roman", 12, SWT.BOLD);

	private Objednavka objednavka;
	private ObjednavkaDAO objednavkaDAO;
	private ObjednatelDAO objednatelDAO;
	private PozadavekDAO pozadavekDAO;
	HibernateHelper persistenceHelper;
	Text obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8, dodNazev, dodUlice,
			dodPsc, dodDic, dodIc, destZNazev, destZUlice, destZPsc,
			destZMesto, destZKontOs, destZKontakt, specZbozi, adr, destDoNazev,
			destDoUlice, destDoPsc, destDoMesto, destDoKontOs, destDoKontakt,
			prepravniPodminky, poznamky, cisloObjednavky, datumObjednavky,
			terminNakladky, hodinaNakladky, dodSapCislo, hmotnost, dodMesto, pocetPalet,
			terminVykladky, hodinaVykladky, cena, poznamka;
	Button stohovatelne;

	Destinace destinace;
	final Logger logger = LoggerFactory.getLogger(NovaDestinaceDialog.class);

	IEventBroker eventBroker;
	IEclipseContext context;

	public FormularDialog(
			@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell,
			IEclipseContext context, IEventBroker eventBroker,
			Objednavka objednavka) {
		this(parentShell, context, null, eventBroker, objednavka);
	}

	@Inject
	public FormularDialog(
			@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell,
			IEclipseContext context, Object object, IEventBroker eventBroker,
			Objednavka objednavka) {
		super(parentShell);
		this.objednavka = objednavka;
		this.objednatelDAO = context.get(ObjednatelDAO.class);
		this.objednavkaDAO = context.get(ObjednavkaDAO.class);
		this.pozadavekDAO = context.get(PozadavekDAO.class);
		this.eventBroker = eventBroker;
		this.context = context;
		persistenceHelper = cz.kpartl.preprava.util.HibernateHelper
				.getInstance();
		fillObjednatele();
	}

	@Override
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.MAX);
	}

	@Override
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		setTitle("OBJEDNÁVKA PØEPRAVY");
		setTitleAreaColor(new RGB(0xc1, 0xc1, 0xc1));

		return contents;
	}

	@Override
	protected Control createDialogArea(Composite superParent) {

		boolean novaObjednavka = objednavka.getId() == null;

		Composite area = (Composite) super.createDialogArea(superParent);

		ScrolledComposite scrolledComposite = new ScrolledComposite(area,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gridData.heightHint = 600;// adjustable size, indispensable
		scrolledComposite.setLayoutData(gridData);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setAlwaysShowScrollBars(true);

		Composite container = new Composite(scrolledComposite, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite parent = new Composite(container, SWT.NONE);

		GridLayout layout = new GridLayout(2, false);
		layout.marginBottom = 5;
		layout.marginLeft = 0;
		layout.marginTop = 5;
		layout.marginRight = 0;

		parent.setLayout(layout);

		GridLayout groupLayout = new GridLayout(1, false);
		Group oGroup = new Group(parent, SWT.NONE);

		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		// gridData.horizontalSpan = 2;
		oGroup.setLayoutData(gridData);
		oGroup.setLayout(groupLayout);

		groupLayout = new GridLayout(1, false);
		Group dGroup = new Group(parent, SWT.NONE);

		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		// gridData.horizontalSpan = 2;
		dGroup.setLayoutData(gridData);
		dGroup.setLayout(new GridLayout(2, false));

		final Label objednavatelLabel = new Label(oGroup, SWT.BORDER_SOLID);
		objednavatelLabel.setLayoutData(getHSpanGridData(2));
		objednavatelLabel.setFont(nr12B);
		// objednavatelLabel.setBackground(gray);
		objednavatelLabel.setText("Objednavatel:");

		final Label dodavatelLabel = new Label(dGroup, SWT.NONE);
		dodavatelLabel.setLayoutData(getHSpanGridData(2));
		dodavatelLabel.setFont(nr12B);
		// dodavatelLabel.setBackground(gray);
		dodavatelLabel.setText("Dodavatel (pøepravce):");

		obj1 = createText(oGroup, objednavka.getObjednavka1());
		obj2 = createText(oGroup, objednavka.getObjednavka2());
		obj3 = createText(oGroup, objednavka.getObjednavka3());
		obj4 = createText(oGroup, objednavka.getObjednavka4());
		obj5 = createText(oGroup, objednavka.getObjednavka5());
		obj6 = createText(oGroup, objednavka.getObjednavka6());
		obj7 = createText(oGroup, objednavka.getObjednavka7());
		obj8 = createText(oGroup, objednavka.getObjednavka8());

		dodNazev = createText(dGroup, notNullStr(objednavka.getDod_nazev()), 2);
		dodUlice = createText(dGroup, notNullStr(objednavka.getDod_ulice()), 2);
		dodPsc = createText(dGroup, notNullStr(objednavka.getDod_psc()));
		dodMesto = createText(dGroup, notNullStr(objednavka.getDod_mesto()));
		getLabel("DIÈ:", dGroup);
		dodDic = createText(dGroup, notNullStr(objednavka.getDod_dic()));
		getLabel("IÈ:", dGroup);
		dodIc = createText(dGroup, notNullStr(objednavka.getDod_ic()));

		getLabel("Dodavatelské èíslo:", dGroup);
		dodSapCislo = createText(dGroup, novaObjednavka &&  objednavka
				.getDopravce() != null ? objednavka.getDopravce().getSap_cislo() 
						: objednavka.getDod_sap_cislo());

		// detail group
		Group detailGroup = new Group(parent, SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.horizontalSpan = 2;
		detailGroup.setLayoutData(gridData);
		groupLayout = new GridLayout(6, false);
		detailGroup.setLayout(groupLayout);

		getLabel("Èíslo objednávky:", detailGroup);

		cisloObjednavky = createText(
				detailGroup,
				formatCislo(String.valueOf(objednavka.getCislo_objednavky()), 6),
				2);

		getLabel("Datum objednávky:", detailGroup);
		datumObjednavky = createText(detailGroup, new SimpleDateFormat(
				"dd.MM.yyyy").format(novaObjednavka ||  objednavka
						.getDatum() == null ? new Date() : objednavka
				.getDatum()), 2);

		getLabel("Termín nakládky:", detailGroup);
		terminNakladky = createText(detailGroup, objednavka.getPozadavek()
				.getDatum_nakladky(), 2);
		getLabel("Hodina nakládky:", detailGroup);
		hodinaNakladky = createText(detailGroup, objednavka.getPozadavek()
				.getHodina_nakladky(), 2);

		getLabel("Místo nakládky:", detailGroup);
		getLabel("Firma:", detailGroup, 2);
		destZNazev = createText(detailGroup, novaObjednavka ? notNullStr(objednavka
				.getPozadavek().getDestinace_z().getNazev()) : objednavka.getNakl_nazev(), 3);

		getLabel("", detailGroup);
		getLabel("Adresa:", detailGroup, 2);
		destZUlice = createText(detailGroup, novaObjednavka ? notNullStr(objednavka
				.getPozadavek().getDestinace_z().getUlice()) : objednavka.getNakl_ulice(), 3);

		getLabel(" ", detailGroup, 3);

		destZPsc = createText(detailGroup, novaObjednavka ? notNullStr(objednavka.getPozadavek()
				.getDestinace_z().getPSC()) : objednavka.getNakl_psc());
		
		destZMesto = createText(detailGroup, novaObjednavka ? notNullStr(objednavka
				.getPozadavek().getDestinace_z().getMesto()) : objednavka.getNakl_mesto(), 2);

		getLabel(" ", detailGroup);
		getLabel("Kontaktní osoba:", detailGroup, 2);
		destZKontOs = createText(detailGroup, novaObjednavka ? notNullStr(objednavka
				.getPozadavek().getDestinace_z().getKontaktni_osoba()) : objednavka.getNakl_kontakt_osoba(), 3);

		getLabel(" ", detailGroup);
		getLabel("Kontakt:", detailGroup, 2);
		destZKontakt = createText(detailGroup, novaObjednavka ? notNullStr(objednavka
				.getPozadavek().getDestinace_z().getKontakt()) :  objednavka.getNakl_kontakt(), 3);

		getLabel("Specifikace zboží:", detailGroup);

		specZbozi = createText(detailGroup,
				notNullStr(objednavka.getSpec_zbozi()), 5);

		getLabel("", detailGroup);
		getLabel("Hmotnost", detailGroup);
		hmotnost = createText(detailGroup, objednavka.getPozadavek()
				.getCelkova_hmotnost(), 2);

		getLabel("ADR", detailGroup);
		adr = createText(detailGroup, notNullStr(objednavka.getAdr()));
		getLabel("", detailGroup);
		getLabel("Poèet palet:", detailGroup);
		pocetPalet = createText(detailGroup, notNullStr(objednavka
				.getPozadavek().getPocet_palet()), 2);
		getLabel("Stohovatelné?", detailGroup);

		stohovatelne = new Button(detailGroup, SWT.CHECK);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		stohovatelne.setLayoutData(gridData);
		stohovatelne.setSelection(objednavka.getPozadavek()
				.getJe_stohovatelne());

		getLabel("Termín vykládky:", detailGroup);
		terminVykladky = createText(detailGroup, notNullStr(objednavka
				.getPozadavek().getDatum_vykladky()), 2);
		getLabel("Hodina vykládky:", detailGroup);
		hodinaVykladky = createText(detailGroup, notNullStr(objednavka
				.getPozadavek().getHodina_vykladky()), 2);

		getLabel("Místo vykládky:", detailGroup);
		getLabel("Firma:", detailGroup, 2);
		destDoNazev = createText(detailGroup, novaObjednavka ? notNullStr(objednavka
				.getPozadavek().getDestinace_do().getNazev()) :  objednavka.getVykl_nazev(), 3);

		getLabel("", detailGroup);
		getLabel("Adresa:", detailGroup, 2);
		destDoUlice = createText(detailGroup, novaObjednavka ? notNullStr(objednavka
				.getPozadavek().getDestinace_do().getUlice()) : objednavka.getVykl_ulice(), 3);

		getLabel(" ", detailGroup, 3);

		destDoPsc = createText(detailGroup, novaObjednavka ? notNullStr(objednavka
				.getPozadavek().getDestinace_do().getPSC()) : objednavka.getVykl_psc());
		
		destDoMesto = createText(detailGroup, novaObjednavka ? notNullStr(objednavka
				.getPozadavek().getDestinace_do().getMesto()) : objednavka.getVykl_mesto(), 2);

		getLabel(" ", detailGroup);
		
		getLabel("Kontaktní osoba:", detailGroup, 2);
		destDoKontOs = createText(detailGroup, novaObjednavka ? notNullStr(objednavka
				.getPozadavek().getDestinace_do().getKontaktni_osoba()) : objednavka.getVykl_kontakt_osoba(), 3);

		getLabel(" ", detailGroup);
		getLabel("Kontakt:", detailGroup, 2);
		destDoKontakt = createText(detailGroup, novaObjednavka ? notNullStr(objednavka
				.getPozadavek().getDestinace_do().getKontakt()) : objednavka.getVykl_kontakt(), 3);

		getLabel("Cena za dopravu:", detailGroup);
		cena = createText(detailGroup,
				notNullStr(objednavka.getCenaFormated()), 5);

		getLabel("Pøepravní podmínky:", detailGroup);
		prepravniPodminky = createText(detailGroup,
				notNullStr(objednavka.getPreprav_podminky()), 5);

		getLabel("Poznámka:", detailGroup);
		poznamka = createText(detailGroup, notNullStr(objednavka.getPozadavek()
				.getPoznamka()), 5);
		poznamky = new Text(detailGroup, SWT.MULTI | SWT.BORDER | SWT.WRAP
				| SWT.V_SCROLL);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 6;
		gridData.heightHint = 90;
		poznamky.setLayoutData(gridData);
		poznamky.setText(getPoznamky());

		scrolledComposite.setContent(container);
		scrolledComposite.setMinSize(container.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));
		
		if (objednavka.getFaze() != 0) {
			setEnabledRecursive(parent, false);
		}
		return area;

	}

	@Override
	protected void okPressed() {
		if (saveBeforeClose())
			super.okPressed();
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {

		// Change parent layout data to fill the whole bar
		parent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		createButton(parent, IDialogConstants.OK_ID, "Uložit", true);
		Button okAndPrintButton = createButton(parent, IDialogConstants.NO_ID,
				"Uložit a tisk", false);
		okAndPrintButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (saveBeforeClose()) {
					final PrintHelper printHelper = new PrintHelper(parent.getShell(), context);
					printHelper
							.tiskVybraneObjednavky(objednavka);
					close();
				}
			}
		});
		// Create a spacer label
		Label spacer = new Label(parent, SWT.NONE);
		spacer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Update layout of the parent composite to count the spacer
		GridLayout layout = (GridLayout) parent.getLayout();
		layout.numColumns++;
		layout.makeColumnsEqualWidth = false;

		createButton(parent, IDialogConstants.CANCEL_ID, "Zrušit", false);

		// //super.createButtonsForButtonBar(parent);
		// Button okAndSaveButton = new Button(parent, SWT.PUSH);
		// //setButtonLayoutData(okAndSaveButton);
		// okAndSaveButton.setText("Uložit a tisk");
		//
		// Button okButton = getButton(IDialogConstants.OK_ID);
		// parent.getShell().setDefaultButton(okButton);
		// //setButtonLayoutData(okButton);
		// okButton.setText("Uložit");
		//
		// Button cancel = getButton(IDialogConstants.CANCEL_ID);
		// cancel.setText("Zrušit");
		// setButtonLayoutData(cancel);
	}

	protected ArrayList<String> validate() {
		ArrayList<String> result = new ArrayList<String>();
		// do result muzu pridat chybove hlasky
		if (!OtherUtils.isValidDate(datumObjednavky.getText()))
			result.add("Špatné datum objednávky.");

		try {
			Long.valueOf(cisloObjednavky.getText());
		} catch (NumberFormatException e) {
			result.add("Špatné èíslo objednávky.");
		}

		cena.setText(cena.getText().trim());
		// nahrazeni des. tecky carkou
		if (cena.getText().length() > 0) {
			final int desTecka = cena.getText().lastIndexOf('.');
			if (desTecka > 0 && desTecka > cena.getText().length() - 4)
				cena.setText(cena.getText().substring(0, desTecka) + ","
						+ cena.getText().substring(desTecka + 1));
		}
		final String cenaText = cena.getText().replaceAll("\\.", "")
				.replace(',', '.');
		if ("" != cenaText) {

			try {
				BigDecimal.valueOf(Double.valueOf(cenaText));

			} catch (NumberFormatException ex) {
				result.add("Špatnì zadaná cena dopravy " + cenaText
						+ ". Musí být ve tvaru 12345,67");
			}
		}
		return result;
	}

	private GridData getGridData() {
		return new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
	}

	private GridData getHSpanGridData(int span) {
		GridData result = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		result.horizontalSpan = span;
		return result;
	}

	private Label getLabel(String text, Composite parent, int span) {
		Label result = new Label(parent, SWT.NONE);
		result.setLayoutData(span > 1 ? getHSpanGridData(span) : getGridData());
		result.setText(text);
		result.setFont(nr12B);
		// result.setBackground( new Color(parent.getDisplay(), new RGB(0xc1,
		// 0xc1, 0xc1)));
		return result;
	}

	private Label getLabel(String text, Composite parent) {
		return getLabel(text, parent, 1);
	}

	private String formatCislo(String cislo, int pozic) {
		String result = cislo;
		while (result.length() < pozic)
			result = "0" + result;
		return result;
	}

	private Text createText(Composite parent, String str) {
		return createText(parent, str, 1);
	}

	private Text createText(Composite parent, String str, int span) {
		final Text text = new Text(parent, SWT.BORDER | SWT.BEGINNING);
		text.setText(notNullStr(str));
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		// gd.widthHint = 200;
		gd.horizontalSpan = span;
		text.setLayoutData(gd);
		return text;
	}

	public static String notNullStr(String text) {
		return text != null ? text : "";
	}

	private void fillObjednatele() {
		if (objednavka.getObjednavka1() == null) {
			Objednatel o = objednatelDAO.read(1L);
			if (o != null) {
				objednavka.setObjednavka1(o.getO1());
				objednavka.setObjednavka2(o.getO2());
				objednavka.setObjednavka3(o.getO3());
				objednavka.setObjednavka4(o.getO4());
				objednavka.setObjednavka5(o.getO5());
				objednavka.setObjednavka6(o.getO6());
				objednavka.setObjednavka7(o.getO7());
				objednavka.setObjednavka8(o.getO8());
			}
		}
	}

	private void fillObjednavka() {
		objednavka.setObjednavka1(obj1.getText());
		objednavka.setObjednavka2(obj2.getText());
		objednavka.setObjednavka3(obj3.getText());
		objednavka.setObjednavka4(obj4.getText());
		objednavka.setObjednavka5(obj5.getText());
		objednavka.setObjednavka6(obj6.getText());
		objednavka.setObjednavka7(obj7.getText());
		objednavka.setObjednavka8(obj8.getText());
		objednavka.setDod_nazev(dodNazev.getText());
		objednavka.setDod_ulice(dodUlice.getText());
		objednavka.setDod_psc(dodPsc.getText());
		objednavka.setDod_mesto(dodMesto.getText());
		objednavka.setDod_dic(dodDic.getText());
		objednavka.setDod_ic(dodIc.getText());
		objednavka.setNakl_nazev(destZNazev.getText());
		objednavka.setNakl_ulice(destZUlice.getText());
		objednavka.setNakl_psc(destZPsc.getText());
		objednavka.setNakl_mesto(destZMesto.getText());
		objednavka.setNakl_kontakt_osoba(destZKontOs.getText());
		objednavka.setNakl_kontakt(destZKontakt.getText());
		objednavka.setSpec_zbozi(specZbozi.getText());
		objednavka.setAdr(adr.getText());
		objednavka.setVykl_nazev(destDoNazev.getText());
		objednavka.setVykl_ulice(destDoUlice.getText());
		objednavka.setVykl_psc(destDoPsc.getText());
		objednavka.setVykl_mesto(destDoMesto.getText());
		objednavka.setVykl_kontakt_osoba(destDoKontOs.getText());
		objednavka.setVykl_kontakt(destDoKontakt.getText());
		objednavka.setPreprav_podminky(prepravniPodminky.getText());
		objednavka.setDatum(OtherUtils.parseDate(datumObjednavky.getText()));
		objednavka.setCislo_objednavky(Long.valueOf(cisloObjednavky.getText()));
		objednavka.setDod_sap_cislo(dodSapCislo.getText());
		objednavka.getPozadavek().setDatum_nakladky(terminNakladky.getText());
		objednavka.getPozadavek().setHodina_nakladky(hodinaNakladky.getText());
		if ("" != cena.getText())
			objednavka.setCena(BigDecimal.valueOf(Double.valueOf(cena.getText()
					.replaceAll("\\.", "").replace(',', '.'))));
		objednavka.getPozadavek().setDatum_vykladky(terminVykladky.getText());
		objednavka.getPozadavek().setHodina_vykladky(hodinaVykladky.getText());
		objednavka.getPozadavek().setCelkova_hmotnost(hmotnost.getText());
		objednavka.getPozadavek().setPocet_palet(pocetPalet.getText());
		objednavka.getPozadavek().setJe_stohovatelne(
				stohovatelne.getSelection());
		objednavka.getPozadavek().setPoznamka(poznamka.getText());
		

		fillPoznamky();
	}

	private String getPoznamky() {
		String ret ="";
		 ret = notNullStr(objednavka.getPoznamka1())
				+ notNullStr(objednavka.getPoznamka2())
				+ notNullStr(objednavka.getPoznamka3())
				+ notNullStr(objednavka.getPoznamka4())
				+ notNullStr(objednavka.getPoznamka5());
		return ret;
	}

	private void fillPoznamky() {
		objednavka.setPoznamka1("");
		objednavka.setPoznamka2("");
		objednavka.setPoznamka3("");
		objednavka.setPoznamka4("");
		objednavka.setPoznamka5("");
		int width = 96;
		if (poznamky.getText().length() > 0)
			objednavka.setPoznamka1(getSubstring(poznamky.getText(), 0, width));
		else
			return;
		if (poznamky.getText().length() > 255)
			objednavka.setPoznamka2(getSubstring(poznamky.getText(), width, 2*width));
		else
			return;
		if (poznamky.getText().length() > 510)
			objednavka.setPoznamka3(getSubstring(poznamky.getText(), 2*width, 3*width));
		else
			return;
		if (poznamky.getText().length() > 3*width)
			objednavka
					.setPoznamka4(getSubstring(poznamky.getText(), 3*width, 4*width));
		else
			return;
		if (poznamky.getText().length() > 4*width)
			objednavka
					.setPoznamka5(getSubstring(poznamky.getText(), 4*width, 5*width));
		else
			return;

	}

	private String getSubstring(String str, int from, int to) {
		if (str == null)
			return "";
		else
			return str.length() < to ? str.substring(from, str.length()) : str
					.substring(from, to);
	}

	private boolean saveBeforeClose() {
		boolean novaObjednavka = objednavka.getId() == null;
		final ArrayList<String> validace = validate();
		if (validace.size() == 0) {
			fillObjednavka();
			// destinace.setUlice(ulice.getText());
			Transaction tx = persistenceHelper.beginTransaction();
			try {
				if (novaObjednavka) {
					Long maxCislo = objednavkaDAO.getMaxCisloObjednavky();
					if (maxCislo == null)
						maxCislo = (long) 0;
					objednavka.setCislo_objednavky(maxCislo + 1);
					objednavka.setId(objednavkaDAO.create(objednavka));
					pozadavekDAO.update(objednavka.getPozadavek());
				} else {
					persistenceHelper.getSession().flush();
					persistenceHelper.getSession().clear();
					pozadavekDAO.update(objednavka.getPozadavek());
					objednavkaDAO.update(objednavka);
				}
				tx.commit();
				persistenceHelper.getSession().flush();
				// persistenceHelper.getSession().close();

				eventBroker.send(EventConstants.REFRESH_VIEWERS, "");
				eventBroker.send(EventConstants.OBJEDNAVKA_SELECTION_CHANGED, objednavka);

				return true;
			} catch (Exception ex) {
				tx.rollback();
				setErrorMessage("Pøi zápisu do databáze došlo k chybì, kontaktujte prosím tvùrce aplikace."
						.concat(System.getProperty("line.separator")).concat(
								ex.getMessage()));
				logger.error("Nelze vložit/upravit destinaci", ex);
				return false;
			}

		} else {
			String errString = "";
			for (String validaceMessage : validace)
				errString = errString.concat(validaceMessage).concat(
						System.getProperty("line.separator"));
			setErrorMessage(errString);
			return false;
		}

	}
	
	public static void setEnabledRecursive(final Composite composite, final boolean enabled)
	{
	    if (composite == null)
	    	return;

	    Control[] children = composite.getChildren();

	    for (int i = 0; i < children.length; i++)
	    {
	        if (children[i] instanceof Composite)
	        {
	            setEnabledRecursive((Composite) children[i], enabled);
	        }
	        else
	        {
	            children[i].setEnabled(enabled);
	        }
	    }

	    composite.setEnabled(enabled);
	}

}
