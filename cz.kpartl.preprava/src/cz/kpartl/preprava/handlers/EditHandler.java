package cz.kpartl.preprava.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kpartl.preprava.dialog.NovaDestinaceDialog;
import cz.kpartl.preprava.dialog.NovyDopravceDialog;
import cz.kpartl.preprava.dialog.NovyPozadavekDialog;
import cz.kpartl.preprava.dialog.NovyUzivatelDialog;
import cz.kpartl.preprava.model.Pozadavek;
import cz.kpartl.preprava.model.User;
import cz.kpartl.preprava.view.AbstractTableView;
import cz.kpartl.preprava.view.DestinaceView;
import cz.kpartl.preprava.view.DopravceView;
import cz.kpartl.preprava.view.ObjednanoView;
import cz.kpartl.preprava.view.PozadavkyView;
import cz.kpartl.preprava.view.UkoncenoView;
import cz.kpartl.preprava.view.UzivatelView;

public class EditHandler {

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Inject
	IEclipseContext context;

	@Execute
	public void execute(Shell parentShell, IEclipseContext context,
			@Active MPart activePart, IEventBroker eventBroker

	) {

		if (activePart.getElementId().equals(PozadavkyView.ID)) {
			((PozadavkyView) activePart.getObject()).editSelectedPozadavek();
		} else if (activePart.getElementId().equals(ObjednanoView.ID)) {
			((ObjednanoView) activePart.getObject()).editSelectedObjednavka();
		} else if (activePart.getElementId().equals(UkoncenoView.ID)) {
			((UkoncenoView) activePart.getObject()).editSelectedObjednavka();
		} else if (activePart.getElementId().equals(DestinaceView.ID)) {
			((DestinaceView) activePart.getObject()).editSelectedDestinace();
		} else if (activePart.getElementId().equals(DopravceView.ID)) {
			((DopravceView) activePart.getObject()).editSelectedDopravce();
		} else if (activePart.getElementId().equals(UzivatelView.ID)) {
			((UzivatelView) activePart.getObject()).editSelectedUzivatele();
		}

	}

	@CanExecute
	public boolean canExecute(@Active MPart activePart) {

		boolean result = true;

		TableViewer viewer = ((AbstractTableView) activePart.getObject()).viewer;
		// logger.info("((StructuredSelection) viewer.getSelection()).getFirstElement(): "
		// + ((StructuredSelection) viewer.getSelection()) .getFirstElement());
		if (((StructuredSelection) viewer.getSelection()).size()!= 1) {
			result = false;
		} else {
			// logger.info("activePart.getElementId().equals(PozadavkyView.ID)"
			// + activePart.getElementId().equals(PozadavkyView.ID));
			// logger.info("((User) context.get(User.CONTEXT_NAME)).isAdministrator()"
			// + ((User) context.get(User.CONTEXT_NAME)).isAdministrator());

			result = activePart.getElementId().equals(PozadavkyView.ID)
					|| ((User) context.get(User.CONTEXT_NAME))
							.isAdministrator();
		}		

		return result;
	}
	
	

}
