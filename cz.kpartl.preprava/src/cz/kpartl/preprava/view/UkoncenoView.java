package cz.kpartl.preprava.view;

import javax.inject.Inject;

import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.swt.widgets.Composite;

import cz.kpartl.preprava.dao.ObjednavkaDAO;
import cz.kpartl.preprava.model.Objednavka;

public class UkoncenoView extends ObjednanoView {

	@Inject
	public UkoncenoView(Composite parent, IStylingEngine styleEngine,
			ObjednavkaDAO objednavkaDAO) {
		super(parent, styleEngine, objednavkaDAO);
		
	}
	
	@Override
	protected void createViewer(Composite parent, Object data) {
		superCreateViewer(parent, data);
	}
	
	@Override
	protected Object getModelData() {		
		return objednavkaDAO.findByFaze(Objednavka.FAZE_UKONCENO);
	}
}
