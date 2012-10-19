package cz.kpartl.preprava.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import cz.kpartl.preprava.dao.DestinaceDAO;
import cz.kpartl.preprava.dao.PozadavekDAO;
import cz.kpartl.preprava.dialog.NovyPozadavekDialog;
import cz.kpartl.preprava.model.User;
import cz.kpartl.preprava.view.AbstractTableView;
import cz.kpartl.preprava.view.PozadavkyView;

public class NovyPozadavekHandler {
	@Inject
	@Named("cz.kpartl.preprava.dao.DestinaceDAO")
	DestinaceDAO destinaceDAO;

	@Inject
	@Named(User.CONTEXT_NAME)
	User user;

	// @Inject
	// @Named("cz.kpartl.preprava.view.PozadavkyView") PozadavkyView
	// pozadavkyView;

	@Inject
	PozadavekDAO pozadavekDAO;
	
	

	@Execute
	public void execute(
			Shell parentShell,	
			@Optional @Named("cz.kpartl.preprava.view.PozadavkyView") PozadavkyView pozadavkyView,
			IEclipseContext context, IEventBroker eventBroker

	) {
		Object o = context.get(PozadavekDAO.class);
		final NovyPozadavekDialog dialog = new NovyPozadavekDialog(parentShell,
				context, eventBroker);
		if (dialog.open() == Window.OK) {

			//pozadavkyView.refreshInputData();
		}

	}

	/*@CanExecute
	public boolean canExecute(@Optional  PozadavkyView pozadavkyView) {
		return pozadavkyView != null;
	
	}
*/
	/*
	 * @Override public Object execute(ExecutionEvent event) throws
	 * ExecutionException {
	 * 
	 * IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
	 * IWorkbenchPage page = window.getActivePage(); AbstractTableView view =
	 * (AbstractTableView) page.findView(PozadavkyView.ID); NovyPozadavekDialog
	 * dialog = new NovyPozadavekDialog(window.getShell(), destinaceDAO, user,
	 * pozadavekDAO, null); dialog.open(); view.refresh();
	 * 
	 * return null; }
	 */
}
