package cz.kpartl.preprava.dialog;

import javax.inject.Inject;

import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import javax.inject.Named;

import cz.kpartl.preprava.model.Pozadavek;

public class NovyPozadavekDialog extends TitleAreaDialog {

	private Text text1;
	private Text text2;
	private Pozadavek pozadavek;
	private Button button1;
	private Combo combo1;

	public Pozadavek getPozadavek() {
		return pozadavek;
	}

	@Inject
	public NovyPozadavekDialog(
			@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		setTitle("Vytvoøení / editace požadavku na pøepravu");
		setMessage("Please enter the data of the new person",
				IMessageProvider.INFORMATION);
		return contents;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);
		Label label1 = new Label(parent, SWT.NONE);
		label1.setText("First Name");
		text1 = new Text(parent, SWT.BORDER);
		return parent;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		((GridLayout) parent.getLayout()).numColumns++;

		Button button = new Button(parent, SWT.PUSH);
		button.setText("OK");
		button.setFont(JFaceResources.getDialogFont());
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (text1.getText().length() != 0) {
					pozadavek = new Pozadavek();
					close();

				} else {
					setErrorMessage("Please enter all data");
				}
			}
		});
	}

}
