 
package cz.kpartl.preprava.handlers;

import java.util.Date;
import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.di.AboutToHide;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;

public class ImportExportMenu {
	@AboutToShow
	public void aboutToShow(List<MMenuElement> items) {
		MDirectMenuItem dynamicItem = MMenuFactory.INSTANCE
	            .createDirectMenuItem();
	    dynamicItem.setLabel("Dynamic Menu Item (" + new Date() + ")");
	    dynamicItem.setContributorURI("platform:/cz.kpartl.preprava");
	    dynamicItem
	            .setContributionURI("bundleclass://cz.kpartl.preprava/cz.kpartl.preprava.handlers.ImportDestinaciHandler");   
	        items.add(dynamicItem);
	}
	
	
	@AboutToHide
	public void aboutToHide(List<MMenuElement> items) {
		System.out.println("ÄboutToHide");
	}
		
}