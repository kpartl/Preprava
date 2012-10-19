package cz.kpartl.preprava.util;



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
			
	UserDAO userDAO = null;
	PozadavekDAO pozadavekDAO = null;
	DestinaceDAO zakaznikDAO= null;
	ObjednavkaDAO objednavkaDAO = null;
	DopravceDAO dopravceDAO = null;
	
	
	
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
	      
	      context.set(ADD_ICON,Activator.getImageDescriptor("icons/add_obj.gif").createImage());
	      context.set(EDIT_ICON,Activator.getImageDescriptor("icons/editor.gif").createImage());
	      context.set(DELETE_ICON,Activator.getImageDescriptor("icons/delete_obj.gif").createImage());
	      context.set(OBJEDNAVKA_ICON,Activator.getImageDescriptor("icons/objednavka.gif").createImage());
	      
	      
	      InitUtil initUtil = ContextInjectionFactory.make(InitUtil.class, context);
	      
	      initUtil.initDBData();
	      
	      tryLogin(shell, context);	 
	      
	   }	
	 
	 private void tryLogin(Shell shell, IEclipseContext context) 
	 {
		/* final LoginDialog dialog = new LoginDialog(shell);
	      dialog.create();
	 	      	      
	 
	      if (dialog.open() != Window.OK) {
	         System.exit(0);
	      }	
	      	 
	      	      */
	      	     
	      
	      
	      
	     /* String username = dialog.getUsername();
	      
	      String password = userDAO.encryptPassword((dialog.getPassword()));*/
	      
	      String username = InitUtil.LOGIN;
	      String password = UserDAO.encryptPassword(InitUtil.PASSWORD);	    
	      	      	      
	      //try to login
	      User user = userDAO.login(username, password);
	      
	      if(user != null){ //successful login

	    	  //add the logged user to the context
	    	  context.set(User.CONTEXT_NAME, user);
	    	  
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

}