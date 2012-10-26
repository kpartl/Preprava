package cz.kpartl.preprava.view;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
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

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MApplicationFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.impl.HandledToolItemImpl;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.window.Window;
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

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.part.ViewPart;
import org.hibernate.Transaction;
import org.osgi.service.event.EventHandler;

import cz.kpartl.preprava.dao.DestinaceDAO;
import cz.kpartl.preprava.dao.PozadavekDAO;
import cz.kpartl.preprava.dialog.NovaObjednavkaDialog;
import cz.kpartl.preprava.dialog.NovyPozadavekDialog;
import cz.kpartl.preprava.model.Destinace;
import cz.kpartl.preprava.model.Pozadavek;
import cz.kpartl.preprava.model.User;

import cz.kpartl.preprava.sorter.PozadavekTableViewerComparator;
import cz.kpartl.preprava.sorter.TableViewerComparator;
import cz.kpartl.preprava.util.EventConstants;
import cz.kpartl.preprava.util.HibernateHelper;
import cz.kpartl.preprava.util.Login;
import cz.kpartl.preprava.Activator;

@SuppressWarnings("restriction")
public class PozadavkyView extends AbstractTableView {

	public static final String ID = "cz.kpartl.preprava.part.tablepartpozadane";

	private PozadavekDAO pozadavekDAO;

	private Shell shell;

	@Inject
	@Optional
	EPartService partService;
	MPart objednavkaDetailView;
	MPart pozadavekDetailView;

	@Inject
	public PozadavkyView(Composite parent,
			@Optional IStylingEngine styleEngine, IEclipseContext context,
			EPartService partService) {

		super(styleEngine);
		this.context = context;
		context.set(ID, this);
		shell = parent.getShell();

		this.pozadavekDAO = context.get(PozadavekDAO.class);
		objednavkaDetailView = partService.findPart(ObjednavkaDetailView.ID);
		pozadavekDetailView = partService.findPart(PozadavekDetailView.ID);

		createPartControl(parent);
	}

	@Override
	protected Object getModelData() {		
		return pozadavekDAO.findNeobjednane();
	}

	public TableViewer getViewer() {
		return viewer;
	}

	@Override
	protected void createViewer(Composite parent, Object data) {
		final Label nadpisLabel = new Label(parent, SWT.NONE);
		nadpisLabel.setText("Pøehled požadavkù na pøepravu");
		nadpisLabel.setFont(JFaceResources.getHeaderFont());
		super.createViewer(parent, data);
		createMenuItems(headerMenu);
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				Pozadavek selectedPozadavek = (Pozadavek) ((StructuredSelection) viewer
						.getSelection()).getFirstElement();
				if (selectedPozadavek == null)
					return;
				NovyPozadavekDialog dialog = new NovyPozadavekDialog(shell,
						context, selectedPozadavek, eventBroker);
				if (dialog.open() == Window.OK) {
					// refreshInputData();
				}

			}

		});
	}

	protected void createMenuItems(Menu parent) {
		final MenuItem newItem = new MenuItem(parent, SWT.PUSH);
		newItem.setText("Vytvoøit nový požadavek");
		newItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				new NovyPozadavekDialog(shell, context, eventBroker).open();

			}
		});

		final MenuItem editItem = new MenuItem(parent, SWT.PUSH);
		editItem.setText("Editovat požadavek");
		editItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				editSelectedPozadavek();
			}
		});

		final MenuItem smazatItem = new MenuItem(parent, SWT.PUSH);
		smazatItem.setText("Smazat požadavek");
		smazatItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				deleteSelectedPozadavek();
			}
		});

		
		new MenuItem(parent, SWT.SEPARATOR);

		final MenuItem objednavkaItem = new MenuItem(parent, SWT.PUSH);
		objednavkaItem.setText("Pøevést požadavek na objednávku");
		objednavkaItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
			prevedSelectedPozadavek();
			}
		});

		newItem.setImage((Image) context.get(Login.ADD_ICON));
		smazatItem.setImage((Image) context.get(Login.DELETE_ICON));
		editItem.setImage((Image) context.get(Login.EDIT_ICON));
		objednavkaItem.setImage((Image) context.get(Login.OBJEDNAVKA_ICON));
		objednavkaItem.setEnabled(((User) context.get(User.CONTEXT_NAME)).isAdministrator());

	}

	@Override
	protected TableViewerComparator getComparator() {
		return new PozadavekTableViewerComparator();
	}
	
	@Focus
	public void setFocus() {		
		super.setFocus();
		novyMenuItem.setVisible(true);
		editMenuItem.setVisible(true);
		smazatMenuItem.setVisible(true);
		prevestMenuItem.setVisible(true);

		novyMenuItem.setTooltip("Vytvoøit nový požadavek");
		editMenuItem.setTooltip("Editovat požadavek");
		smazatMenuItem.setTooltip("Smazat požadavek");
		prevestMenuItem.setTooltip("Pøevést požadavek na objednávku");

		if(!pozadavekDetailView.isVisible()) pozadavekDetailView.setVisible(true);
		if(objednavkaDetailView.isVisible()) objednavkaDetailView.setVisible(false);	
		
		final Object selectedObject = (((StructuredSelection) viewer.getSelection())
				.getFirstElement());
		if(selectedObject != null )eventBroker
				.post(EventConstants.POZADAVEK_SELECTION_CHANGED,
						selectedObject);
		

	}

	public void editSelectedPozadavek() {
		Pozadavek selectedPozadavek = (Pozadavek) ((StructuredSelection) viewer
				.getSelection()).getFirstElement();
		if (selectedPozadavek != null)
			new NovyPozadavekDialog(shell, context, selectedPozadavek,
					eventBroker).open();
	}

	public void deleteSelectedPozadavek() {
		Pozadavek selectedPozadavek = (Pozadavek) ((StructuredSelection) viewer
				.getSelection()).getFirstElement();
		boolean result = MessageDialog.openConfirm(shell,
				"Potvrzení smazání požadavku",
				"Opravdu chcete smazat tento požadavek?");
		if (result) {
			Transaction tx = HibernateHelper.getInstance().beginTransaction();
			pozadavekDAO.delete(selectedPozadavek);
			tx.commit();
			eventBroker.post(EventConstants.REFRESH_VIEWERS, "");
		}
		;
	}
	
	public void prevedSelectedPozadavek(){
		Pozadavek selectedPozadavek = (Pozadavek) ((StructuredSelection) viewer
				.getSelection()).getFirstElement();

		NovaObjednavkaDialog novaObjednavkaDialog = new NovaObjednavkaDialog(
				shell, context,
				selectedPozadavek, eventBroker);
		if (novaObjednavkaDialog.open() == Window.OK) {
			//eventBroker.send(EventConstants.REFRESH_VIEWERS, "");
			eventBroker.send(EventConstants.POZADAVEK_SELECTION_CHANGED, selectedPozadavek);
			
			partService.showPart(
					"cz.kpartl.preprava.part.tablepartobjednane",
					EPartService.PartState.VISIBLE);
		}

	}
	
	@PreDestroy
	protected void preDestroy() {
		eventBroker.send(EventConstants.DISPOSE_DETAIL, "");
	}
	
	
	
	@Inject
	@Optional
	void refreshInput(@UIEventTopic(EventConstants.REFRESH_VIEWERS) String s) {
		viewer.setInput(getModelData());
		if(((ArrayList)viewer.getInput()).isEmpty()){
			eventBroker.send(EventConstants.EMPTY_POZADAVEK_SEND, "");
		}
	}

}
