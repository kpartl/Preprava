package cz.kpartl.preprava.view;

import java.util.ArrayList;
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
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.kpartl.preprava.dao.ObjednatelDAO;
import cz.kpartl.preprava.model.Objednatel;
import cz.kpartl.preprava.sorter.TableViewerComparator;
import cz.kpartl.preprava.util.EventConstants;
import cz.kpartl.preprava.util.HibernateHelper;
import cz.kpartl.preprava.util.Login;

@SuppressWarnings("unused")
public class ObjednatelView extends AbstractTableView {

	public static final String ID = "cz.kpartl.preprava.part.objednatel";

	private ObjednatelDAO objednatelDAO;

	private Objednatel objednatel;

	private Shell shell;

	final Logger logger = LoggerFactory.getLogger(ObjednatelView.class);

	@Inject
	public ObjednatelView(Composite parent,
			@Optional IStylingEngine styleEngine,
			@Optional IEclipseContext context, IEventBroker eventBroker) {
		super(styleEngine);
		this.context = context;
		this.eventBroker = eventBroker;
		context.set("cz.kpartl.preprava.view.ObjednatelView", this);
		this.objednatelDAO = context.get(ObjednatelDAO.class);
		shell = parent.getShell();
		createPartControl(parent);
	}

	protected Object getModelData() {
		objednatel = objednatelDAO.read(1L);
		List<Value> result = new ArrayList<Value>();
		if (objednatel != null) {
			result.add(new Value(objednatel.getO1()));
			result.add(new Value(objednatel.getO2()));
			result.add(new Value(objednatel.getO3()));
			result.add(new Value(objednatel.getO4()));
			result.add(new Value(objednatel.getO5()));
			result.add(new Value(objednatel.getO6()));
			result.add(new Value(objednatel.getO7()));
			result.add(new Value(objednatel.getO8()));
		}

		return result;
	}

	private void fillObjednatel() {
		objednatel.setId(1L);
		objednatel.setO1(viewer.getTable().getItem(0).getData().toString());
		objednatel.setO2(viewer.getTable().getItem(1).getData().toString());
		objednatel.setO3(viewer.getTable().getItem(2).getData().toString());
		objednatel.setO4(viewer.getTable().getItem(3).getData().toString());
		objednatel.setO5(viewer.getTable().getItem(4).getData().toString());
		objednatel.setO6(viewer.getTable().getItem(5).getData().toString());
		objednatel.setO7(viewer.getTable().getItem(6).getData().toString());
		objednatel.setO8(viewer.getTable().getItem(7).getData().toString());
	}

	protected void saveObjednatel() {
		Transaction tx = HibernateHelper.getInstance().beginTransaction();
		try {
			if (objednatel != null) {
				fillObjednatel();
				objednatelDAO.update(objednatel);
			} else {
				objednatel = new Objednatel();
				fillObjednatel();
				objednatelDAO.create(objednatel);
			}

			tx.commit();

		} catch (Exception ex) {
			MessageDialog.openError(shell, "Chyba pøi zápisu do databáze",
					"Nepodaøilo se aktualizovat objednavatele.");

			logger.error("Nelze aktualizovat objednavatele", ex);
			tx.rollback();
		}
	}

	protected void createViewer(Composite parent, Object data) {
		final Label nadpisLabel = new Label(parent, SWT.NONE);
		nadpisLabel.setText("Objednavatel");
		nadpisLabel.setFont(JFaceResources.getHeaderFont());
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, true, 2,
				1));

		// Add layout
		layout = new TableColumnLayout();
		composite.setLayout(layout);

		// Define the TableViewer
		viewer = new TableViewer(composite, SWT.SINGLE | SWT.BORDER);
		viewer.setComparator(null);
		
		CellEditor[] editors = new CellEditor[1];
		String[] PROPS = { "name" };
		viewer.setColumnProperties(PROPS);
		viewer.setCellModifier(new ObjednatelCellModifier(viewer));
		editors[0] = new TextCellEditor(viewer.getTable());
		viewer.setCellEditors(editors);

		// Create the columns
		createColumns(parent);

		// Make lines and make header visible
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// Set the ContentProvider
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setInput(data);
		
	}
	
	// This will create the columns for the table
	protected void createColumns(final Composite parent) {

		TableViewerColumn col = createTableViewerColumn("Øádky objednavatele",
				300, 1, "");

		col.setLabelProvider(new TooltipColumnLabelProvider("") {
			@Override
			public String getText(Object element) {

				return element != null ? ((Value) element).getValue() : "";
			}
		});
	}

	

	@Focus
	public void setFocus() {
		novyMenuItem.setVisible(false);
		editMenuItem.setVisible(false);
		smazatMenuItem.setVisible(false);
		prevestMenuItem.setVisible(false);
		tisknoutMenuItem.setVisible(false);
		

		novyMenuItem.setTooltip("");
		editMenuItem.setTooltip("");
		smazatMenuItem.setTooltip("");

		super.setFocus();
	}

	@Override
	protected TableViewerComparator getComparator() {
		return null;
	}

	
	class ObjednatelCellModifier implements ICellModifier {
		private Viewer viewer;

		public ObjednatelCellModifier(Viewer viewer) {
			this.viewer = viewer;
		}

		public boolean canModify(Object element, String property) {
			return true;
		}

		public Object getValue(Object element, String property) {
			return element.toString();
		}

		public void modify(Object element, String property, Object value) {
			if (element instanceof Item)
				element = ((Item) element).getData();

			((Value) element).setValue((String) value);
			saveObjednatel();
			viewer.refresh();
		}
	}

	class Value {
		String value;

		public Value(String val) {
			this.value = val;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String val) {
			this.value = val;
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof Value))
				return false;
			if (this.value == null)
				return false;
			if (other == null)
				return false;
			return this.getValue().equals(((Value) other).getValue());
		}

		@Override
		public String toString() {
			return value;
		}

	}

}
