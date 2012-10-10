package cz.kpartl.preprava.dialog;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hibernate.Transaction;

import cz.kpartl.preprava.dao.DestinaceDAO;
import cz.kpartl.preprava.model.Destinace;
import cz.kpartl.preprava.model.Pozadavek;
import cz.kpartl.preprava.util.HibernateHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NovaDestinaceDialog extends TitleAreaDialog {

	private DestinaceDAO destinaceDAO;
	HibernateHelper persistenceHelper;
	final Logger logger = LoggerFactory.getLogger(NovaDestinaceDialog.class); 

	Text nazev;
	Text cislo;
	Text kontakt;
	Text kontaktniOsoba;
	Text ulice;
	Text psc;
	Text mesto;
	Destinace destinace;

	@Inject
	public NovaDestinaceDialog(
			@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell,
			IEclipseContext context) {
		super(parentShell);
		this.destinaceDAO = context.get(DestinaceDAO.class);
		persistenceHelper = cz.kpartl.preprava.util.HibernateHelper
				.getInstance();

	}

	@Override
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		setTitle("Vytvoøení / editace destinace");
		setMessage("Zadejte data destinace", IMessageProvider.INFORMATION);
		return contents;
	}

	protected void setShellStyle(int newShellStyle) {
		// TODO Auto-generated method stub
		super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.MAX);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout(2, false);
		layout.marginBottom = 5;
		layout.marginLeft = 5;
		layout.marginTop = 5;
		layout.marginRight = 5;

		parent.setLayout(layout);

		Label nazevlabel = new Label(parent, SWT.NONE);
		GridData gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false);
		nazevlabel.setLayoutData(gridData);
		nazevlabel.setText("Název");

		nazev = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		gridData.widthHint = 200;
		nazev.setLayoutData(gridData);

		Label cislolabel = new Label(parent, SWT.NONE);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		cislolabel.setLayoutData(gridData);
		cislolabel.setText("Èíslo");

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
		mestoabel.setText("Mìsto");

		mesto = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		gridData.widthHint = 200;
		mesto.setLayoutData(gridData);

		Label psclabel = new Label(parent, SWT.NONE);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		psclabel.setLayoutData(gridData);
		psclabel.setText("PSÈ");

		psc = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		gridData.widthHint = 70;
		psc.setLayoutData(gridData);

		Label kontaktniOsobalabel = new Label(parent, SWT.NONE);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		kontaktniOsobalabel.setLayoutData(gridData);
		kontaktniOsobalabel.setText("Kontaktní osoba");

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
		gridData = new GridData(80, 25);
		okButton.setLayoutData(gridData);
		okButton.setText("OK");

		okButton.setLayoutData(gridData);
		okButton.setData(IDialogConstants.OK_ID);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				final ArrayList<String> validace = validate();
				if (validace.size() == 0) {
					boolean novaDestinace = destinace == null;
					if (novaDestinace) {
						destinace = new Destinace();
					}
					destinace.setCislo(Integer.valueOf(cislo.getText()));
					destinace.setKontakt(kontakt.getText());
					destinace.setKontaktni_osoba(kontaktniOsoba.getText());
					destinace.setMesto(mesto.getText());
					destinace.setNazev(nazev.getText());
					destinace.setPSC(Integer.valueOf(psc.getText()));
					destinace.setUlice(ulice.getText());
					Transaction tx = persistenceHelper.beginTransaction();
					try {
					if (novaDestinace)
						destinace.setId(destinaceDAO.create(destinace));
					else
						destinaceDAO.update(destinace);
					tx.commit();					

					close();
					} catch (Exception ex){
						setErrorMessage("Pøi zápisu do databáze došlo k chybì, kontaktujte prosím tvùrce aplikace."
								.concat(System.getProperty("line.separator")).concat(ex.getMessage()));
						logger.error("Nelze vlozit/updatovat destinaci", e);
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
		fillFields();

		return parent;

	}

	protected ArrayList<String> validate() {
		ArrayList<String> result = new ArrayList<String>();
		if ("".equals(cislo.getText().trim())) {
			result.add("Není zadané èíslo destinace");
		} else {
			try {
				final Integer l_cislo = Integer.valueOf(cislo.getText());
			} catch (NumberFormatException e) {
				result.add("Èíslo destinace ".concat(cislo.getText()).concat(
						" není platné èíslo"));
			}
			if (destinaceDAO.findByCislo(Long.valueOf(cislo.getText())) != null) {
				result.add("Destinace s èíslem ".concat(cislo.getText())
						.concat(" již existuje"));
			}
		}

		if (!("".equals(psc.getText().trim()))) {

			try {
				final Integer l_psc = Integer.valueOf(psc.getText());
			} catch (NumberFormatException e) {
				result.add("Hodnota v poli PSÈ není platné èíslo");
			}
		}
		return result;
	}

	protected void fillFields() {
		if (destinace == null)
			return;

		nazev.setText(destinace.getNazev());
		cislo.setText(String.valueOf(destinace.getCislo()));
		mesto.setText(destinace.getMesto());
		ulice.setText(destinace.getUlice());
		psc.setText(String.valueOf(destinace.getPSC()));
		kontakt.setText(destinace.getKontakt());
		kontaktniOsoba.setText(destinace.getKontaktni_osoba());

	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
	}

}
