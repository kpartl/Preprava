package cz.kpartl.preprava.util;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kpartl.preprava.Activator;
import cz.kpartl.preprava.dao.DestinaceDAO;
import cz.kpartl.preprava.dao.DopravceDAO;
import cz.kpartl.preprava.dao.ObjednavkaDAO;
import cz.kpartl.preprava.dao.PozadavekDAO;
import cz.kpartl.preprava.dao.UserDAO;
import cz.kpartl.preprava.dialog.LoginDialog;
import cz.kpartl.preprava.importer.DataImporter;
import cz.kpartl.preprava.runnable.RefreshRunnable;

public class Login {

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	// public static final Boolean TEST = true;

	public static final String ADD_ICON = "ADD_ICON";
	public static final String EDIT_ICON = "EDIT_ICON";
	public static final String DELETE_ICON = "DELETE_ICON";
	public static final String OBJEDNAVKA_ICON = "OBJEDNAVKA_ICON";
	public static final String CALENDAR_ICON = "CALENDAR_ICON";
	public static final String CHECKED_ICON = "CHECKED_ICON";
	public static final String UNCHECKED_ICON = "UNCHECKED_ICON";
	public static final String TISK_ICON = "TISK_ICON";

	volatile boolean authenticated = false;

	// volatile String username, password;

	UserDAO userDAO = null;
	PozadavekDAO pozadavekDAO = null;
	DestinaceDAO zakaznikDAO = null;
	ObjednavkaDAO objednavkaDAO = null;
	DopravceDAO dopravceDAO = null;

	java.util.Properties nastaveni = new Properties();

	private final ExecutorService pingService = Executors.newFixedThreadPool(1);

	private final ExecutorService refreshService = Executors
			.newFixedThreadPool(1);

	Image addIcon, editIcon, deleteIcon, objednavkaIcon, calendarIcon,
			checkedIcon, uncheckedIcon, loginIcon, tiskIcon;

	@PostContextCreate
	public void login(IEclipseContext context, IEventBroker eventBroker) {

		String[] args = Platform.getCommandLineArgs();

		int i = 0;
		while (i < args.length) {
			if (args[i].equals("-destinaceImport")) {
				i++;
				new DataImporter(args[i]).importDestinace();
				System.exit(0);
			}
			i++;
		}

		final Shell shell = new Shell(SWT.INHERIT_NONE);

		userDAO = ContextInjectionFactory.make(UserDAO.class, context);
		pozadavekDAO = ContextInjectionFactory
				.make(PozadavekDAO.class, context);
		zakaznikDAO = ContextInjectionFactory.make(DestinaceDAO.class, context);
		objednavkaDAO = ContextInjectionFactory.make(ObjednavkaDAO.class,
				context);
		dopravceDAO = ContextInjectionFactory.make(DopravceDAO.class, context);

		/*
		 * final Connection temporaryCon =
		 * HibernateHelper.getInstance().getSession().connection();
		 * 
		 * try { logger.info("Temporary connection is alive? " +
		 * temporaryCon.isValid(10)); } catch (SQLException e) {
		 * logger.error("",e); }
		 */

		context.set(UserDAO.class, userDAO);
		context.set(PozadavekDAO.class, pozadavekDAO);
		context.set(ObjednavkaDAO.class, objednavkaDAO);
		context.set(DestinaceDAO.class, zakaznikDAO);
		context.set(DopravceDAO.class, dopravceDAO);
		// context.set(Connection.class, temporaryCon);

		addIcon = Activator.getImageDescriptor("icons/add_obj.gif")
				.createImage();
		editIcon = Activator.getImageDescriptor("icons/editor.gif")
				.createImage();
		deleteIcon = Activator.getImageDescriptor("icons/delete_obj.gif")
				.createImage();
		objednavkaIcon = Activator.getImageDescriptor("icons/objednavka.gif")
				.createImage();
		calendarIcon = Activator.getImageDescriptor("icons/calendar_icon.jpg")
				.createImage();
		checkedIcon = Activator.getImageDescriptor("icons/checked.gif")
				.createImage();
		uncheckedIcon = Activator.getImageDescriptor("icons/unchecked.gif")
				.createImage();
		loginIcon = Activator.getImageDescriptor("icons/preprava250.gif")
				.createImage();
		tiskIcon = Activator.getImageDescriptor("icons/printview_tsk.gif")
				.createImage();

		context.set(ADD_ICON, addIcon);
		context.set(EDIT_ICON, editIcon);
		context.set(DELETE_ICON, deleteIcon);
		context.set(OBJEDNAVKA_ICON, objednavkaIcon);
		context.set(CALENDAR_ICON, calendarIcon);
		context.set(CHECKED_ICON, checkedIcon);
		context.set(UNCHECKED_ICON, uncheckedIcon);
		context.set(TISK_ICON, tiskIcon);

		InitUtil initUtil = ContextInjectionFactory.make(InitUtil.class,
				context);

		initUtil.initDBData();

		pingService.submit(new Runnable() {
			Session session;

			public void run() {
				while (true) {
					session = HibernateHelper.getInstance().openSession();
					try {
						session.connection().getMetaData();
					} catch (Exception e) {
						logger.error("", e);
					}
					logger.info("Zavolan ping na databazi");
					try {
						Thread.sleep(60 * 1000);
					} catch (InterruptedException e) {
						break;
					}
					session.close();
				}
			}
		});

		/*
		 * TRY LOGIN
		 */
		tryLogin(shell, context);

		try {
			nastaveni.load(this.getClass().getClassLoader()
					.getResourceAsStream("nastaveni.properties"));
		} catch (Exception e) {
			logger.error("Nelze nacist nastaveni.properties", e);
		}

		int refreshInterval = Integer.valueOf(
				nastaveni.getProperty("refresh_interval_in_minutes"))
				.intValue() * 60000;
		refreshService
				.submit(new RefreshRunnable(eventBroker, refreshInterval));

		context.set(ExecutorService.class, pingService);

	}

