package cz.kpartl.preprava.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import cz.kpartl.preprava.dao.DestinaceDAO;
import cz.kpartl.preprava.dialog.NovyPozadavekDialog;

public class NovyPozadavekHandler {
	
	@Execute
	public void execute(Shell parentShell, @Named("cz.kpartl.preprava.dao.DestinaceDAO") DestinaceDAO destinaceDAO){
		NovyPozadavekDialog dialog = new NovyPozadavekDialog(parentShell, destinaceDAO);
		dialog.create();
		
		if (dialog.open() == Window.OK) {
			System.out.println("OK");
		//TODO
		}
	
	}
	
	@CanExecute
	public boolean canExecute(){
		return true;
	}
}
