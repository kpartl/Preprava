package cz.kpartl.preprava.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Shell;

import cz.kpartl.preprava.dialog.DopravceImportDialog;
import cz.kpartl.preprava.model.User;

public class ImportDopravcuHandler {

	@Inject
	IEclipseContext context;

	@Execute
	public void execute(Shell parentShell, IEclipseContext context,
			IEventBroker eventBroker) {
		new DopravceImportDialog(parentShell, context, eventBroker).open();
	}

	@CanExecute
	public boolean canExecute() {
		return ((User) context.get(User.CONTEXT_NAME)).isAdministrator();
	}

}