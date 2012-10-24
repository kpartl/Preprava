package cz.kpartl.preprava.view;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kpartl.preprava.dao.DopravceDAO;

import cz.kpartl.preprava.dialog.NovyDopravceDialog;

import cz.kpartl.preprava.model.Dopravce;
import cz.kpartl.preprava.sorter.TableViewerComparator;
import cz.kpartl.preprava.util.EventConstants;
import cz.kpartl.preprava.util.HibernateHelper;
import cz.kpartl.preprava.util.Login;

public class DopravceView extends AbstractTableView {

	public static final String ID = "cz.kpartl.preprava.part.dopravce";

	private DopravceDAO dopravceDAO;

	private Shell shell;

	final Logger logger = LoggerFactory.getLogger(DopravceView.class);

	@Inject
	@Optional
	protected ESelectionService selectionService;

	@Inject
	public DopravceView(Composite parent, @Optional IStylingEngine styleEngine,
			@Optional IEclipseContext context, IEventBroker eventBroker) {
		super(styleEngine);
		this.context = context;
		context.set(ID, this);
		this.selectionService = selectionService;
		this.dopravceDAO = context.get(DopravceDAO.class);
		this.eventBroker = eventBroker;
		shell = parent.getShell();
		createPartControl(parent);
	}

	protected Object getModelData() {
		return dopravceDAO.findAll();
	}

	protected void createViewer(Composite parent, Object data) {
		final Label nadpisLabel = new Label(parent, SWT.NONE);
		nadpisLabel.setText("Pøehled dopravcù");
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
				Dopravce selectedDopravce = (Dopravce) ((StructuredSelection) viewer
						.getSelection()).getFirstElement();
				if (selectedDopravce == null)
					return;
				NovyDopravceDialog dialog = new NovyDopravceDialog(shell,
						context, selectedDopravce, eventBroker);
				if (dialog.open() == Window.OK) {
					//refreshInputData();
				}

			}

		});
	}// This will create the columns for the table

	protected void createColumns(final Composite parent) {
		TableViewerColumn col = createTableViewerColumn("Název", 200,
				columnIndex++, "Název dopravce");

		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Dopravce) element).getNazev();
			}
		});

	}

	protected void createMenuItems(Menu parent) {
		final MenuItem newItem = new MenuItem(parent, SWT.PUSH);
		newItem.setText("Vytvoøit nového dopravce");
		newItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				new NovyDopravceDialog(event.widget.getDisplay()
						.getActiveShell(), context, eventBroker).open();

			}
		});
		//new MenuItem(parent, SWT.SEPARATOR);

		final MenuItem editItem = new MenuItem(parent, SWT.PUSH);
		editItem.setText("Editovat dopravce");
		editItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				editSelectedDopravce();
			}
		});

		//new MenuItem(parent, SWT.SEPARATOR);

		final MenuItem smazatItem = new MenuItem(parent, SWT.PUSH);
		smazatItem.setText("Smazat dopravce");
		smazatItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				deleteSelectedDopravce();
			}
		});
		
		viewer.getTable().addMouseListener(new MouseAdapter(){
			public void mouseDown(MouseEvent event) {
				if(event.button == 3 && viewer.getSelection().isEmpty()){ //prave tlacitko mysi
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
	
	public void editSelectedDopravce() {
		boolean b = viewer.getSelection().isEmpty();
		Dopravce selectedDopravce = (Dopravce) ((StructuredSelection) viewer
				.getSelection()).getFirstElement();
		if (selectedDopravce == null) {
			return;
		}
		new NovyDopravceDialog(shell, context,
				selectedDopravce, eventBroker).open();	
		
		eventBroker.post(EventConstants.REFRESH_VIEWERS, "");
	}
	public void deleteSelectedDopravce(){
		Dopravce selectedDopravce = (Dopravce) ((StructuredSelection) viewer
				.getSelection()).getFirstElement();
		boolean result = MessageDialog.openConfirm(shell,
				"Potvrzení smazání dopravce",
				"Opravdu chcete smazat tohoto dopravce?");
		if (result) {
			try {
				Transaction tx = HibernateHelper.getInstance()
						.beginTransaction();
				dopravceDAO.delete(selectedDopravce);
				tx.commit();
				
				eventBroker.post(EventConstants.REFRESH_VIEWERS, "");
				
			} catch (Exception ex) {
				MessageDialog
						.openError(shell,
								"Chyba pøi zápisu do databáze",
								"Pøi zápisu do databáze došlo k chybì, kontaktujte prosím tvùrce aplikace.");

				logger.error("Nelze vložit/upravit dopravce", ex);
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
		
		novyMenuItem.setTooltip("Vytvoøit nového dopravce");
		editMenuItem.setTooltip("Editovat dopravce");
		smazatMenuItem.setTooltip("Smazat dopravce");
		
		super.setFocus();
	}

	@Override
	protected TableViewerComparator getComparator() {
		return new TableViewerComparator();
	}
	
	@Inject
	@Optional
	void refreshInput(@UIEventTopic(EventConstants.REFRESH_VIEWERS) Dopravce d) {
		viewer.setInput(getModelData());
	}
}