package cz.kpartl.preprava.dialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.AbstractTableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;

import cz.kpartl.preprava.Activator;
import cz.kpartl.preprava.calendar.SWTCalendarEvent;
import cz.kpartl.preprava.calendar.SWTCalendarListener;
import cz.kpartl.preprava.dao.DestinaceDAO;
import cz.kpartl.preprava.dao.PozadavekDAO;
import cz.kpartl.preprava.dao.UserDAO;
import cz.kpartl.preprava.model.Destinace;
import cz.kpartl.preprava.model.Pozadavek;
import cz.kpartl.preprava.model.User;
import cz.kpartl.preprava.util.EventConstants;
import cz.kpartl.preprava.util.HibernateHelper;
import cz.kpartl.preprava.util.Login;
import cz.kpartl.preprava.util.OtherUtils;
import cz.kpartl.preprava.view.AbstractTableView;

public class NovyPozadavekDialog extends TitleAreaDialog {

	final Logger logger = LoggerFactory.getLogger(NovyPozadavekDialog.class);

	protected Shell parentShell;
	protected DestinaceDAO destinaceDAO;
	protected PozadavekDAO pozadavekDAO;
	protected User user;

	protected IEclipseContext context;
	protected IEventBroker eventBroker;
	// IEventBroker broker;

	protected Text datumNakladky;
	protected Text datumVykladky;
	protected Text hmotnost;
	protected Text hodinaNakladky;
	protected Text hodinaVykladky;
	protected Pozadavek pozadavek;
	protected Button datumNakladkyButton;
	protected Button datumVykladkyButton;
	protected Button stohovatelne;
	protected Button termin_konecny;
	protected Button destinaceOdkudButton;
	protected Button destinaceKamButton;
	private Button taxi;
	protected Text odkud;
	protected Text kam;
	protected Text odkudKontakt;
	protected Text odkudKontaktOsoba;
	protected Text kamKontakt;
	protected Text kamKontaktOsoba;
	protected Text pocetPalet;
	protected Text poznamka;

	private Destinace destinaceZ, destinaceDo;

	protected cz.kpartl.preprava.util.HibernateHelper persistenceHelper;

	// private HashMap<Integer, Destinace> destinaceMap = null;
	String[] destinaceItems = null;

	public Pozadavek getPozadavek() {
		return pozadavek;
	}

	@Inject
	public NovyPozadavekDialog(
			@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell,
			IEclipseContext context, IEventBroker eventBroker) {
		this(parentShell, context, null, eventBroker);
	}

	@Inject
	public NovyPozadavekDialog(
			@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell,
			IEclipseContext context, Pozadavek pozadavek,
			IEventBroker eventBroker) {
		super(parentShell);
		this.context = context;
		this.destinaceDAO = context.get(DestinaceDAO.class);
		this.parentShell = parentShell;
		this.pozadavekDAO = context.get(PozadavekDAO.class);
		this.user = (User) context.get(User.CONTEXT_NAME);
		this.pozadavek = pozadavek;
		this.eventBroker = eventBroker;
		setTitle("Nový požadavek na pøepravu");
		persistenceHelper = cz.kpartl.preprava.util.HibernateHelper
				.getInstance();
	}

	public Control publicCreateContents(Composite parent) {
		return createContents(parent);
	}

