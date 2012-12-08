package cz.kpartl.preprava.sorter;

import org.eclipse.jface.viewers.Viewer;

import cz.kpartl.preprava.model.Pozadavek;

public class PozadavekTableViewerComparator extends TableViewerComparator {
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
			rc = comparator.compare(p1.getDatum_nakladky(),
					p2.getDatum_nakladky());
			break;
		case 2:
			rc = comparator.compare(p1.getDatum_vykladky(),
					p2.getDatum_vykladky());
			break;		
		case 3:
			rc = comparator.compare(p1.getDestinace_z().getNazevACislo(), p2
					.getDestinace_z().getNazevACislo());
			break;
		case 4:
			rc = comparator.compare(p1.getDestinace_do().getNazevACislo(), p2
					.getDestinace_do().getNazevACislo());
			break;
		case 5:
			rc = comparator.compare(p1.getCelkova_hmotnost(),
					p2.getCelkova_hmotnost());
			break;
		case 6:
			rc = comparator.compare(p1.getPocet_palet(), p2.getPocet_palet());
			break;
		case 7:
			if (p1.getJe_stohovatelne() == p2.getJe_stohovatelne()) {
				rc = 0;
			} else
				rc = (p1.getJe_stohovatelne() ? 1 : -1);
		case 8:
			if (p1.getJe_termin_konecny() == p2.getJe_termin_konecny()) {
				rc = 0;
			} else
				rc = (p1.getJe_termin_konecny() ? 1 : -1);
			break;
		case 9:
			if (p1.getTaxi() == p2.getTaxi()) {
				rc = 0;
			} else
				rc = (p1.getTaxi() ? 1 : -1);
			break;
		case 10:
			rc = comparator.compare(p1.getDestinace_z()
					.getKontaktniOsobuAKontakt(), p2.getDestinace_z()
					.getKontaktniOsobuAKontakt());
			break;
		case 11:
			rc = comparator.compare(p1.getDestinace_do()
					.getKontaktniOsobuAKontakt(), p2.getDestinace_do()
					.getKontaktniOsobuAKontakt());
			break;
		case 12:
			rc = comparator.compare(p1.getHodina_nakladky(),
					p2.getHodina_nakladky());
			break;
		case 13:
			rc = comparator.compare(p1.getHodina_vykladky(),
					p2.getHodina_vykladky());
			break;
		case 14:
			rc = comparator.compare(p1.getZadavatel().getUsername(), p2
					.getZadavatel().getUsername());
			break;
		case 15:
			rc = comparator.compare(p1.getPoznamka(), p2.getPoznamka());
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
