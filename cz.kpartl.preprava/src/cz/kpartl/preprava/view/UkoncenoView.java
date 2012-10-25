package cz.kpartl.preprava.view;

import java.util.ArrayList;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.w3c.dom.events.EventTarget;

import cz.kpartl.preprava.dao.ObjednavkaDAO;
import cz.kpartl.preprava.dao.PozadavekDAO;
import cz.kpartl.preprava.dialog.NovaObjednavkaDialog;
import cz.kpartl.preprava.model.Objednavka;
import cz.kpartl.preprava.util.EventConstants;

public class UkoncenoView extends ObjednanoView {
	
	public static final String ID = "cz.kpartl.preprava.part.tablepartukoncene";

	@Inject
	public UkoncenoView(Composite parent, IStylingEngine styleEngine,
			ObjednavkaDAO objednavkaDAO, PozadavekDAO pozadavekDAO, IEclipseContext context, IEventBroker eventBroker, EPartService partService) {
		super(parent, styleEngine, objednavkaDAO, pozadavekDAO, context, partService);
		context.getParent().set(UkoncenoView.class, this);
		
	}
	
	@Override
	protected void createViewer(Composite parent, Object data) {
		final Label nadpisLabel = new Label(parent, SWT.NONE);
		nadpisLabel.setText("Pøehled ukonèených pøeprav");
		nadpisLabel.setFont(JFaceResources.getHeaderFont());
		
		superCreateViewer(parent, data);
		createMenuItems(headerMenu);
		
		

		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {

				Objednavka selectedObjednavka = (Objednavka) ((StructuredSelection) viewer
						.getSelection()).getFirstElement();
				if (selectedObjednavka == null)
					return;
				NovaObjednavkaDialog dialog = new NovaObjednavkaDialog(shell,
						context, selectedObjednavka, eventBroker);
				if (dialog.open() == Window.OK) {
					//refreshInputData();
				}

			}

		});
	}
	
	@Override
	protected Object getModelData() {		
		return objednavkaDAO.findByFaze(Objednavka.FAZE_UKONCENO);
	}
	
	
	
	
}
