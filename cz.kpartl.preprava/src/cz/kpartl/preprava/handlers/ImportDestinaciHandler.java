package cz.kpartl.preprava.handlers;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.model.application.ui.menu.impl.HandledToolItemImpl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Shell;

import cz.kpartl.preprava.dialog.DestinaceImportDialog;
import cz.kpartl.preprava.model.User;

public class ImportDestinaciHandler {
	@Inject
	IEclipseContext context;

	@Inject
	MApplication app;

	@Execute
	public void execute(Shell parentShell, IEclipseContext context,
			IEventBroker eventBroker) {		
		new DestinaceImportDialog(parentShell, context, eventBroker).open();
	}

	@CanExecute
	public boolean canExecute() {
		return ((User) context.get(User.CONTEXT_NAME)).isAdministrator();
		
		// return ((String)
		// app.getContext().get("cz.kpartl.preprava.administrace")).equals("1");

		// Find the required MMenu Entry in the Application Model
		/*if (app == null)
			return true;
		EModelService modelService = (EModelService) app.getContext().get(
				EModelService.class.getName());
		MPart part = (MPart) modelService.find(
				"cz.kpartl.preprava.menu.ImportExport", app);
		List<MToolBarElement> lmte = part.getToolbar().getChildren();
		HandledToolItemImpl htil = null;
		for (MToolBarElement mToolBarElement : lmte) {
			if (mToolBarElement
					.getElementId()
					.equals("at.medevit.emr.contacts.ui.contactselector.toolbar.handledtoolitem.filter"))
				htil = (HandledToolItemImpl) mToolBarElement;
		}
		if (htil != null) {
			MMenu elemMenu = htil.getMenu();
			// --- 2 ---
			// Found it hopefully, let's start the real work, simply add a new
			// item
			MDirectMenuItem mdi = MMenuFactory.INSTANCE.createDirectMenuItem();
			mdi.setLabel("Counter ");

			// --- 3 ---
			elemMenu.getChildren().add(mdi); // ConcurrentModificationException
			
		
		}*/
		
		
	}

}