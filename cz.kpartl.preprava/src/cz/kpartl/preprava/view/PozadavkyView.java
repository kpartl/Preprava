package cz.kpartl.preprava.view;

import java.util.ArrayList;

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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kpartl.preprava.dao.PozadavekDAO;
import cz.kpartl.preprava.dialog.NovaObjednavkaDialog;
import cz.kpartl.preprava.dialog.NovyPozadavekDialog;
import cz.kpartl.preprava.model.Pozadavek;
import cz.kpartl.preprava.model.User;
import cz.kpartl.preprava.sorter.PozadavekTableViewerComparator;
import cz.kpartl.preprava.sorter.TableViewerComparator;
import cz.kpartl.preprava.util.EventConstants;
import cz.kpartl.preprava.util.HibernateHelper;
import cz.kpartl.preprava.util.Login;

@SuppressWarnings("restriction")
public class PozadavkyView extends AbstractTableView {

	final Logger logger = LoggerFactory.getLogger(this.getClass());

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
		cz.kpartl.preprava.util.HibernateHelper.getInstance()
		.getSession().clear();
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
		newItem = new MenuItem(parent, SWT.PUSH);
		newItem.setText("Vytvoøit nový požadavek");
		newItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				new NovyPozadavekDialog(shell, context, eventBroker).open();

			}
		});

		editItem = new MenuItem(parent, SWT.PUSH);
		editItem.setText("Editovat požadavek");
		editItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				editSelectedPozadavek();
			}
		});

		smazatItem = new MenuItem(parent, SWT.PUSH);
		smazatItem.setText("Smazat požadavek");
		smazatItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				deleteSelectedPozadavek();
			}
		});

		newItem.setImage((Image) context.get(Login.ADD_ICON));
		smazatItem.setImage((Image) context.get(Login.DELETE_ICON));
		editItem.setImage((Image) context.get(Login.EDIT_ICON));

		if (((User) context.get(User.CONTEXT_NAME)).isAdministrator()) {
			new MenuItem(parent, SWT.SEPARATOR);

			objednavkaItem = new MenuItem(parent, SWT.PUSH);
			objednavkaItem.setText("Pøevést požadavek na objednávku");
			objednavkaItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					prevedSelectedPozadavek();
				}
			});
			objednavkaItem.setImage((Image) context.get(Login.OBJEDNAVKA_ICON));
			objednavkaItem.setEnabled(true);
		}
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
		prevestMenuItem.setVisible(((User) context.get(User.CONTEXT_NAME))
				.isAdministrator());

		novyMenuItem.setTooltip("Vytvoøit nový požadavek");
		editMenuItem.setTooltip("Editovat požadavek");
		smazatMenuItem.setTooltip("Smazat požadavek");
		prevestMenuItem.setTooltip("Pøevést požadavek na objednávku");

		if (!pozadavekDetailView.isVisible())
			pozadavekDetailView.setVisible(true);
		if (objednavkaDetailView.isVisible())
			objednavkaDetailView.setVisible(false);

		final Object selectedObject = (((StructuredSelection) viewer
				.getSelection()).getFirstElement());
		if (selectedObject != null) {
			enableMenuItems(true);
			eventBroker.send(EventConstants.POZADAVEK_SELECTION_CHANGED,
					selectedObject);
		} else {
			enableMenuItems(false);
			eventBroker.send(EventConstants.EMPTY_POZADAVEK_SEND,
					EventConstants.EMPTY_POZADAVEK_SEND);
		}

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
			eventBroker.send(EventConstants.REFRESH_VIEWERS, "");
		}
		;
	}

	public void prevedSelectedPozadavek() {
		Pozadavek selectedPozadavek = (Pozadavek) ((StructuredSelection) viewer
				.getSelection()).getFirstElement();

		NovaObjednavkaDialog novaObjednavkaDialog = new NovaObjednavkaDialog(
				shell, context, selectedPozadavek, eventBroker);
		if (novaObjednavkaDialog.open() == Window.OK) {
			// eventBroker.send(EventConstants.REFRESH_VIEWERS, "");
			eventBroker.send(EventConstants.POZADAVEK_SELECTION_CHANGED,
					selectedPozadavek);

			partService.showPart("cz.kpartl.preprava.part.tablepartobjednane",
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
		StructuredSelection selection = (StructuredSelection) viewer
				.getSelection();
		viewer.setInput(getModelData());
		if (!((ArrayList) viewer.getInput()).contains(selection
				.getFirstElement())) {
			viewer.getTable().select(-1);

			eventBroker.send(EventConstants.EMPTY_POZADAVEK_SEND,
					EventConstants.EMPTY_POZADAVEK_SEND);
		}
	}

	@Inject
	@Optional
	void selectionChanged(
			@UIEventTopic(EventConstants.POZADAVEK_SELECTION_CHANGED) Pozadavek p) {
		for (int i = 0; i < viewer.getTable().getItemCount(); i++) {
			if (p.getId().equals(
					((Pozadavek) viewer.getTable().getItem(i).getData())
							.getId())) {
				viewer.getTable().select(i);
				break;
			}
		}

	}

	protected void enableMenuItems(boolean enable) {
		editItem.setEnabled(enable);
		smazatItem.setEnabled(enable);
		if(objednavkaItem != null)objednavkaItem.setEnabled(enable);

		logger.debug("EnableMenuItems " + enable);
	}

}
