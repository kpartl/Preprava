package cz.kpartl.preprava.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.impl.ToolBarImpl;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.hibernate.Transaction;

import cz.kpartl.preprava.dao.ObjednavkaDAO;
import cz.kpartl.preprava.dao.PozadavekDAO;
import cz.kpartl.preprava.dialog.NovaObjednavkaDialog;
import cz.kpartl.preprava.dialog.NovyPozadavekDialog;
import cz.kpartl.preprava.model.Dopravce;
import cz.kpartl.preprava.model.Objednavka;
import cz.kpartl.preprava.model.Pozadavek;
import cz.kpartl.preprava.model.User;
import cz.kpartl.preprava.sorter.ObjednavkaTableViewerComparator;
import cz.kpartl.preprava.sorter.PozadavekTableViewerComparator;
import cz.kpartl.preprava.sorter.TableViewerComparator;
import cz.kpartl.preprava.util.EventConstants;
import cz.kpartl.preprava.util.HibernateHelper;
import cz.kpartl.preprava.util.Login;

public class ObjednanoView extends AbstractTableView {

	public static final String ID = "cz.kpartl.preprava.part.tablepartobjednane";

	public static final String TYP_OBJEDNANO = "Objednáno";
	public static final String TYP_PREPRAVA_ZAHAJENA = "Pøeprava zahájena";
	public static final String TYP_PREPRAVA_UKONCENA = "Pøeprav ukonèena";
	public static final String TYP_DOKLADY_KOMPLETNI = "Doklady kompletní";
	public static final String TYP_FAKTUROVANO = "Fakturováno";
	public static final String TYP_UKONCENO = "Ukonèeno";

	public static final HashMap<Integer, String> typyHashMap;

	static {
		typyHashMap = new HashMap<Integer, String>();

		typyHashMap.put(Objednavka.FAZE_OBJEDNANO, TYP_OBJEDNANO);
		typyHashMap.put(Objednavka.FAZE_PREPRAVA_ZAHAJENA,
				TYP_PREPRAVA_ZAHAJENA);
		typyHashMap.put(Objednavka.FAZE_PREPRAVA_UKONCENA,
				TYP_PREPRAVA_UKONCENA);
		typyHashMap.put(Objednavka.FAZE_DOKLADY_KOMPLETNI,
				TYP_DOKLADY_KOMPLETNI);
		typyHashMap.put(Objednavka.FAZE_FAKTUROVANO, TYP_FAKTUROVANO);
		typyHashMap.put(Objednavka.FAZE_UKONCENO, TYP_UKONCENO);
		// typyHashMap.put(Objednavka.FAZE_UKONCENO, TYP_UKONCENO);

	}

	protected ObjednavkaDAO objednavkaDAO;
	protected PozadavekDAO pozadavekDAO;

	private Combo typCombo;
	protected Shell shell;
	private int faze = 0;

	EPartService partService;
	MPart pozadavekDetailView;
	MPart objednavkaDetailView;

	MenuItem editItem;
	MenuItem smazatItem;

	@Inject
	public ObjednanoView(Composite parent,
			@Optional IStylingEngine styleEngine,
			@Optional ObjednavkaDAO objednavkaDAO,
			@Optional PozadavekDAO pozadavekDAO, IEclipseContext context,
			EPartService partService) {
		super(styleEngine);

		shell = parent.getShell();
		this.objednavkaDAO = objednavkaDAO;
		this.pozadavekDAO = pozadavekDAO;
		this.context = context;
		this.partService = partService;

		pozadavekDetailView = partService.findPart(PozadavekDetailView.ID);

		objednavkaDetailView = partService.findPart(ObjednavkaDetailView.ID);

		context.getParent().set(ObjednanoView.ID, this);

		createPartControl(parent);

	}

	@Override
	protected Object getModelData() {
		return objednavkaDAO.findByFaze(faze);
	}

