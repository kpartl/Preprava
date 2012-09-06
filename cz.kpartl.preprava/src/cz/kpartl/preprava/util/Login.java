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
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import cz.kpartl.preprava.dialog.LoginDialog;



public class Login {
		
		
	 @PostContextCreate
	   public void login(IEclipseContext context2) {
		 
	
	      final Shell shell = new Shell(SWT.INHERIT_NONE);
	 
	      final LoginDialog dialog = new LoginDialog(shell);
	      dialog.create();
	 
	      
	      Bundle bundle = WorkbenchSWTActivator.getDefault().getBundle();
	      BundleContext context = bundle.getBundleContext();
	      
	  
	      
			ServiceReference ref = context
					.getServiceReference(IThemeManager.class.getName());
			IThemeManager mgr = (IThemeManager) context.getService(ref);
			final IThemeEngine engine = mgr.getEngineForDisplay(shell.getDisplay());
			
			engine.setTheme("de.vogella.e4.todo.logintheme", false);
	 
	      //PartRenderingEngine.initializeStyling(shell.getDisplay(), context2);
	 
	      if (dialog.open() != Window.OK) {
	         System.exit(0);
	      }
	   }

}