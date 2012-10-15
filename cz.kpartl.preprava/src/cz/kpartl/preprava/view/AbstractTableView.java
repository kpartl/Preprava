package cz.kpartl.preprava.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javassist.expr.Instanceof;

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
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import cz.kpartl.preprava.util.EventConstants;

import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.menu.impl.HandledToolItemImpl;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;

import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.osgi.service.event.EventHandler;

import cz.kpartl.preprava.dao.PozadavekDAO;
import cz.kpartl.preprava.model.Destinace;
import cz.kpartl.preprava.model.Objednavka;
import cz.kpartl.preprava.model.Pozadavek;
import cz.kpartl.preprava.model.User;

import cz.kpartl.preprava.sorter.TableViewerComparator;
import cz.kpartl.preprava.Activator;

public abstract class AbstractTableView extends ViewPart {

	protected TableViewer viewer;

	protected TableColumnLayout layout;

	protected EventHandler eventHandler;

	protected IStylingEngine styleEngine;

	protected static final Image CHECKED = Activator.getImageDescriptor(
			"icons/checked.gif").createImage();
	protected static final Image UNCHECKED = Activator.getImageDescriptor(
			"icons/unchecked.gif").createImage();

	protected Menu headerMenu;

	protected TableViewerComparator comparator;

	protected int columnIndex = 0;
	
	@Inject
	@Optional
	IEclipseContext context;

	@Inject
	@Optional
	protected IEventBroker eventBroker;

	//@Inject
	//@Optional
	protected ESelectionService selectionService;
	
	protected HandledToolItemImpl novyMenuItem;

	public AbstractTableView(IStylingEngine styleEngine) {
		this.styleEngine = styleEngine;
	}

	// Vrati data pro tabulku
	protected abstract Object getModelData();
	
	protected abstract TableViewerComparator getComparator();

	
	@Inject 
	@PostConstruct
	public void init(EModelService modelService, MApplication app) {
		novyMenuItem = (HandledToolItemImpl) modelService.find("cz.kpartl.preprava.toolItem.novyPozadavek",app);

	}
	
	

	
	
