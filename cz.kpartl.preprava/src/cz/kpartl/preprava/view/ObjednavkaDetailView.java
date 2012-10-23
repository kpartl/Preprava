package cz.kpartl.preprava.view;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.ui.part.ViewPart;

public class ObjednavkaDetailView extends ViewPart {

	@Inject
	public ObjednavkaDetailView(Composite parent, Shell parentShell,
			IEclipseContext context, IEventBroker eventBroker) {
		super();

	}

	public static final String ID = "cz.kpartl.preprava.part.objednavkaDetailPart";

	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(4, false);
		layout.horizontalSpacing = 30;
		parent.setLayout(layout);

		
		Label datum = new Label(parent, SWT.NONE);

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
