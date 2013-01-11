package cz.kpartl.preprava.view;

import java.text.SimpleDateFormat;
import java.util.List;

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
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.part.ViewPart;
import org.hibernate.Transaction;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kpartl.preprava.dao.DestinaceDAO;
import cz.kpartl.preprava.dialog.NovaDestinaceDialog;
import cz.kpartl.preprava.dialog.NovyPozadavekDialog;
import cz.kpartl.preprava.model.Destinace;
import cz.kpartl.preprava.model.Objednavka;
import cz.kpartl.preprava.model.Pozadavek;
import cz.kpartl.preprava.sorter.DestinaceTableViewerComparator;
import cz.kpartl.preprava.sorter.TableViewerComparator;
import cz.kpartl.preprava.util.EventConstants;
import cz.kpartl.preprava.util.HibernateHelper;
import cz.kpartl.preprava.util.Login;

public class DestinaceView extends AbstractTableView {

	public static final String ID = "cz.kpartl.preprava.part.destinace";

	private DestinaceDAO destinaceDAO;

	private Shell shell;

	final Logger logger = LoggerFactory.getLogger(DestinaceView.class);

	@Inject
	@Optional
	protected ESelectionService selectionService;

	@Inject
	public DestinaceView(Composite parent,
			@Optional IStylingEngine styleEngine,
			@Optional IEclipseContext context, IEventBroker eventBroker) {
		super(styleEngine);
		this.context = context;
		this.eventBroker = eventBroker;
		context.set("cz.kpartl.preprava.view.DestinaceView", this);
		this.selectionService = selectionService;
		this.destinaceDAO = context.get(DestinaceDAO.class);
		shell = parent.getShell();
		createPartControl(parent);
	}

	protected Object getModelData() {
		return destinaceDAO.findAll();
	}

	protected void createViewer(Composite parent, Object data) {
		final Label nadpisLabel = new Label(parent, SWT.NONE);
		nadpisLabel.setText("Pøehled destinací");
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
				Destinace selectedDestinace = (Destinace) ((StructuredSelection) viewer
						.getSelection()).getFirstElement();
				if (selectedDestinace == null)
					return;
				refreshDestinace(selectedDestinace);
				new NovaDestinaceDialog(shell,
						context, selectedDestinace, eventBroker).open();				
			}

		});
	}

	// This will create the columns for the table
	protected void createColumns(final Composite parent) {
		TableViewerColumn col = createTableViewerColumn("Název", 200,
				columnIndex++, "Název destinace");

		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Destinace) element).getNazev();
			}
		});

		col = createTableViewerColumn("Èíslo", 70, columnIndex++,
				"Èíslo destinace");

		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return String.valueOf(((Destinace) element).getCislo());
			}
		});

		col = createTableViewerColumn("Ulice", 200, columnIndex++, "Ulice");

		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Destinace) element).getUlice();
			}
		});

		col = createTableViewerColumn("Mìsto", 200, columnIndex++, "Mìsto");

		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Destinace) element).getMesto();
			}
		});

		col = createTableViewerColumn("PSÈ", 200, columnIndex++, "PSÈ");

		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {				
				return ((Destinace) element).getPSC() != null ? String.valueOf(((Destinace) element).getPSC()) : "";
			}
		});
		col = createTableViewerColumn("Kontaktní osoba", 200, columnIndex++,
				"Kontaktní osoba");

		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Destinace) element).getKontaktni_osoba();
			}
		});
		col = createTableViewerColumn("Kontakt", 200, columnIndex++,
				"Kontakt na kontaktní osobu");

		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Destinace) element).getKontakt();
			}
		});

	}

	protected void createMenuItems(Menu parent) {
		newItem = new MenuItem(parent, SWT.PUSH);
		newItem.setText("Vytvoøit novou destinaci");
		newItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (new NovaDestinaceDialog(event.widget.getDisplay()
						.getActiveShell(), context, eventBroker).open() == Window.OK) {
					// refreshInputData();
				}
			}
		});
		// new MenuItem(parent, SWT.SEPARATOR);

		editItem = new MenuItem(parent, SWT.PUSH);
		editItem.setText("Editovat destinaci");
		editItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				editSelectedDestinace();
			}
		});

		// new MenuItem(parent, SWT.SEPARATOR);

		final MenuItem smazatItem = new MenuItem(parent, SWT.PUSH);
		smazatItem.setText("Smazat destinaci");
		smazatItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				deleteSelectedDestinace();
			}
		});

		newItem.setImage((Image) context.get(Login.ADD_ICON));
		smazatItem.setImage((Image) context.get(Login.DELETE_ICON));
		editItem.setImage((Image) context.get(Login.EDIT_ICON));
	}

	@Focus
	public void setFocus() {						
		novyMenuItem.setVisible(true);
		editMenuItem.setVisible(true);
		smazatMenuItem.setVisible(true);
		prevestMenuItem.setVisible(false);
		tisknoutMenuItem.setVisible(false);
		
		
		novyMenuItem.setTooltip("Vytvoøit novou destinaci");
		editMenuItem.setTooltip("Editovat destinaci");
		smazatMenuItem.setTooltip("Smazat destinaci");
		
		super.setFocus();
	}

	public void editSelectedDestinace() {
		Destinace selectedDestinace = (Destinace) ((StructuredSelection) viewer
				.getSelection()).getFirstElement();
		if (selectedDestinace != null){
			refreshDestinace(selectedDestinace);
			new NovaDestinaceDialog(shell, context, selectedDestinace,
					eventBroker).open();
			//eventBroker.send(EventConstants.REFRESH_VIEWERS, "");
		}
	}

	public void deleteSelectedDestinace() {
		Destinace selectedDestinace = (Destinace) ((StructuredSelection) viewer
				.getSelection()).getFirstElement();
		boolean result = MessageDialog.openConfirm(shell,
				"Potvrzení smazání destinace",
				"Opravdu chcete smazat tuto destinaci?");
		if (result) {		
				Transaction tx = HibernateHelper.getInstance()
						.beginTransaction();
				try{
				destinaceDAO.delete(selectedDestinace);
				tx.commit();
				
				eventBroker.send(EventConstants.REFRESH_VIEWERS, "");
				
			} catch (Exception ex) {				
				MessageDialog
						.openError(shell,
								"Chyba pøi zápisu do databáze",
								"Nepodaøilo se smazat destinaci.");

				logger.error("Nelze smazatt destinaci", ex);
				tx.rollback();
			}
			
		}
		;
	}

	@Override
	protected TableViewerComparator getComparator() {
		return new DestinaceTableViewerComparator();
	}
	
	@Inject
	@Optional
	void refreshInput(@UIEventTopic(EventConstants.REFRESH_VIEWERS) String s) {
		List<Destinace>list = (List<Destinace>) getModelData();
		for(Destinace d: list){
			refreshDestinace(d);
		}
		viewer.setInput(getModelData());
	}
	
	protected void refreshDestinace(Destinace destinace){
		destinaceDAO.refreshSession(destinace);
	}

}
