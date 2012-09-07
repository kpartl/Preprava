package cz.kpartl.preprava.util;



import javax.inject.Inject;

import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.e4.ui.css.swt.theme.IThemeManager;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.internal.workbench.swt.E4Application;
import org.eclipse.e4.ui.internal.workbench.swt.PartRenderingEngine;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.e4.core.contexts.IEclipseContext;

import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import cz.kpartl.preprava.dao.DAOFactory;
import cz.kpartl.preprava.dao.UserDAO;
import cz.kpartl.preprava.dialog.LoginDialog;
import cz.kpartl.preprava.model.User;



public class Login {
			
	
	 @PostContextCreate
	   public void login(IEclipseContext context) {
		 
	
	      final Shell shell = new Shell(SWT.INHERIT_NONE);
	      
	      InitUtil.initDBData();
	      
	      tryLogin(shell, context);	 
	      
	   }	
	 
	 private void tryLogin(Shell shell, IEclipseContext context) 
	 {
		 final LoginDialog dialog = new LoginDialog(shell);
	      dialog.create();
	 	      	      
	 
	      if (dialog.open() != Window.OK) {
	         System.exit(0);
	      }	
	      	      
	      
	      DAOFactory daoFactory = new DAOFactory();
	      
	      UserDAO userDAO = daoFactory.getUserDAO();
	      
	      String username = dialog.getUsername();
	      
	      String password = userDAO.encryptPassword((dialog.getPassword()));
	      
	      //add the DAOFactory into the context
	      context.set(DAOFactory.CONTEXT_NAME, daoFactory);	      
	      
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