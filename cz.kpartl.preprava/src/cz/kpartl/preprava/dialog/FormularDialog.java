package cz.kpartl.preprava.dialog;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kpartl.preprava.dao.DestinaceDAO;
import cz.kpartl.preprava.dao.ObjednatelDAO;
import cz.kpartl.preprava.dao.ObjednavkaDAO;
import cz.kpartl.preprava.model.Destinace;
import cz.kpartl.preprava.model.Objednatel;
import cz.kpartl.preprava.model.Objednavka;
import cz.kpartl.preprava.util.EventConstants;
import cz.kpartl.preprava.util.HibernateHelper;

public class FormularDialog extends TitleAreaDialog {

	final Font nr12B = new org.eclipse.swt.graphics.Font(null,
			"Times New Roman", 12, SWT.BOLD);

	private Objednavka objednavka;
	private ObjednavkaDAO objednavkaDAO;
	private ObjednatelDAO objednatelDAO;
	HibernateHelper persistenceHelper;
	Text obj1, obj2, obj3,obj4,obj5,obj6,obj7,obj8,dodNazev,dodUlice,dodPsc,dodDic,dodIc,destZNazev,destZUlice,destZPsc,destZMesto,destZKontOs,destZKontakt,specZbozi,adr,destDoNazev,destDoUlice,
	destDoPsc,destDoMesto,destDoKontOs,destDoKontakt,prepravniPodminky,poznamky;

	
	Destinace destinace;
	final Logger logger = LoggerFactory.getLogger(NovaDestinaceDialog.class);

