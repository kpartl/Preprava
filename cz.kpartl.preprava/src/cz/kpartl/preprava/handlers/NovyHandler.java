package cz.kpartl.preprava.handlers;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;

import cz.kpartl.preprava.dialog.NovaDestinaceDialog;
import cz.kpartl.preprava.dialog.NovyPozadavekDialog;
import cz.kpartl.preprava.view.DestinaceView;
import cz.kpartl.preprava.view.PozadavkyView;

public class NovyHandler {

	@Execute
	public void execute(Shell parentShell, IEclipseContext context,
			@Active MPart activePart

	) {
		if (activePart.getElementId().equals(PozadavkyView.ID)) {
			new NovyPozadavekDialog(parentShell, context).open();
		} else if (activePart.getElementId().equals(DestinaceView.ID)) {
			new NovaDestinaceDialog(parentShell, context).open();
		}

	}

}
