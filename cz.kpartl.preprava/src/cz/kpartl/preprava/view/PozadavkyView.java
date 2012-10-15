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
import org.eclipse.e4.ui.model.application.ui.menu.impl.HandledToolItemImpl;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
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
import cz.kpartl.preprava.dialog.NovyPozadavekDialog;
import cz.kpartl.preprava.model.Destinace;
import cz.kpartl.preprava.model.Pozadavek;
import cz.kpartl.preprava.model.User;

import cz.kpartl.preprava.sorter.PozadavekTableViewerComparator;
import cz.kpartl.preprava.sorter.TableViewerComparator;
import cz.kpartl.preprava.util.EventConstants;
import cz.kpartl.preprava.util.HibernateHelper;
import cz.kpartl.preprava.Activator;

@SuppressWarnings("restriction")
public class PozadavkyView extends AbstractTableView {
	
	public static final String ID = "cz.kpartl.preprava.part.tablepartpozadane";

	private PozadavekDAO pozadavekDAO;
	
	private Shell shell;
		
	
	

	@Inject
	public PozadavkyView(Composite parent,
			@Optional IStylingEngine styleEngine, IEclipseContext context)
	{
		
		super(styleEngine);
		this.context = context;
		context.set(ID, this);
		shell = parent.getShell();

		this.pozadavekDAO = context.get(PozadavekDAO.class);

		createPartControl(parent);
	}

	@Override
	protected Object getModelData() {
		return pozadavekDAO.findNeobjednane();
	}
	
	@Override
	protected void createViewer(Composite parent, Object data) {
		final Label nadpisLabel = new Label(parent, SWT.NONE);
		nadpisLabel.setText("P�ehled po�adavk� na p�epravu");
		nadpisLabel.setFont(JFaceResources.getHeaderFont());
		super.createViewer(parent, data);
		createMenuItems(headerMenu);
		viewer.addDoubleClickListener(new IDoubleClickListener()
		{
		            @Override
		            public void doubleClick(DoubleClickEvent event) {
		            	Pozadavek selectedPozadavek  = (Pozadavek) ((StructuredSelection) viewer.getSelection()).getFirstElement();
						if(selectedPozadavek==null) return;
						NovyPozadavekDialog dialog = new NovyPozadavekDialog(shell,
								context, selectedPozadavek);
						if (dialog.open() == Window.OK){
							refreshInputData();
						}
		                
		            }

		});
	}
	
	
	protected void createMenuItems(Menu parent) {
		final MenuItem newItem = new MenuItem(parent, SWT.PUSH);
		newItem.setText("Vytvo�it nov� po�adavek");
		newItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {								
				if (new NovyPozadavekDialog(shell,
						context).open() == Window.OK){
					refreshInputData();
				}
			}});
		
		
		
		final MenuItem editItem = new MenuItem(parent, SWT.PUSH);
		editItem.setText("Editovat po�adavek");		
		editItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				Pozadavek selectedPozadavek  = (Pozadavek) ((StructuredSelection) viewer.getSelection()).getFirstElement();
				if(selectedPozadavek==null) return;
				NovyPozadavekDialog dialog = new NovyPozadavekDialog(event.widget.getDisplay().getActiveShell(),
						context, selectedPozadavek);
				if (dialog.open() == Window.OK){
					refreshInputData();
				}
			}});
			
		
		
		final MenuItem smazatItem = new MenuItem(parent, SWT.PUSH);
		smazatItem.setText("Smazat po�adavek");		
		smazatItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				Pozadavek selectedPozadavek  = (Pozadavek) ((StructuredSelection) viewer.getSelection()).getFirstElement();
				boolean result = MessageDialog.openConfirm(event.widget.getDisplay().getActiveShell(), "Potvrzen� smaz�n� po�adavku", "Opravdu chcete smazat tento po�adavek?");
				if(result){
					Transaction tx =HibernateHelper.getInstance().beginTransaction(); 
					pozadavekDAO.delete(selectedPozadavek);
					tx.commit();					
					refreshInputData();
				};  
			}
		});
		
		new MenuItem(parent, SWT.SEPARATOR);
		
		final MenuItem objednavkaItem = new MenuItem(parent, SWT.PUSH);
		objednavkaItem.setText("P�ev�st po�adavek na objedn�vku");		

	}

	@Override
	protected TableViewerComparator getComparator() {
		return new PozadavekTableViewerComparator();
	}
	
	@Focus
	public void setFocus(){
		novyMenuItem.setVisible(true);
		novyMenuItem.setLabel("Nov� po�adavek");
		if(viewer.getSelection().isEmpty()) viewer.getTable().select(0);
		super.setFocus();
	}
	
	
	
	/*@Inject
	public void setPozadavek(@Named(IServiceConstants.ACTIVE_SELECTION) @Optional Pozadavek pozadavek) {
		System.out.println("SetPozadavek called");
		if( pozadavek != null ) {
		System.out.println("Pozadavek ="+pozadavek);
			//this.folder = folder;
			//viewer.setInput(folder.getSession().getMails(folder, 0, folder.getMailCount()));
		}
	}*/

}
