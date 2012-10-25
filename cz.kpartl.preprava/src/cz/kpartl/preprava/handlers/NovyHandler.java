package cz.kpartl.preprava.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;

import cz.kpartl.preprava.dialog.NovaDestinaceDialog;
import cz.kpartl.preprava.dialog.NovyDopravceDialog;
import cz.kpartl.preprava.dialog.NovyPozadavekDialog;
import cz.kpartl.preprava.dialog.NovyUzivatelDialog;
import cz.kpartl.preprava.model.User;
import cz.kpartl.preprava.view.DestinaceView;
import cz.kpartl.preprava.view.DopravceView;
import cz.kpartl.preprava.view.PozadavkyView;
import cz.kpartl.preprava.view.UzivatelView;

public class NovyHandler {
	
	@Inject
	IEclipseContext context;

	@Execute
	public void execute(Shell parentShell, IEclipseContext context,
			@Active MPart activePart, IEventBroker eventBroker

	) {
		if (activePart.getElementId().equals(PozadavkyView.ID)) {
			new NovyPozadavekDialog(parentShell, context, eventBroker).open();
		} else if (activePart.getElementId().equals(DestinaceView.ID)) {
			new NovaDestinaceDialog(parentShell, context, eventBroker).open();
		} else if (activePart.getElementId().equals(DopravceView.ID)) {
			new NovyDopravceDialog(parentShell, context, eventBroker).open();
		} else if (activePart.getElementId().equals(UzivatelView.ID)) {
			new NovyUzivatelDialog(parentShell, context, eventBroker).open();
		}

	}

	@CanExecute
	public boolean canExecute(@Active MPart activePart) {
		
		return activePart.getElementId().equals(PozadavkyView.ID) ||((User) context.get(User.CONTEXT_NAME))
				.isAdministrator();		
	}
}
