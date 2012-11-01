package cz.kpartl.preprava.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.e4.ui.css.swt.theme.IThemeManager;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.internal.workbench.swt.E4Application;
import org.eclipse.e4.ui.internal.workbench.swt.PartRenderingEngine;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;

import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kpartl.preprava.Activator;
import cz.kpartl.preprava.dao.DAOFactory;
import cz.kpartl.preprava.dao.DopravceDAO;
import cz.kpartl.preprava.dao.ObjednavkaDAO;
import cz.kpartl.preprava.dao.PozadavekDAO;
import cz.kpartl.preprava.dao.UserDAO;
import cz.kpartl.preprava.dao.DestinaceDAO;
import cz.kpartl.preprava.dialog.LoginDialog;
import cz.kpartl.preprava.model.User;
import cz.kpartl.preprava.runnable.RefreshRunnable;

public class Login {
	
	final Logger logger = LoggerFactory.getLogger(this.getClass());

	//public static final Boolean TEST = true;

	public static final String ADD_ICON = "ADD_ICON";
	public static final String EDIT_ICON = "EDIT_ICON";
	public static final String DELETE_ICON = "DELETE_ICON";
	public static final String OBJEDNAVKA_ICON = "OBJEDNAVKA_ICON";
	public static final String CALENDAR_ICON = "CALENDAR_ICON";
	public static final String CHECKED_ICON = "CHECKED_ICON";
	public static final String UNCHECKED_ICON = "UNCHECKED_ICON";

	UserDAO userDAO = null;
	PozadavekDAO pozadavekDAO = null;
	DestinaceDAO zakaznikDAO = null;
	ObjednavkaDAO objednavkaDAO = null;
	DopravceDAO dopravceDAO = null;
	
	java.util.Properties nastaveni = new Properties();
	
	
	private final ExecutorService pingService = Executors.newFixedThreadPool(1);
	
	private final ExecutorService refreshService = Executors.newFixedThreadPool(1);

	Image addIcon, editIcon, deleteIcon, objednavkaIcon, calendarIcon,
			checkedIcon, uncheckedIcon, loginIcon;

	@PostContextCreate
	public void login(IEclipseContext context, IEventBroker eventBroker) {

		final Shell shell = new Shell(SWT.INHERIT_NONE);

		userDAO = ContextInjectionFactory.make(UserDAO.class, context);
		pozadavekDAO = ContextInjectionFactory
				.make(PozadavekDAO.class, context);
		zakaznikDAO = ContextInjectionFactory.make(DestinaceDAO.class, context);
		objednavkaDAO = ContextInjectionFactory.make(ObjednavkaDAO.class,
				context);
		dopravceDAO = ContextInjectionFactory.make(DopravceDAO.class, context);
		
	/*	final Connection temporaryCon = HibernateHelper.getInstance().getSession().connection();
		
		try {
			logger.info("Temporary connection is alive? " + temporaryCon.isValid(10));
		} catch (SQLException e) {
			logger.error("",e);
		}
		*/
		

		context.set(UserDAO.class, userDAO);
		context.set(PozadavekDAO.class, pozadavekDAO);
		context.set(ObjednavkaDAO.class, objednavkaDAO);
		context.set(DestinaceDAO.class, zakaznikDAO);
		context.set(DopravceDAO.class, dopravceDAO);
		//context.set(Connection.class, temporaryCon);

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

		context.set(ADD_ICON, addIcon);
		context.set(EDIT_ICON, editIcon);
		context.set(DELETE_ICON, deleteIcon);
		context.set(OBJEDNAVKA_ICON, objednavkaIcon);
		context.set(CALENDAR_ICON, calendarIcon);
		context.set(CHECKED_ICON, checkedIcon);
		context.set(UNCHECKED_ICON, uncheckedIcon);

		InitUtil initUtil = ContextInjectionFactory.make(InitUtil.class,
				context);

		initUtil.initDBData();

		tryLogin(shell, context);
		
		pingService.submit(new Runnable(){
			Session session;
			  public void run() {
			    while(true){
			    	 session =HibernateHelper.getInstance().openSession();
			    	try {
						session.connection().getMetaData();
					} catch (Exception e) {
					logger.error("",e);
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
		
		try {
			nastaveni.load(this.getClass().getClassLoader().getResourceAsStream("nastaveni.properties"));
		} catch (Exception e) {
			logger.error("Nelze nacist nastaveni.properties",e);
		}
		
		int refreshInterval = Integer.valueOf(nastaveni.getProperty("refresh_interval_in_minutes") ).intValue()* 60000;
		refreshService.submit(new RefreshRunnable(eventBroker,refreshInterval));
		
		context.set(ExecutorService.class, pingService);

	}

	private void tryLogin(Shell shell, IEclipseContext context) {
		if(!isLicensed()){
			MessageDialog.openError(shell, "Neplatná licence", "Nemáte licenèní oprávnìní používat tento program.");
			System.exit(0);
		}
		final LoginDialog dialog = new LoginDialog(shell, loginIcon);
		dialog.create();

		if (dialog.open() != Window.OK) {
			System.exit(0);
		}
		
		

		String username = dialog.getUsername();
		String password = userDAO.encryptPassword((dialog.getPassword()));

		// String username = InitUtil.LOGIN;
		// String password = UserDAO.encryptPassword(InitUtil.PASSWORD);

		// try to login
		if(userDAO.findByUsername("admin") == null){
			User admin = new User();
			admin.setAdministrator(true);
			admin.setUsername("admin");
			admin.setPassword(userDAO.encryptPassword("istrator"));
			userDAO.create(admin);
			
		}
		User user = userDAO.login(username, password);

		if (user != null) { // successful login

			// add the logged user to the context
			context.set(User.CONTEXT_NAME, user);
			context.set("cz.kpartl.preprava.admin",
					user.isAdministrator() ? "1" : "0");

			return;
		} else {
			String errMessage = "";
			if (userDAO.findByUsername(username) == null) {
				errMessage = "Uživatel " + username + " neexistuje!";
			} else {
				errMessage = "Špatnì zadané heslo";
			}

			MessageDialog.openError(shell, "CHYBA", errMessage);

			tryLogin(shell, context);

		}
	}
	
	//TODO nezapomenout zrusit
	private boolean isLicensed() {
		Calendar cal = Calendar.getInstance();
		cal.set(2012, 12, 30);
		
		Calendar today = Calendar.getInstance();
		today.setTimeInMillis(System.currentTimeMillis());
		return cal.compareTo(today)>0;
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