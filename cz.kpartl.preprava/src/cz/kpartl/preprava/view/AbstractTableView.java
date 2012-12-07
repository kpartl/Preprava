package cz.kpartl.preprava.view;

import java.text.SimpleDateFormat;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import cz.kpartl.preprava.util.EventConstants;
import cz.kpartl.preprava.util.Login;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.impl.TrimmedWindowImpl;
import org.eclipse.e4.ui.model.application.ui.menu.impl.HandledToolItemImpl;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;

import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import cz.kpartl.preprava.model.Objednavka;
import cz.kpartl.preprava.model.Pozadavek;
import cz.kpartl.preprava.model.User;

import cz.kpartl.preprava.sorter.TableViewerComparator;

@SuppressWarnings("restriction")
public abstract class AbstractTableView extends ViewPart {

	public TableViewer viewer;

	protected TableColumnLayout layout;

	// protected EventHandler eventHandler;

	protected IStylingEngine styleEngine;

	protected Menu headerMenu;

	protected TableViewerComparator comparator;

	protected int columnIndex = 0;

	@Inject
	@Optional
	IEclipseContext context;

	@Inject
	@Optional
	protected IEventBroker eventBroker;

	// @Inject
	// @Optional
	protected ESelectionService selectionService;

	protected HandledToolItemImpl novyMenuItem;
	protected HandledToolItemImpl editMenuItem;
	protected HandledToolItemImpl smazatMenuItem;
	protected HandledToolItemImpl prevestMenuItem;

	protected Image checkedImage;
	protected Image uncheckedImage;
	MenuItem newItem;
	MenuItem objednavkaItem;

	public AbstractTableView(IStylingEngine styleEngine) {
		this.styleEngine = styleEngine;
	}

	MenuItem editItem;
	MenuItem smazatItem;

	// Vrati data pro tabulku
	protected abstract Object getModelData();

	protected abstract TableViewerComparator getComparator();

	@Inject
	@PostConstruct
	public void init(EModelService modelService, MApplication app) {
		novyMenuItem = (HandledToolItemImpl) modelService.find(
				"cz.kpartl.preprava.toolItem.novyPozadavek", app);
		editMenuItem = (HandledToolItemImpl) modelService.find(
				"cz.kpartl.preprava.handledtoolitem.edit", app);
		smazatMenuItem = (HandledToolItemImpl) modelService.find(
				"cz.kpartl.preprava.handledtoolitem.delete", app);
		prevestMenuItem = (HandledToolItemImpl) modelService.find(
				"cz.kpartl.preprava.handledtoolitem.prevod", app);
		
		final TrimmedWindowImpl mainWindow = (TrimmedWindowImpl) modelService.find("cz.kpartl.preprava.mainwindow",app);
		mainWindow.setLabel("P�EPRAVA - p�ihl�en� u�ivatel: " + ((User) context.get(User.CONTEXT_NAME)).getUsername());

	}

	/*
	 * @PreDestroy void unhookEvents() { if( eventBroker != null && eventHandler
	 * != null ) { eventBroker.unsubscribe(eventHandler); } }
	 */
	/**
	 * Passing the focus request to the viewer's control.
	 */

	@Focus
	public void setFocus() {
		// viewer.getControl().setFocus();

		if (viewer.getSelection().isEmpty()) {
			viewer.getTable().select(0);
			StructuredSelection sel = (StructuredSelection) viewer
					.getSelection();
			if (sel.getFirstElement() instanceof Pozadavek)
				eventBroker.send(EventConstants.POZADAVEK_SELECTION_CHANGED,
						sel.getFirstElement());
			else if (sel.getFirstElement() instanceof Objednavka)
				eventBroker.send(EventConstants.OBJEDNAVKA_SELECTION_CHANGED,
						sel.getFirstElement());
			else if (sel.getFirstElement() == null)
				eventBroker.send(EventConstants.EMPTY_OBJEDNAVKA_SEND,
						EventConstants.EMPTY_OBJEDNAVKA_SEND);
		}

	}

	@Inject
	@Optional
	public void partActivation(
			@UIEventTopic(UIEvents.UILifeCycle.ACTIVATE) org.osgi.service.event.Event event,
			MApplication application, EPartService partService, EModelService modelService) {

		MPart activePart = (MPart) event
				.getProperty(UIEvents.EventTags.ELEMENT);

		// kvuli ukoncenym objednavkam to nefunguje pro objednanoView
		/*
		 * if(activePart.getElementId().equals(ObjednavkaDetailView.ID)){
		 * partService.activate(partService.findPart(ObjednanoView.ID)); }
		 */
		
		if (activePart.getElementId().equals(PozadavekDetailView.ID)) {
			partService.activate(partService.findPart(PozadavkyView.ID));
		}

		// if(!pozadavekDetailView.isVisible())
		// pozadavekDetailView.setVisible(true);
		// if(objednavkaDetailView.isVisible())
		// objednavkaDetailView.setVisible(false);
	}

