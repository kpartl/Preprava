package cz.kpartl.preprava.view;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;

import cz.kpartl.preprava.dao.ObjednavkaDAO;
import cz.kpartl.preprava.model.Dopravce;
import cz.kpartl.preprava.model.Objednavka;
import cz.kpartl.preprava.sorter.TableViewerComparator;

public class ObjednanoView extends AbstractTableView {
	
	private static final String TYP_OBJEDNANO = "Objedn�no";
	private static final String TYP_PREPRAVA_ZAHAJENA = "P�eprava zah�jena";
	private static final String TYP_PREPRAVA_UKONCENA = "P�eprav ukon�ena";
	private static final String TYP_DOKLADY_KOMPLETNI ="Doklady kompletn�";
	private static final String TYP_FAKTUROVANO = "Fakturov�no";
	
	protected ObjednavkaDAO objednavkaDAO;
	
	private Combo typCombo;
	private String faze= TYP_OBJEDNANO;
	
	@Inject
	public ObjednanoView(Composite parent,
			@Optional IStylingEngine styleEngine,
			@Optional ObjednavkaDAO objednavkaDAO) {
		super(styleEngine);

		this.objednavkaDAO = objednavkaDAO;
		

		createPartControl(parent);
	}

	@Override
	protected Object getModelData() {
		int typ = 0;
		if(faze.equals(TYP_OBJEDNANO)) typ =Objednavka.FAZE_OBJEDNANO;
		else if(faze.equals(TYP_PREPRAVA_ZAHAJENA)) typ =Objednavka.FAZE_PREPRAVA_ZAHAJENA;
		else if(faze.equals(TYP_PREPRAVA_UKONCENA)) typ =Objednavka.FAZE_PREPRAVA_UKONCENA;
		else if(faze.equals(TYP_FAKTUROVANO)) typ =Objednavka.FAZE_FAKTUROVANO;
		else if(faze.equals(TYP_DOKLADY_KOMPLETNI)) typ =Objednavka.FAZE_DOKLADY_KOMPLETNI;
		return objednavkaDAO.findByFaze(typ);
	}
	
	@Override
	protected void createColumns(final Composite parent) {				
		TableViewerColumn col = createTableViewerColumn("�. objedn�vky", 110, columnIndex++, "��slo objedn�vky");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn().getToolTipText()) {
			@Override
			public String getText(Object element) {
				return String.valueOf(((Objednavka) element).getId());
			}
		});
		
		col = createTableViewerColumn("Dopravce", 110, columnIndex++, "Dopravce");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn().getToolTipText()) {
			@Override
			public String getText(Object element) {
				final Dopravce dopravce = ((Objednavka) element).getDopravce();
				if(dopravce == null) return "";
				
				return dopravce.getNazev();
			}
		});
		
		col = createTableViewerColumn("Cena", 50, columnIndex++, "Cena dopravy");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn().getToolTipText()) {
			@Override
			public String getText(Object element) {
				return String.valueOf(((Objednavka) element).getCena());
			}
		});
		
		col = createTableViewerColumn("M�na", 40, columnIndex++, "M�na");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn().getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Objednavka) element).getMena();
			}
		});
		
		super.createColumns(parent);		
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
		String typy[]= {TYP_OBJEDNANO,TYP_PREPRAVA_ZAHAJENA,TYP_PREPRAVA_UKONCENA, TYP_DOKLADY_KOMPLETNI, TYP_FAKTUROVANO};
		typCombo.setItems(typy);
		typCombo.addSelectionListener(new SelectionAdapter() {		
						
			@Override
			public void widgetSelected(SelectionEvent e) {
				faze = typCombo.getText();
				viewer.setInput(getModelData());
				viewer.refresh();							
			}				
		});
				
		super.createViewer(parent, data);
		
		typCombo.select(0);
	}
	
	protected void superCreateViewer(Composite parent, Object data){
		super.createViewer(parent, data);
	}
	
	protected void createMenuItem(Menu parent, final TableColumn column) {}

	@Override
	protected TableViewerComparator getComparator() {
		return new TableViewerComparator();
	}
	
	@Focus
	public void setFocus(){
		novyMenuItem.setVisible(false);
		novyMenuItem.setLabel("");
		if(viewer.getSelection().isEmpty()) viewer.getTable().select(0);
		super.setFocus();
	}

}
