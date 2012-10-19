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

import cz.kpartl.preprava.dao.UserDAO;
import cz.kpartl.preprava.model.User;
import cz.kpartl.preprava.util.HibernateHelper;
import cz.kpartl.preprava.view.AbstractTableView;

public class NovyUzivatelDialog extends TitleAreaDialog {

	UserDAO uzivatelDAO;
	IEventBroker eventBroker;
	
	HibernateHelper persistenceHelper;
	final Logger logger = LoggerFactory.getLogger(NovyUzivatelDialog.class);
	User uzivatel;

	Text username;
	Text password; 
	Button administrator;

	@Inject
	public NovyUzivatelDialog(
			@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell,
			IEclipseContext context, IEventBroker eventBroker) {
		this(parentShell, context, null, eventBroker);
	}

	@Inject
	public NovyUzivatelDialog(
			@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell,
			IEclipseContext context, User uzivatel, IEventBroker eventBroker) {
		super(parentShell);
		this.uzivatel = uzivatel;
		this.uzivatelDAO = context.get(UserDAO.class);
		this.eventBroker = eventBroker;
		persistenceHelper = cz.kpartl.preprava.util.HibernateHelper
				.getInstance();

	}

	@Override
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		setTitle("Vytvoøení / editace doprace");
		setMessage("Zadejte data uživatele", IMessageProvider.INFORMATION);
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

		Label usernamelabel = new Label(parent, SWT.NONE);
		GridData gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false);
		usernamelabel.setLayoutData(gridData);
		usernamelabel.setText("Uživatelské jméno");

		username = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		gridData.widthHint = 200;
		username.setLayoutData(gridData);
		
		Label passwordlabel = new Label(parent, SWT.NONE);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false);
		passwordlabel.setLayoutData(gridData);
		passwordlabel.setText("Heslo");

		password = new Text(parent, SWT.BORDER | SWT.PASSWORD);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		gridData.widthHint = 200;
		password.setLayoutData(gridData);
		
		Label adminLabel = new Label(parent, SWT.BOLD);
		adminLabel.setText("Administrátor");
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		adminLabel.setLayoutData(gridData);

		administrator = new Button(parent, SWT.CHECK);
		gridData = new GridData(SWT.END, SWT.CENTER, true, false);
		gridData.horizontalIndent = 10;


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
				boolean novyUzivatel = uzivatel == null;
				final ArrayList<String> validace = validate(novyUzivatel);
				if (validace.size() == 0) {

					if (novyUzivatel) {
						uzivatel = new User();
					}

					uzivatel.setUsername(username.getText());
					uzivatel.setPassword(UserDAO.encryptPassword(password.getText()));
					uzivatel.setAdministrator(administrator.getSelection());
					
					Transaction tx = persistenceHelper.beginTransaction();
					try {
						if (novyUzivatel)
							uzivatel.setId(uzivatelDAO.create(uzivatel));
						else
							uzivatelDAO.update(uzivatel);
						tx.commit();
						eventBroker.post(AbstractTableView.REFRESH_VIEWERS, "");
						close();
					} catch (Exception ex) {
						setErrorMessage("Pøi zápisu do databáze došlo k chybì, kontaktujte prosím tvùrce aplikace."
								.concat(System.getProperty("line.separator"))
								.concat(ex.getMessage()));
						logger.error("Nelze vložit/upravit uživatele", e);
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

		// editace existujiciho uzivatel
		fillFields();

		return parent;

	}

	protected ArrayList<String> validate(boolean novyUser) {
		ArrayList<String> result = new ArrayList<String>();
		if ("".equals(username.getText().trim()))
			result.add("Není zadáno uživatelské jméno");
		if ("".equals(password.getText().trim()))
			result.add("Není zadáno heslo");
		final User existujici = uzivatelDAO.findByUsername(username.getText());
		if (existujici != null) {
			if (novyUser || !existujici.getId().equals(uzivatel.getId())) {
				result.add("Uživatel se jménem " + username.getText()
						+ " již existuje");
			}
		}

		return result;
	}

	protected void fillFields() {
		if (uzivatel == null)
			return;

		username.setText(uzivatel.getUsername());
		password.setText("");
		administrator.setSelection(uzivatel.isAdministrator());
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
	}
}