	@Override
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		if (pozadavek == null) { // novy pozadavek
			setTitle("Vytvoøení nového požadavku na pøepravu");
			setMessage("Zadejte data nového požadavku na pøepravu",
					IMessageProvider.INFORMATION);
		} else { // novy pozadavek
			setTitle("Editace požadavku na pøepravu");
			setMessage("Upravte data požadavku na pøepravu",
					IMessageProvider.INFORMATION);
		}
		return contents;
	}

	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.MAX);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout(3, false);
		layout.marginBottom = 5;
		layout.marginLeft = 5;
		layout.marginTop = 5;
		layout.marginRight = 5;
		parent.setLayout(layout);
		createWidgets(parent);
		createButtons(parent);

		return parent;
	}

	protected Composite createWidgets(Composite parent) {

		Label datumNakladkylabel = new Label(parent, SWT.NONE);
		GridData gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false);
		datumNakladkylabel.setLayoutData(gridData);
		datumNakladkylabel.setText("Požadované datum nakládky");

		datumNakladky = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		datumNakladky.setLayoutData(gridData);

		// datumNakladky.setLayoutData(new GridData(GridData.FILL_HORIZONTAL |
		// GridData.HORIZONTAL_ALIGN_FILL));

		datumNakladkyButton = new Button(parent, SWT.PUSH);
		datumNakladkyButton.setImage((Image) context.get(Login.CALENDAR_ICON));
		gridData = new GridData(SWT.BEGINNING, SWT.FILL, false, false);
		gridData.heightHint = 15;
		gridData.widthHint = 22;
		// gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		datumNakladkyButton.setLayoutData(gridData);
		// datumNakladkyButton.setLayoutData(new
		// GridData(GridData.FILL_HORIZONTAL ));
		datumNakladkyButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				getCalendarDialog(datumNakladky).open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				getCalendarDialog(datumNakladky).open();

			}
		});

		Label hodinaNakladkyLabel = new Label(parent, SWT.BOLD);
		hodinaNakladkyLabel.setText("Hodina nakládky");
		gridData = new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL);
		// gridData = new GridData(GridData.FILL_HORIZONTAL |
		// GridData.GRAB_HORIZONTAL, SWT.CENTER, true, false);
		// gridData.horizontalIndent = 10;
		hodinaNakladkyLabel.setLayoutData(gridData);

		hodinaNakladky = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.NONE, SWT.CENTER, false, false);
		gridData.widthHint = 100;
		hodinaNakladky.setLayoutData(gridData);

		// empty column
		new Label(parent, SWT.NONE);

		Label datumVykladkylabel = new Label(parent, SWT.BOLD);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		// gridData.horizontalIndent = 10;
		datumVykladkylabel.setLayoutData(gridData);
		datumVykladkylabel.setText("Požadované datum vykládky");
		datumVykladkylabel.setLayoutData(gridData);

		datumVykladky = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.NONE, SWT.CENTER, false, false);
		datumVykladky.setLayoutData(gridData);

		datumVykladkyButton = new Button(parent, SWT.PUSH);
		gridData = new GridData(SWT.BEGINNING, SWT.FILL, false, false);
		gridData.heightHint = 5;
		gridData.widthHint = 21;
		datumVykladkyButton.setLayoutData(gridData);
		datumVykladkyButton.setImage((Image) context.get(Login.CALENDAR_ICON));
		datumVykladkyButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				getCalendarDialog(datumVykladky).open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				getCalendarDialog(datumVykladky).open();

			}
		});

		Label hodinaVykladkyLabel = new Label(parent, SWT.BOLD);
		hodinaVykladkyLabel.setText("Hodina vykládky");
		gridData = new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL);

		hodinaVykladkyLabel.setLayoutData(gridData);

		hodinaVykladky = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.NONE, SWT.CENTER, false, false);
		gridData.widthHint = 100;
		hodinaVykladky.setLayoutData(gridData);

		new Label(parent, SWT.NONE);

		/*
		 * datumVykladky.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL |
		 * GridData.HORIZONTAL_ALIGN_FILL));
		 */

		Label odkudLabel = new Label(parent, SWT.NONE);
		odkudLabel.setText("Odkud");
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		odkudLabel.setLayoutData(gridData);

		odkud = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.NONE, SWT.CENTER, false, false);
		gridData.widthHint = 100;
		odkud.setLayoutData(gridData);
		odkud.setEditable(false);

		destinaceOdkudButton = new Button(parent, SWT.PUSH);
		gridData = new GridData(SWT.BEGINNING, SWT.FILL, false, false);
		gridData.heightHint = 5;
		gridData.widthHint = 21;
		destinaceOdkudButton.setLayoutData(gridData);
		// destinaceOdkudButton.setImage((Image)
		// context.get(Login.CALENDAR_ICON));
		destinaceOdkudButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				final DestinaceListDialog dialog = getDestinaceListDialog();
				if (dialog.open() == Window.OK) {
					if (dialog.selectedDestinace != null) {
						destinaceZ = dialog.selectedDestinace;
						odkud.setText(dialog.selectedDestinace.getNazevACislo());
						odkudKontakt.setText(dialog.selectedDestinace
								.getKontakt());
						odkudKontaktOsoba.setText(dialog.selectedDestinace
								.getKontaktni_osoba());
					}
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				getDestinaceListDialog().open();

			}
		});

		/*
		 * gridData = new GridData(SWT.NONE, SWT.CENTER, false, false);
		 * gridData.horizontalSpan = 2; /* odkud.setLayoutData(gridData);
		 * odkud.addSelectionListener(new SelectionAdapter() { public void
		 * widgetSelected(SelectionEvent e) { odkudKontakt.setText(destinaceMap
		 * .get(odkud.getSelectionIndex()) .getKontaktniOsobuAKontakt());
		 * 
		 * } }); odkud.addModifyListener(new ModifyListener() {
		 * 
		 * @Override public void modifyText(ModifyEvent e) {
		 * odkudKontakt.setText(destinaceMap .get(odkud.getSelectionIndex())
		 * .getKontaktniOsobuAKontakt());
		 * 
		 * } });
		 */
		Label odkudKontaktLabel = new Label(parent, SWT.NONE);
		odkudKontaktLabel.setText("Kontakt");
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		// gridData.horizontalIndent = 10;
		odkudKontaktLabel.setLayoutData(gridData);
		
		odkudKontaktOsoba = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		odkudKontaktOsoba.setLayoutData(gridData);
		odkudKontaktOsoba.setEditable(false);
		
		odkudKontakt = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		odkudKontakt.setLayoutData(gridData);
		odkudKontakt.setEditable(false);

		Label kamLabel = new Label(parent, SWT.NONE);
		kamLabel.setText("Kam");
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		kamLabel.setLayoutData(gridData);

		kam = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.NONE, SWT.CENTER, false, false);
		gridData.widthHint = 100;
		kam.setLayoutData(gridData);
		kam.setEditable(false);

		destinaceKamButton = new Button(parent, SWT.PUSH);
		gridData = new GridData(SWT.BEGINNING, SWT.FILL, false, false);
		gridData.heightHint = 5;
		gridData.widthHint = 21;
		destinaceKamButton.setLayoutData(gridData);
		destinaceKamButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				final DestinaceListDialog dialog = getDestinaceListDialog();
				if (dialog.open() == Window.OK) {
					if (dialog.selectedDestinace != null) {
						destinaceDo = dialog.selectedDestinace;
						kam.setText(dialog.selectedDestinace.getNazevACislo());
						kamKontakt.setText(dialog.selectedDestinace
								.getKontakt());
						kamKontaktOsoba.setText(dialog.selectedDestinace
								.getKontaktni_osoba());
					}
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				getDestinaceListDialog().open();

			}
		});

		/*
		 * Label kamLabel = new Label(parent, SWT.NONE);
		 * kamLabel.setText("Kam"); gridData = new GridData(SWT.BEGINNING,
		 * SWT.CENTER, false, false); kamLabel.setLayoutData(gridData);
		 * 
		 * kam = new Combo(parent, SWT.READ_ONLY); kam.setBounds(new
		 * Rectangle(50, 50, 150, 65)); // kam.setItems(destinaceItems);
		 * gridData = new GridData(SWT.NONE, SWT.CENTER, false, false);
		 * gridData.horizontalSpan = 2; kam.setLayoutData(gridData);
		 * kam.addSelectionListener(new SelectionAdapter() { public void
		 * widgetSelected(SelectionEvent e) {
		 * kamKontakt.setText(destinaceMap.get(kam.getSelectionIndex())
		 * .getKontaktniOsobuAKontakt());
		 * 
		 * } });
		 * 
		 * kam.addModifyListener(new ModifyListener() {
		 * 
		 * @Override public void modifyText(ModifyEvent e) {
		 * kamKontakt.setText(destinaceMap.get(kam.getSelectionIndex())
		 * .getKontaktniOsobuAKontakt());
		 * 
		 * } });
		 */

		Label kamKontaktLabel = new Label(parent, SWT.NONE);
		kamKontaktLabel.setText("Kontakt");
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		// gridData.horizontalIndent = 10;
		kamKontaktLabel.setLayoutData(gridData);
		
		kamKontaktOsoba = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.FILL, SWT.CENTER, false, false);		
		kamKontaktOsoba.setLayoutData(gridData);
		kamKontaktOsoba.setEditable(false);
		
		kamKontakt = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.FILL, SWT.CENTER, false, false);		
		kamKontakt.setLayoutData(gridData);
		kamKontakt.setEditable(false);

		Label hmotnostLabel = new Label(parent, SWT.BOLD);
		hmotnostLabel.setText("Hmotnost");
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		hmotnostLabel.setLayoutData(gridData);

		hmotnost = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.NONE, SWT.CENTER, false, false);
		gridData.widthHint = 70;
		hmotnost.setLayoutData(gridData);

		new Label(parent, SWT.NONE);

		Label pocetPaletLabel = new Label(parent, SWT.BOLD);
		pocetPaletLabel.setText("Poèet palet");
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		pocetPaletLabel.setLayoutData(gridData);

		pocetPalet = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.NONE, SWT.CENTER, false, false);
		gridData.widthHint = 70;
		pocetPalet.setLayoutData(gridData);

		new Label(parent, SWT.NONE);

		Label stohovatelneLabel = new Label(parent, SWT.BOLD);
		stohovatelneLabel.setText("Palety jsou stohovatelné");
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		stohovatelneLabel.setLayoutData(gridData);

		stohovatelne = new Button(parent, SWT.CHECK);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		stohovatelne.setLayoutData(gridData);

		new Label(parent, SWT.NONE);

		Label terminLabel = new Label(parent, SWT.BOLD);
		terminLabel.setText("Termín nakládky koneèný");
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		terminLabel.setLayoutData(gridData);

		termin_konecny = new Button(parent, SWT.CHECK);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		termin_konecny.setLayoutData(gridData);

		new Label(parent, SWT.NONE);

		Label taxiLabel = new Label(parent, SWT.BOLD);
		taxiLabel.setText("Taxi");
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		taxiLabel.setLayoutData(gridData);

		taxi = new Button(parent, SWT.CHECK);
		gridData = new GridData(SWT.END, SWT.CENTER, true, false);
		gridData.horizontalIndent = 10;

		new Label(parent, SWT.NONE);

		Label poznamkaLabel = new Label(parent, SWT.NONE);
		poznamkaLabel.setText("Poznámka");
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		poznamkaLabel.setLayoutData(gridData);

		poznamka = new Text(parent, SWT.BORDER);
		gridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gridData.horizontalSpan = 2;
		poznamka.setLayoutData(gridData);

		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);

		/*
		 * Composite composite = new Composite(parent, SWT.None);
		 * 
		 * GridLayout compositelayout = new GridLayout();
		 * compositelayout.numColumns = 2; // compositelayout.marginHeight = 15;
		 * composite.setLayout(compositelayout); GridData data = new
		 * GridData(GridData.FILL_BOTH); data.horizontalSpan = 2;
		 * composite.setLayoutData(data);
		 */

		// editace existujiciho pozadavku
		fillFields();

		return parent;
	}

	protected ArrayList<String> validatePozadavek() {
		ArrayList<String> result = new ArrayList<String>();
		if (!OtherUtils.isValidDate(datumNakladky.getText()))
			result.add("Špatný datum nakládky.");
		// if(datumNakladky.getText().isEmpty())
		// result.add("Není zadán Datum nakládky");

		if (!OtherUtils.isValidDate(datumVykladky.getText()))
			result.add("Špatný datum vykládky.");

		// if(datumVykladky.getText().isEmpty())
		// result.add("Není zadán Datum vykládky");
		if (odkud.getText().isEmpty())
			result.add("Není zadáno Odkud");
		if (kam.getText().isEmpty())
			result.add("Není zadáno Kam");

		return result;
	}

	protected void createButtons(Composite parent) {
		Button okButton = new Button(parent, SWT.PUSH);
		GridData gridData = new GridData(80, 25);
		// data.horizontalAlignment = data.HORIZONTAL_ALIGN_FILL |
		// data.GRAB_HORIZONTAL;
		parent.getShell().setDefaultButton(okButton);

		okButton.setLayoutData(gridData);
		okButton.setText("OK");

		okButton.setLayoutData(gridData);
		okButton.setData(IDialogConstants.OK_ID);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Transaction tx = persistenceHelper.beginTransaction();
				try {
					if (updatePozadavek(true)) {
						tx.commit();
						persistenceHelper.getSession().flush();
						// persistenceHelper.getSession().close();

						eventBroker.send(EventConstants.REFRESH_VIEWERS, "");
						eventBroker.send(
								EventConstants.POZADAVEK_SELECTION_CHANGED,
								pozadavek);
						close();
					} else {
						tx.rollback();
					}

				} catch (Exception ex) {
					tx.rollback();
					setErrorMessage("Pøi zápisu do databáze došlo k chybì, kontaktujte prosím tvùrce aplikace."
							.concat(System.getProperty("line.separator"))
							.concat(ex.getMessage()));
					logger.error("Nelze vlozit/updatovat pozadavek", ex);
				}
			}
		});

		Button cancelButton = new Button(parent, SWT.PUSH);
		gridData = new GridData(80, 25);
		// data.horizontalAlignment =SWT.RIGHT;
		cancelButton.setLayoutData(gridData);
		cancelButton.setText("Zrušit");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				close();
			}
		});

	}

	protected int getDestinaceIndex(String destinace) {
		for (int i = 0; i < destinaceItems.length; i++) {
			if (destinaceItems[i].equals(destinace))
				return i;
		}
		;

		return -1;
	}

	// zapise pozadavek do databaze
	protected boolean updatePozadavek(boolean save) {
		final ArrayList<String> validace = validatePozadavek();
		if (validace.size() == 0) {
			boolean novyPozadavek = pozadavek == null;
			if (novyPozadavek) {
				pozadavek = new Pozadavek();
			}

			pozadavek.setCelkova_hmotnost(hmotnost.getText());
			if (pozadavek.getDatum() == null || pozadavek.getDatum().equals("")) {
				pozadavek.setDatum(new Date(System.currentTimeMillis()));
			}
			pozadavek.setDatum_nakladky(datumNakladky.getText());
			pozadavek.setDatum_vykladky(datumVykladky.getText());
			pozadavek.setDestinace_z(destinaceZ);
			pozadavek.setDestinace_do(destinaceDo);
			pozadavek.setHodina_nakladky(hodinaNakladky.getText());
			pozadavek.setHodina_vykladky(hodinaVykladky.getText());
			pozadavek.setJe_termin_konecny(termin_konecny.getSelection());
			pozadavek.setPocet_palet(pocetPalet.getText());
			pozadavek.setJe_stohovatelne(stohovatelne.getSelection());
			pozadavek.setTaxi(taxi.getSelection());
			if (pozadavek.getZadavatel() == null)
				pozadavek.setZadavatel(user);
			pozadavek.setPoznamka(poznamka.getText());

			if (save) {
				if (novyPozadavek)
					pozadavek.setId(pozadavekDAO.create(pozadavek));
				else {
					persistenceHelper.getSession().flush();
					persistenceHelper.getSession().clear();
					pozadavekDAO.update(pozadavek);
				}
			}

			return true;
		} else {
			String errString = "";
			for (String validaceMessage : validace)
				errString = errString.concat(validaceMessage).concat(
						System.getProperty("line.separator"));
			setErrorMessage(errString);

			return false;
		}

	}

	protected void fillFields() {
		if (pozadavek == null)
			return;
		HibernateHelper.getInstance().getSession();
		
		hmotnost.setText(pozadavek.getCelkova_hmotnost() != null ? pozadavek
				.getCelkova_hmotnost() : "");
		datumNakladky.setText(pozadavek.getDatum_nakladky() != null ? pozadavek
				.getDatum_nakladky() : "");
		datumVykladky.setText(pozadavek.getDatum_vykladky() != null ? pozadavek
				.getDatum_vykladky() : "");
		if (pozadavek.getDestinace_z() != null) {
			destinaceZ = pozadavek.getDestinace_z();
			odkud.setText(pozadavek.getDestinaceZNazevACislo());
			odkudKontakt.setText(destinaceZ.getKontakt());
			odkudKontaktOsoba.setText(destinaceZ.getKontaktni_osoba());
		}
		if (pozadavek.getDestinace_do() != null) {
			destinaceDo = pozadavek.getDestinace_do();
			kam.setText(pozadavek.getDestinaceDoNazevACislo());
			kamKontakt.setText(destinaceDo.getKontakt());
			kamKontaktOsoba.setText(destinaceDo.getKontaktni_osoba());
		}
		hodinaNakladky
				.setText(pozadavek.getHodina_nakladky() != null ? pozadavek
						.getHodina_nakladky() : "");
		hodinaVykladky
				.setText(pozadavek.getHodina_vykladky() != null ? pozadavek
						.getHodina_vykladky() : "");
		termin_konecny.setSelection(pozadavek.getJe_termin_konecny());
		pocetPalet.setText(pozadavek.getPocet_palet() != null ? pozadavek
				.getPocet_palet() : "");
		stohovatelne.setSelection(pozadavek.getJe_stohovatelne());
		taxi.setSelection(pozadavek.getTaxi());
		poznamka.setText(pozadavek.getPoznamka() != null ? pozadavek
				.getPoznamka() : "");

	}

	/*
	 * protected String[] getDestinaceItems() { List<String> result = new
	 * ArrayList<String>(); if (destinaceDAO != null) { List<Destinace>
	 * destinace = destinaceDAO.findAll(); if (destinaceMap == null)
	 * destinaceMap = new HashMap<Integer, Destinace>(destinace.size()); int
	 * index = 0; for (Destinace des : destinace) {
	 * result.add(des.getNazevACislo());
	 * destinaceMap.put(Integer.valueOf(index++), des); } }
	 * 
	 * return result.toArray(new String[result.size()]); }
	 */

	private SWTCalendarDialog getCalendarDialog(final Text text) {
		SWTCalendarDialog calendarDialog = new SWTCalendarDialog(this
				.getShell().getDisplay());
		calendarDialog.addDateChangedListener(new SWTCalendarListener() {
			public void dateChanged(SWTCalendarEvent calendarEvent) {
				text.setText(new SimpleDateFormat("dd.MM.yyyy")
						.format(calendarEvent.getCalendar().getTime()));
				// Object source = calendarEvent.getSource();

			}
		});

		return calendarDialog;
	}

	private DestinaceListDialog getDestinaceListDialog() {
		DestinaceListDialog result = new DestinaceListDialog(parentShell,
				context);
		return result;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {

	}

}
