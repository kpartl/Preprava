package cz.kpartl.preprava.sorter;

import java.math.BigDecimal;

import org.eclipse.jface.viewers.Viewer;

import cz.kpartl.preprava.model.Objednavka;

public class ObjednavkaTableViewerComparator extends
		PozadavekTableViewerComparator {
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		Objednavka o1 = (Objednavka) e1;
		Objednavka o2 = (Objednavka) e2;

		int rc = 0;
		
		if(propertyIndex >5){
			propertyIndex = propertyIndex - 6;
			rc =  super.compare(viewer, o1.getPozadavek(), o2.getPozadavek());
			propertyIndex=propertyIndex +6;
			
			return rc;
		}
		
		switch (propertyIndex) {
		case 0:
			rc = o1.getId().compareTo(o2.getId());
			break;
		case 1:
			final String d1= o1.getDopravce()!= null ? o1.getDopravce().getNazev() : "";
			final String d2= o2.getDopravce()!= null ? o2.getDopravce().getNazev() : "";
			rc = comparator.compare(d1,d2);
			break;
		case 2: 
			final BigDecimal b1 = (BigDecimal) (o1.getCena() != null ? o1.getCena() : BigDecimal.valueOf(-1));
			final BigDecimal b2 = (BigDecimal) (o2.getCena() != null ? o2.getCena() : BigDecimal.valueOf(-1));
			rc=b1.compareTo(b2);
			break;
		case 3:
			rc=o1.getMena().compareTo(o2.getMena());
			break;
		case 4:
			rc=comparator.compare(o1.getZmena_nakladky(),o2.getZmena_nakladky());
			break;
		case 5:
			final Integer i1 = o1.getCislo_faktury_dopravce() != null ? o1.getCislo_faktury_dopravce() : -1;
			final Integer i2 = o2.getCislo_faktury_dopravce() != null ? o2.getCislo_faktury_dopravce() : -1;
			rc = i1.compareTo(i2);
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
