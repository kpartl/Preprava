package cz.kpartl.preprava.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.swt.widgets.Shell;

import cz.kpartl.preprava.dialog.ObjednavkaExportDialog;
import cz.kpartl.preprava.model.User;

public class ExportObjednavekHandler {
	@Inject
	IEclipseContext context;

	@Inject
	MApplication app;

	@Execute
	public void execute(Shell parentShell, IEclipseContext context,
			IEventBroker eventBroker) {		
		new ObjednavkaExportDialog(parentShell, context, eventBroker).open();
	}

	@CanExecute
	public boolean canExecute() {
		return ((User) context.get(User.CONTEXT_NAME)).isAdministrator();
	}
}
