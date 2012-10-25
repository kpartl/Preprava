package cz.kpartl.preprava.sorter;

import org.eclipse.jface.viewers.Viewer;

import cz.kpartl.preprava.model.Dopravce;
import cz.kpartl.preprava.model.User;

public class UzivatelTableViewerComparator extends TableViewerComparator {
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		User d1 = (User) e1;
		User d2 = (User) e2;

		int rc = 0;

		switch (propertyIndex) {
		case 0:
			rc = comparator.compare(d1.getUsername(), d2.getUsername());
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
