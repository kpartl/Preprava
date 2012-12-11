package cz.kpartl.preprava.dialog;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import cz.kpartl.preprava.dao.DestinaceDAO;
import cz.kpartl.preprava.filter.DestinaceFilter;
import cz.kpartl.preprava.model.Destinace;
import cz.kpartl.preprava.model.Pozadavek;
import cz.kpartl.preprava.sorter.DestinaceTableViewerComparator;
import cz.kpartl.preprava.sorter.TableViewerComparator;
import cz.kpartl.preprava.util.EventConstants;
import cz.kpartl.preprava.util.HibernateHelper;
import cz.kpartl.preprava.view.TooltipColumnLabelProvider;

public class DestinaceListDialog extends Dialog {

	private DestinaceDAO destinaceDAO;
	HibernateHelper persistenceHelper;

	public TableViewer viewer;

	private Text searchText;

	protected TableColumnLayout layout;

	protected int columnIndex = 0;

	protected TableViewerComparator comparator;

	Destinace selectedDestinace = null;

	@Inject
	public DestinaceListDialog(
			@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell,
			IEclipseContext context) {
		super(parentShell);
		this.destinaceDAO = context.get(DestinaceDAO.class);
		persistenceHelper = cz.kpartl.preprava.util.HibernateHelper
				.getInstance();
	}

	@Override
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);		
		return contents;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		Label searchLabel = new Label(parent, SWT.NONE);
		searchLabel.setText("Destinace: ");
		searchText = new Text(parent, SWT.BORDER | SWT.SEARCH);
		searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));

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

		comparator = getComparator();
		viewer.setComparator(comparator);

		Menu headerMenu = new Menu(parent.getShell(), SWT.POP_UP);
		viewer.getTable().setMenu(headerMenu);

		// Create the columns
		createColumns(parent);

		// Make lines and make header visible
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// Set the ContentProvider
		viewer.setContentProvider(ArrayContentProvider.getInstance());

		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				Destinace dest = (Destinace) ((StructuredSelection) viewer
						.getSelection()).getFirstElement();
				if (dest == null)
					return;

				selectedDestinace = dest;
				//searchText.setText(selectedDestinace.getNazevACislo());
				close();

			}
		});
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {				
				final Object selectedObject = ((StructuredSelection) event
						.getSelection()).getFirstElement();
				if (selectedObject instanceof Destinace)
					selectedDestinace = (Destinace)selectedObject;
					searchText.setText(selectedDestinace.getNazev());
			}
		});


		viewer.setInput(destinaceDAO.findAll());

		final DestinaceFilter filter = new DestinaceFilter();
		viewer.addFilter(filter);

		searchText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {				
				filter.setSearchText(searchText.getText());
				viewer.refresh();
			}
		});

		// Layout the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);

		ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.NO_RECREATE);

		return parent;
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
				return ((Destinace) element).getPSC() != null ? String
						.valueOf(((Destinace) element).getPSC()) : "";
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

	protected TableViewerColumn createTableViewerColumn(String title,
			int bound, final int colNumber, final String toolTip) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
				SWT.NONE);

		final TableColumn column = viewerColumn.getColumn();
		// if (colNumber == 0)
		layout.setColumnData(column, new ColumnPixelData(bound, true, true));
		// else
		// layout.setColumnData(column, new
		// ColumnWeightData(bound,ColumnWeightData.MINIMUM_WIDTH, true));
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
			}
		};
		return selectionAdapter;
	}

	protected TableViewerComparator getComparator() {
		return new DestinaceTableViewerComparator();
	}

	@Override
	protected void cancelPressed() {
		selectedDestinace = null;
		super.cancelPressed();
	}

	@Override
	protected void okPressed() {
		selectedDestinace = (Destinace) ((IStructuredSelection)(viewer.getSelection())).getFirstElement();
		super.okPressed();
	}
	
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.MAX);
	}

}
