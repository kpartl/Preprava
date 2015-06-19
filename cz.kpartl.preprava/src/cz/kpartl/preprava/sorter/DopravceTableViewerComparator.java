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
		case 1:
			rc = comparator.compare(d1.getUlice(), d2.getUlice());
			break;
		case 2:
			rc = comparator.compare(d1.getMesto(), d2.getMesto());
			break;
		case 3:
			rc = comparator.compare(d1.getPsc(), d2.getPsc());
			break;
		case 4:
			rc = comparator.compare(d1.getIc(), d2.getIc());
			break;
		case 5:
			rc = comparator.compare(d1.getDic(), d2.getDic());
			break;
		case 6:
			rc = comparator.compare(d1.getSap_cislo(), d2.getSap_cislo());
			break;
		case 7:
			rc = comparator.compare(d1.getKontaktni_osoba(), d2.getKontaktni_osoba());
			break;
		case 8:
			rc = comparator.compare(d1.getKontaktni_telefon(), d2.getKontaktni_telefon());
			break;
		case 9:
			rc = comparator.compare(d1.getKontakt_ostatni(), d2.getKontakt_ostatni());
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
