 
package cz.kpartl.preprava.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

import cz.kpartl.preprava.util.EventConstants;

public class RefreshHandler {
	protected cz.kpartl.preprava.util.HibernateHelper persistenceHelper;
	@Execute
	public void execute(IEventBroker eventBroker) {
		persistenceHelper.getInstance().close();
		eventBroker.send(EventConstants.REFRESH_VIEWERS, "");
	}
		
}