package cz.kpartl.preprava.dialog;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cz.kpartl.preprava.dao.DestinaceDAO;
import cz.kpartl.preprava.importer.DataImporter;
import cz.kpartl.preprava.util.EventConstants;
import cz.kpartl.preprava.util.HibernateHelper;

public class DestinaceImportDialog extends TitleAreaDialog {
	private DestinaceDAO destinaceDAO;
	HibernateHelper persistenceHelper;
	Shell parentShell;
	IEventBroker eventBroker;

	@Inject
	public DestinaceImportDialog(
			@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell,
			IEclipseContext context, IEventBroker eventBroker) {
		super(parentShell);
		this.parentShell = parentShell;
		this.eventBroker = eventBroker;
		this.destinaceDAO = context.get(DestinaceDAO.class);
		persistenceHelper = cz.kpartl.preprava.util.HibernateHelper
				.getInstance();
	}

	@Override
	protected void setShellStyle(int newShellStyle) {
		// super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.MAX);
		super.setShellStyle(newShellStyle);
	}

	@Override
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		setTitle("Import destinací");
		setMessage("Vyberte csv soubor");
		return contents;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {

		GridLayout layout = new GridLayout(2, false);
		layout.marginBottom = 5;
		layout.marginLeft = 5;
		layout.marginTop = 5;
		layout.marginRight = 5;

		parent.setLayout(layout);

		Label searchLabel = new Label(parent, SWT.NONE);
		searchLabel.setText("Importovat csv soubor");

		new Label(parent, SWT.NONE);

		final Text soubor = new Text(parent, SWT.BORDER);
		GridData gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false);
		gridData.widthHint = 300;
		soubor.setLayoutData(gridData);

		final Button souborButton = new Button(parent, SWT.PUSH);
		souborButton.setText("...");
		souborButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				FileDialog fileDialog = new FileDialog(parentShell, SWT.OPEN);
				fileDialog.setFilterExtensions(new String[] { "*.csv" });
				String souborPath = fileDialog.open();
				if (souborPath != null)
					soubor.setText(souborPath);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);

		final Button importButton = new Button(parent, SWT.PUSH);

		new Label(parent, SWT.NONE);

		final Text messageList = new Text(parent, SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.RESIZE);

		gridData = new GridData(SWT.BEGINNING, SWT.FILL, false, false);
		// gridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		gridData.horizontalSpan = 2;

		gridData.widthHint = 300;
		gridData.heightHint = 100;
		messageList.setLayoutData(gridData);

		importButton.setText("Importovat");
		importButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				setMessage("Importuji destinace", IMessageProvider.INFORMATION);

				final DataImporter dataImporter = new DataImporter(soubor
						.getText());
				Runnable job = new Runnable() {
					public void run() {
						dataImporter.importDestinace();
					}
				};

				BusyIndicator.showWhile(parent.getDisplay(), job);

				final String eol = System.getProperty("line.separator");
				StringBuffer messagesBuffer = new StringBuffer();
				setMessage("Import dokonèen", IMessageProvider.INFORMATION);
				for (String message : dataImporter.getMessages()) {
					// messageList.add(message.toString());
					messageList.append(eol);
					messageList.append(message);
				}
				;

				eventBroker.send(EventConstants.REFRESH_VIEWERS, "");
				// messageList.pack();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		return parent;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(380, 340);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {

		// Add a SelectionListener

	}

	protected Button createOkButton(Composite parent, int id, String label,
			boolean defaultButton) {
		// increment the number of columns in the button bar
		// ((GridLayout) parent.getLayout()).numColumns++;
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		button.setFont(JFaceResources.getDialogFont());
		button.setData(new Integer(id));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {

				okPressed();

			}
		});
		if (defaultButton) {
			Shell shell = parent.getShell();
			if (shell != null) {
				shell.setDefaultButton(button);
			}
		}
		setButtonLayoutData(button);
		return button;
	}

	@Override
	protected boolean isResizable() {
		return false;
	}

}
