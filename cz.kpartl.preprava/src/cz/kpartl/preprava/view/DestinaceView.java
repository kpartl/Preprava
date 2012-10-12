package cz.kpartl.preprava.view;

import java.text.SimpleDateFormat;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.JFaceResources;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
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
import cz.kpartl.preprava.sorter.TableViewerComparator;
import cz.kpartl.preprava.util.HibernateHelper;

public class DestinaceView extends AbstractTableView {

	private DestinaceDAO destinaceDAO;

	final Logger logger = LoggerFactory.getLogger(DestinaceView.class);

	@Inject
	@Optional
	protected ESelectionService selectionService;

	@Inject
	public DestinaceView(Composite parent,
			@Optional IStylingEngine styleEngine,
			@Optional IEclipseContext context) {
		super(styleEngine);
		this.context = context;
		context.set("cz.kpartl.preprava.view.DestinaceView", this);
		this.selectionService = selectionService;
		this.destinaceDAO = context.get(DestinaceDAO.class);
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
		viewer = new TableViewer(composite, SWT.MULTI | SWT.H_SCROLL
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

		col = createTableViewerColumn("PSÈ", 200, columnIndex++, "PSÈe");

		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return String.valueOf(((Destinace) element).getPSC());
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
		final MenuItem editItem = new MenuItem(parent, SWT.PUSH);
		editItem.setText("Editovat");
		editItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				Destinace selectedDestinace = (Destinace) ((StructuredSelection) viewer
						.getSelection()).getFirstElement();
				if (selectedDestinace == null)
					return;
				NovaDestinaceDialog dialog = new NovaDestinaceDialog(
						event.widget.getDisplay().getActiveShell(), context, selectedDestinace);
				if (dialog.open() == Window.OK) {
					refreshInputData();
				}
			}
		});

		final MenuItem smazatItem = new MenuItem(parent, SWT.PUSH);
		smazatItem.setText("Smazat");
		smazatItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				Destinace selectedDestinace = (Destinace) ((StructuredSelection) viewer
						.getSelection()).getFirstElement();
				boolean result = MessageDialog.openConfirm(event.widget
						.getDisplay().getActiveShell(),
						"Potvrzení smazání destinace",
						"Opravdu chcete smazat tuto destinaci?");
				if (result) {
					try {
						Transaction tx = HibernateHelper.getInstance()
								.beginTransaction();
						destinaceDAO.delete(selectedDestinace);
						tx.commit();
					} catch (Exception ex) {
						MessageDialog
								.openError(event.widget.getDisplay()
										.getActiveShell(),
										"Chyba pøi zápisu do databáze",
										"Pøi zápisu do databáze došlo k chybì, kontaktujte prosím tvùrce aplikace.");

						logger.error("Nelze vlozit/updatovat destinaci", ex);
					}
					refreshInputData();
				}
				;
			}
		});

	}

	@Override
	protected TableViewerComparator getComparator() {
		return new TableViewerComparator();
	}

}
