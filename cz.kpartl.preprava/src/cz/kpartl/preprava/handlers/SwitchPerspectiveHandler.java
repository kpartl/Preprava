package cz.kpartl.preprava.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class SwitchPerspectiveHandler {	

	@SuppressWarnings("restriction")
	@Execute
	public void execute(MApplication app, EPartService partService,
			EModelService modelService, MWindow window, MPerspective activePerspective) {	
		
		//MPerspective activePerspective = modelService.getPerspectiveFor(window);

		MPerspective prepravaPerspective = (MPerspective) modelService.find(
				"cz.kpartl.preprava.perspective.preprava", app);

		MPerspective administracePerspective = (MPerspective) modelService
				.find("cz.kpartl.preprava.perspective.administrace", app);

		if (prepravaPerspective.equals(activePerspective))
			partService.switchPerspective(administracePerspective);
		else
			partService.switchPerspective(prepravaPerspective);
	}

}