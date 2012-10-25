package cz.kpartl.preprava.sorter;

import org.eclipse.jface.viewers.Viewer;

import cz.kpartl.preprava.model.Dopravce;

public class DopravceTableViewerComparator extends TableViewerComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		Dopravce d1 = (Dopravce) e1;
		Dopravce d2 = (Dopravce) e2;

		int rc = 0;

		switch (propertyIndex) {
		case 0:
			rc = comparator.compare(d1.getNazev(), d2.getNazev());
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
