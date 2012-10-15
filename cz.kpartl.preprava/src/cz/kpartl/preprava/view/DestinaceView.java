package cz.kpartl.preprava.view;

import java.text.SimpleDateFormat;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
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
import cz.kpartl.preprava.sorter.TableViewerComparator;
import cz.kpartl.preprava.util.HibernateHelper;

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
			@Optional IEclipseContext context) {
		super(styleEngine);
		this.context = context;
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
		nadpisLabel.setText("P�ehled destinac�");
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
				NovaDestinaceDialog dialog = new NovaDestinaceDialog(shell,
						context, selectedDestinace);
				if (dialog.open() == Window.OK) {
					refreshInputData();
				}

			}

		});
	}

	// This will create the columns for the table
	protected void createColumns(final Composite parent) {
		TableViewerColumn col = createTableViewerColumn("N�zev", 200,
				columnIndex++, "N�zev destinace");

		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Destinace) element).getNazev();
			}
		});

		col = createTableViewerColumn("��slo", 70, columnIndex++,
				"��slo destinace");

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

		col = createTableViewerColumn("M�sto", 200, columnIndex++, "M�sto");

		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Destinace) element).getMesto();
			}
		});

		col = createTableViewerColumn("PS�", 200, columnIndex++, "PS�e");

		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return String.valueOf(((Destinace) element).getPSC());
			}
		});
		col = createTableViewerColumn("Kontaktn� osoba", 200, columnIndex++,
				"Kontaktn� osoba");

		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Destinace) element).getKontaktni_osoba();
			}
		});
		col = createTableViewerColumn("Kontakt", 200, columnIndex++,
				"Kontakt na kontaktn� osobu");

		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Destinace) element).getKontakt();
			}
		});

	}

	protected void createMenuItems(Menu parent) {
		final MenuItem newItem = new MenuItem(parent, SWT.PUSH);
		newItem.setText("Vytvo�it novou destinaci");
		newItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (new NovaDestinaceDialog(event.widget.getDisplay()
						.getActiveShell(), context).open() == Window.OK) {
					refreshInputData();
				}
			}
		});
		new MenuItem(parent, SWT.SEPARATOR);

		final MenuItem editItem = new MenuItem(parent, SWT.PUSH);
		editItem.setText("Editovat destinaci");
		editItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				Destinace selectedDestinace = (Destinace) ((StructuredSelection) viewer
						.getSelection()).getFirstElement();
				if (selectedDestinace == null)
					return;
				NovaDestinaceDialog dialog = new NovaDestinaceDialog(
						event.widget.getDisplay().getActiveShell(), context,
						selectedDestinace);
				if (dialog.open() == Window.OK) {
					refreshInputData();
				}
			}
		});

		new MenuItem(parent, SWT.SEPARATOR);

		final MenuItem smazatItem = new MenuItem(parent, SWT.PUSH);
		smazatItem.setText("Smazat destinaci");
		smazatItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				Destinace selectedDestinace = (Destinace) ((StructuredSelection) viewer
						.getSelection()).getFirstElement();
				boolean result = MessageDialog.openConfirm(event.widget
						.getDisplay().getActiveShell(),
						"Potvrzen� smaz�n� destinace",
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
										"Chyba p�i z�pisu do datab�ze",
										"P�i z�pisu do datab�ze do�lo k chyb�, kontaktujte pros�m tv�rce aplikace.");

						logger.error("Nelze vlo�it/upravit destinaci", ex);
					}
					refreshInputData();
				}
				;
			}
		});

	}

	@Focus
	public void setFocus() {
		novyMenuItem.setVisible(true);
		novyMenuItem.setLabel("Nov� destinace");
		if(viewer.getSelection().isEmpty()) viewer.getTable().select(0);
		super.setFocus();
	}

	@Override
	protected TableViewerComparator getComparator() {
		return new TableViewerComparator();
	}

}
