package cz.kpartl.preprava.handlers;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import cz.kpartl.preprava.dialog.NovaDestinaceDialog;

public class NovaDestinaceHandler {
	
	@Execute
	public void execute(
			Shell parentShell,
			IEclipseContext context){
		final NovaDestinaceDialog novaDestinaceDialog = new NovaDestinaceDialog(parentShell, context);
		novaDestinaceDialog.open();
		
	}

}
