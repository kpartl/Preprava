package cz.kpartl.preprava.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kpartl.preprava.dao.ObjednavkaDAO;
import cz.kpartl.preprava.dao.PozadavekDAO;
import cz.kpartl.preprava.dialog.FormularDialog;
import cz.kpartl.preprava.dialog.NovaObjednavkaDialog;
import cz.kpartl.preprava.model.Dopravce;
import cz.kpartl.preprava.model.Objednavka;
import cz.kpartl.preprava.model.User;
import cz.kpartl.preprava.sorter.ObjednavkaTableViewerComparator;
import cz.kpartl.preprava.sorter.TableViewerComparator;
import cz.kpartl.preprava.util.EventConstants;
import cz.kpartl.preprava.util.HibernateHelper;
import cz.kpartl.preprava.util.Login;
import cz.kpartl.preprava.util.PrintHelper;

public class ObjednanoView extends AbstractTableView {

	public static final String ID = "cz.kpartl.preprava.part.tablepartobjednane";

	public static final String TYP_OBJEDNANO = "Objednáno";
	public static final String TYP_PREPRAVA_ZAHAJENA = "Pøeprava zahájena";
	public static final String TYP_PREPRAVA_UKONCENA = "Pøeprav ukonèena";
	public static final String TYP_DOKLADY_KOMPLETNI = "Doklady kompletní";
	public static final String TYP_FAKTUROVANO = "Fakturováno";
	public static final String TYP_UKONCENO = "Ukonèeno";
	public static final String TYP_VSE = "Vše";
	

	public static final HashMap<String, Integer> typyHashMap;

	static {
		typyHashMap = new HashMap<String, Integer>();

		typyHashMap.put(TYP_OBJEDNANO,Objednavka.FAZE_OBJEDNANO);
		typyHashMap.put(TYP_PREPRAVA_ZAHAJENA,Objednavka.FAZE_PREPRAVA_ZAHAJENA
				);
		typyHashMap.put(TYP_PREPRAVA_UKONCENA,Objednavka.FAZE_PREPRAVA_UKONCENA
				);
		typyHashMap.put(TYP_DOKLADY_KOMPLETNI,Objednavka.FAZE_DOKLADY_KOMPLETNI
				);
		typyHashMap.put(TYP_FAKTUROVANO,Objednavka.FAZE_FAKTUROVANO );
		//typyHashMap.put(Objednavka.FAZE_VSE, TYP_VSE);
		typyHashMap.put(TYP_UKONCENO,Objednavka.FAZE_UKONCENO);

	}

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected ObjednavkaDAO objednavkaDAO;
	protected PozadavekDAO pozadavekDAO;

	private Combo typCombo;
	protected Shell shell;
	private String faze = TYP_OBJEDNANO;

	EPartService partService;
	MPart pozadavekDetailView;
	MPart objednavkaDetailView;

