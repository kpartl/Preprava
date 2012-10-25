package cz.kpartl.preprava.sorter;

import java.text.ParseException;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kpartl.preprava.model.Pozadavek;
import cz.kpartl.preprava.util.CzechComparator;

public class TableViewerComparator extends ViewerComparator {
	protected int propertyIndex;
	protected static final int DESCENDING = 1;
	protected int direction = DESCENDING;

	static final Logger logger = LoggerFactory
			.getLogger(TableViewerComparator.class);
	protected static CzechComparator comparator = null;

	static {
		try {
			comparator = new CzechComparator();
		} catch (ParseException e) {
			logger.error("Chyba pri parsovani retezce", e);
			e.printStackTrace();
		}
	}

	public TableViewerComparator() {
		this.propertyIndex = 0;
		direction = DESCENDING;
	}

	public int getDirection() {
		return direction == 1 ? SWT.DOWN : SWT.UP;
	}

	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		} else {
			// New column; do an ascending sort
			this.propertyIndex = column;
			direction = DESCENDING;
		}
	}

	@Override
	public void sort(Viewer viewer, Object[] elements) {
		super.sort(viewer, elements);
	}

}
