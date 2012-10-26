package cz.kpartl.preprava.dialog;

import java.awt.event.PaintEvent;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
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

	private Image loginIcon;
	
	Shell shell;

	public LoginDialog(Shell parentShell, Image loginIcon) {
		super(parentShell);
		this.loginIcon = loginIcon;
		

	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	protected Control createDialogArea(Composite parent) {
		Composite comp = (Composite) super.createDialogArea(parent);
		final GridLayout gridLayout = new GridLayout();
		comp.setLayout(gridLayout);
		//comp.setBackgroundImage(loginIcon);

	
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL,
		true, true);
		
		
		comp.setLayoutData(layoutData); 
		comp.redraw();
		
		Canvas canvas = new Canvas(comp, SWT.FILL);
		 layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.horizontalSpan =2;
		canvas.setLayoutData(layoutData);
		canvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(org.eclipse.swt.events.PaintEvent e) {
				e.gc.drawImage(loginIcon, 0, 0);

			}
		});
//if(true) return comp;
		
		Label usernameLabel = new Label(comp, SWT.RIGHT);
		usernameLabel.setText("U�ivatelsk� jm�no: ");

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
		getButton(IDialogConstants.OK_ID).setText("P�ihl�sit");
		getButton(IDialogConstants.CANCEL_ID).setText("Zru�it");
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("P�ihl�en� do aplikace P�eprava");
		//shell.setSize(300, 125);
		//setShellStyle( SWT.APPLICATION_MODAL); 		
		shell.setSize(260,410);
	}

	@Override
	protected void okPressed() {
		username = usernameField.getText();
		password = passwordField.getText();

		super.okPressed();
	}

}