	// Used to update the viewer from outsite
	public void refresh() {
		viewer.refresh();
	}

	public void createPartControl(Composite parent) {
		checkedImage = (Image) context.get(Login.CHECKED_ICON);
		uncheckedImage = (Image) context.get(Login.UNCHECKED_ICON);

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
	}

	protected TableViewerColumn createTableViewerColumn(String title,
			int bound, final int colNumber, final String toolTip) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
				SWT.NONE);

		final TableColumn column = viewerColumn.getColumn();
		//if (colNumber == 0)
			layout.setColumnData(column, new ColumnPixelData(bound, true, true));
		//else
			//layout.setColumnData(column, new ColumnWeightData(bound,ColumnWeightData.MINIMUM_WIDTH, true));
		column.setToolTipText(toolTip);
		column.setText(title);
		// column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		// column.setImage(Activator.getImageDescriptor("./icons/header.gif").createImage());
		column.addSelectionListener(getSelectionAdapter(column, colNumber));
		// Create the menu item for this column
		// createMenuItem(headerMenu, column);

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
				eventBroker.send(EventConstants.POZADAVEK_SELECTION_CHANGED, e);
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
				columnIndex++, "Datum po�adavku");

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

		col = createTableViewerColumn("Datum nakl�dky", 90, columnIndex++,
				"Po�adovan� datum nakl�dky");
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

		col = createTableViewerColumn("Datum vykl�dky", 90, columnIndex++,
				"Po�adovan� datum vykl�dky");
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
				"V�choz� destinace");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				if (element instanceof Objednavka) {
					element = ((Objednavka) element).getPozadavek();
				}
				return ((Pozadavek) element).getDestinaceZNazevACislo();

			}
		});

		col = createTableViewerColumn("Kam", 150, columnIndex++,
				"C�lov� destinace");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				if (element instanceof Objednavka) {
					element = ((Objednavka) element).getPozadavek();
				}
				return ((Pozadavek) element).getDestinaceDoNazevACislo();

			}
		});

		col = createTableViewerColumn("Hmotnost Kg", 60, columnIndex++,
				"Celkov� hmotnost z�silky v Kg");
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

		col = createTableViewerColumn("Palet", 60, columnIndex++, "Po�et EUR palet");
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
		
		col = createTableViewerColumn("Stohovateln�?", 120, columnIndex++,
				"Jsou palety stohovateln�?");
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
				if (((Pozadavek) element).getJe_stohovatelne()) {
					return checkedImage;
				}
				return uncheckedImage;
			}
		});


		col = createTableViewerColumn("Term�n kone�n�?", 120, columnIndex++,
				"Je term�n nakl�dky kone�n�?");
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
					return checkedImage;
				}
				return uncheckedImage;
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
					return checkedImage;
				}
				return uncheckedImage;
			}
		});

		col = createTableViewerColumn("Kontakt ODKUD", 200, columnIndex++,
				"Kontaktn� osoba u v�choz� destinace");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				if (element instanceof Objednavka) {
					element = ((Objednavka) element).getPozadavek();
				}
				return ((Pozadavek) element).getDestinaceZKontaktAOsobu();

			}
		});

		col = createTableViewerColumn("Kontakt KAM", 200, columnIndex++,
				"Kontaktn� osoba u c�lov� destinace");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				if (element instanceof Objednavka) {
					element = ((Objednavka) element).getPozadavek();
				}
				return ((Pozadavek) element).getDestinaceDoKontaktAOsobu();

			}
		});

		col = createTableViewerColumn("Hodina nakl�dky", 100, columnIndex++,
				"Hodina nakl�dky u dodavatele");
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
		
		col = createTableViewerColumn("Hodina vykl�dky", 100, columnIndex++,
				"Hodina vykl�dky u z�kazn�ka");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				if (element instanceof Objednavka) {
					element = ((Objednavka) element).getPozadavek();
				}
				return ((Pozadavek) element).getHodina_vykladky();
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

		col = createTableViewerColumn("Pozn�mka", 100, columnIndex++,
				"Pozn�mka");
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
				final Object selectedObject = ((StructuredSelection) event
						.getSelection()).getFirstElement();
				if (selectedObject instanceof Pozadavek)
					eventBroker.send(
							EventConstants.POZADAVEK_SELECTION_CHANGED,
							selectedObject);
				else if (selectedObject instanceof Objednavka)
					eventBroker.send(
							EventConstants.OBJEDNAVKA_SELECTION_CHANGED,
							selectedObject);
			}
		});

		// Work around for 4.0 Bug of not cleaning up on Window-close
		viewer.getControl().addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {

			}
		});

	}

	

	/*
	 * public void refreshInputData() { viewer.setInput(getModelData()); }
	 */

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
