package cz.kpartl.preprava.util;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

public class MyMessageDialog extends MessageDialog {
	
	public MyMessageDialog(Shell parentShell){
		this(parentShell, "","");
	}

	public MyMessageDialog(Shell parentShell, String dialogTitle,
			 String dialogMessage) {
		super(parentShell, dialogTitle, null, dialogMessage,
				SWT.NONE, new String[]{"OK", "Zrušit"}, 0);
		
	}
	
	  public boolean confirm(){		  
		  return this.open() == 1;
	  } 

}
