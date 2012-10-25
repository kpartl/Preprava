package cz.kpartl.preprava.util;



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

import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import cz.kpartl.preprava.Activator;
import cz.kpartl.preprava.dao.DAOFactory;
import cz.kpartl.preprava.dao.DopravceDAO;
import cz.kpartl.preprava.dao.ObjednavkaDAO;
import cz.kpartl.preprava.dao.PozadavekDAO;
import cz.kpartl.preprava.dao.UserDAO;
import cz.kpartl.preprava.dao.DestinaceDAO;
import cz.kpartl.preprava.dialog.LoginDialog;
import cz.kpartl.preprava.model.User;



public class Login {
	//TODO zakazat nektere view pro neadminy
	
	public static final Boolean TEST = true; 
	
	public static final String ADD_ICON="ADD_ICON";
	public static final String EDIT_ICON="EDIT_ICON";
	public static final String DELETE_ICON="DELETE_ICON";
	public static final String OBJEDNAVKA_ICON="OBJEDNAVKA_ICON";
	public static final String CALENDAR_ICON="CALENDAR_ICON";
	public static final String CHECKED_ICON="CHECKED_ICON";
	public static final String UNCHECKED_ICON="UNCHECKED_ICON";
			
	UserDAO userDAO = null;
	PozadavekDAO pozadavekDAO = null;
	DestinaceDAO zakaznikDAO= null;
	ObjednavkaDAO objednavkaDAO = null;
	DopravceDAO dopravceDAO = null;
	
	Image addIcon, editIcon, deleteIcon, objednavkaIcon, calendarIcon, checkedIcon, uncheckedIcon;
	
	
	
	 @PostContextCreate
	   public void login(IEclipseContext context) {
		 
		 
		 	
	      final Shell shell = new Shell(SWT.INHERIT_NONE);
	      
	      userDAO = ContextInjectionFactory.make(UserDAO.class, context);
	      pozadavekDAO = ContextInjectionFactory.make(PozadavekDAO.class, context);
	      zakaznikDAO = ContextInjectionFactory.make(DestinaceDAO.class, context);
	      objednavkaDAO = ContextInjectionFactory.make(ObjednavkaDAO.class, context);
	      dopravceDAO = ContextInjectionFactory.make(DopravceDAO.class, context);
	      
	      context.set(UserDAO.class, userDAO); 
	      context.set(PozadavekDAO.class, pozadavekDAO);
	      context.set(ObjednavkaDAO.class, objednavkaDAO);
	      context.set(DestinaceDAO.class, zakaznikDAO);
	      context.set(DopravceDAO.class, dopravceDAO);
	      
	      addIcon = Activator.getImageDescriptor("icons/add_obj.gif").createImage();
	      editIcon = Activator.getImageDescriptor("icons/editor.gif").createImage();
	      deleteIcon = Activator.getImageDescriptor("icons/delete_obj.gif").createImage();
	      objednavkaIcon =Activator.getImageDescriptor("icons/objednavka.gif").createImage();
	      calendarIcon = Activator.getImageDescriptor("icons/calendar_icon.jpg").createImage();
	      checkedIcon = Activator.getImageDescriptor("icons/checked.gif").createImage();
	      uncheckedIcon = Activator.getImageDescriptor("icons/unchecked.gif").createImage();
	      
	      context.set(ADD_ICON, addIcon);
	      context.set(EDIT_ICON,editIcon);
	      context.set(DELETE_ICON,deleteIcon);
	      context.set(OBJEDNAVKA_ICON,objednavkaIcon);
	      context.set(CALENDAR_ICON, calendarIcon);
	      context.set(CHECKED_ICON, checkedIcon);
	      context.set(UNCHECKED_ICON, uncheckedIcon);
	      
	      
	      InitUtil initUtil = ContextInjectionFactory.make(InitUtil.class, context);
	      
	      initUtil.initDBData();
	      
	      tryLogin(shell, context);	 
	      
	   }	
	 
	 private void tryLogin(Shell shell, IEclipseContext context) 
	 {
		 final LoginDialog dialog = new LoginDialog(shell);
	      dialog.create();
	 	      	      
	 
	      if (dialog.open() != Window.OK) {
	         System.exit(0);
	      }	
	      	 
	     String username = dialog.getUsername();	      
	      String password = userDAO.encryptPassword((dialog.getPassword()));
	      
	    //  String username = InitUtil.LOGIN;
	     // String password = UserDAO.encryptPassword(InitUtil.PASSWORD);	    
	      	      	      
	      //try to login
	      User user = userDAO.login(username, password);
	      
	      if(user != null){ //successful login

	    	  //add the logged user to the context
	    	  context.set(User.CONTEXT_NAME, user);
	    	  context.set("cz.kpartl.preprava.admin", user.isAdministrator() ? "1":"0");
	    	  
	    	  return;
	      }
	      else {
	    	  String errMessage ="";
	    	  if(userDAO.findByUsername(username) == null){
	    		  errMessage = "Uživatel " + username + " neexistuje!";
	    	  }
	    	  else {
	    		  errMessage = "Špatnì zadané heslo";
	    	  }
	    	  
	    	  MessageDialog.openError(shell, "CHYBA", errMessage);
	    	  
	    	  tryLogin(shell, context);
	    	  
	      }
	 }
	 
	 @PreDestroy
	 private void preDestroy() {
		 addIcon.dispose();
		 deleteIcon.dispose();
		 editIcon.dispose();
		 objednavkaIcon.dispose();
		 calendarIcon.dispose();
	 }

}