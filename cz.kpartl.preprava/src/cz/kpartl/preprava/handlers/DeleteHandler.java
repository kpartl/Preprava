package cz.kpartl.preprava.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Shell;

import cz.kpartl.preprava.dialog.NovaDestinaceDialog;
import cz.kpartl.preprava.dialog.NovyDopravceDialog;
import cz.kpartl.preprava.dialog.NovyUzivatelDialog;
import cz.kpartl.preprava.model.User;
import cz.kpartl.preprava.view.DestinaceView;
import cz.kpartl.preprava.view.DopravceView;
import cz.kpartl.preprava.view.ObjednanoView;
import cz.kpartl.preprava.view.PozadavkyView;
import cz.kpartl.preprava.view.UkoncenoView;
import cz.kpartl.preprava.view.UzivatelView;

public class DeleteHandler {
	
	@Inject
	IEclipseContext context;

	@Execute
	public void execute(Shell parentShell, IEclipseContext context,
			@Active MPart activePart, IEventBroker eventBroker)

	{
		if (activePart.getElementId().equals(PozadavkyView.ID)) {
			((PozadavkyView) activePart.getObject()).deleteSelectedPozadavek();		
		} else if (activePart.getElementId().equals(ObjednanoView.ID)) {
			((ObjednanoView) activePart.getObject()).deleteSelectedObjednavka();
		} else if (activePart.getElementId().equals(UkoncenoView.ID)) {
			((UkoncenoView) activePart.getObject()).deleteSelectedObjednavka();
		} else if (activePart.getElementId().equals(DestinaceView.ID)) {
			((DestinaceView) activePart.getObject()).deleteSelectedDestinace();
		} else if (activePart.getElementId().equals(DopravceView.ID)) {
			((DopravceView) activePart.getObject()).deleteSelectedDopravce();
		} else if (activePart.getElementId().equals(UzivatelView.ID)) {
			((UzivatelView) activePart.getObject()).deleteSelectedUzivatel();
		}
	}
	
	@CanExecute
	public boolean canExecute(@Active MPart activePart) {
		
		return activePart.getElementId().equals(PozadavkyView.ID) ||((User) context.get(User.CONTEXT_NAME))
				.isAdministrator();		
	}
}