	IEventBroker eventBroker;

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
		this.eventBroker = eventBroker;
		persistenceHelper = cz.kpartl.preprava.util.HibernateHelper
				.getInstance();
		fillObjednatele();
	}

	@Override
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.MAX );
	}

	@Override
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		setTitle("OBJEDN�VKA P�EPRAVY");
		setTitleAreaColor(new RGB(0xc1, 0xc1, 0xc1));

		return contents;
	}

	@Override
	protected Control createDialogArea(Composite superParent) {

		 Composite area = (Composite) super.createDialogArea (superParent);

		 ScrolledComposite scrolledComposite = new ScrolledComposite (area, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		 GridData gridData = new GridData (SWT.FILL, SWT.FILL, false, false, 1, 1);
		 gridData.heightHint = 600 ;// adjustable size, indispensable
		 scrolledComposite.setLayoutData (gridData);
		 scrolledComposite.setExpandHorizontal (true);
		 scrolledComposite.setExpandVertical (true);
		 scrolledComposite.setAlwaysShowScrollBars (true);


		 Composite container = new Composite (scrolledComposite, SWT.NONE);
		 container.setLayout (new FillLayout (SWT.HORIZONTAL));

		 Composite parent = new Composite (container, SWT.NONE);
		 

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
		dGroup.setLayout(groupLayout);

		final Label objednavatelLabel = new Label(oGroup, SWT.BORDER_SOLID);
		objednavatelLabel.setLayoutData(getHSpanGridData(2));
		objednavatelLabel.setFont(nr12B);
		// objednavatelLabel.setBackground(gray);
		objednavatelLabel.setText("Objednavatel");

		final Label dodavatelLabel = new Label(dGroup, SWT.NONE);
		dodavatelLabel.setLayoutData(getHSpanGridData(2));
		dodavatelLabel.setFont(nr12B);
		// dodavatelLabel.setBackground(gray);
		dodavatelLabel.setText("Dodavatel (p�epravce):");

		obj1 = createText(oGroup, objednavka.getObjednavka1());
		obj2 = createText(oGroup, objednavka.getObjednavka2());
		obj3 = createText(oGroup, objednavka.getObjednavka3());
		obj4 = createText(oGroup, objednavka.getObjednavka4());
		obj5 = createText(oGroup, objednavka.getObjednavka5());
		obj6 = createText(oGroup, objednavka.getObjednavka6());
		obj7 = createText(oGroup, objednavka.getObjednavka7());
		obj8 = createText(oGroup, objednavka.getObjednavka8());

		dodNazev = createText(dGroup, notNullStr(objednavka.getDod_nazev()));
		dodUlice = createText(dGroup, notNullStr(objednavka.getDod_ulice()));
		dodPsc = createText(dGroup, notNullStr(objednavka.getDod_psc()) + ", "
				+ notNullStr(objednavka.getDod_mesto()));
		dodDic=createText(dGroup, "DI�: " + notNullStr(objednavka.getDod_dic()));
		dodIc=createText(dGroup, "I�: " + notNullStr(objednavka.getDod_ic()));

		Label sap = new Label(dGroup, SWT.NONE);
		sap.setLayoutData(getHSpanGridData(2));
		sap.setText("Dodavatelsk� ��slo: ");
		if (objednavka.getDopravce() != null) {
			sap.setText(sap.getText() + objednavka.getDopravce().getSap_cislo());
		}

		// detail group
		Group detailGroup = new Group(parent, SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.FILL,  true, false);
		gridData.horizontalSpan = 2;
		detailGroup.setLayoutData(gridData);
		groupLayout = new GridLayout(6, false);
		detailGroup.setLayout(groupLayout);
		
		getGrayLabel("��slo objedn�vky:", detailGroup);

		Label cislo = new Label(detailGroup, SWT.NONE);
		cislo.setLayoutData(getHSpanGridData(2));
		cislo.setText(formatCislo(
				String.valueOf(objednavka.getCislo_objednavky()), 6));

		
		getGrayLabel("Datum objedn�vky:", detailGroup);
		Label datum = new Label(detailGroup, SWT.NONE);
		datum.setLayoutData(getHSpanGridData(2));
		datum.setText(new SimpleDateFormat("dd.MM.yyyy").format(objednavka
				.getDatum() != null ? objednavka.getDatum() : new Date()));

		getGrayLabel("Term�n nakl�dky:", detailGroup);
		Label termin = new Label(detailGroup, SWT.NONE);
		termin.setLayoutData(getHSpanGridData(5));
		termin.setText(objednavka.getPozadavek().getDatum_nakladky());

		getGrayLabel("M�sto nakl�dky:", detailGroup);
		getGrayLabel("Firma:", detailGroup, 2);
		destZNazev=createText(detailGroup, notNullStr(objednavka.getPozadavek()
				.getDestinace_z().getNazev()), 3);

		getGrayLabel("", detailGroup);
		getGrayLabel("Adresa:", detailGroup, 2);
		destZUlice=createText(detailGroup, notNullStr(objednavka.getPozadavek()
				.getDestinace_z().getUlice()), 3);

		getGrayLabel(" ", detailGroup, 3);

		destZPsc=createText(detailGroup, notNullStr(objednavka.getPozadavek()
				.getDestinace_z().getPSC()));
		destZMesto=createText(detailGroup, notNullStr(objednavka.getPozadavek()
				.getDestinace_z().getMesto()), 2);

		getGrayLabel(" ", detailGroup);
		getGrayLabel("Kontaktn� osoba", detailGroup, 2);
		destZKontOs=createText(detailGroup, notNullStr(objednavka.getPozadavek()
				.getDestinace_z().getKontaktni_osoba()), 3);

		getGrayLabel(" ", detailGroup);
		getGrayLabel("Kontakt", detailGroup, 2);
		destZKontakt=createText(detailGroup, notNullStr(objednavka.getPozadavek()
				.getDestinace_z().getKontakt()), 3);

		getGrayLabel("Specifikace zbo��:", detailGroup);
		specZbozi=createText(detailGroup, "", 5);

		getGrayLabel("", detailGroup);
		getGrayLabel("Hmotnost", detailGroup);
		getWhiteLabel(objednavka.getPozadavek().getCelkova_hmotnost(),
				detailGroup, 2);
		getGrayLabel("ADR", detailGroup);
		adr=createText(detailGroup, notNullStr(objednavka.getAdr()));
		getGrayLabel("", detailGroup);
		getGrayLabel("Po�et palet:", detailGroup);
		getWhiteLabel(objednavka.getPozadavek().getPocet_palet(), detailGroup, 2);
		getGrayLabel("Stohovateln�?", detailGroup);
		getGrayLabel(objednavka.getPozadavek().getJe_stohovatelne() ? "ano"
				: "ne", detailGroup);

		getGrayLabel("Term�n vykl�dky:", detailGroup);
		getGrayLabel(notNullStr(objednavka.getPozadavek().getDatum_vykladky()),
				detailGroup, 5);

		getGrayLabel("M�sto vykl�dky:", detailGroup);
		getGrayLabel("Firma:", detailGroup, 2);
		destDoNazev=createText(detailGroup, notNullStr(objednavka.getPozadavek()
				.getDestinace_do().getNazev()), 3);

		getGrayLabel("", detailGroup);
		getGrayLabel("Adresa:", detailGroup, 2);
		destDoUlice=createText(detailGroup, notNullStr(objednavka.getPozadavek()
				.getDestinace_do().getUlice()), 3);

		getGrayLabel(" ", detailGroup, 3);

		destDoPsc=createText(detailGroup, notNullStr(objednavka.getPozadavek()
				.getDestinace_do().getPSC()));
		destDoMesto=createText(detailGroup, notNullStr(objednavka.getPozadavek()
				.getDestinace_do().getMesto()), 2);

		getGrayLabel(" ", detailGroup);
		getGrayLabel("Kontaktn� osoba", detailGroup, 2);
		destDoKontOs=createText(detailGroup, notNullStr(objednavka.getPozadavek()
				.getDestinace_do().getKontaktni_osoba()), 3);

		getGrayLabel(" ", detailGroup);
		getGrayLabel("Kontakt", detailGroup, 2);
		destDoKontakt=createText(detailGroup, notNullStr(objednavka.getPozadavek()
				.getDestinace_do().getKontakt()), 3);

		getGrayLabel("Cena za dopravu:", detailGroup);
		getWhiteLabel(notNullStr(objednavka.getCenaFormated()), detailGroup, 5);

		getGrayLabel("P�epravn� podm�nky:", detailGroup);
		prepravniPodminky=createText(detailGroup, "", 5);

		getGrayLabel("Pozn�mka:", detailGroup);
		getWhiteLabel(notNullStr(objednavka.getPozadavek().getPoznamka()),
				detailGroup, 5);
		poznamky = new Text(detailGroup, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 6;
		gridData.heightHint=90;
		poznamky.setLayoutData(gridData);
		
		final Button okButton = new Button(parent, SWT.PUSH);
		parent.getShell().setDefaultButton(okButton);
		gridData = new GridData(80, 25);
		okButton.setLayoutData(gridData);
		okButton.setText("Ulo�it");

		okButton.setLayoutData(gridData);
		okButton.setData(IDialogConstants.OK_ID);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean novaObjednavka = objednavka.getId() == null;
				final ArrayList<String> validace = validate();
				if (validace.size() == 0) {
					fillObjednavka();
					// destinace.setUlice(ulice.getText());
					Transaction tx = persistenceHelper.beginTransaction();
					try {
						if (novaObjednavka){
							Long maxCislo = objednavkaDAO.getMaxCisloObjednavky();
							if(maxCislo==null) maxCislo = (long) 0;
							objednavka.setCislo_objednavky(maxCislo+1);
							objednavka.setId(objednavkaDAO.create(objednavka));
						}
						else {
							persistenceHelper.getSession().flush();
							persistenceHelper.getSession().clear();
							objednavkaDAO.update(objednavka);
						}
						tx.commit();
						persistenceHelper.getSession().flush();
						// persistenceHelper.getSession().close();

						eventBroker.send(EventConstants.REFRESH_VIEWERS, "");

						close();
					} catch (Exception ex) {
						tx.rollback();
						setErrorMessage("P�i z�pisu do datab�ze do�lo k chyb�, kontaktujte pros�m tv�rce aplikace."
								.concat(System.getProperty("line.separator"))
								.concat(ex.getMessage()));
						logger.error("Nelze vlo�it/upravit destinaci", ex);
					}

				} else {
					String errString = "";
					for (String validaceMessage : validace)
						errString = errString.concat(validaceMessage).concat(
								System.getProperty("line.separator"));
					setErrorMessage(errString);
				}
			}
		});

		Button cancelButton = new Button(parent, SWT.PUSH);
		gridData = new GridData(80, 25);
		cancelButton.setLayoutData(gridData);
		cancelButton.setText("Zru�it");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				close();
			}
		});

		// editace existujiciho pozadavku
		// fillFields();

		 scrolledComposite.setContent (container);
		 scrolledComposite.setMinSize (container.computeSize (SWT.DEFAULT, SWT.DEFAULT));
		return area;

	}

	protected ArrayList<String> validate() {
		ArrayList<String> result = new ArrayList<String>();
		// do result muzu pridat chybove hlasky
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

	private Label getGrayLabel(String text, Composite parent, int span) {
		Label result = new Label(parent, SWT.NONE);
		result.setLayoutData(span > 1 ? getHSpanGridData(span) : getGridData());
		result.setText(text);
		result.setFont(nr12B);
		// result.setBackground( new Color(parent.getDisplay(), new RGB(0xc1,
		// 0xc1, 0xc1)));
		return result;
	}

	private Label getGrayLabel(String text, Composite parent) {
		return getGrayLabel(text, parent, 1);
	}

	private Label getWhiteLabel(String text, Composite parent) {
		return getWhiteLabel(text, parent, 1);
	}

	private Label getWhiteLabel(String text, Composite parent, int span) {
		Label result = new Label(parent, SWT.NONE);
		result.setLayoutData(span > 1 ? getHSpanGridData(span) : getGridData());
		result.setText(text);
		// result.setFont(nr12B);
		// result.setBackground( new Color(parent.getDisplay(), new RGB(0xc1,
		// 0xc1, 0xc1)));
		return result;
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
		final Text text = new Text(parent, SWT.BORDER | SWT.BEGINNING );
		text.setText(notNullStr(str));
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		//gd.widthHint = 200;
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
		fillPoznamky();
	}
	
	private void fillPoznamky() {
		if (poznamky.getText().length() > 0)
			objednavka.setPoznamka1(getSubstring(poznamky.getText(), 0, 255));
		else return;
		if (poznamky.getText().length() > 255)
			objednavka.setPoznamka2(getSubstring(poznamky.getText(), 255, 510));
		else return;
		if (poznamky.getText().length() > 510)
			objednavka.setPoznamka3(getSubstring(poznamky.getText(), 510, 765));
		else return;
		if (poznamky.getText().length() > 765)
			objednavka.setPoznamka4(getSubstring(poznamky.getText(), 765, 1010));
		else return;
		if (poznamky.getText().length() > 1010)
			objednavka.setPoznamka5(getSubstring(poznamky.getText(), 1010, 1265));
		else return;
		
	}
	
	private String getSubstring(String str, int from, int to) {
		if (str == null)
			return "";
		else
			return str.length() < to ? str.substring(from, str.length()) : str
					.substring(from, to);
	}

}
