package cz.kpartl.preprava.sorter;

import java.text.Collator;
import java.text.ParseException;

import org.eclipse.jface.viewers.Viewer;

import cz.kpartl.preprava.model.Destinace;
import cz.kpartl.preprava.util.CzechComparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DestinaceTableViewerComparator extends TableViewerComparator {
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		Destinace d1 = (Destinace) e1;
		Destinace d2 = (Destinace) e2;

		int rc = 0;

		switch (propertyIndex) {
		case 0:
			rc = comparator.compare(d1.getNazev(),d2.getNazev());
			break;
		case 1:
			final Integer i1 = d1.getCislo();
			final Integer i2 = d2.getCislo();
			rc = i1.compareTo(i2);
			break;
		case 2:
			rc = d1.getUlice().compareTo(d2.getUlice());
			break;		
		case 3:
			rc = comparator.compare(d1.getMesto(),d2.getMesto());
			break;
		case 4:
			final Integer p1 = d1.getPSC() != null ? d1.getPSC() : -1;
			final Integer p2 = d2.getPSC() != null ? d2.getPSC() : -1;
			rc = p1.compareTo(p2);
			break;
		case 5:
			rc = comparator.compare(d1.getKontaktni_osoba(), d2.getKontaktni_osoba());			
			break;
		case 6:
			rc=comparator.compare(d1.getKontakt(),d2.getKontakt());
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
