package cz.kpartl.preprava.view;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.ui.part.ViewPart;

import cz.kpartl.preprava.model.Dopravce;
import cz.kpartl.preprava.model.Objednavka;
import cz.kpartl.preprava.model.Pozadavek;
import cz.kpartl.preprava.util.EventConstants;

public class ObjednavkaDetailView extends PozadavekDetailView {

	protected Objednavka objednavka;

	protected Label cisloObjednavky;
	protected Label faze;
	protected Label cena;
	protected Label mena;
	protected Label dopravce;
	protected Label zmenaTerminuNakladky;
	protected Label cisloFakturyDopravce;
	protected Label pridruzenaObjednavka;

	@Inject
	public ObjednavkaDetailView(Composite parent, Shell parentShell,
			IEclipseContext context, IEventBroker eventBroker) {
		super(parent, parentShell, context, eventBroker);

	}

	public static final String ID = "cz.kpartl.preprava.part.objednavkaDetailPart";

	@Override
	public void createPartControl(Composite parent) {

		createBoldLabel(parent, "Èíslo objednávky: ");
		cisloObjednavky = new Label(parent, SWT.NONE);

		createBoldLabel(parent, "Fáze objednávky: ");
		faze = new Label(parent, SWT.NONE);

		createBoldLabel(parent, "Dopravce: ");
		dopravce = new Label(parent, SWT.NONE);

		createBoldLabel(parent, "Èíslo faktury dopravce: ");
		cisloFakturyDopravce = new Label(parent, SWT.NONE);

		createBoldLabel(parent, "Cena dopravy: ");
		cena = new Label(parent, SWT.NONE);

		createBoldLabel(parent, "Mìna: ");
		mena = new Label(parent, SWT.NONE);

		createBoldLabel(parent, "Zmìna termínu nakládky: ");
		zmenaTerminuNakladky = new Label(parent, SWT.NONE);

		createBoldLabel(parent, "Pøidružená objednávka: ");
		pridruzenaObjednavka = new Label(parent, SWT.NONE);

		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);

		final Label separator = new Label(parent, SWT.SEPARATOR
				| SWT.HORIZONTAL);
		final GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL);
		gridData.horizontalSpan = 4;
		separator.setLayoutData(gridData);

		super.createPartControl(parent);

	}

	@Override
	public void setFocus() {
		super.setFocus();

	}

	@Override
	protected void fillData() {
		if (objednavka == null) {
			cisloObjednavky.setText("");
			faze.setText("");
			dopravce.setText("");
			zmenaTerminuNakladky.setText("");
			cena.setText("");
			mena.setText("");
			cisloFakturyDopravce.setText("");
			pridruzenaObjednavka.setText("");

			super.fillData();
			return;
		}

		cisloObjednavky.setText(String.valueOf(objednavka.getCislo_objednavky()));

		faze.setText(ObjednanoView.getComboItems(true)[objednavka.getFaze()]);

		final Dopravce d = objednavka.getDopravce();
		if (d != null) {
			dopravce.setText(d.getNazev());
		}

		cena.setText(objednavka.getCenaFormated());

		zmenaTerminuNakladky.setText(objednavka.getZmena_nakladky());
		cisloFakturyDopravce.setText(objednavka
				.getCisloFakturyDopravceAsString());

		mena.setText(objednavka.getMena());
		if (objednavka.getPridruzena_objednavka() != null)
			pridruzenaObjednavka.setText(String.valueOf(objednavka
					.getPridruzena_objednavka().getCislo_objednavky()));
		else pridruzenaObjednavka.setText("");
		super.fillData();
	}

	@Inject
	@Optional
	void selectionChanged(
			@UIEventTopic(EventConstants.OBJEDNAVKA_SELECTION_CHANGED) Objednavka o) {
		this.objednavka = o;
		this.pozadavek = o.getPozadavek();
		fillData();

	}

	@Inject
	@Optional
	void selectionChangedToEmpty(
			@UIEventTopic(EventConstants.EMPTY_OBJEDNAVKA_SEND) String s) {
		if (s.equals(EventConstants.EMPTY_OBJEDNAVKA_SEND)) {
			this.objednavka = null;
			this.pozadavek = null;
			fillData();
		}
	}

}
