package cz.kpartl.preprava.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.application.WorkbenchAdvisor;


public class SwitchPerspectiveHandler {
	
	
	
	@Execute
	public void execute() {
		try {
			ResourcePlugin.
			IWorkbench workbench = PlatformUI.getWorkbench();
			/*MyPlugin.getDefault().getActiveWorkbenchWindow().getActivePage()
					.getPerspective();*/
			if (workbench.getActiveWorkbenchWindow().getActivePage()
					.getPerspective() != null && workbench.getActiveWorkbenchWindow().getActivePage()
							.getPerspective().getId().equals(
					"cz.kpartl.preprava.perspective.preprava")) {
				PlatformUI.getWorkbench().showPerspective(
						"cz.kpartl.preprava.perspective.administrace",
						PlatformUI.getWorkbench().getActiveWorkbenchWindow());
			} else {
				PlatformUI.getWorkbench().showPerspective(
						"cz.kpartl.preprava.perspective.preprava",
						PlatformUI.getWorkbench().getActiveWorkbenchWindow());
			}

		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
	}

}