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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kpartl.preprava.dao.DopravceDAO;

import cz.kpartl.preprava.model.Dopravce;
import cz.kpartl.preprava.util.EventConstants;
import cz.kpartl.preprava.util.HibernateHelper;

import cz.kpartl.preprava.view.AbstractTableView;

public class NovyDopravceDialog extends TitleAreaDialog {

	DopravceDAO dopravceDAO;
	HibernateHelper persistenceHelper;
	final Logger logger = LoggerFactory.getLogger(NovyDopravceDialog.class);
	Dopravce dopravce;

	IEventBroker eventBroker;

	Text nazev;

	@Inject
	public NovyDopravceDialog(
			@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell,
			IEclipseContext context, IEventBroker eventBroker) {
		this(parentShell, context, null, eventBroker);
	}

	@Inject
	public NovyDopravceDialog(
			@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell,
			IEclipseContext context, Dopravce dopravce, IEventBroker eventBroker) {
		super(parentShell);
		this.dopravce = dopravce;
		this.dopravceDAO = context.get(DopravceDAO.class);
		this.eventBroker = eventBroker;
		persistenceHelper = cz.kpartl.preprava.util.HibernateHelper
				.getInstance();

	}

	@Override
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		setTitle("Vytvoøení / editace doprace");
		setMessage("Zadejte data dopravce", IMessageProvider.INFORMATION);
		return contents;
	}

	protected void setShellStyle(int newShellStyle) {
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
				boolean novyDopravce = dopravce == null;
				final ArrayList<String> validace = validate(novyDopravce);
				if (validace.size() == 0) {

					if (novyDopravce) {
						dopravce = new Dopravce();
					}

					dopravce.setNazev(nazev.getText());
					Transaction tx = persistenceHelper.beginTransaction();
					try {
						if (novyDopravce)
							dopravce.setId(dopravceDAO.create(dopravce));
						else
							dopravceDAO.update(dopravce);
						tx.commit();
						persistenceHelper.getSession().flush();persistenceHelper.getSession().flush();
						//persistenceHelper.getSession().close();
						eventBroker.send(EventConstants.REFRESH_VIEWERS, "");

						close();
					} catch (Exception ex) {
						setErrorMessage("Pøi zápisu do databáze došlo k chybì, kontaktujte prosím tvùrce aplikace."
								.concat(System.getProperty("line.separator"))
								.concat(ex.getMessage()));
						logger.error("Nelze vložit/upravit dopravce", e);
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

		// editace existujiciho dopravce
		fillFields();

		return parent;

	}

	protected ArrayList<String> validate(boolean novyDopravce) {
		ArrayList<String> result = new ArrayList<String>();
		if ("".equals(nazev.getText().trim()))
			result.add("Není zadán název dopravce");
		final Dopravce existujici = dopravceDAO.findByNazev(nazev.getText());
		if (existujici != null) {
			if (novyDopravce || !existujici.getId().equals(dopravce.getId())) {
				result.add("Dopravce s názvem " + nazev.getText()
						+ " již existuje");
			}
		}

		return result;
	}

	protected void fillFields() {
		if (dopravce == null)
			return;

		nazev.setText(dopravce.getNazev());
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
	}
}
