package cz.kpartl.preprava.dialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hibernate.Transaction;
import org.hibernate.envers.synchronization.work.FakeBidirectionalRelationWorkUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kpartl.preprava.dao.DestinaceDAO;
import cz.kpartl.preprava.dao.DopravceDAO;
import cz.kpartl.preprava.dao.ObjednavkaDAO;
import cz.kpartl.preprava.dao.PozadavekDAO;
import cz.kpartl.preprava.model.Destinace;
import cz.kpartl.preprava.model.Dopravce;
import cz.kpartl.preprava.model.Objednavka;
import cz.kpartl.preprava.model.Pozadavek;
import cz.kpartl.preprava.model.User;
import cz.kpartl.preprava.util.EventConstants;
import cz.kpartl.preprava.util.OtherUtils;
import cz.kpartl.preprava.view.AbstractTableView;
import cz.kpartl.preprava.view.ObjednanoView;
import cz.kpartl.preprava.view.UkoncenoView;

public class NovaObjednavkaDialog extends NovyPozadavekDialog {

	final Logger logger = LoggerFactory.getLogger(NovaObjednavkaDialog.class);

	ObjednavkaDAO objednavkaDAO;
	DopravceDAO dopravceDAO;
	Objednavka objednavka;

	Group oGroup;
	Text cena;
	Text mena;
	Combo dopravceCombo;
	Combo fazeCombo;
	Text zmenaTerminuNakladky;
	Text cisloFakturyDopravce;

	private HashMap<Integer, Dopravce> dopravceMap = null;
	String[] dopravceItems = null;

	@Inject
	public NovaObjednavkaDialog(
			@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell,
			IEclipseContext context, Pozadavek pozadavek, IEventBroker eventBroker) {
		super(parentShell, context, eventBroker);
		objednavka = new Objednavka();
		objednavka.setPozadavek(pozadavek);
		this.pozadavek = pozadavek;	
		afterConstruct(parentShell, context, objednavka);

	}

	@Inject
	public NovaObjednavkaDialog(
			@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell,
			IEclipseContext context, Objednavka objednavka, IEventBroker eventBroker) {
		super(parentShell, context, eventBroker);
		afterConstruct(parentShell, context, objednavka);

	}

	protected void afterConstruct(Shell parentShell, IEclipseContext context,
			Objednavka objednavka) {
		this.objednavkaDAO = context.get(ObjednavkaDAO.class);
		this.dopravceDAO = context.get(DopravceDAO.class);

		this.objednavka = objednavka;
		if (objednavka != null)
			this.pozadavek = objednavka.getPozadavek();
		setTitle("Objednávka pøepravy");
	}

	@Override
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		setTitle("Objednávka pøepravy");
		setMessage("Zadejte data objednávky pøepravy",
				IMessageProvider.INFORMATION);

		return contents;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout(3, false);
		layout.marginBottom = 5;
		layout.marginLeft = 5;
		layout.marginTop = 5;
		layout.marginRight = 5;
		parent.setLayout(layout);

		createWidgets(parent);

