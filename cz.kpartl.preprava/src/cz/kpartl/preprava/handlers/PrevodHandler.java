package cz.kpartl.preprava.handlers;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Shell;

import cz.kpartl.preprava.view.DestinaceView;
import cz.kpartl.preprava.view.DopravceView;
import cz.kpartl.preprava.view.ObjednanoView;
import cz.kpartl.preprava.view.PozadavkyView;
import cz.kpartl.preprava.view.UkoncenoView;
import cz.kpartl.preprava.view.UzivatelView;

public class PrevodHandler {
	@Execute
	public void execute(Shell parentShell, IEclipseContext context,
			@Active MPart activePart, IEventBroker eventBroker

	) {

		if (activePart.getElementId().equals(PozadavkyView.ID)) {
			((PozadavkyView) activePart.getObject()).prevedSelectedPozadavek();
		}

	}

}
