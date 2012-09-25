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

	private PozadavekDAO pozadavekDAO;

	@Inject
	public PozadavkyView(Composite parent,
			@Optional IStylingEngine styleEngine,
			@Optional PozadavekDAO pozadavekDAO) {
		super(styleEngine);

		this.pozadavekDAO = pozadavekDAO;
		

		createPartControl(parent);
	}

	

	// This will create the columns for the table
	protected void createColumns(final Composite parent) {

		headerMenu = new Menu(parent.getShell(), SWT.POP_UP);
		viewer.getTable().setMenu(headerMenu);
		

		TableViewerColumn col = createTableViewerColumn("Datum", 70,
				0, "Datum požadavku");

		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn().getToolTipText()) {
			@Override
			public String getText(Object element) {
				return new SimpleDateFormat("dd.MM.yyyy")
						.format(((Pozadavek) element).getDatum());
			}					
		});

		col = createTableViewerColumn("Datum nakládky", 90, 1, "Požadované datum nakládky");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn().getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Pozadavek) element).getDatum_nakladky();
			}
		});

		col = createTableViewerColumn("Datum vykládky", 90, 2, "Požadované datum vykládky");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn().getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Pozadavek) element).getDatum_vykladky();
			}
		});

		col = createTableViewerColumn("Odkud", 150, 3, "Výchozí destinace");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn().getToolTipText()) {
			@Override
			public String getText(Object element) {
				final Destinace destinace_z = ((Pozadavek) element)
						.getDestinace_z();
				if (destinace_z == null)
					return "";
				else
					return destinace_z
							.getNazev()
							.concat("(")
							.concat(String.valueOf(destinace_z.getCislo())
									.concat(")"));

			}
		});

		col = createTableViewerColumn("Kam", 150, 4,"Cílová destinace");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn().getToolTipText()) {
			@Override
			public String getText(Object element) {
				final Destinace destinace_do = ((Pozadavek) element)
						.getDestinace_do();
				if (destinace_do == null)
					return "";
				else
					return destinace_do
							.getNazev()
							.concat(" (")
							.concat(String.valueOf(destinace_do.getCislo())
									.concat(")"));

			}
		});

		col = createTableViewerColumn("Hmotnost", 60, 5, "Celková hmotnost zásilky");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn().getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Pozadavek) element).getCelkova_hmotnost();
			}
		});

		col = createTableViewerColumn("Termín koneèný?", 120, 6, "Je termín koneèný?");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn().getToolTipText()) {
			@Override
			public String getText(Object element) {
				return "";
			}

			@Override
			public Image getImage(Object element) {
				if (((Pozadavek) element).getJe_termin_konecny()) {
					return CHECKED;
				}
				return UNCHECKED;
			}
		});

		col = createTableViewerColumn("TAXI", 50, 7,"TAXI?");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn().getToolTipText()) {
			@Override
			public String getText(Object element) {
				return "";
			}

			@Override
			public Image getImage(Object element) {
				if (((Pozadavek) element).getTaxi()) {
					return CHECKED;
				}
				return UNCHECKED;
			}
		});

		col = createTableViewerColumn("Kontakt ODKUD", 200, 8,"Kontaktní osoba u výchozí destinace");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn().getToolTipText()) {
			@Override
			public String getText(Object element) {
				final Destinace destinace_z = ((Pozadavek) element)
						.getDestinace_z();
				if (destinace_z == null)
					return "";
				else
					return (destinace_z.getKontaktni_osoba()).concat(" (")
							.concat(destinace_z.getKontakt()).concat(")");

			}
		});
		
		col = createTableViewerColumn("Kontakt KAM", 200, 8, "Kontaktní osoba u cílové destinace");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn().getToolTipText()) {
			@Override
			public String getText(Object element) {
				final Destinace destinace_do = ((Pozadavek) element)
						.getDestinace_do();
				if (destinace_do == null)
					return "";
				else
					return (destinace_do.getKontaktni_osoba()).concat(" (")
							.concat(destinace_do.getKontakt()).concat(")");

			}
		});

		col = createTableViewerColumn("Hodina nakládky", 100, 9, "Hodina nakládky u dodavatele");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn().getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Pozadavek) element).getHodina_nakladky();
			}
		});
		
		col = createTableViewerColumn("Zadavatel", 80, 10, "Zadavatel");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn().getToolTipText()) {
			@Override
			public String getText(Object element) {
				final User zadavatel = ((Pozadavek) element)
						.getZadavatel();
				if (zadavatel == null)
					return "";
				else
					return zadavatel.getUsername();

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
			}
		});

		// Work around for 4.0 Bug of not cleaning up on Window-close
		viewer.getControl().addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				unhookEvents();
			}
		});
	}

	

	@Override
	protected Object getModelData() {
		return pozadavekDAO.findNeobjednane();
	}	

}

