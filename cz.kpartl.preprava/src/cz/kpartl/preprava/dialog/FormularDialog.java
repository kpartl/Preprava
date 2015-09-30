package cz.kpartl.preprava.dialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
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
import cz.kpartl.preprava.model.Destinace;
import cz.kpartl.preprava.model.Objednavka;
import cz.kpartl.preprava.util.EventConstants;
import cz.kpartl.preprava.util.HibernateHelper;

public class FormularDialog extends TitleAreaDialog {
	
	final Font nr12B = new org.eclipse.swt.graphics.Font(null, "Times New Roman", 12, SWT.BOLD);
	
	private Objednavka objednavka;
	private DestinaceDAO destinaceDAO;
	HibernateHelper persistenceHelper;
	Text nazev;
	Text cislo;
	Text kontakt;
	Text kontaktniOsoba;
	Label ulice;
	Text psc;
	Label mesto;
	Destinace destinace;
	final Logger logger = LoggerFactory.getLogger(NovaDestinaceDialog.class);

	IEventBroker eventBroker;

	public FormularDialog(
			@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell,
		IEclipseContext context, IEventBroker eventBroker, Objednavka objednavka) {
	this(parentShell, context, null, eventBroker, objednavka);
	}

	@Inject
	public FormularDialog(
			@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell,
			IEclipseContext context,
			Object object, IEventBroker eventBroker,
			Objednavka objednavka) {
		super(parentShell);
		this.objednavka = objednavka;
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
	protected Control createDialogArea(Composite parent) {
		final Color gray = new Color(parent.getDisplay(), new RGB(0xc1, 0xc1, 0xc1));
		GridLayout layout = new GridLayout(2, false);
		layout.marginBottom = 5;
		layout.marginLeft = 0;
		layout.marginTop = 5;
		layout.marginRight = 0;

		parent.setLayout(layout);

		
		GridLayout groupLayout = new GridLayout(1, false);
		Group oGroup = new Group(parent, SWT.NONE);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		//gridData.horizontalSpan = 2;
		oGroup.setLayoutData(gridData);
		oGroup.setLayout(groupLayout);
		
		groupLayout = new GridLayout(1, false);
		Group dGroup = new Group(parent, SWT.NONE);

		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		//gridData.horizontalSpan = 2;
		dGroup.setLayoutData(gridData);
		dGroup.setLayout(groupLayout);

		final Label objednavatelLabel = new Label(oGroup, SWT.BORDER_SOLID);
		objednavatelLabel.setLayoutData(getHSpanGridData(2));
		objednavatelLabel.setFont(nr12B);
		objednavatelLabel.setBackground(gray);
		objednavatelLabel.setText("Objednavatel");
		
		final Label dodavatelLabel = new Label(dGroup, SWT.NONE);
		dodavatelLabel.setLayoutData(getHSpanGridData(2));
		dodavatelLabel.setFont(nr12B);
		dodavatelLabel.setBackground(gray);
		dodavatelLabel.setText("Dodavatel (pøepravce):");

		final Label kernLabel = new Label(oGroup, SWT.NONE);
		kernLabel.setText("KERN-LIEBERS CR s.r.o.");
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		kernLabel.setLayoutData(getHSpanGridData(2));
		
		final Label kernUliceLabel = new Label(oGroup, SWT.NONE);
		kernUliceLabel.setText("Okružní 607");
		kernUliceLabel.setLayoutData(getHSpanGridData(2));
		
		final Label kernMestoLabel = new Label(oGroup, SWT.NONE);
		kernMestoLabel.setText("370 01  Èeské Budìjovice");
		kernMestoLabel.setLayoutData(getHSpanGridData(2));
		
		final Label kernDicLabel = new Label(oGroup, SWT.NONE);
		kernDicLabel.setText("DIÈ: CZ60849827");
		kernDicLabel.setLayoutData(getHSpanGridData(2));
		

		Label nazev = new Label(dGroup, SWT.NONE);
		//gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		//gridData.widthHint = 200;
		nazev.setLayoutData(getHSpanGridData(2));
		if (objednavka.getDopravce() != null) {
			nazev.setText(objednavka.getDopravce().getNazev());
		}
		
		Label ulice = new Label(dGroup, SWT.NONE);
		ulice.setLayoutData(getHSpanGridData(2));
		if (objednavka.getDopravce() != null) {
			ulice.setText(objednavka.getDopravce().getUlice());
		}
		
		Label mesto = new Label(dGroup, SWT.NONE);
		mesto.setLayoutData(getHSpanGridData(2));
		if (objednavka.getDopravce() != null) {
			mesto.setText(objednavka.getDopravce().getPsc() + " " + objednavka.getDopravce().getMesto());
		}
		
		Label dic = new Label(dGroup, SWT.NONE);
		dic.setLayoutData(getHSpanGridData(2));
		dic.setText("DIÈ: ");
		if (objednavka.getDopravce() != null) {
			dic.setText(dic.getText() + objednavka.getDopravce().getDic());
		} 
		
		Label sap = new Label(dGroup, SWT.NONE);
		sap.setLayoutData(getHSpanGridData(2));
		sap.setText("Dodavatelské èíslo: ");
		if (objednavka.getDopravce() != null) {
			sap.setText(sap.getText() + objednavka.getDopravce().getSap_cislo());
		} 

		Group detailGroup = new Group(parent, SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.horizontalSpan = 2;
		detailGroup.setLayoutData(gridData);
		
		groupLayout = new GridLayout(6, false);
		detailGroup.setLayout(groupLayout);
		getGrayLabel("Èíslo objednávky:", detailGroup);

		Label cislo = new Label(detailGroup, SWT.NONE);
		cislo.setLayoutData(getHSpanGridData(5));
		cislo.setText(formatCislo(String.valueOf(objednavka.getCislo_objednavky()), 6));
		
		getGrayLabel("Datum objednávky:", detailGroup);
		Label datum = new Label(detailGroup, SWT.NONE);
		datum.setLayoutData(getHSpanGridData(3));
		datum.setText(new SimpleDateFormat("dd.MM.yyyy").format(objednavka.getPozadavek().getDatum()));

		getGrayLabel("Kontakt:", detailGroup);
		Label fucikova = new Label(detailGroup, SWT.RIGHT);
		fucikova.setLayoutData(getGridData());
		fucikova.setText("Alena Fuèíková");
		
		getGrayLabel("Termín nakládky:", detailGroup);
		Label termin = new Label(detailGroup, SWT.NONE);
		termin.setLayoutData(getHSpanGridData(3));
		termin.setText(objednavka.getPozadavek().getDatum_nakladky());
		
		Label telefon = new Label(detailGroup, SWT.NONE);
		telefon.setLayoutData(getHSpanGridData(2));
		telefon.setText("Tel.: 389 608 124 / 734 310 217");
		
		getGrayLabel("Místo nakládky:", detailGroup);
		getGrayLabel("Firma:", detailGroup, 2);
		Label nazevFirmy = new Label(detailGroup, SWT.NONE);
		nazevFirmy.setLayoutData(getHSpanGridData(3));
		nazevFirmy.setText(objednavka.getPozadavek().getDestinace_z().getNazev());
		
		getGrayLabel("", detailGroup);
		getGrayLabel("Adresa:", detailGroup, 2);
		Label adresa = new Label(detailGroup, SWT.NONE);
		adresa.setLayoutData(getHSpanGridData(3));
		adresa.setText(objednavka.getPozadavek().getDestinace_z().getUlice());
		
		
		getGrayLabel("", detailGroup);
		getGrayLabel("", detailGroup, 2);
		Label mesto_a_psc = new Label(detailGroup, SWT.NONE);
		mesto_a_psc.setLayoutData(getHSpanGridData(3));
		String PSC = objednavka.getPozadavek().getDestinace_z().getPSC() != null ?
				", " + objednavka.getPozadavek().getDestinace_z().getPSC() : " ";
		mesto_a_psc.setText(objednavka.getPozadavek().getDestinace_z().getMesto() + PSC);
		
		getGrayLabel("", detailGroup);
		getGrayLabel("Kontaktní osoba", detailGroup, 2);
		Label kontOs = new Label(detailGroup, SWT.NONE);
		kontOs.setLayoutData(getHSpanGridData(3));
		kontOs.setText(objednavka.getPozadavek().getDestinace_z().getKontaktni_osoba());
		
		getGrayLabel("", detailGroup);
		getGrayLabel("Kontakt", detailGroup, 2);
		Label kont = new Label(detailGroup, SWT.NONE);
		kont.setLayoutData(getHSpanGridData(3));
		kont.setText(objednavka.getPozadavek().getDestinace_z().getKontakt());
		
		getGrayLabel("Specifikace zboží:", detailGroup);
		Text specZbozi = new Text(detailGroup, SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gridData.horizontalSpan = 5;
		specZbozi.setLayoutData(gridData);
		
		getGrayLabel("", detailGroup);
		getGrayLabel("Hmotnost", detailGroup, 1);
		Label hmotn = new Label(detailGroup, SWT.NONE);
		hmotn.setLayoutData(getHSpanGridData(2));
		hmotn.setText(objednavka.getPozadavek().getCelkova_hmotnost());
		getGrayLabel("ADR", detailGroup, 1);
		Text adrTxt = new Text(detailGroup, SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		adrTxt.setLayoutData(gridData);
		
		

		final Button okButton = new Button(parent, SWT.PUSH);
		parent.getShell().setDefaultButton(okButton);
		gridData = new GridData(80, 25);
		okButton.setLayoutData(gridData);
		okButton.setText("OK");

		okButton.setLayoutData(gridData);
		okButton.setData(IDialogConstants.OK_ID);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean novaDestinace = destinace == null;
				final ArrayList<String> validace = validate(novaDestinace);
				if (validace.size() == 0) {

					if (novaDestinace) {
						destinace = new Destinace();
					}
					//destinace.setCislo(Integer.valueOf(cislo.getText()));
					destinace.setKontakt(kontakt.getText());
					destinace.setKontaktni_osoba(kontaktniOsoba.getText());
				//	destinace.setMesto(mesto.getText());
					//destinace.setNazev(nazev.getText());
					if (psc.getText() != "")
						destinace.setPSC(psc.getText());
				//	destinace.setUlice(ulice.getText());
					Transaction tx = persistenceHelper.beginTransaction();
					try {
						if (novaDestinace)
							destinace.setId(destinaceDAO.create(destinace));
						else {
							persistenceHelper.getSession().flush();
							persistenceHelper.getSession().clear();
							destinaceDAO.update(destinace);
						}
						tx.commit();
						persistenceHelper.getSession().flush();
						// persistenceHelper.getSession().close();

						eventBroker.send(EventConstants.REFRESH_VIEWERS, "");

						close();
					} catch (Exception ex) {
						tx.rollback();
						setErrorMessage("Pøi zápisu do databáze došlo k chybì, kontaktujte prosím tvùrce aplikace."
								.concat(System.getProperty("line.separator"))
								.concat(ex.getMessage()));
						logger.error("Nelze vložit/upravit destinaci", ex);
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
		cancelButton.setText("Zrušit");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				close();
			}
		});

		// editace existujiciho pozadavku
		//fillFields();

		return parent;

	}
	
	protected ArrayList<String> validate(boolean novaDestinace) {
		ArrayList<String> result = new ArrayList<String>();
		if ("".equals(nazev.getText().trim()))
			result.add("Není zadán název destinace");
		if ("".equals(cislo.getText().trim())) {
			result.add("Není zadané èíslo destinace");
		} else {
			try {
				final Integer l_cislo = Integer.valueOf(cislo.getText());
			} catch (NumberFormatException e) {
				result.add("Èíslo destinace ".concat(cislo.getText()).concat(
						" není platné èíslo"));
			}
			final Destinace existujici = destinaceDAO.findByCislo(Integer
					.valueOf(cislo.getText()));
			if (existujici != null) {
				if (novaDestinace
						|| !existujici.getId().equals(destinace.getId())) {
					result.add("Destinace s èíslem ".concat(cislo.getText())
							.concat(" již existuje"));
				}
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
	
	private Label getGrayLabel(String text, Composite parent, int span) {		
		Label result = new Label(parent, SWT.NONE);
		result.setLayoutData(span > 1 ? getHSpanGridData(span) : getGridData());
		result.setText(text);
		result.setFont(nr12B);
		result.setBackground( new Color(parent.getDisplay(), new RGB(0xc1, 0xc1, 0xc1)));
		return result;
	}
	
	private Label getGrayLabel(String text, Composite parent) {
		return getGrayLabel(text, parent, 1);
	}
	
	private Label getWhiteLabel(String text, Composite parent) {
		GridData gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		gd.widthHint = 150;
		Label result = new Label(parent, SWT.NONE);
		result.setLayoutData(gd);
		result.setText(text);
		return result;
	}
	
	private String formatCislo(String cislo, int pozic) {
		String result = cislo;
		while (result.length() < pozic)
			result = "0" + result;
		return result;
	}

}
