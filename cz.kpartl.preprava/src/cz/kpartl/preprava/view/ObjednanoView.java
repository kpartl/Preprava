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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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

	public static final String TYP_OBJEDNANO = "Objedn�no";
	public static final String TYP_PREPRAVA_ZAHAJENA = "P�eprava zah�jena";
	public static final String TYP_PREPRAVA_UKONCENA = "P�eprav ukon�ena";
	public static final String TYP_DOKLADY_KOMPLETNI = "Doklady kompletn�";
	public static final String TYP_FAKTUROVANO = "Fakturov�no";
	public static final String TYP_UKONCENO = "Ukon�eno";
	public static final String TYP_VSE = "V�e";

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
		typyHashMap.put(Objednavka.FAZE_VSE, TYP_VSE);
		typyHashMap.put(Objednavka.FAZE_UKONCENO, TYP_UKONCENO);

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
	MenuItem sparovatItem;
	MenuItem zrusitParovaniItem;

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
		if (faze != Objednavka.FAZE_VSE) {
			return objednavkaDAO.findByFaze(faze);
		} else {
			return objednavkaDAO.findNeukoncene();
		}
	}

	@Override
	protected void createColumns(final Composite parent) {
		TableViewerColumn col = createTableViewerColumn("�. objedn�vky", 110,
				columnIndex++, "��slo objedn�vky");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return String.valueOf(((Objednavka) element)
						.getCislo_objednavky());
			}
		});

		col = createTableViewerColumn("Status", 80, columnIndex++,
				"Stav objedn�vky");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return typyHashMap.get(((Objednavka) element).getFaze());
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

		col = createTableViewerColumn("M�na", 20, columnIndex++, "M�na");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Objednavka) element).getMena();
			}
		});

		col = createTableViewerColumn("Zm�na term�nu", 40, columnIndex++,
				"Zm�na term�nu nakl�dky");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Objednavka) element).getZmena_nakladky();
			}
		});

		col = createTableViewerColumn("��slo faktury", 40, columnIndex++,
				"��slo faktury dopravce");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn()
				.getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Objednavka) element).getCisloFakturyDopravceAsString();
			}
		});

		super.createColumns(parent);

		col = createTableViewerColumn("P�idru�en� objedn�vka", 40,
				columnIndex++, "P�idru�en� objedn�vka");
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
	}

	@Override
	protected void createViewer(Composite parent, Object data) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		final Label nadpisLabel = new Label(composite, SWT.NONE);
		nadpisLabel.setText("P�ehled objednan�ch p�eprav ve stavu");
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

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				if (sparovatItem != null
						&& ((StructuredSelection) viewer.getSelection()).size() == 2) {
					final Iterator it = ((StructuredSelection) viewer
							.getSelection()).iterator();
					final Objednavka obj1 = (Objednavka) it.next();
					final Objednavka obj2 = (Objednavka) it.next();
					if (obj1.getPridruzena_objednavka() == null
							&& obj2.getPridruzena_objednavka() == null) {
						sparovatItem.setEnabled(true);
					}
				} else {
					sparovatItem.setEnabled(false);
				}

				if (zrusitParovaniItem != null
						&& ((StructuredSelection) viewer.getSelection()).size() == 1) {
					final Objednavka obj = (Objednavka) ((StructuredSelection) viewer
							.getSelection()).getFirstElement();
					if (obj.getPridruzena_objednavka() != null)
						zrusitParovaniItem.setEnabled(true);
					else
						zrusitParovaniItem.setEnabled(false);
				}
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
		editItem.setText("Editovat objedn�vku");
		editItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				editSelectedObjednavka();
			}
		});

		smazatItem = new MenuItem(parent, SWT.PUSH);
		smazatItem.setText("Smazat objedn�vku");
		smazatItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				deleteSelectedObjednavka();
			}
		});

		smazatItem.setImage((Image) context.get(Login.DELETE_ICON));
		editItem.setImage((Image) context.get(Login.EDIT_ICON));

		sparovatItem = new MenuItem(parent, SWT.PUSH);
		sparovatItem.setText("Sp�rovat objedn�vky");
		sparovatItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				sparujSelectedObjednavky();
			}
		});
		zrusitParovaniItem = new MenuItem(parent, SWT.PUSH);
		zrusitParovaniItem.setText("Zru�it sp�rov�n�");
		zrusitParovaniItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				zrusSparovani();
			}
		});

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

		editMenuItem.setTooltip("Editovat objedn�vku");
		smazatMenuItem.setTooltip("Smazat objedn�vku");

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
				"Potvrzen� smaz�n� objedn�vky",
				"Opravdu chcete smazat tuto objedn�vku?");
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
				tx.rollback();
			}

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
		sparovatItem.setEnabled(false);
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
		objednavkaDAO.update(obj1);
		objednavkaDAO.update(obj2);
		tx.commit();
		eventBroker.send(EventConstants.REFRESH_VIEWERS, "");
	}

	protected void zrusSparovani() {
		final Objednavka obj = (Objednavka) ((StructuredSelection) viewer
				.getSelection()).getFirstElement();
		final Objednavka refObj = obj.getPridruzena_objednavka();
		obj.setPridruzena_objednavka(null);
		refObj.setPridruzena_objednavka(null);
		final Transaction tx = HibernateHelper.getInstance().beginTransaction();
		objednavkaDAO.update(obj);
		objednavkaDAO.update(refObj);
		tx.commit();
		eventBroker.send(EventConstants.REFRESH_VIEWERS, "");

	}
}
