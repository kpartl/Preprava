package cz.kpartl.preprava.sorter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.viewers.Viewer;

import cz.kpartl.preprava.model.Objednavka;

public class ObjednavkaTableViewerComparator extends
		PozadavekTableViewerComparator {
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		Objednavka o1 = (Objednavka) e1;
		Objednavka o2 = (Objednavka) e2;

		int rc = 0;
		
		if (!(propertyIndex < 8 || (propertyIndex >=11 && propertyIndex <= 14))) {		
			propertyIndex = propertyIndex - 8;
			rc =  super.compare(viewer, o1.getPozadavek(), o2.getPozadavek());
			propertyIndex=propertyIndex + 8;
			
			return rc;
		}
		
		switch (propertyIndex) {
		case 0:
			rc = o1.getCislo_objednavky().compareTo(o2.getCislo_objednavky());
			break;
		case 1:
			final Long l1 = o1.getPridruzena_objednavka() != null ? o1. getPridruzena_objednavka().getCislo_objednavky() : -1;
			final Long l2 = o2.getPridruzena_objednavka() != null ? o2. getPridruzena_objednavka().getCislo_objednavky() : -1;
			rc = l1.compareTo(l2);
			break;
		case 2:
			rc = Integer.valueOf(o1.getFaze()).compareTo(Integer.valueOf(o2.getFaze()));
			break;
		case 3:
			final String d1= o1.getDopravce()!= null ? o1.getDopravce().getNazev() : "";
			final String d2= o2.getDopravce()!= null ? o2.getDopravce().getNazev() : "";
			rc = comparator.compare(d1,d2);
			break;
		case 4: 
			final BigDecimal b1 = (BigDecimal) (o1.getCena() != null ? o1.getCena() : BigDecimal.valueOf(-1));
			final BigDecimal b2 = (BigDecimal) (o2.getCena() != null ? o2.getCena() : BigDecimal.valueOf(-1));
			rc=b1.compareTo(b2);
			break;
		case 5:
			rc=o1.getMena().compareTo(o2.getMena());
			break;
		case 6:
			rc=comparator.compare(o1.getZmena_nakladky(),o2.getZmena_nakladky());
			break;
		case 7:
			final Integer i1 = o1.getCislo_faktury_dopravce() != null ? o1.getCislo_faktury_dopravce() : -1;
			final Integer i2 = o2.getCislo_faktury_dopravce() != null ? o2.getCislo_faktury_dopravce() : -1;
			rc = i1.compareTo(i2);
			break;		
		case 11:
			rc = comparator.compare(o1.getNakl_nazev(), o2.getNakl_nazev());			
			break;
		case 12:
			rc = comparator.compare(o1.getVykl_nazev(), o2.getVykl_nazev());			
			break;
		case 13:
			rc = comparator.compare(o1.getNakl_kontakt_osoba(), o2.getNakl_kontakt_osoba());			
			break;
		case 14:
			rc = comparator.compare(o1.getVykl_kontakt_osoba(), o2.getVykl_kontakt_osoba());			
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
