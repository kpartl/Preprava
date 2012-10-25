package cz.kpartl.preprava.view;

import java.util.ArrayList;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kpartl.preprava.dao.UserDAO;
import cz.kpartl.preprava.dialog.NovyUzivatelDialog;
import cz.kpartl.preprava.model.Objednavka;
import cz.kpartl.preprava.model.Pozadavek;
import cz.kpartl.preprava.model.User;
import cz.kpartl.preprava.sorter.TableViewerComparator;
import cz.kpartl.preprava.sorter.UzivatelTableViewerComparator;
import cz.kpartl.preprava.util.EventConstants;
import cz.kpartl.preprava.util.HibernateHelper;
import cz.kpartl.preprava.util.Login;
import cz.kpartl.preprava.util.MyMessageDialog;

public class UzivatelView extends AbstractTableView {

	public static final String ID = "cz.kpartl.preprava.part.uzivatele";

	private UserDAO userDAO;

	private Shell shell;

	final Logger logger = LoggerFactory.getLogger(UzivatelView.class);

	@Inject
	@Optional
	protected ESelectionService selectionService;

	@Inject
	public UzivatelView(Composite parent, @Optional IStylingEngine styleEngine,
			@Optional IEclipseContext context, IEventBroker eventBroker) {
		super(styleEngine);
		this.context = context;
		this.eventBroker = eventBroker;
		context.set(ID, this);
		this.selectionService = selectionService;
		this.userDAO = context.get(UserDAO.class);
		shell = parent.getShell();
		createPartControl(parent);
	}

	protected Object getModelData() {
		return userDAO.findAll();
	}