	@PreDestroy
	void unhookEvents() {
		if( eventBroker != null && eventHandler != null ) {
			eventBroker.unsubscribe(eventHandler);
		}
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

	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		parent.setLayout(layout);
		createViewer(parent, getModelData());
		// Set the sorter for the table		
		comparator = getComparator();
		viewer.setComparator(comparator);
		
		ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.NO_RECREATE);
	}

	protected void createViewer(Composite parent, Object data) {
		// Create the composite
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
		//getSite().setSelectionProvider(viewer); // HELE JA NEVIM NA CO TO JE

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
	
	

	protected TableViewerColumn createTableViewerColumn(String title,
			int bound, final int colNumber, final String toolTip) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
				SWT.NONE);

		final TableColumn column = viewerColumn.getColumn(); 
		if (colNumber == 0)
			layout.setColumnData(column, new ColumnPixelData(bound, true, true));
		else
			layout.setColumnData(column, new ColumnWeightData(bound,
					ColumnWeightData.MINIMUM_WIDTH, true));
		column.setToolTipText(toolTip);
		column.setText(title);
		// column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		// column.setImage(Activator.getImageDescriptor("./icons/header.gif").createImage());
		column.addSelectionListener(getSelectionAdapter(column, colNumber));
		// Create the menu item for this column
		//createMenuItem(headerMenu, column);
						
		return viewerColumn;
	}
	
		
		
	

	protected SelectionAdapter getSelectionAdapter(final TableColumn column,
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

	// This will create the columns for the table
	protected void createColumns(final Composite parent) {

		// TableViewerColumn col = createTableViewerColumn("", 0,columnIndex++,
		// ""); //kvuli chybe we Windowsech, kdy 1. sloupec ma jinak pozicovany
		// data

		TableViewerColumn col = createTableViewerColumn("Datum", 70,
				columnIndex++, "Datum požadavku");

		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				if (element instanceof Objednavka) {
					element = ((Objednavka) element).getPozadavek();
				}
				return new SimpleDateFormat("dd.MM.yyyy")
						.format(((Pozadavek) element).getDatum());
			}
		});

		col = createTableViewerColumn("Datum nakládky", 90, columnIndex++,
				"Požadované datum nakládky");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				if (element instanceof Objednavka) {
					element = ((Objednavka) element).getPozadavek();
				}
				return ((Pozadavek) element).getDatum_nakladky();
			}
		});

		col = createTableViewerColumn("Datum vykládky", 90, columnIndex++,
				"Požadované datum vykládky");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				if (element instanceof Objednavka) {
					element = ((Objednavka) element).getPozadavek();
				}
				return ((Pozadavek) element).getDatum_vykladky();
			}
		});

		col = createTableViewerColumn("Odkud", 150, columnIndex++,
				"Výchozí destinace");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				if (element instanceof Objednavka) {
					element = ((Objednavka) element).getPozadavek();
				}
				final Destinace destinace_z = ((Pozadavek) element)
						.getDestinace_z();
				if (destinace_z == null)
					return "";
				else
					return destinace_z.getNazevACislo();

			}
		});

		col = createTableViewerColumn("Kam", 150, columnIndex++,
				"Cílová destinace");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				if (element instanceof Objednavka) {
					element = ((Objednavka) element).getPozadavek();
				}
				final Destinace destinace_do = ((Pozadavek) element)
						.getDestinace_do();
				if (destinace_do == null)
					return "";
				else
					return destinace_do.getNazevACislo();

			}
		});

		col = createTableViewerColumn("Hmotnost", 60, columnIndex++,
				"Celková hmotnost zásilky");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				if (element instanceof Objednavka) {
					element = ((Objednavka) element).getPozadavek();
				}
				return ((Pozadavek) element).getCelkova_hmotnost();
			}
		});
		
		col = createTableViewerColumn("Palet", 60, columnIndex++,
				"Poèet palet");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				if (element instanceof Objednavka) {
					element = ((Objednavka) element).getPozadavek();
				}
				return ((Pozadavek) element).getPocet_palet();
			}
		});

		col = createTableViewerColumn("Termín koneèný?", 120, columnIndex++,
				"Je termín nakládky koneèný?");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return "";
			}

			@Override
			public Image getImage(Object element) {
				if (element instanceof Objednavka) {
					element = ((Objednavka) element).getPozadavek();
				}
				if (((Pozadavek) element).getJe_termin_konecny()) {
					return CHECKED;
				}
				return UNCHECKED;
			}
		});

		col = createTableViewerColumn("TAXI", 50, columnIndex++, "TAXI?");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return "";
			}

			@Override
			public Image getImage(Object element) {
				if (element instanceof Objednavka) {
					element = ((Objednavka) element).getPozadavek();
				}
				if (((Pozadavek) element).getTaxi()) {
					return CHECKED;
				}
				return UNCHECKED;
			}
		});

		col = createTableViewerColumn("Kontakt ODKUD", 200, columnIndex++,
				"Kontaktní osoba u výchozí destinace");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				if (element instanceof Objednavka) {
					element = ((Objednavka) element).getPozadavek();
				}
				final Destinace destinace_z = ((Pozadavek) element)
						.getDestinace_z();
				if (destinace_z == null)
					return "";
				else
					return (destinace_z.getKontaktniOsobuAKontakt());

			}
		});

		col = createTableViewerColumn("Kontakt KAM", 200, columnIndex++,
				"Kontaktní osoba u cílové destinace");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				if (element instanceof Objednavka) {
					element = ((Objednavka) element).getPozadavek();
				}
				final Destinace destinace_do = ((Pozadavek) element)
						.getDestinace_do();
				if (destinace_do == null)
					return "";
				else
					return destinace_do.getKontaktniOsobuAKontakt();

			}
		});

		col = createTableViewerColumn("Hodina nakládky", 100, columnIndex++,
				"Hodina nakládky u dodavatele");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				if (element instanceof Objednavka) {
					element = ((Objednavka) element).getPozadavek();
				}
				return ((Pozadavek) element).getHodina_nakladky();
			}
		});

		col = createTableViewerColumn("Zadavatel", 80, columnIndex++,
				"Zadavatel");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				if (element instanceof Objednavka) {
					element = ((Objednavka) element).getPozadavek();
				}
				final User zadavatel = ((Pozadavek) element).getZadavatel();
				if (zadavatel == null)
					return "";
				else
					return zadavatel.getUsername();

			}
			
			
		});
		
		col = createTableViewerColumn("Poznámka", 100, columnIndex++,
				"Poznámka");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				if (element instanceof Objednavka) {
					element = ((Objednavka) element).getPozadavek();
				}
				return ((Pozadavek) element).getPoznamka();
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
	
	public void refreshInputData() {
		viewer.setInput(getModelData());
		}

	/*
	 * protected void createMenuItem(Menu parent, final TableColumn column) {
	 * final MenuItem itemName = new MenuItem(parent, SWT.CHECK);
	 * itemName.setText(column.getText());
	 * itemName.setSelection(column.getResizable());
	 * itemName.addListener(SWT.Selection, new Listener() { public void
	 * handleEvent(Event event) { if (itemName.getSelection()) {
	 * column.setWidth(100); column.setResizable(true); } else {
	 * column.setWidth(0); column.setResizable(false); } } });
	 * 
	 * }
	 */

	/*
	 * public void changePerspective() {
	 * getViewSite().getWorkbenchWindow().getActivePage
	 * ().setPerspective(perspective) }
	 */

}