	@Override
	protected void createColumns(final Composite parent) {
		TableViewerColumn col = createTableViewerColumn("È. objednávky", 110,
				columnIndex++, "Èíslo objednávky");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return String.valueOf(((Objednavka) element).getId());
			}
		});

		col = createTableViewerColumn("Dopravce", 110, columnIndex++,
				"Dopravce");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				final Dopravce dopravce = ((Objednavka) element).getDopravce();
				if (dopravce == null)
					return "";

				return dopravce.getNazev();
			}
		});

		col = createTableViewerColumn("Cena", 50, columnIndex++, "Cena dopravy");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Objednavka) element).getCenaFormated();
			}
		});

		col = createTableViewerColumn("Mìna", 20, columnIndex++, "Mìna");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Objednavka) element).getMena();
			}
		});

		col = createTableViewerColumn("Zmìna termínu", 40, columnIndex++,
				"Zmìna termínu nakládky");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Objednavka) element).getZmena_nakladky();
			}
		});

		col = createTableViewerColumn("Èíslo faktury", 40, columnIndex++,
				"Èíslo faktury dopravce");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Objednavka) element).getCisloFakturyDopravceAsString();
			}
		});

		super.createColumns(parent);
	}

	@Override
	protected void createViewer(Composite parent, Object data) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		final Label nadpisLabel = new Label(composite, SWT.NONE);
		nadpisLabel.setText("Pøehled objednaných pøeprav ve stavu");
		nadpisLabel.setFont(JFaceResources.getHeaderFont());
		typCombo = new Combo(composite, SWT.READ_ONLY);
		typCombo.setBounds(new Rectangle(50, 50, 150, 65));

		typCombo.setItems(getComboItems(false));
		typCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				faze = typCombo.getSelectionIndex();
				viewer.setInput(getModelData());
				viewer.refresh();
				if (viewer.getSelection().isEmpty()) {
					viewer.getTable().select(0);
				}
				final Objednavka selectedObjednavka = (Objednavka) ((StructuredSelection) viewer
						.getSelection()).getFirstElement();
				if (selectedObjednavka != null) {
					enableMenuItems(true);
					eventBroker.send(
							EventConstants.OBJEDNAVKA_SELECTION_CHANGED,
							selectedObjednavka);
				} else {
					enableMenuItems(false);
					eventBroker.send(EventConstants.EMPTY_OBJEDNAVKA_SEND,
							EventConstants.EMPTY_OBJEDNAVKA_SEND);
				}

			}
		});

		super.createViewer(parent, data);
		createMenuItems(headerMenu);

		if (((User) context.get(User.CONTEXT_NAME)).isAdministrator()) {
			viewer.addDoubleClickListener(new IDoubleClickListener() {
				@Override
				public void doubleClick(DoubleClickEvent event) {

					Objednavka selectedObjednavka = (Objednavka) ((StructuredSelection) viewer
							.getSelection()).getFirstElement();
					if (selectedObjednavka == null)
						return;
					new NovaObjednavkaDialog(shell, context,
							selectedObjednavka, eventBroker).open();
				}

			});
		}

		typCombo.select(0);
	}

	protected void superCreateViewer(Composite parent, Object data) {
		super.createViewer(parent, data);
	}

	protected void createMenuItem(Menu parent, final TableColumn column) {
	}

	protected void createMenuItems(Menu parent) {
		if (!((User) context.get(User.CONTEXT_NAME)).isAdministrator())
			return; // neadmin nema narok

		editItem = new MenuItem(parent, SWT.PUSH);
		editItem.setText("Editovat objednávku");
		editItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				editSelectedObjednavka();
			}
		});

		smazatItem = new MenuItem(parent, SWT.PUSH);
		smazatItem.setText("Smazat objednávku");
		smazatItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				deleteSelectedObjednavka();
			}
		});

		smazatItem.setImage((Image) context.get(Login.DELETE_ICON));
		editItem.setImage((Image) context.get(Login.EDIT_ICON));
	}

	@Override
	protected TableViewerComparator getComparator() {
		return new ObjednavkaTableViewerComparator();
	}

	@Focus
	@Inject
	public void setFocus(EPartService partService) {

		novyMenuItem.setVisible(false);
		editMenuItem.setVisible(true);
		editMenuItem.setEnabled(true);
		smazatMenuItem.setVisible(true);
		prevestMenuItem.setVisible(false);

		editMenuItem.setTooltip("Editovat objednávku");
		smazatMenuItem.setTooltip("Smazat objednávku");

		if (!objednavkaDetailView.isVisible())
			objednavkaDetailView.setVisible(true);
		if (pozadavekDetailView.isVisible())
			pozadavekDetailView.setVisible(false);
		// objednavkaDetailView.setToBeRendered(true);

		if (viewer.getTable().getSelectionIndex() < 0)
			viewer.getTable().setSelection(0);

		final Object selectedObject = (((StructuredSelection) viewer
				.getSelection()).getFirstElement());
		if (selectedObject != null) {
			enableMenuItems(true);
			eventBroker.send(EventConstants.OBJEDNAVKA_SELECTION_CHANGED,
					selectedObject);

		} else {
			enableMenuItems(false);
			eventBroker.send(EventConstants.EMPTY_OBJEDNAVKA_SEND,
					EventConstants.EMPTY_OBJEDNAVKA_SEND);
		}

	}

	public void editSelectedObjednavka() {
		Objednavka selectedObjednavka = (Objednavka) ((StructuredSelection) viewer
				.getSelection()).getFirstElement();
		if (selectedObjednavka == null)
			return;
		new NovaObjednavkaDialog(shell, context, selectedObjednavka,
				eventBroker).open();

		// eventBroker.send(EventConstants.REFRESH_VIEWERS, "");
	}

	public void deleteSelectedObjednavka() {
		Objednavka selectedObjednavka = (Objednavka) ((StructuredSelection) viewer
				.getSelection()).getFirstElement();
		boolean result = MessageDialog.openConfirm(shell,
				"Potvrzení smazání objednávky",
				"Opravdu chcete smazat tuto objednávku?");
		if (result) {
			Transaction tx = HibernateHelper.getInstance().beginTransaction();
			pozadavekDAO.delete(selectedObjednavka.getPozadavek());
			objednavkaDAO.delete(selectedObjednavka);
			tx.commit();

			eventBroker.send(EventConstants.REFRESH_VIEWERS, "");
		}
		;

	}

	public static String[] getComboItems(boolean withUkonceno) {
		Vector<String> result = new Vector<String>(typyHashMap.size());
		for (Integer key : typyHashMap.keySet()) {
			if (!withUkonceno && key.equals(Objednavka.FAZE_UKONCENO))
				continue;
			else
				result.add(key, typyHashMap.get(key));
		}
		return result.toArray(new String[result.size()]);

	}

	@Inject
	@Optional
	void refreshInput(@UIEventTopic(EventConstants.REFRESH_VIEWERS) String o) {
		final StructuredSelection selection = (StructuredSelection) viewer
				.getSelection();
		Runnable job = new Runnable() {
			public void run() {
				viewer.setInput(getModelData());
				viewer.setInput(getModelData());
				if (!((ArrayList) viewer.getInput()).contains(selection
						.getFirstElement())) {
					viewer.getTable().select(-1);

					eventBroker.send(EventConstants.EMPTY_OBJEDNAVKA_SEND,
							EventConstants.EMPTY_OBJEDNAVKA_SEND);
				}
			};
		};

		BusyIndicator.showWhile(shell.getDisplay(), job);
	}

	@PreDestroy
	protected void preDestroy() {
		eventBroker.send(EventConstants.DISPOSE_DETAIL, "");
	}

	protected void enableMenuItems(boolean enable) {
		if (editItem != null)
			editItem.setEnabled(enable);
		if (smazatItem != null)
			smazatItem.setEnabled(enable);

	}
}
