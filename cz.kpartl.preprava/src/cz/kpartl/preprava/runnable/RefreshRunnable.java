package cz.kpartl.preprava.runnable;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kpartl.preprava.util.EventConstants;

public class RefreshRunnable implements Runnable{
	
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	IEventBroker eventBroker;
	
	private int refreshInterval;
	
	public  RefreshRunnable(IEventBroker eventBroker, int refreshInterval){
		this.eventBroker = eventBroker;
		this.refreshInterval = refreshInterval;
		
	}

	@Override
	public void run() {
		
		while(true){
			try {
				logger.info("RefreshInterval = " + getRefreshInterval());
				Thread.sleep(getRefreshInterval());
				logger.info("Posilam REFRESH_VIEWERS");
				eventBroker.send(EventConstants.REFRESH_VIEWERS , "");
			} catch (InterruptedException e) {
				break;
			}
		}		
	}
	
	public void setRefreshInterval(int refreshInterval){
		this.refreshInterval = refreshInterval;
	}
	
	public int getRefreshInterval(){
		return refreshInterval;
	}

}