	protected void createViewer(Composite parent, Object data) {
		final Label nadpisLabel = new Label(parent, SWT.NONE);
		nadpisLabel.setText("P�ehled u�ivatel�");
		nadpisLabel.setFont(JFaceResources.getHeaderFont());
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));

		// Add TableColumnLayout
		layout = new TableColumnLayout();
		composite.setLayout(layout);
		// Define the TableViewer
		viewer = new TableViewer(composite, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		headerMenu = new Menu(parent.getShell(), SWT.POP_UP);
		viewer.getTable().setMenu(headerMenu);

		// Create the columns
		createColumns(parent);

		// Make lines and make header visible
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// Set the ContentProvider
		viewer.setContentProvider(ArrayContentProvider.getInstance());

		viewer.setInput(data);

		// Make the selection available to other Views
		// getSite().setSelectionProvider(viewer); // HELE JA NEVIM NA CO TO JE

		if (styleEngine != null) {
			styleEngine.setClassname(this.viewer.getControl(), "pozadavkyList");
		}

		// Layout the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);

		createMenuItems(headerMenu);
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				User selectedUser = (User) ((StructuredSelection) viewer
						.getSelection()).getFirstElement();
				if (selectedUser == null)
					return;
				if (selectedUser.getUsername().equals("admin")) {
					MessageDialog.openError(shell,
							"Nelze editovat tohoto u�ivatele",
							"U�ivatele admin nelze editovat ani smazat");
				} else {
					new NovyUzivatelDialog(shell,
							context, selectedUser, eventBroker).open();					
				}

			}

		});
	}// This will create the columns for the table

	protected void createColumns(final Composite parent) {
		TableViewerColumn col = createTableViewerColumn("U�ivatelsk� jm�no",
				200, columnIndex++, "U�ivatelsk� jm�no");

		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((User) element).getUsername();
			}
		});

		col = createTableViewerColumn("Administr�tor", 120, columnIndex++,
				"M� u�ivatel administr�torsk� opr�vn�n�?");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return "";
			}

			@Override
			public Image getImage(Object element) {
				if (((User) element).isAdministrator()) {
					return checkedImage;
				}
				return uncheckedImage;
			}
		});

	}

	protected void createMenuItems(Menu parent) {
		final MenuItem newItem = new MenuItem(parent, SWT.PUSH);
		newItem.setText("Vytvo�it nov�ho u�ivatele");
		newItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (new NovyUzivatelDialog(shell, context, eventBroker).open() == Window.OK) {
					// refreshInputData();
				}
			}
		});
		// new MenuItem(parent, SWT.SEPARATOR);

		final MenuItem editItem = new MenuItem(parent, SWT.PUSH);
		editItem.setText("Editovat u�ivatele");
		editItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				editSelectedUzivatele();
			}
		});

		// new MenuItem(parent, SWT.SEPARATOR);

		final MenuItem smazatItem = new MenuItem(parent, SWT.PUSH);
		smazatItem.setText("Smazat u�ivatele");
		smazatItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				deleteSelectedUzivatel();
			}
		});

		viewer.getTable().addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent event) {
				if (event.button == 3 && viewer.getSelection().isEmpty()) { // prave
																			// tlacitko
																			// mysi
					headerMenu.setVisible(false);
					viewer.getTable().getMenu().setEnabled(false);
					headerMenu.setEnabled(false);
				}
			}
		});

		newItem.setImage((Image) context.get(Login.ADD_ICON));
		smazatItem.setImage((Image) context.get(Login.DELETE_ICON));
		editItem.setImage((Image) context.get(Login.EDIT_ICON));
	}

	public void editSelectedUzivatele() {
		final boolean b = viewer.getSelection().isEmpty();
		User selectedUser = (User) ((StructuredSelection) viewer.getSelection())
				.getFirstElement();
		if (selectedUser == null) {
			return;
		}

		if (selectedUser.getUsername().equals("admin")) {
			MessageDialog.openError(shell, "Nelze editovat tohoto u�ivatele",
					"U�ivatele admin nelze editovat");
			return;
		}

		new NovyUzivatelDialog(shell, context, selectedUser, eventBroker).open();
		//eventBroker.post(EventConstants.REFRESH_VIEWERS, "");

	}

	public void deleteSelectedUzivatel() {
		final User selectedUzivatel = (User) ((StructuredSelection) viewer
				.getSelection()).getFirstElement();
		if (selectedUzivatel.getUsername().equals("admin")) {
			MessageDialog.openError(shell, "Nelze smazat tohoto u�ivatele",
					"U�ivatele admin nelze smazat");
			return;
		}
		final String message = "Opravdu chcete smazat u�ivatele ".concat(selectedUzivatel
				.getUsername().concat("?"));
		
		if (new MyMessageDialog(shell, "Potvrzen� smaz�n� u�ivatele", message).confirm()) {
			try {
				Transaction tx = HibernateHelper.getInstance()
						.beginTransaction();
				userDAO.delete(selectedUzivatel);
				tx.commit();
				eventBroker.post(EventConstants.REFRESH_VIEWERS, "");
			} catch (Exception ex) {
				MessageDialog
						.openError(shell, "Chyba p�i z�pisu do datab�ze",
								"P�i z�pisu do datab�ze do�lo k chyb�, kontaktujte pros�m tv�rce aplikace.");

				logger.error("Nelze vlo�it/upravit u�ivatele", ex);
			}
			
		}
		;
	}

	@Focus
	public void setFocus() {
		novyMenuItem.setVisible(true);
		editMenuItem.setVisible(true);
		smazatMenuItem.setVisible(true);
		prevestMenuItem.setVisible(false);

		novyMenuItem.setTooltip("Vytvo�it nov�ho u�ivatele");
		editMenuItem.setTooltip("Editovat u�ivatele");
		smazatMenuItem.setTooltip("Smazat u�ivatele");

		super.setFocus();
	}

	@Override
	protected TableViewerComparator getComparator() {
		return new UzivatelTableViewerComparator();
	}
	
	@Inject
	@Optional
	void refreshInput(@UIEventTopic(EventConstants.REFRESH_VIEWERS) String u) {
		viewer.setInput(getModelData());		
	}
}
