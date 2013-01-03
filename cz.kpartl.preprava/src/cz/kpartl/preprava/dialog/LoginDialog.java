package cz.kpartl.preprava.dialog;

import java.awt.event.PaintEvent;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kpartl.preprava.dao.UserDAO;
import cz.kpartl.preprava.model.User;
import cz.kpartl.preprava.util.HibernateHelper;

public class LoginDialog extends Dialog {
	private static final int RESET_ID = IDialogConstants.NO_TO_ALL_ID + 1;

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	private Text usernameField;

	private Text passwordField;

	private String username;

	private String password;

	private Image loginIcon;

	private ProgressBar progressBar;

	private volatile int loginStatus = -1;

	private UserDAO userDAO;

	IEclipseContext context;

	Shell shell;

	public LoginDialog(Shell parentShell, Image loginIcon, UserDAO userDAO,
			IEclipseContext context) {
		super(parentShell);
		this.loginIcon = loginIcon;
		this.userDAO = userDAO;
		this.context = context;
		this.shell = parentShell;

	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	protected Control createDialogArea(Composite parent) {
		Composite comp = (Composite) super.createDialogArea(parent);
		final GridLayout gridLayout = new GridLayout();
		comp.setLayout(gridLayout);
		// comp.setBackgroundImage(loginIcon);

		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true,
				true);

		comp.setLayoutData(layoutData);
		comp.redraw();

		Canvas canvas = new Canvas(comp, SWT.FILL);
		layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.horizontalSpan = 2;
		canvas.setLayoutData(layoutData);
		canvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(org.eclipse.swt.events.PaintEvent e) {
				e.gc.drawImage(loginIcon, 0, 0);

			}
		});
		// if(true) return comp;

		Label usernameLabel = new Label(comp, SWT.RIGHT);
		usernameLabel.setText("Uživatelské jméno: ");

		usernameField = new Text(comp, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		usernameField.setLayoutData(data);

		Label passwordLabel = new Label(comp, SWT.RIGHT);
		passwordLabel.setText("Heslo: ");

		passwordField = new Text(comp, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		passwordField.setLayoutData(data);

		progressBar = new ProgressBar(comp, SWT.INDETERMINATE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		progressBar.setLayoutData(data);
		progressBar.setVisible(false);

		return comp;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setText("Pøihlásit");
		getButton(IDialogConstants.CANCEL_ID).setText("Zrušit");
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Pøihlášení do aplikace Pøeprava");
		// shell.setSize(300, 125);
		// setShellStyle( SWT.APPLICATION_MODAL);
		shell.setSize(260, 410);
	}

	@Override
	protected void cancelPressed() {
		loginStatus = -2;
		super.cancelPressed();
	}

	@Override
	protected void okPressed() {
		username = usernameField.getText();
		password = UserDAO.encryptPassword(passwordField.getText());

		showProgressBar(true);

		Thread thread = new Thread(new Runnable() {
			public void run() {
				if (userDAO.findByUsername("admin") == null) {
					User admin = new User();
					admin.setAdministrator(true);
					admin.setUsername("admin");
					admin.setPassword(userDAO.encryptPassword("istrator"));
					Transaction tx = HibernateHelper.getInstance()
							.beginTransaction();
					try {
						userDAO.create(admin);
						tx.commit();
						logger.debug("Vlozen user admin");
					} catch (Exception ex) {
						MessageDialog.openError(shell,
								"Chyba pøi zápisu do databáze",
								"Nepodaøilo se vložit uživatele admin.");

						logger.error("Nelze vlozit uživatele", ex);
						tx.rollback();
					}
				}

				User user = userDAO.login(username, password);

				if (user != null) { // successful login

					// add the logged user to the context
					context.set(User.CONTEXT_NAME, user);
					context.set("cz.kpartl.preprava.admin",
							user.isAdministrator() ? "1" : "0");
					loginStatus = 1;
					HibernateHelper.getInstance().getSession().close();
					return;
				} else {
					loginStatus = 2;

					// tryLogin(shell, context);

				}
			}
		});

		thread.start();
		while (loginStatus == -1) {
			if (shell.getDisplay().readAndDispatch() == false) {
			}

		}

	}

	public int getLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(int status) {
		this.loginStatus = status;
	}

	public void showProgressBar(boolean how) {
		progressBar.setVisible(how);
		progressBar.getParent().layout();

	}

}