	MenuItem editItem;
	MenuItem smazatItem;
	MenuItem sparovatItem;
	MenuItem zrusitParovaniItem;
	MenuItem tisknoutItem;

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
		if (!TYP_VSE.equals(faze)) {			
			return objednavkaDAO.findByFaze(typyHashMap.get(faze));
		} else {
			return objednavkaDAO.findNeukoncene();
		}
	}

	@Override
	protected void createColumns(final Composite parent) {
		TableViewerColumn col = createTableViewerColumn("È. objednávky", 110,
				columnIndex++, "Èíslo objednávky");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return String.valueOf(((Objednavka) element)
						.getCislo_objednavky());
			}
		});

		col = createTableViewerColumn("Pøidružená objednávka", 40,
				columnIndex++, "Pøidružená objednávka");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				final Objednavka obj = ((Objednavka) element)
						.getPridruzena_objednavka();
				if (obj != null) {
					return String.valueOf(obj.getCislo_objednavky());
				} else
					return "";
			}
		});

		col = createTableViewerColumn("Status", 80, columnIndex++,
				"Stav objednávky");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				//return typyHashMap.get(((Objednavka) element).getFaze());
				return getFazeKey(((Objednavka) element).getFaze());
			}
		});

		col = createTableViewerColumn("Dopravce", 110, columnIndex++,
				"Dopravce");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return notNullStr(((Objednavka) element).getDod_nazev());

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

		typCombo.setItems(getComboItems());
		typCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				faze = typCombo.getText();
				viewer.setInput(getModelData());
				viewer.refresh();
				if (viewer.getSelection().isEmpty()) {
					viewer.getTable().select(0);
				}
				final Objednavka selectedObjednavka = (Objednavka) ((StructuredSelection) viewer
						.getSelection()).getFirstElement();
				if (selectedObjednavka != null
						&& ((StructuredSelection) viewer.getSelection()).size() == 1) {
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

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				enableMenuItems(((StructuredSelection) viewer.getSelection())
						.size() == 1);
			}
		});

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

		sparovatItem = new MenuItem(parent, SWT.PUSH);
		sparovatItem.setText("Spárovat objednávky");
		sparovatItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				sparujSelectedObjednavky();
			}
		});
		zrusitParovaniItem = new MenuItem(parent, SWT.PUSH);
		zrusitParovaniItem.setText("Zrušit spárování");
		zrusitParovaniItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				zrusSparovani();
			}
		});

		tisknoutItem = new MenuItem(parent, SWT.PUSH);
		tisknoutItem.setText("Tisknout");
		tisknoutItem.setImage((Image) context.get(Login.TISK_ICON));
		tisknoutItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				tiskVybraneObjednavky();
			}
		});

	}

	public void tiskVybraneObjednavky() {
		final PrintHelper printHelper = new PrintHelper(shell, context);
		printHelper
				.tiskVybraneObjednavky((Objednavka) ((StructuredSelection) viewer
						.getSelection()).getFirstElement());
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
		// tisknoutMenuItem.setEnabled(true);
		tisknoutMenuItem.setVisible(true);

		editMenuItem.setTooltip("Editovat objednávku");
		smazatMenuItem.setTooltip("Smazat objednávku");
		tisknoutMenuItem.setTooltip("Tisknout objednávku");

		if (!objednavkaDetailView.isVisible())
			objednavkaDetailView.setVisible(true);
		if (pozadavekDetailView.isVisible())
			pozadavekDetailView.setVisible(false);
		// objednavkaDetailView.setToBeRendered(true);

		if (viewer.getTable().getSelectionIndex() < 0)
			viewer.getTable().setSelection(0);

		final Object selectedObject = (((StructuredSelection) viewer
				.getSelection()).getFirstElement());
		if (selectedObject != null
				&& ((StructuredSelection) viewer.getSelection()).size() == 1) {
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
			try {
				pozadavekDAO.delete(selectedObjednavka.getPozadavek());
				final Objednavka pridruzenaObj = selectedObjednavka
						.getPridruzena_objednavka();
				if (pridruzenaObj != null) {
					pridruzenaObj.setPridruzena_objednavka(null);
					objednavkaDAO.update(pridruzenaObj);
				}
				objednavkaDAO.delete(selectedObjednavka);
				tx.commit();
			} catch (Exception e) {
				logger.error("Nelze smazat objednavku", e);
				MessageDialog.openError(shell, "Chyba pøi zápisu do databáze",
						"Nepodaøilo se smazat objednávku.");
				tx.rollback();
			}

			eventBroker.send(EventConstants.REFRESH_VIEWERS, "");
		}
		;

	}

	public static String[] getComboItems() {
		return new String[] {TYP_OBJEDNANO,TYP_PREPRAVA_ZAHAJENA,TYP_PREPRAVA_UKONCENA,TYP_DOKLADY_KOMPLETNI,TYP_FAKTUROVANO, TYP_VSE};		
	}

	@Inject
	@Optional
	void refreshInput(@UIEventTopic(EventConstants.REFRESH_VIEWERS) String o) {
		if (isBeingDisposed)
			return;

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
		if (sparovatItem != null)
			sparovatItem.setEnabled(false);
		if (tisknoutItem != null)
			tisknoutItem.setEnabled(enable);
		if (zrusitParovaniItem != null)
			zrusitParovaniItem.setEnabled(false);
		if (((StructuredSelection) viewer.getSelection()).size() == 2) {
			final Objednavka obj1 = (Objednavka) ((StructuredSelection) viewer
					.getSelection()).iterator().next();
			final Objednavka obj2 = (Objednavka) ((StructuredSelection) viewer
					.getSelection()).iterator().next();
			if (obj1.getPridruzena_objednavka() == null
					&& obj2.getPridruzena_objednavka() == null) {
				sparovatItem.setEnabled(true);
			}
		}

		if (((StructuredSelection) viewer.getSelection()).size() == 1) {
			final Objednavka obj1 = (Objednavka) ((StructuredSelection) viewer
					.getSelection()).getFirstElement();
			if (obj1.getPridruzena_objednavka() != null)
				zrusitParovaniItem.setEnabled(true);
		}

	}

	public void sparujSelectedObjednavky() {
		final Iterator it = ((StructuredSelection) viewer.getSelection())
				.iterator();
		final Objednavka obj1 = (Objednavka) it.next();
		final Objednavka obj2 = (Objednavka) it.next();

		obj1.setPridruzena_objednavka(obj2);
		obj2.setPridruzena_objednavka(obj1);

		final Transaction tx = HibernateHelper.getInstance().beginTransaction();
		try {
			objednavkaDAO.update(obj1);
			objednavkaDAO.update(obj2);
			tx.commit();
			eventBroker.send(EventConstants.REFRESH_VIEWERS, "");
		} catch (Exception e) {
			logger.error("Nelze sparovat objednavky", e);
			MessageDialog.openError(shell, "Chyba pøi zápisu do databáze",
					"Nepodaøilo se spárovat objednávky.");
			tx.rollback();
		}
	}

	protected void zrusSparovani() {
		final Objednavka obj = (Objednavka) ((StructuredSelection) viewer
				.getSelection()).getFirstElement();
		final Objednavka refObj = obj.getPridruzena_objednavka();
		obj.setPridruzena_objednavka(null);
		refObj.setPridruzena_objednavka(null);
		final Transaction tx = HibernateHelper.getInstance().beginTransaction();
		try {
			objednavkaDAO.update(obj);
			objednavkaDAO.update(refObj);
			tx.commit();
			eventBroker.send(EventConstants.REFRESH_VIEWERS, "");
		} catch (Exception e) {
			logger.error("Nelze zrusit parovani objednavek", e);
			tx.rollback();
			MessageDialog.openError(shell, "Chyba pøi zápisu do databáze",
					"Nepodaøilo se zrušit spárování objednávek.");
		}

	}
	
	public static String getFazeKey(Integer value){
		for(String key:typyHashMap.keySet()){
			if(typyHashMap.get(key) == value) return key;
		}
		
		return "";
	}
	
	public static String notNullStr(String text) {
		return text != null ? text : "";
	}

}
