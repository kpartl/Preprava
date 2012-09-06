package cz.kpartl.preprava.ui.part;

import javax.inject.Inject;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class TablePart {
	
	Label label;
	
	@PostConstruct
	  public void createControls(Composite parent) {
			label = new Label(parent,SWT.NONE);
			System.out.println("TodoOverviewPart postConstruct called");
		} 
	  
	  @PreDestroy
		public void dispose(){
			
		}
	  
	  @Focus
	  private void setFocus() {
	  	label.setFocus();
	  	System.out.println("TablePart setFocus called");
	  } 

}
