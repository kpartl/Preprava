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

import cz.kpartl.preprava.model.Objednavka;
import cz.kpartl.preprava.model.User;
import cz.kpartl.preprava.view.AbstractTableView;
import cz.kpartl.preprava.view.ObjednanoView;
import cz.kpartl.preprava.view.PozadavkyView;



public class TiskHandler {
	@Inject
	IEclipseContext context;
	
	@Execute
	public void execute(Shell parentShell, IEclipseContext context,
			@Active MPart activePart, IEventBroker eventBroker

	) {

		if (activePart.getElementId().equals(ObjednanoView.ID)) {			
			((ObjednanoView) activePart.getObject()).tiskVybraneObjednavky();
		}

	}
	
	@CanExecute
	public boolean canExecute(@Active MPart activePart) {				
		final TableViewer viewer = ((AbstractTableView) activePart.getObject()).viewer;
		if (((StructuredSelection) viewer.getSelection()).size() != 1) {
			return false;
		}
		
		return true;		
	}

}
