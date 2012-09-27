package cz.kpartl.preprava.view;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.Composite;

import cz.kpartl.preprava.dao.ObjednavkaDAO;
import cz.kpartl.preprava.dao.PozadavekDAO;
import cz.kpartl.preprava.model.Objednavka;
import cz.kpartl.preprava.model.Pozadavek;
import cz.kpartl.preprava.model.Dopravce;;

public class ObjednanoView extends AbstractTableView {
	
	private ObjednavkaDAO objednavkaDAO;
	
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
		return objednavkaDAO.findByFaze(Objednavka.FAZE_OBJEDNANO);
	}
	
	@Override
	protected void createColumns(final Composite parent) {				
		TableViewerColumn col = createTableViewerColumn("». objedn·vky", 110, columnIndex++, "»Ìslo objedn·vky");
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
		
		col = createTableViewerColumn("MÏna", 40, columnIndex++, "MÏna");
		col.setLabelProvider(new TooltipColumnLabelProvider(col.getColumn().getToolTipText()) {
			@Override
			public String getText(Object element) {
				return ((Objednavka) element).getMena();
			}
		});
		
		super.createColumns(parent);
		
	
	}

}