	private void tryLogin(final Shell shell, final IEclipseContext context) {
		if (!isLicensed()) {
			MessageDialog.openError(shell, "Neplatná licence",
					"Nemáte licenèní oprávnìní používat tento program.");
			System.exit(0);
		}
		final LoginDialog dialog = new LoginDialog(shell, loginIcon, userDAO,
				context);
		dialog.create();
		dialog.setBlockOnOpen(false);
		dialog.open();

		/*
		 * if (dialog.open() != Window.OK) { System.exit(0); }
		 */

		while (!authenticated) {
			if (!shell.getDisplay().readAndDispatch()) {
				if (dialog.getLoginStatus() == 1) {
					dialog.showProgressBar(false);
					authenticated = true;
					dialog.setLoginStatus(-1);
					dialog.close();
				} else if (dialog.getLoginStatus() == 2) {
					dialog.showProgressBar(false);
					dialog.setLoginStatus(-1);
					String errMessage = "";
					if (userDAO.findByUsername(dialog.getUsername()) == null) {
						errMessage = "Uživatel " + dialog.getUsername()
								+ " neexistuje!";
					} else {
						errMessage = "Špatnì zadané heslo";
					}

					MessageDialog.openError(shell, "CHYBA", errMessage);
				} else if (dialog.getLoginStatus() == -2)// cancel pressed
				{
					System.exit(0);
				}
			}

		}

	}

	// TODO nezapomenout zrusit
	private boolean isLicensed() {
		/*
		 * Calendar cal = Calendar.getInstance(); cal.set(Calendar.YEAR,2013);
		 * cal.set(Calendar.MONTH,2); cal.set(Calendar.DAY_OF_MONTH,31);
		 * 
		 * 
		 * Calendar today = Calendar.getInstance();
		 * today.setTimeInMillis(System.currentTimeMillis());
		 * 
		 * return cal.compareTo(today) > 0;
		 */

		return true;
	}

	@PreDestroy
	private void preDestroy() {
		addIcon.dispose();
		deleteIcon.dispose();
		editIcon.dispose();
		objednavkaIcon.dispose();
		calendarIcon.dispose();
		checkedIcon.dispose();
		uncheckedIcon.dispose();
		loginIcon.dispose();
		pingService.shutdown();
		refreshService.shutdown();
	}

}