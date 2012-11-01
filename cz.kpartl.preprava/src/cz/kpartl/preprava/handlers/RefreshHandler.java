 
package cz.kpartl.preprava.handlers;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kpartl.preprava.util.EventConstants;

public class RefreshHandler {
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected cz.kpartl.preprava.util.HibernateHelper persistenceHelper;
	@Execute
	public void execute(IEventBroker eventBroker, IEclipseContext context) {		
		//persistenceHelper.getInstance().close();
		eventBroker.send(EventConstants.REFRESH_VIEWERS, "");
	}
		
}