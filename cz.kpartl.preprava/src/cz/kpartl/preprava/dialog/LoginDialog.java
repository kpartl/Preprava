package cz.kpartl.preprava.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class LoginDialog extends Dialog {
  private static final int RESET_ID = IDialogConstants.NO_TO_ALL_ID + 1;

  private Text usernameField;  

  private Text passwordField;
  
  private String username;
  
  private String password;
  

  public LoginDialog(Shell parentShell) {
    super(parentShell);
    
  }
  
  public String getUsername() 
  {
	  return username;	  
  }
  
  public String getPassword()
  {
	  return password;	  
  }

  protected Control createDialogArea(Composite parent) {
    Composite comp = (Composite) super.createDialogArea(parent);

    GridLayout layout = (GridLayout) comp.getLayout();
    layout.numColumns = 2;

    Label usernameLabel = new Label(comp, SWT.RIGHT);
    usernameLabel.setText("Uživatelské jméno: ");

    usernameField = new Text(comp, SWT.SINGLE | SWT.BORDER);
    GridData data = new GridData(GridData.FILL_HORIZONTAL);
    usernameField.setLayoutData(data);

    Label passwordLabel = new Label(comp, SWT.RIGHT);
    passwordLabel.setText("Heslo: ");

    passwordField = new Text(comp, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
    data = new GridData(GridData.FILL_HORIZONTAL);
    passwordField.setLayoutData(data);

    return comp;
  }

  
  @Override
protected void createButtonsForButtonBar(Composite parent) {	
	super.createButtonsForButtonBar(parent);
	getButton(IDialogConstants.OK_ID).setText("Pøihlásit");
	getButton(IDialogConstants.CANCEL_ID).setText("Zrušit");
}

  @Override
protected void configureShell(Shell shell) {
      super.configureShell(shell);
      shell.setText("Pøihlášení uživatele do aplikace Pøeprava"); 
      shell.setSize(300, 125);
      setShellStyle(SWT.RESIZE);
   }

@Override
protected void okPressed() {
	username = usernameField.getText();
	password = passwordField.getText();
	
	super.okPressed();
}
  
  
}