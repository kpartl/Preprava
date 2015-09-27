package cz.kpartl.preprava.dialog;

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
	
	private Objednavka objednavka;
	private DestinaceDAO destinaceDAO;
	HibernateHelper persistenceHelper;
	Text nazev;
	Text cislo;
	Text kontakt;
	Text kontaktniOsoba;
	Text ulice;
	Text psc;
	Text mesto;
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
		setTitle("OBJEDN�VKA P�EPRAVY");
		setTitleAreaColor(new RGB(0xc1, 0xc1, 0xc1));
		
		return contents;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout(4, false);
		layout.marginBottom = 5;
		layout.marginLeft = 0;
		layout.marginTop = 5;
		layout.marginRight = 0;

		parent.setLayout(layout);
		final Font nr12B = new org.eclipse.swt.graphics.Font(null, "Times New Roman", 12, SWT.BOLD);
		final Color gray = new Color(parent.getDisplay(), new RGB(0xc1, 0xc1, 0xc1));
		
		GridLayout groupLayout = new GridLayout(2, false);
		Group oGroup = new Group(parent, SWT.NONE);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.horizontalSpan = 2;
		oGroup.setLayoutData(gridData);
		oGroup.setLayout(groupLayout);
		
		groupLayout = new GridLayout(2, false);
		Group dGroup = new Group(parent, SWT.NONE);

		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.horizontalSpan = 2;
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
		dodavatelLabel.setText("Dodavatel (p�epravce):");

		final Label kernLabel = new Label(oGroup, SWT.NONE);
		kernLabel.setText("KERN-LIEBERS CR s.r.o.");
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		kernLabel.setLayoutData(getHSpanGridData(2));
		
		final Label kernUliceLabel = new Label(oGroup, SWT.NONE);
		kernUliceLabel.setText("Okru�n� 607");
		kernUliceLabel.setLayoutData(getHSpanGridData(2));
		
		final Label kernMestoLabel = new Label(oGroup, SWT.NONE);
		kernMestoLabel.setText("370 01  �esk� Bud�jovice DI�: CZ60849827");
		kernMestoLabel.setLayoutData(getHSpanGridData(2));
		
		final Label kernDicLabel = new Label(oGroup, SWT.NONE);
		kernDicLabel.setText(" DI�: CZ60849827");
		kernDicLabel.setLayoutData(getHSpanGridData(2));
		

		Text nazev = new Text(dGroup, SWT.BORDER);
		//gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		//gridData.widthHint = 200;
		nazev.setLayoutData(getHSpanGridData(2));
		nazev.setEnabled(false);
		if (objednavka.getDopravce() != null) {
			nazev.setText(objednavka.getDopravce().getNazev());
		}

		Label cislolabel = new Label(parent, SWT.NONE);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		cislolabel.setLayoutData(gridData);
		cislolabel.setText("��slo");

		cislo = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		gridData.widthHint = 70;
		cislo.setLayoutData(gridData);

		Label ulicelabel = new Label(parent, SWT.NONE);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		ulicelabel.setLayoutData(gridData);
		ulicelabel.setText("Ulice");

		ulice = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		gridData.widthHint = 200;
		ulice.setLayoutData(gridData);

		Label mestoabel = new Label(parent, SWT.NONE);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		mestoabel.setLayoutData(gridData);
		mestoabel.setText("M�sto");

		mesto = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		gridData.widthHint = 200;
		mesto.setLayoutData(gridData);

		Label psclabel = new Label(parent, SWT.NONE);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		psclabel.setLayoutData(gridData);
		psclabel.setText("PS�");

		psc = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		gridData.widthHint = 70;
		psc.setLayoutData(gridData);

		Label kontaktniOsobalabel = new Label(parent, SWT.NONE);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		kontaktniOsobalabel.setLayoutData(gridData);
		kontaktniOsobalabel.setText("Kontaktn� osoba");

		kontaktniOsoba = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		gridData.widthHint = 200;
		kontaktniOsoba.setLayoutData(gridData);

		Label kontaktlabel = new Label(parent, SWT.NONE);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		kontaktlabel.setLayoutData(gridData);
		kontaktlabel.setText("Kontakt");

		kontakt = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		gridData.widthHint = 200;
		kontakt.setLayoutData(gridData);

		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);

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
					destinace.setCislo(Integer.valueOf(cislo.getText()));
					destinace.setKontakt(kontakt.getText());
					destinace.setKontaktni_osoba(kontaktniOsoba.getText());
					destinace.setMesto(mesto.getText());
					//destinace.setNazev(nazev.getText());
					if (psc.getText() != "")
						destinace.setPSC(psc.getText());
					destinace.setUlice(ulice.getText());
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
		//fillFields();

		return parent;

	}
	
	protected ArrayList<String> validate(boolean novaDestinace) {
		ArrayList<String> result = new ArrayList<String>();
		if ("".equals(nazev.getText().trim()))
			result.add("Nen� zad�n n�zev destinace");
		if ("".equals(cislo.getText().trim())) {
			result.add("Nen� zadan� ��slo destinace");
		} else {
			try {
				final Integer l_cislo = Integer.valueOf(cislo.getText());
			} catch (NumberFormatException e) {
				result.add("��slo destinace ".concat(cislo.getText()).concat(
						" nen� platn� ��slo"));
			}
			final Destinace existujici = destinaceDAO.findByCislo(Integer
					.valueOf(cislo.getText()));
			if (existujici != null) {
				if (novaDestinace
						|| !existujici.getId().equals(destinace.getId())) {
					result.add("Destinace s ��slem ".concat(cislo.getText())
							.concat(" ji� existuje"));
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

}
