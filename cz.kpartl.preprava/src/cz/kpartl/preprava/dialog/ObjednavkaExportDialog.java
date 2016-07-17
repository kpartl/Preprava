package cz.kpartl.preprava.dialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cz.kpartl.preprava.calendar.SWTCalendarEvent;
import cz.kpartl.preprava.calendar.SWTCalendarListener;
import cz.kpartl.preprava.exporter.DataExporter;
import cz.kpartl.preprava.util.EventConstants;
import cz.kpartl.preprava.util.HibernateHelper;
import cz.kpartl.preprava.util.Login;

public class ObjednavkaExportDialog extends TitleAreaDialog {
	
	HibernateHelper persistenceHelper;
	Shell parentShell;
	IEventBroker eventBroker;
	protected IEclipseContext context;
	Text datumText;

	public ObjednavkaExportDialog(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell,
			IEclipseContext context, IEventBroker eventBroker) {
		super(parentShell);
		this.parentShell = parentShell;
		this.eventBroker = eventBroker;
		persistenceHelper = cz.kpartl.preprava.util.HibernateHelper
				.getInstance();
		this.context = context;
	}
	
	@Override
	protected void setShellStyle(int newShellStyle) { 
		super.setShellStyle(newShellStyle);
	}

	@Override
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		setTitle("Export objednávek");
		setMessage("Zvolte název csv souboru a poèáteèní datum");		
		return contents;
	}
	

	@Override
	protected Control createDialogArea(final Composite parent) {

		GridLayout layout = new GridLayout(3, false);
		layout.marginBottom = 5;
		layout.marginLeft = 5;
		layout.marginTop = 5;
		layout.marginRight = 5;

		parent.setLayout(layout);

		Label searchLabel = new Label(parent, SWT.NONE);
		searchLabel.setText("Cesta k csv souboru");

		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);

		final Text soubor = new Text(parent, SWT.BORDER);
		GridData gridData = new GridData(SWT.BEGINNING, SWT.CENTER, true,
				false);
		gridData.widthHint = 300;
		gridData.horizontalSpan = 2;
		soubor.setLayoutData(gridData);

		final Button souborButton = new Button(parent, SWT.PUSH);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gridData.widthHint = 70;
		souborButton.setLayoutData(gridData);
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
		
		Label datumLabel = new Label(parent, SWT.NONE);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false);
		datumLabel.setLayoutData(gridData);
		datumLabel.setText("Poèáteèní datum");
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);

		datumText = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		gridData.widthHint = 80;
		datumText.setLayoutData(gridData);

		Button datumButton = new Button(parent, SWT.PUSH);
		datumButton.setImage((Image) context.get(Login.CALENDAR_ICON));
		gridData = new GridData(SWT.BEGINNING, SWT.FILL, false, false);
		gridData.heightHint = 15;
		gridData.widthHint = 80;
		datumButton.setLayoutData(gridData);;
		datumButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				getCalendarDialog(datumText).open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				getCalendarDialog(datumText).open();
			}
		});

		new Label(parent, SWT.NONE);

		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);
		
		final Button importButton = new Button(parent, SWT.PUSH);
		
		gridData = new GridData(SWT.BEGINNING, SWT.FILL, false, false);
		gridData.widthHint = 80;
		final Button closeButton = new Button(parent, SWT.PUSH);
		closeButton.setLayoutData(gridData);

		gridData = new GridData(SWT.BEGINNING, SWT.FILL, false, false);
		gridData.horizontalSpan = 2;

		gridData.widthHint = 290;
		gridData.heightHint = 20;
		
		closeButton.setText("Zavøít");
		
		closeButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				close();	
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
				
			}
		});

		importButton.setText("Exportovat");
		gridData = new GridData(SWT.BEGINNING, SWT.FILL, false, false);
		gridData.widthHint = 80;
		importButton.setLayoutData(gridData);
		importButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				setMessage("Exportuji objednávky", IMessageProvider.INFORMATION);

				final DataExporter dataExporter = new DataExporter();
				Runnable job = new Runnable() {
					public void run() {
						final Date date = getDate();
						if (date != null) {
							if (!soubor.getText().isEmpty()) {
								dataExporter.exportObjednavky(date, soubor.getText());
							} else {
								setMessage("Vložte název výstupního souboru.");
							}
						} else {
							//dataExporter.getMessages().add("Chybnì zadané datum");
							setMessage("Chybnì zadané datum.", IMessageProvider.ERROR);
						}
					}
				};

				BusyIndicator.showWhile(parent.getDisplay(), job);

				final String eol = System.getProperty("line.separator");
				if (dataExporter.getMessages().isEmpty() && dataExporter.getPocetVyexportovanychObj() > -1) {
					setMessage("Export dokonèen, vyexportováno záznamù: " + dataExporter.getPocetVyexportovanychObj(),  IMessageProvider.INFORMATION);
				} else {
					setMessage(getMessage(), IMessageProvider.ERROR);
					for (String message : dataExporter.getMessages()) {
						setMessage(getMessage() + " " + message);
						//messageList.append(eol);
						//messageList.append(message);
					}
				}
				;

				eventBroker.send(EventConstants.REFRESH_VIEWERS, "");
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
		return new Point(380, 300);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
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

	private SWTCalendarDialog getCalendarDialog(final Text text) {
		SWTCalendarDialog calendarDialog = new SWTCalendarDialog(this
				.getShell().getDisplay());
		calendarDialog.addDateChangedListener(new SWTCalendarListener() {
			public void dateChanged(SWTCalendarEvent calendarEvent) {
				text.setText(new SimpleDateFormat("dd.MM.yyyy")
						.format(calendarEvent.getCalendar().getTime()));
			}
		});

		return calendarDialog;
	}
	
	private java.util.Date getDate() {
		try {
			return new SimpleDateFormat("dd.MM.yyyy").parse( datumText.getText());
		} catch (ParseException e) {
			return null;
		}
	}
}
