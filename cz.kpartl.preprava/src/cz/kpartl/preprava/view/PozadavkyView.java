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
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;

import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
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

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;

import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.part.ViewPart;
import org.osgi.service.event.EventHandler;

import cz.kpartl.preprava.dao.PozadavekDAO;
import cz.kpartl.preprava.model.Destinace;
import cz.kpartl.preprava.model.Pozadavek;
import cz.kpartl.preprava.model.User;

import cz.kpartl.preprava.sorter.TableViewerComparator;
import cz.kpartl.preprava.Activator;

@SuppressWarnings("restriction")
public class PozadavkyView extends AbstractTableView {
	
	public static final String ID = "cz.kpartl.preprava.view.PozadavkyView";

	private PozadavekDAO pozadavekDAO;

	@Inject
	public PozadavkyView(Composite parent,
			@Optional IStylingEngine styleEngine,
			@Optional PozadavekDAO pozadavekDAO) {
		super(styleEngine);

		this.pozadavekDAO = pozadavekDAO;

		createPartControl(parent);
	}

	@Override
	protected Object getModelData() {
		return pozadavekDAO.findNeobjednane();
	}
	
	@Override
	protected void createViewer(Composite parent, Object data) {
		final Label nadpisLabel = new Label(parent, SWT.NONE);
		nadpisLabel.setText("Pøehled požadavkù na pøepravu");
		nadpisLabel.setFont(JFaceResources.getHeaderFont());
		super.createViewer(parent, data);
	}

}
