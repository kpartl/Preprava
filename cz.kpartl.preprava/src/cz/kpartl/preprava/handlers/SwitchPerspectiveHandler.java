package cz.kpartl.preprava.handlers;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuContribution;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuContributions;
import org.eclipse.e4.ui.model.application.ui.menu.impl.HandledItemImpl;
import org.eclipse.e4.ui.model.application.ui.menu.impl.HandledToolItemImpl;
import org.eclipse.e4.ui.model.application.ui.menu.impl.MenuImpl;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
//import org.eclipse.e4.ui.workbench.swt.modeling.EMenuService;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.menus.UIElement;

import cz.kpartl.preprava.model.User;
import cz.kpartl.preprava.view.ObjednavkaDetailView;
import cz.kpartl.preprava.view.PozadavekDetailView;
import cz.kpartl.preprava.view.PozadavkyView;

import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;

public class SwitchPerspectiveHandler {

	@Inject
	IEclipseContext context;

	@SuppressWarnings("restriction")
	@Execute
	public void execute(MApplication app, EPartService partService,
			EModelService modelService, MWindow window,
			MPerspective activePerspective, Event event,
			IEclipseContext context2) {

		MenuItem importExportMenuItem = (MenuItem) app.getContext().get(
				"cz.kpartl.preprava.menu.ImportExport");

		if (importExportMenuItem == null) {
			for (MenuItem menuItem : ((MenuItem) event.widget).getParent()
					.getItems()) {
				if ("Import/Export".equals(menuItem.getText())) {
					importExportMenuItem = menuItem;
					app.getContext().set(
							"cz.kpartl.preprava.menu.ImportExport", menuItem);
				}
			}
		}

		// MPerspective activePerspective =
		// modelService.getPerspectiveFor(window);

		MPerspective prepravaPerspective = (MPerspective) modelService.find(
				"cz.kpartl.preprava.perspective.preprava", app);

		MPerspective administracePerspective = (MPerspective) modelService
				.find("cz.kpartl.preprava.perspective.administrace", app);

		// Object menuitem =menuService.
		// find("cz.kpartl.preprava.handledmenuitem.administrace", app);

		if (prepravaPerspective.equals(activePerspective)) {

			app.getContext().set("cz.kpartl.preprava.administrace", "1");
			context2.set("cz.kpartl.preprava.administrace", 1);
			

			/*
			 * partService.findPart(PozadavekDetailView.ID).setVisible(false);
			 * 
			 * partService.findPart(ObjednavkaDetailView.ID).setVisible(false);
			 */

			partService.switchPerspective(administracePerspective);

			((MenuItem) event.widget).setText("Požadavky/objednávky");

			//importExportMenuItem.setText("Import/Export");
			//importExportMenuItem.setEnabled(true);

		} else {

			/*
			 * final MPart pozadavekDetailView
			 * =partService.findPart(PozadavekDetailView.ID); if
			 * (pozadavekDetailView != null)
			 * pozadavekDetailView.setVisible(true);
			 * 
			 * final MPart objednavkaDetailView
			 * =partService.findPart(ObjednavkaDetailView.ID); if
			 * (objednavkaDetailView != null)
			 * objednavkaDetailView.setVisible(true);
			 */

			app.getContext().set("cz.kpartl.preprava.administrace", "0");
			context2.set("cz.kpartl.preprava.administrace",0);

			// menu.setVisible(false);

			partService.switchPerspective(prepravaPerspective);
			((MenuItem) event.widget).setText("Administrace");
			// ((MenuItem) event.widget).getParent().setVisible(false);
			final Menu menu = ((MenuItem) event.widget).getParent();

			//importExportMenuItem.setText(" ");
			//importExportMenuItem.setEnabled(false);

		}

	}

	@CanExecute
	public boolean canExecute() {

		return ((User) context.get(User.CONTEXT_NAME)).isAdministrator();
	}

	/*
	 * @Inject
	 * 
	 * @Optional public void partActivation(
	 * 
	 * @UIEventTopic(UIEvents.UILifeCycle.ACTIVATE) org.osgi.service.event.Event
	 * event, MApplication application) {
	 * 
	 * MPart activePart = (MPart) event
	 * .getProperty(UIEvents.EventTags.ELEMENT); IEclipseContext context =
	 * application.getContext(); if (activePart != null) {
	 * context.set("myactivePartId", activePart.getElementId()); } }
	 */

}