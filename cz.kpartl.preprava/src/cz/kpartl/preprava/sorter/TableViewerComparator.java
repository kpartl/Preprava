package cz.kpartl.preprava.sorter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import cz.kpartl.preprava.model.Pozadavek;

public class TableViewerComparator extends ViewerComparator {
	protected int propertyIndex;
	protected static final int DESCENDING = 1;
	protected int direction = DESCENDING;

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

	

}
