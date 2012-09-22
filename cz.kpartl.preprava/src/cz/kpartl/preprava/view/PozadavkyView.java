package cz.kpartl.preprava.view;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;

import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.part.ViewPart;
import org.osgi.service.event.EventHandler;

import cz.kpartl.preprava.dao.PozadavekDAO;
import cz.kpartl.preprava.model.Dodavatel;
import cz.kpartl.preprava.model.Pozadavek;
import cz.kpartl.preprava.model.Zakaznik;
import cz.kpartl.preprava.sorter.TableViewerComparator;
import cz.kpartl.preprava.Activator;

@SuppressWarnings("restriction")
public class PozadavkyView extends ViewPart {

	private TableViewer viewer;

	private EventHandler eventHandler;

	private PozadavekDAO pozadavekDAO;

	private IStylingEngine styleEngine;

	private static final Image CHECKED = Activator.getImageDescriptor(
			"icons/checked.gif").createImage();
	private static final Image UNCHECKED = Activator.getImageDescriptor(
			"icons/unchecked.gif").createImage();

	private Menu headerMenu;

	private TableViewerComparator comparator;

	@Inject
	@Optional
	private IEventBroker eventBroker;

	@Inject
	@Optional
	private ESelectionService selectionService;

	@Inject
	public PozadavkyView(Composite parent,
			@Optional IStylingEngine styleEngine,
			@Optional PozadavekDAO pozadavekDAO) {

		this.pozadavekDAO = pozadavekDAO;

		this.styleEngine = styleEngine;
		
		createPartControl(parent);
	}

	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);
		createViewer(parent);
		// Set the sorter for the table
		comparator = new TableViewerComparator();
		viewer.setComparator(comparator);
	}

	// This will create the columns for the table
	private void createColumns(final Composite parent) {

		headerMenu = new Menu(parent.getShell(), SWT.POP_UP);

		TableViewerColumn col = createTableViewerColumn("Datum požadavku", 100,
				0);

		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return new SimpleDateFormat("dd.MM.yyyy")
						.format(((Pozadavek) element).getDatum());
			}
		});

		col = createTableViewerColumn("Požadované datum nakládky", 100, 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Pozadavek) element).getDatum_nakladky();
			}
		});

		col = createTableViewerColumn("Požadované datum vykládky", 100, 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Pozadavek) element).getDatum_vykladky();
			}
		});

		col = createTableViewerColumn("Zákazník", 250, 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Zakaznik zakaznik = ((Pozadavek) element).getZakaznik();
				if (zakaznik == null)
					return "";
				else
					return zakaznik
							.getNazev()
							.concat("(")
							.concat(String.valueOf(zakaznik.getCislo()).concat(
									")"));

			}
		});

		col = createTableViewerColumn("Celková hmotnost zásilky", 250, 4);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Pozadavek) element).getCelkova_hmotnost();
			}
		});

		col = createTableViewerColumn("Je termín koneèný", 250, 5);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return "";
			}

			@Override
			public Image getImage(Object element) {
				if (((Pozadavek) element).getJe_termin_konecny()) {
					return CHECKED;
				}
				return UNCHECKED;
			}
		});

		/*
		 * Enumeration e = viewer.getColumnModel().getColumns(); while
		 * (e.hasMoreElements()) { ((TableColumn)
		 * e.nextElement()).setHeaderRenderer(renderer); }
		 */

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				if (selectionService != null) {
					selectionService.setSelection(((IStructuredSelection) event
							.getSelection()).getFirstElement());
				}
			}
		});

		// Work around for 4.0 Bug of not cleaning up on Window-close
		viewer.getControl().addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				unhookEvents();
			}
		});
	}

	private void createViewer(Composite parent) {
		// Define the TableViewer
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		// Create the columns
		createColumns(parent);

		// Make lines and make header visible
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// Set the ContentProvider
		viewer.setContentProvider(ArrayContentProvider.getInstance());

		/*for (Iterator<Pozadavek> iter = pozadavekDAO.findAll().iterator(); iter
				.hasNext();)
			viewer.add(iter.next());*/
		
		viewer.setInput(pozadavekDAO.findAll());

		// Make the selection available to other Views
		//getSite().setSelectionProvider(viewer);  // HELE JA NEVIM NA CO TO JE

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
	}

	@PostConstruct
	public void init() {

	}

	@PostConstruct
	void hookEvents() {

	}

	@PreDestroy
	void unhookEvents() {
		if (eventBroker != null && eventHandler != null) {
			eventBroker.unsubscribe(eventHandler);
		}
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound,
			final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
				SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		column.addSelectionListener(getSelectionAdapter(column, colNumber));
		// Create the menu item for this column
		createMenuItem(headerMenu, column);
		return viewerColumn;
	}

	private SelectionAdapter getSelectionAdapter(final TableColumn column,
			final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setColumn(index);
				int dir = comparator.getDirection();
				viewer.getTable().setSortDirection(dir);
				viewer.getTable().setSortColumn(column);
				viewer.refresh();
			}
		};
		return selectionAdapter;
	}

	private void createMenuItem(Menu parent, final TableColumn column) {
		final MenuItem itemName = new MenuItem(parent, SWT.CHECK);
		itemName.setText(column.getText());
		itemName.setSelection(column.getResizable());
		itemName.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (itemName.getSelection()) {
					column.setWidth(150);
					column.setResizable(true);
				} else {
					column.setWidth(0);
					column.setResizable(false);
				}
			}
		});

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	// Used to update the viewer from outsite
	public void refresh() {
		viewer.refresh();
	}

}
