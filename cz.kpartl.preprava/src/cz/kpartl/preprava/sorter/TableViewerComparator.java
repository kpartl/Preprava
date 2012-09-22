package cz.kpartl.preprava.sorter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import cz.kpartl.preprava.model.Pozadavek;



public class TableViewerComparator extends ViewerComparator {
	private int propertyIndex;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;

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
	public int compare(Viewer viewer, Object e1, Object e2) {
		Pozadavek p1 = (Pozadavek) e1;
		Pozadavek p2 = (Pozadavek) e2;
		int rc = 0;
		switch (propertyIndex) {
		case 0:
			rc = p1.getDatum().compareTo(p2.getDatum());
			break;
		case 1:
			rc = p1.getDatum_nakladky().compareTo(p2.getDatum_nakladky());
			break;
		case 2:
			rc = p1.getDatum_vykladky().compareTo(p2.getDatum_vykladky());
			break;
		case 3:
			if (p1.getJe_termin_konecny() == p2.getJe_termin_konecny()) {
				rc = 0;
			} else
				rc = (p1.getJe_termin_konecny() ? 1 : -1);
			break;
		default:
			rc = 0;
		}
		// If descending order, flip the direction
		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}

} 