		return parent;
	}

	@Override
	protected Composite createWidgets(Composite parent) {
		GridLayout layout = new GridLayout(2, false);

		oGroup = new Group(parent, SWT.NONE);

		oGroup.setText("Objednávka");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.horizontalSpan = 3;
		oGroup.setLayoutData(gridData);
		oGroup.setLayout(layout);

		Label fazeObjednavkyLabel = new Label(oGroup, SWT.NONE);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		fazeObjednavkyLabel.setLayoutData(gridData);
		fazeObjednavkyLabel.setText("Fáze objednávky");

		fazeCombo = new Combo(oGroup, SWT.READ_ONLY);
		fazeCombo.setBounds(new Rectangle(50, 50, 200, 65));
		fazeCombo.setItems(ObjednanoView.getComboItems(true));

		Label dopravceLabel = new Label(oGroup, SWT.NONE);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		dopravceLabel.setLayoutData(gridData);
		dopravceLabel.setText("Dopravce");

		dopravceCombo = new Combo(oGroup, SWT.READ_ONLY);
		dopravceCombo.setBounds(new Rectangle(50, 50, 200, 65));
		
		dopravceItems = getDopravceItems();
		dopravceCombo.setItems(dopravceItems);

		Label cenaLabel = new Label(oGroup, SWT.NONE);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);		
		cenaLabel.setLayoutData(gridData);
		cenaLabel.setText("Cena dopravy");

		cena = new Text(oGroup, SWT.BORDER);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		gridData.widthHint = 70;
		cena.setLayoutData(gridData);

		Label menaLabel = new Label(oGroup, SWT.NONE);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		menaLabel.setLayoutData(gridData);
		menaLabel.setText("Mìna");

		mena = new Text(oGroup, SWT.BORDER);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		gridData.widthHint = 20;
		mena.setLayoutData(gridData);

		Label zmenaLabel = new Label(oGroup, SWT.NONE);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		zmenaLabel.setLayoutData(gridData);
		zmenaLabel.setText("Zmìna termínu nakládky");

		zmenaTerminuNakladky = new Text(oGroup, SWT.BORDER);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		gridData.widthHint = 300;
		zmenaTerminuNakladky.setLayoutData(gridData);

		Label cisloFakturyLabel = new Label(oGroup, SWT.NONE);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		cisloFakturyLabel.setLayoutData(gridData);
		cisloFakturyLabel.setText("Èíslo faktury dopravce");

		cisloFakturyDopravce = new Text(oGroup, SWT.BORDER);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		gridData.widthHint = 70;
		cisloFakturyDopravce.setLayoutData(gridData);

		Group pozadavekGroup = new Group(parent, SWT.NONE);

		pozadavekGroup.setText("Požadavek");
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.horizontalSpan = 3;
		pozadavekGroup.setLayoutData(gridData);
		pozadavekGroup.setLayout(new GridLayout(3, false));

		super.createWidgets(pozadavekGroup);

		createButtons(parent);

		fillFields();

		return parent;
	}

	protected String[] getDopravceItems() {
		List<String> result = new ArrayList<String>();
		if (dopravceDAO != null) {
			List<Dopravce> dopravce = dopravceDAO.findAll();
			if (dopravceMap == null)
				dopravceMap = new HashMap<Integer, Dopravce>(dopravce.size());
			int index = 0;
			for (Dopravce dop : dopravce) {
				result.add(dop.getNazev());
				dopravceMap.put(Integer.valueOf(index++), dop);
			}
		}

		return result.toArray(new String[result.size()]);
	}

	protected int getDopravceIndex(String dopravce) {
		for (int i = 0; i < dopravceItems.length; i++) {
			if (dopravceItems[i].equals(dopravce))
				return i;
		}
		;

		return -1;
	}

	@Override
	protected void createButtons(Composite parent) {
		Button okButton = new Button(parent, SWT.PUSH);
		GridData gridData = new GridData(80, 25);
		// data.horizontalAlignment = data.HORIZONTAL_ALIGN_FILL |
		// data.GRAB_HORIZONTAL;

		okButton.setLayoutData(gridData);
		okButton.setText("OK");

		okButton.setLayoutData(gridData);
		okButton.setData(IDialogConstants.OK_ID);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Transaction tx = persistenceHelper.beginTransaction();
				try {
					if (updatePozadavek(tx) && updateObjednavku(tx)) {
						tx.commit();
						close();
					} else {
						tx.rollback();
					}

				} catch (Exception ex) {
					setErrorMessage("Pøi zápisu do databáze došlo k chybì, kontaktujte prosím tvùrce aplikace."
							.concat(System.getProperty("line.separator"))
							.concat(ex.toString()));
					logger.error("Nelze vlozit/updatovat destinaci", ex);
				}
			}
		});

		Button cancelButton = new Button(parent, SWT.PUSH);
		gridData = new GridData(80, 25);
		// data.horizontalAlignment =SWT.RIGHT;
		cancelButton.setLayoutData(gridData);
		cancelButton.setText("Zrušit");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				close();
			}
		});

	}

	// zapise objednavku do databaze
	protected boolean updateObjednavku(Transaction tx) {
		final ArrayList<String> validace = validate();
		if (validace.size() == 0) {
			boolean novaObjednavka = objednavka.getId() == null;
			if ("" != cena.getText())
				objednavka.setCena(BigDecimal.valueOf(Double.valueOf(cena
						.getText().replace(',', '.'))));
			if ("" != cisloFakturyDopravce.getText())
				objednavka.setCislo_faktury_dopravce(Integer
						.valueOf(cisloFakturyDopravce.getText()));
			objednavka.setMena(mena.getText());
			objednavka.setZmena_nakladky(zmenaTerminuNakladky.getText());
			objednavka.setFaze(fazeCombo.getSelectionIndex());
			objednavka.setDopravce(dopravceMap.get(dopravceCombo
					.getSelectionIndex()));

			if (novaObjednavka)
				objednavka.setId(objednavkaDAO.create(objednavka));
			else
				objednavkaDAO.update(objednavka);
			
			eventBroker.send(EventConstants.REFRESH_VIEWERS, objednavka);
			
			/*UkoncenoView ukoncenoView = (UkoncenoView) context.get(UkoncenoView.class);
			ObjednanoView objednanoView = (ObjednanoView)context.get(ObjednanoView.ID);
			if(ukoncenoView != null){
				ukoncenoView.refreshInputData();
			}
			
			if(objednanoView != null){
				objednanoView.refreshInputData();
			}*/

			return true;
		} else {
			String errString = "";
			for (String validaceMessage : validace)
				errString = errString.concat(validaceMessage).concat(
						System.getProperty("line.separator"));
			setErrorMessage(errString);

			return false;
		}

	}

	protected ArrayList<String> validate() {
		final ArrayList<String> result = new ArrayList<String>();
		final String cenaText = cena.getText().replace(',', '.');
		if ("" != cenaText) {
			try {
				BigDecimal.valueOf(Double.valueOf(cenaText));

				if ("".equals(mena.getText()))
					result.add("Není zadána mìna");

			} catch (NumberFormatException ex) {
				result.add("Špatnì zadaná cena dopravy. Musí být ve tvaru 12345,67");
			}
		}
		if ("" != cisloFakturyDopravce.getText()) {
			try {
				Integer.valueOf(cisloFakturyDopravce.getText());
			} catch (NumberFormatException ex) {
				result.add("Špatnì zadané èíslo faktury dopravce. Musí být celé èíslo");
			}
		}

		return result;
	}

	protected void fillFields() {
		super.fillFields();
		if (objednavka.getId() == null) {
			fazeCombo.select(Objednavka.FAZE_OBJEDNANO);
			return;
		}
if(objednavka.getCena() != null){
		String cenaText = String.valueOf(objednavka.getCena());
		cena.setText(cenaText);
}

		mena.setText(objednavka.getMena());
		fazeCombo.select(objednavka.getFaze());
		if(objednavka.getDopravce() != null)
		dopravceCombo.select(getDopravceIndex(objednavka.getDopravce()
				.getNazev()));
		if(objednavka.getCislo_faktury_dopravce() != null)
		cisloFakturyDopravce.setText(String.valueOf(objednavka
				.getCislo_faktury_dopravce()));
		zmenaTerminuNakladky.setText(objednavka.getZmena_nakladky());
	}
}
