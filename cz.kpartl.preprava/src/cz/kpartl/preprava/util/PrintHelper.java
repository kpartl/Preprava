package cz.kpartl.preprava.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Shell;

import cz.kpartl.preprava.model.Objednavka;
import cz.kpartl.preprava.view.ObjednanoView;

public class PrintHelper {
	Shell shell;

	Objednavka objednavka;
	Font normalFont, boldFont,boldItalicFont, boldUnderFont, titleFont;
	Color foregroundColor, backgroundColor;

	Printer printer;
	GC gc;
	Font titleText, normalText, boldText, boldItalicText, boldUnderText, nadpisFont, nadpisText, odstavecFont, odstavecText, textFont, textText;
	Color printerForegroundColor, printerBackgroundColor;

	int lineHeight = 0;
	float lineHeightConst = 1.3f;
	float scaleFactor = 3.5f;
	int tabWidth = 0;
	int leftMargin = 100, lineMargin, rightMargin, topMargin, bottomMargin, titleTopMargin ;
	int x, y;
	int index, end;
	int colWidth = 400;
	int col1;
	int col2;
	int col3;
	int col4;
	int col5;
	int col6;
	int formWidth;
	int formHeight;
	int lineOffset;
	String textToPrint;
	ImageData kernIcon;
	String tabs, tabsDouble;
	StringBuffer wordBuffer;
	IEclipseContext context;

	final String separator = "\t";
	final char eol = 0x0d;
	char char1 = 0x0a;
	char char2 = 0x0d;
	//final String enter = String.valueOf(char2) +String.valueOf(char1);

	public PrintHelper(Shell shell, IEclipseContext context) {
		this.shell = shell;
		this.context = context;
	}

	public void tiskVybraneObjednavky(Objednavka selectedObjednavka) {
		this.objednavka = selectedObjednavka;
		PrintDialog dialog = new PrintDialog(shell, SWT.NULL);
		PrinterData data = dialog.open();
		if (data == null)
			return;
		if (data.printToFile) {
			data.fileName = "print.out"; // you probably want to ask the user
											// for a filename
		}

		/*
		 * Get the text to print from the Text widget (you could get it from
		 * anywhere, i.e. your java model)
		 */
		textToPrint = getObjednavkaAsText(selectedObjednavka);
		kernIcon = ((Image) context.get(Login.KERN_ICON)).getImageData();
		
		printer = new Printer(data);
		Rectangle clientArea = printer.getClientArea();
		Rectangle trim = printer.computeTrim(0, 0, 0, 0);
		Point dpi = printer.getDPI();
		
		formHeight = clientArea.height;
		
		formWidth = clientArea.width - leftMargin;
		leftMargin = trim.x + (int) (formWidth / 24.8);
		lineMargin = leftMargin / 5;
		rightMargin = clientArea.width + trim.x + trim.width; 
		titleTopMargin = trim.y + dpi.y / 3;
		topMargin = titleTopMargin + (int)(scaleFactor * kernIcon.height) + 6;//dpi.y / 2 + trim.y; // one inch from top edge of paper
		bottomMargin = clientArea.height + trim.y + trim.height; 
		col1 = leftMargin; //100
		
		col2 = col1 +  (int) (formWidth / 4.9); //590
		col3 = col2 +  (int) (formWidth / 9.19);
		col4 = col3 +  (int) (formWidth / 5.77);
		col5 = col4 +  (int) (formWidth / 5.5);
		col6 = col5 +  (int) (formWidth / 6.7);
		normalFont = new Font(shell.getDisplay(), "Times New Roman", 12, SWT.NORMAL);
		boldFont = new Font(shell.getDisplay(), "Times New Roman", 12, SWT.BOLD);
		boldItalicFont = new Font(shell.getDisplay(), "Times New Roman", 12, SWT.BOLD | SWT.ITALIC);
		boldUnderFont = new Font(shell.getDisplay(), "Times New Roman", 12, SWT.BOLD | SWT.UNDERLINE_DOUBLE);
		titleFont = new Font(shell.getDisplay(), "Times New Roman", 16, SWT.BOLD);
		nadpisFont = new Font(shell.getDisplay(), "Arial", 10, SWT.BOLD);
		textFont = new Font(shell.getDisplay(), "Arial", 6, SWT.NORMAL);
		odstavecFont = new Font(shell.getDisplay(), "Arial", 6, SWT.BOLD);		
		foregroundColor = shell.getDisplay().getSystemColor(SWT.COLOR_BLACK);
		backgroundColor = shell.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		final HibernateHelper persistenceHelper = cz.kpartl.preprava.util.HibernateHelper
				.getInstance();
		Thread printingThread = new Thread("Printing") {
			public void run() {
				// radeji otevru sesnu, obcas vypadne lazy loading
				persistenceHelper.getSession();
				print(printer);
				printer.dispose();
			}
		};
		printingThread.start();
	}

	private String getObjednavkaAsText(Objednavka obj) {
		StringBuffer result = new StringBuffer();
		addLine(result, "Objednávka è.", obj.getCislo_objednavky());
		addLine(result, "Fáze", ObjednanoView.getFazeKey(obj.getFaze()));
		addLine(result, "Dopravce", obj.getDopravce() != null ? obj
				.getDopravce().getNazev() : "");
		addLine(result, "Èíslo faktury dopravce",
				obj.getCislo_faktury_dopravce());
		addLine(result, "Cena dopravy",
				obj.getCenaFormated() + " " + obj.getMena());
		addLine(result, "Zmìna termínu nakládky", obj.getZmena_nakladky());
		addLine(result, "Datum nakládky", obj.getPozadavek()
				.getDatum_nakladky());
		addLine(result, "Datum vykládky", obj.getPozadavek()
				.getDatum_vykladky());
		addLine(result, "Výchozí destinace", obj.getPozadavek()
				.getDestinaceZNazevACislo());
		addLine(result, "Kontaktní osoba ve výchozí destinaci", obj
				.getPozadavek().getDestinaceZKontaktAOsobu());
		addLine(result, "Cílová destinace", obj.getPozadavek()
				.getDestinaceDoNazevACislo());
		addLine(result, "Kontaktní osoba v cílové destinaci", obj
				.getPozadavek().getDestinaceDoKontaktAOsobu());

		return result.toString();
	}

	private void addLine(final StringBuffer sb, final String key,
			final Object value) {
		sb.append(key).append(separator).append(value).append(eol);
	}

	void print(Printer printer) {
		if (printer.startJob("Tisk objednavky")) { // the string is the job name
													// - shows up in the
													// printer's job list

			/* Create a buffer for computing tab width. */
			int tabSize = 10; // is tab width a user setting in your UI?
			StringBuffer tabBuffer = new StringBuffer(tabSize);
			for (int i = 0; i < tabSize; i++)
				tabBuffer.append(' ');
			tabs = tabBuffer.toString();
			tabsDouble = tabs + tabs;

			/*
			 * Create printer GC, and create and set the printer font &
			 * foreground color.
			 */
			gc = new GC(printer);

			FontData fontData = normalFont.getFontData()[0];
			normalText = new Font(printer, fontData.getName(),
					fontData.getHeight(), fontData.getStyle());
			fontData = titleFont.getFontData()[0];
			titleText = new Font(printer, fontData.getName(),
					fontData.getHeight(), fontData.getStyle());
			fontData = boldFont.getFontData()[0];
			boldText = new Font(printer, fontData.getName(),
					fontData.getHeight(), fontData.getStyle());
			fontData = boldItalicFont.getFontData()[0];
			boldItalicText = new Font(printer, fontData.getName(),
					fontData.getHeight(), fontData.getStyle());
			fontData = boldUnderFont.getFontData()[0];
			boldUnderText = new Font(printer, fontData.getName(),
					fontData.getHeight(), fontData.getStyle());
			fontData = nadpisFont.getFontData()[0];
			nadpisText = new Font(printer, fontData.getName(),
					fontData.getHeight(), fontData.getStyle());
			
			fontData = textFont.getFontData()[0];
			textText = new Font(printer, fontData.getName(),
					fontData.getHeight(), fontData.getStyle());
			
			fontData = odstavecFont.getFontData()[0];
			odstavecText = new Font(printer, fontData.getName(),
					fontData.getHeight(), fontData.getStyle());
			
			tabWidth = gc.stringExtent(tabs).x;
			
			

			RGB rgb = foregroundColor.getRGB();
			printerForegroundColor = new Color(printer, rgb);
			gc.setForeground(printerForegroundColor);

			rgb = backgroundColor.getRGB();
			printerBackgroundColor = new Color(printer, rgb);
			gc.setBackground(printerBackgroundColor);

			/* Print text to current gc using word wrap */
			printer.startPage();
			printTitle();
			printBody();			
			printer.endJob();

			/* Cleanup graphics resources used in printing */
			normalText.dispose();
			boldText.dispose();
			titleText.dispose();
			normalFont.dispose();
			boldFont.dispose();
			titleFont.dispose();
			printerForegroundColor.dispose();
			printerBackgroundColor.dispose();
			gc.dispose();
		}
	}

	void printTitle() {
		gc.setFont(titleText);
		int titleLineHeight = gc.getFontMetrics().getHeight();
	
		
		int titleCenter = (kernIcon.width + titleLineHeight) / 2 + titleTopMargin;
		gc.drawString("OBJEDNÁVKA PØEPRAVY", leftMargin , titleCenter - titleLineHeight);		
		
		Image printerImage = new Image(printer, kernIcon);
		gc.drawImage(printerImage, 0, 0, kernIcon.width,
				kernIcon.height, formWidth - ((int)(scaleFactor * kernIcon.width)), titleTopMargin, 
                (int)(scaleFactor * kernIcon.width),
                (int)(scaleFactor * kernIcon.height));
  
              // Clean up
              printerImage.dispose();
	}
	
	void printBody() {
		lineHeightConst = 1.3f;
		y = topMargin;		
		int verticalFrom2;
		gc.setLineWidth(6);
		drawLine();
		
		gc.setFont(boldText);
		gc.drawString("Objednavatel:", col1, y);
		gc.drawString("Dodavatel (pøepravce):", col4, y);
		gc.setFont(normalText);
		newline();
		gc.drawString(objednavka.getObjednavka1(), col1, y);
		gc.drawString(objednavka.getDod_nazev(), col4, y);
		newline();
		gc.drawString(objednavka.getObjednavka2(), col1, y);
		gc.drawString(objednavka.getDod_ulice(), col4, y);
		newline();
		gc.drawString(objednavka.getObjednavka3(), col1, y);
		gc.drawString(objednavka.getDod_psc() + "  " + objednavka.getDod_mesto(), col4, y);
		
		newline();
		gc.drawString(objednavka.getObjednavka4(), col1, y);
		gc.drawString("DIÈ: " + objednavka.getDod_dic(), col4, y);
		newline();
		gc.drawString(objednavka.getObjednavka5(), col1, y);
		gc.drawString("IÈ: " + objednavka.getDod_ic(), col4, y);
		newline();
		gc.drawString(objednavka.getObjednavka6(), col1, y);
		gc.drawString("Dodavatelské èíslo: ", col4, y);
		gc.drawString(objednavka.getDod_sap_cislo(), col5, y);
		newline();
		gc.drawString(objednavka.getObjednavka7(), col1, y);
		newline();
		gc.drawString(objednavka.getObjednavka8(), col1, y);
		newline();
		
		gc.setFont(boldText);
		drawLine();		
		
		gc.drawString("Èíslo objednávky: ", col1, y);
		gc.drawString("Datum objednávky: ", col4, y);
		int verticalLineFrom = y;
		gc.setFont(normalText);
		gc.drawString(String.format("%06d %n", objednavka.getCislo_objednavky()), col2, y);
		gc.drawString(getDatumAsText(objednavka.getDatum()), col5, y);
		newline();
		drawLine();
		drawVerticalLine(col4, topMargin, y);
		
		gc.setFont(boldText);
		gc.drawString("Termín nakládky: ", col1, y);
		gc.setFont(normalText);		
		gc.drawString(objednavka.getPozadavek().getDatum_nakladky() + " " + objednavka.getPozadavek().getHodina_nakladky(), col2, y);
		newline();
		drawLine();
		verticalFrom2 = y - gc.getLineWidth();
		
		gc.drawString("Firma: ", col2, y);
		gc.drawString(objednavka.getNakl_nazev(), col4, y);
		newline();
		drawShortLine();
		
		gc.drawString("Adresa: ", col2, y);
		gc.drawString(objednavka.getNakl_ulice(), col4, y);
		newline();
		
		
		gc.setFont(boldText);
		gc.drawString("Místo nakládky: ", col1, y);
		gc.setFont(normalText);
		gc.drawString(objednavka.getNakl_psc() + "  " + objednavka.getNakl_mesto(), col4, y);		
		newline();
		drawShortLine();
		gc.drawString("Kontaktní osoba:", col2, y);
		gc.drawString(objednavka.getNakl_kontakt_osoba(), col4, y);
		newline();
		drawShortLine();
		
		gc.drawString("Kontakt:", col2, y);
		gc.drawString(objednavka.getNakl_kontakt(), col4, y);		
		newline();
		drawLine();
		drawVerticalLine(col4, verticalFrom2, y);
		
		gc.drawString(objednavka.getSpec_zbozi(), col2, y);
		newline();
		drawShortLine();
		verticalFrom2 = y;
		
		gc.setFont(boldText);
		gc.drawString("Specifikace zboží:", col1, y);
		gc.setFont(normalText);
		gc.drawString("Hmotnost:", col2, y);
		gc.drawString(objednavka.getPozadavek().getCelkova_hmotnost(), col3, y);
		gc.drawString("ADR:", col5, y);
		gc.drawString(objednavka.getAdr(), col6, y);
		newline();
		drawShortLine();
		gc.drawString("Poèet palet:", col2, y);
		gc.drawString(objednavka.getPozadavek().getPocet_palet(), col3, y);
		gc.drawString("Stohovatelné?", col5, y);
		gc.drawString(objednavka.getPozadavek().getJe_stohovatelne() ? "ano":"ne", col6, y);		
		newline();
		drawLine();
		drawVerticalLine(col3, verticalFrom2, y);
		drawVerticalLine(col5, verticalFrom2, y);
		drawVerticalLine(col6, verticalFrom2, y);
		gc.setFont(boldText);
		gc.drawString("Termín vykládky: ", col1, y);
		gc.setFont(normalText);
		gc.drawString(objednavka.getPozadavek().getDatum_vykladky() + " " + objednavka.getPozadavek().getHodina_vykladky(), col2, y);		
		newline();
		drawLine();
		verticalFrom2 = y -gc.getLineWidth();
		
		gc.drawString("Firma: ", col2, y);
		gc.drawString(objednavka.getVykl_nazev(), col4, y);
		newline();
		drawShortLine();
		
		gc.drawString("Adresa: ", col2, y);
		gc.drawString(objednavka.getVykl_ulice(), col4, y);
		newline();		
		
		gc.setFont(boldText);
		gc.drawString("Místo vykládky: ", col1, y);
		gc.setFont(normalText);
		gc.drawString(objednavka.getVykl_psc() + " " + objednavka.getVykl_mesto(), col4, y);		
		newline();
		drawShortLine();
		
		gc.drawString("Kontaktní osoba:", col2, y);
		gc.drawString(objednavka.getVykl_kontakt_osoba(), col4, y);
		newline();
		drawShortLine();
		
		gc.drawString("Kontakt:", col2, y);
		gc.drawString(objednavka.getVykl_kontakt(), col4, y);
		
		newline();
		drawLine();
		drawVerticalLine(col4, verticalFrom2, y);
		
		gc.setFont(boldText);
		gc.drawString("Cena za dopravu:", col1, y);
		gc.setFont(normalText);
		gc.drawString(objednavka.getCenaFormated() + " " + objednavka.getMena(), col2, y);
		
		newline();
		drawLine();
		
		gc.setFont(boldText);
		gc.drawString("Pøepravní podmínky:", col1, y);
		gc.setFont(normalText);
		gc.drawString(objednavka.getPreprav_podminky(), col2, y);
		
		newline();
		drawLine();
		
		gc.setFont(boldText);
		gc.drawString("Poznámka:", col1, y);
		gc.setFont(normalText);
		gc.drawString(objednavka.getPozadavek().getPoznamka(), col2, y);
		
		newline();
		drawLine();
		drawVerticalLine(col2, verticalLineFrom, y);
		printPoznamkaLine(getPoznamky());
		
		gc.setFont(boldItalicText);
		newline();
		gc.drawString("Pøepravní pøíkaz dle podmínek KERN-LIEBERS CR spol. s r.o.", col2, y);
		drawLine();
	
		//newline();
		gc.drawRectangle(new Rectangle(leftMargin - lineMargin, titleTopMargin, formWidth - leftMargin + lineMargin, y));
		newline();
		newline();
		gc.setFont(boldUnderText);
		gc.drawString("Potvrzení objednávky dodavatelem", col1, y);
		gc.setFont(boldText);
		newline();
		newline();
		newline();
		gc.drawString("Datum: .....................", col1, y);
		gc.drawString("Razítko a podpis: ........................................", col4, y);
		
		printSecondPage();
	}
	
	private void printSecondPage() {
		printer.endPage();
		printer.startPage();
		y = titleTopMargin;	
		lineHeightConst = 1.1f;
				
		gc.setFont(nadpisText);
		int center = formWidth /2;
		gc.drawString("Všeobecné pøepravní podmínky Kern-Liebers CR spol. s r.o.", center/2, y);
		newline();
		printOdstavecNadpis("1." + tabs + " Základní ustanovení");
		textToPrint = getOdstavec1();
		rightMargin = formWidth - leftMargin + lineMargin;
		printTextCharByChar();
		printOdstavecNadpis("2." + tabs + "Nákladový list");
		textToPrint = getOdstavec2();
		printTextCharByChar();
		printOdstavecNadpis("3." + tabs + "Škody, ztráta, znièení zásilky");
		textToPrint = getOdstavec3();
		printTextCharByChar();
		printOdstavecNadpis("4." + tabs + "Úplata za pøepravu (pøepravné) a platební podmínky");
		textToPrint = getOdstavec4();
		printTextCharByChar();
		printOdstavecNadpis("5." + tabs + "Konkurenceschopnost");
		textToPrint = getOdstavec5();
		printTextCharByChar();
		printOdstavecNadpis("6." + tabs + "Smluvní pokuty a úrok z prodlení");
		textToPrint = getOdstavec6();
		printTextCharByChar();
		printOdstavecNadpis("7." + tabs + "Povinnost zachovat mlèenlivost");
		textToPrint = getOdstavec7();
		printTextCharByChar();
		printOdstavecNadpis("8." + tabs + "Vyšší moc");
		textToPrint = getOdstavec8();
		printTextCharByChar();
		printOdstavecNadpis("9." + tabs + "Storno objednávky pøepravy");
		textToPrint = getOdstavec9();
		printTextCharByChar();
		printOdstavecNadpis("10." + tabs + "Øešení sporù");
		textToPrint = getOdstavec10();
		printTextCharByChar();
		printOdstavecNadpis("11." + tabs + "Ostatní ustanovení");
		textToPrint = getOdstavec11();
		printTextCharByChar();
	}
	
	private void printOdstavecNadpis(String text) {
		newline();
		gc.setFont(odstavecText);
		gc.drawString(tabs + text, leftMargin, y);
		newline();
		gc.setFont(textText);
	}
	
	void printPoznamkaLine(String line) {
		textToPrint = line;
		printTextCharByChar();
	}
	
	
	void printTextCharByChar() {
		wordBuffer = new StringBuffer();
		x = col1;
		
		
		index = 0;
		end = textToPrint.length();
		while (index < end) {
			char c = textToPrint.charAt(index);
			index++;
			if (c != 0) {
				if (c == 0x0a || c == 0x0d) {
					if (c == 0x0d && index < end
							&& textToPrint.charAt(index) == 0x0a) {
						index++; // if this is cr-lf, skip the lf
					}
					printWordBuffer();
					newline();
				} else {
					if (c != '\t') {
						wordBuffer.append(c);
					}
					if (Character.isWhitespace(c)) {
						printWordBuffer();
						if (c == '\t') {
							x += tabWidth;
						}
					}
				}
			}
		}
		if (y + lineHeight <= bottomMargin) {
			printWordBuffer();
			//printer.endPage();
		}
	}

	void printWordBuffer() {
		if (wordBuffer.length() > 0) {
			String word = wordBuffer.toString();
			int wordWidth = gc.stringExtent(word).x;
			if (x + wordWidth > rightMargin) {
				/* word doesn't fit on current line, so wrap */
				newline();
			}
			gc.drawString(word, x, y, false);
			x += wordWidth;
			wordBuffer = new StringBuffer();
		}
	}

	void newline() {
		lineHeight = (int)(lineHeightConst * (gc.getFontMetrics().getHeight()));
		x = leftMargin;
		y += lineHeight;
		if (y + lineHeight > bottomMargin) {
			printer.endPage();
			if (index + 1 < end) {
				y = topMargin;
				printer.startPage();
			}
		}
	}
	
	void drawLine() {
		gc.drawLine(leftMargin - lineMargin, y - 9, formWidth, y - 9);
	}
	
	
	
	void drawShortLine() {
		gc.drawLine(col2 - lineMargin, y - 9, formWidth, y - 9);
	}
	
	void drawVerticalLine(int col, int from, int to) {
		gc.drawLine(col - lineMargin, from - gc.getLineWidth(), col - lineMargin, to - gc.getLineWidth() - 5);
	}
	
	String getDatumAsText(Date datum) {
		try {
			return new SimpleDateFormat("dd.MM.yyyy")
			.format(datum);
		} catch (Exception e) {
			return "";
		}
		
	}
	
	public static String notNullStr(String text) {
		return text != null ? text : "";
	}
	
	private String getPoznamky() {
		return notNullStr(objednavka.getPoznamka1())
				+ notNullStr(objednavka.getPoznamka2())
				+ notNullStr(objednavka.getPoznamka3())
				+ notNullStr(objednavka.getPoznamka4())
				+ notNullStr(objednavka.getPoznamka5());
	}
	
	private String getOdstavec1() {		
		StringBuffer sb = new StringBuffer();
		sb.append("Potvrzením objednávky na pøepravu zboží zaslanou objednatelem Kern-Liebers  se dopravce zavazuje objednateli pøepravy, že pøepraví zboží (zásilku) z urèeného místa odeslání (místo nakládky) do urèeného místa vyložení");
		sb.append(" (místo vykládky), zároveò plnì souhlasí se všeobecnými pøepravními podmínkami Kern-Liebers a zavazuje se tìmito podmínkami se bezvýhradnì øídit. Objednatel pøepravy se zavazuje uhradit dopravci sjednanou cenu pøepravy  (pøepravné).");
		sb.append(eol);
		sb.append("(1.1) Dopravce potvrzuje objednateli pøepravy fyzické pøevzetí zásilky v dobrém stavu v pøepravním listu, a objednatel pøepravy potvrzuje dopravci požadavek na pøepravu svou objednávkou pøepravy.");
		sb.append(eol);
		sb.append("(1.2) Objednatel pøepravy vždy pøedává dopravci veškeré nezbytné doklady k zásilce. Dopravce odpovídá za provedení kontroly, že doklady jsou kompletní a úplné a pøeprava mùže být realizována. Objednatel pøepravy odpovídá za škodu zpùsobenou dopravci nesprávnými doklady.");
		sb.append(eol);
		sb.append("(1.3) Dopravce je povinen vždy vydat objednateli pøepravy pøi pøevzetí zásilky k pøepravì nákladový list, který je øádnì potvrzen øidièem a má všechny náležitosti (viz.bod 2).");
		sb.append(eol);
		sb.append("(1.4) Dopravce je povinen pøepravu provést do místa urèení s odbornou péèí ve smluvené lhùtì a bez zbyteèného odkladu.");
		sb.append(eol);
		sb.append("(1.5) Není-li písemnì domluveno jinak, zboží není možno pøekládat na jiný dopravní prostøedek, než na který bylo zboží naloženo v Kern-Liebers CØ. Je-li písemnì domluveno, že dopravce mùže objednanou pøepravu plnit pomocí dalšího dopravce nebo zboží bude bìhem pøepravy pøeloženo na jiný pøepravní prostøedek, dopravce tímto plnì odpovídá za všechny škody vzniklé a zpùsobené objednateli pøepravy, jako by pøepravu uskuteèòoval sám.");
		sb.append(eol);
		sb.append("(1.6) V pøípadì, že objednatel pøepravy objedná u dopravce nakládku zboží a jeho pøepravu od svého dodavatele, pøi dodací podmínce EXW dle Incoterms 2000, dopravce se zavazuje provést pøed a pøi nakládce prvotní kontrolu zdali :");
		sb.append(eol);
		sb.append(tabsDouble + "a) není zboží na první pohled poškozené (zvláštì u dodávek páskovin se kontroluje koroze zboží),"); 
		sb.append(eol);
		sb.append(tabsDouble + "b) nejsou-li porušené èi znièené obaly zboží."); 
		sb.append(eol);
		sb.append(tabsDouble + "Pokud nastane situace dle bodu a / b, pak tuto skuteènost dopravce neprodlenì oznámí objednateli pøepravy, který stanoví další postup.");
		sb.append(eol);
		sb.append("(1.7) Není-li písemnì domluveno jinak, zboží není možno stohovat."); 
		sb.append(eol);
		sb.append("(1.8) Dopravce (jím povìøený øidiè) ruèí a odpovídá za nakládku zboží a jeho správnou fixaci (ukotvení) na ložné ploše pøepravního prostøedku, aby bìhem pøepravy nemohlo dojít k posunu zboží na ložné ploše."); 
		sb.append(eol);
		sb.append("(1.9) Tyto všeobecné pøepravní podmínky jsou nadøazené všeobecným evropským pøepravním podmínkám CMR.");
		return sb.toString();		
	}
	
	private String getOdstavec2() {		
		StringBuffer sb = new StringBuffer();
		sb.append("(2.1) Dopravce je povinen zásilku vyložit a pøedat odpovìdné osobì pøíjemce na místì urèení podle nákladového listu, a zároveò si v nákladovém listu nechat pøíjemcem potvrdit pøevzetí zásilky.")
		.append(eol).append("(2.2) Nákladový list je vždy øádnì a èitelnì vyplnìn.") 
		.append(eol).append("(2.3) Dopravce je povinen v nákladovém listu uvést:")
		.append(eol).append(tabsDouble + "a) správný a úplný název firmy dopravce vèetnì adresy, IÈO, DIÈ ,")
		.append(eol).append(tabsDouble).append("b) správný a úplný název firmy odesílatele a pøíjemce vèetnì adresy,") 
		.append(eol).append(tabsDouble).append("c) oznaèení pøepravovaného zboží, popøípadì uvést odkaz na dodací list èi fakturu, které se k dané zásilce vztahují, a zároveò i pøesný poèet a druh pøepravních obalù,") 
		.append(eol).append(tabsDouble).append("d) údaj o pøedání zboží pøíjemci, v pøípadì, že zásilka je poškozená musí dopravce toto uvést do nákladového listu s popisem a rozsahem poškození,") 
		.append(eol).append(tabsDouble).append("e) místo urèení (vykládky),") 
		.append(eol).append(tabsDouble).append("f) místo a datum vystavení nákladového listu a podpis dopravce")
		.append(eol).append("(2.4) Nákladový list se vystavuje ve více stejnopisech (1x dopravce, 1x objednatel pøepravy, 1x pøíjemce).") 
		.append(eol).append("(2.5) Za znièený nebo ztracený nákladový list je povinen dopravce vydat odesílateli nový nákladový list s vyznaèením, že jde o duplikát (list náhradní).");
		return sb.toString();
		}
	
	private String getOdstavec3() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"(3.1) Dopravce odpovídá za veškeré škody zpùsobené na zásilce, jež vznikla po jejím pøevzetí dopravcem až do jejího úplného vydání pøíjemci.")
				.append(eol)
				.append("(3.2) Dopravce se zavazuje provádìt pøepravu takovým zpùsobem, aby nemohlo dojít k poškození zásilky a dále, že pøípadné hrozící poškození zásilky odvrátí odborným zásahem. O tomto rovnìž neprodlenì informuje objednatele pøepravy.")
				.append(eol)
				.append("(3.3) Pøi již vzniklé škodì na zásilce bìhem pøepravy je dopravce povinen vynaložit odbornou péèi, aby škoda byla co nejmenší.")
				.append(eol)
				.append("(3.4) Dopravce je povinen bez odkladu (ihned) podat objednateli pøepravy zprávu o škodì na zásilce vzniklé do jejího pøedání pøíjemci. Jestliže však pøíjemce nabyl práva na vydání zásilky, je povinen tuto zprávu podat také pøíjemci. Dopravce odpovídá za škodu zpùsobenou objednateli pøepravy porušením této povinnosti. Dopravce má za povinnost rovnìž bez odkladu hlásit objednateli pøepravy i posun zboží pøi pøepravì, i pøesto, že obal je na pohled neporušen.")
				.append(eol)
				.append("(3.5) Pøi ztrátì nebo znièení zásilky je dopravce povinen hradit objednateli pøepravy plnou cenu zboží, jež bylo pøedmìtem pøepravy, kterou zásilka mìla v dobì, kdy byla pøedána dopravci.")
				.append(eol)
				.append("(3.6) Objednatel pøepravy je povinen poskytnout dopravci správné údaje o zásilce, pokud si je dopravce vyžádá.");
		return sb.toString();
	}

	private String getOdstavec4() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"(4.1) Dopravci pøísluší smluvená úplata za realizovanou pøepravu. Ceny pøeprav jsou smluveny pøedem.")
				.append(eol)
				.append("(4.2) Cena je splatná na základì faktury. Faktura musí obsahovat náležitosti dle § 12.2 zák. è. 588/1982 Sb. v platném znìní a dále:")
				.append(eol)
				.append(tabsDouble)
				.append("- údaje dle pøíslušných daòových a úèetních zákonù")
				.append(eol)
				.append(tabsDouble)
				.append("- pøedmìt plnìní a datum uskuteènìní pøepravy (dodání)")
				.append(eol)
				.append(tabsDouble)
				.append("- èíslo dodacího (balícího) listu")
				.append(eol)
				.append(tabsDouble)
				.append("- lhùtu splatnosti faktury")
				.append(eol)
				.append(tabsDouble)
				.append("- èíslo objednávky objednatele pøepravy")
				.append(eol)
				.append(tabsDouble)
				.append("- datum uskuteènìného zdanitelného plnìní")
				.append(eol)
				.append(tabsDouble)
				.append("- pøílohou je CMR doklad potvrzený pøíjemcem")
				.append(eol)
				.append("(4.3) Dopravce je oprávnìn vystavit a odeslat fakturu po øádném splnìní pøepravy (potvrzený nákladový list pøíjemcem, který bude pøílohou faktury).")
				.append(eol)
				.append("(4.4) V pøípadì, že faktura má nedostatky, objednatel pøepravy je oprávnìn takovou fakturu vrátit zpìt bez úhrady a s uvedením dùvodu vrácení. Dopravce je povinen podle povahy závad fakturu opravit nebo novì vyhotovit. ")
				.append("Oprávnìným vrácením faktury pøestává bìžet pùvodní lhùta splatnosti. Nová lhùta splatnosti bìží znovu ode dne doruèení opravené nebo novì vyhotovené faktury.")
				.append(eol)
				.append("(4.5) Nemùže-li dopravce dokonèit pøepravu pro skuteènosti, za nìž neodpovídá, má nárok na pomìrnou èást pøepravného s pøihlédnutím k pøepravì již uskuteènìné.")
				.append(eol)
				.append("(4.6) Objednatel pøepravy se zavazuje uhradit dopravci pøepravné proti jeho faktuøe a to ve smluvené splatnosti 60 dnù od data vystavení faktury, není-li uvedeno jinak.")
				.append(eol)
				.append("(4.7) Dopravce odpovídá za jakékoli danì, poplatky, cla a podobné platby, které souvisejí s pøepravou a které je povinen hradit dle platných právních pøedpisù.");
		return sb.toString();
	}

	private String getOdstavec5() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"(5.1) Dopravce se zavazuje vyvíjet trvalou aktivitu smìøující ke snížení cen pøepravy. V pøípadì, že konkurenèní dopravci budou nabízet výhodnìjší ceny, bude dopravce písemnì informovat a poskytne mu pøimìøenou lhùtu, která ")
				.append("zohlední rozsah opatøení u dopravce potøebných k tomu, aby byla obnovena jeho konkurenceschopnost. Dopravce neprodlenì vypracuje plán opatøení k obnovení konkurenceschopnosti a seznámí s ním objednatele pøepravy.")
				.append(eol)
				.append("(5.2) Dopravce se zavazuje, že uèiní všechna potøebná opatøení k tomu, aby udržel konkurenceschopnost jeho pøepravních služeb k objednateli pøepravy. K udržení konkurenceschopnosti budou obì smluvní strany spolupracovat ")
				.append("tak, aby bylo dosaženo dalšího plynulého zlepšení v nákladech, kvalitì a logistice.");
		return sb.toString();
	}

	private String getOdstavec6() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"(6.1) Objednatel pøepravy má právo dopravci úètovat a dopravce je povinen zaplatit smluvní pokutu za prodlení s øádnì objednanou pøepravou zboží ve smluveném termínu ve výši 5% z celkové ceny pøepravy bez DPH za každý")
				.append("zapoèatý kalendáøní den prodlení, min. však 1000,- Kè.")
				.append(eol)
				.append("(6.2) V pøípadì, že je objednatel pøepravy v prodlení s úhradou plateb za øádnì provedené a pøedané zásilky dle objednávky, má dopravce právo objednateli pøepravy úètovat a objednatel pøeprava je povinen zaplatit dopravci")
				.append("úrok z prodlení ve výši 0,05% z dlužné èástky za každý den prodlení.")
				.append(eol)
				.append("(6.3) Uplatnìním jakékoli výše uvedené smluvní pokuty nezaniká nárok objednatele pøepravy na náhradu škody dle následujícího odstavce ve výši pøesahující rámec smluvní pokuty.")
				.append(eol)
				.append("(6.4) Dopravce se zavazuje uhradit veškerou škodu zpùsobenou objednateli pøepravy porušením smluvních povinností ze strany dopravce, a to v souladu s ustanovením § 373 a násl. obchodního zákoníku. Hradí se škoda ve ")
				.append("skuteèné výši, vèetnì ušlého zisku a nákladù, které poškozené stranì vznikly jako následek nedodržení podmínek této smlouvy a øádnì objednané pøepravy èi jiného porušení povinnosti dopravce. Škodou se rozumí rovnìž ")
				.append("veškeré smluvní pokuty èi jiné sankce, které jsou uvaleny na objednatele pøepravy jeho zákazníky a koneènými spotøebiteli.");
		return sb.toString();
	}

	private String getOdstavec7() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"(7.1) Objednatel i dopravce se dohodly, že veškeré skuteènosti, se kterými pøijdou do styku pøi realizaci pøíslušných objednávek na pøepravu zboží, tvoøí pøedmìt obchodního tajemství.")
				.append(eol)
				.append("(7.2) Dopravce se zavazuje, že všechny získané informace od objednatel pøepravy nebude poskytovat tøetím stranám a osobám.")
				.append(eol)
				.append("(7.3) Informace a s nimi spojené know-how bude používat pouze pro úèely splnìní udìlených objednávek, nikoli pro vlastní potøebu nebo pro potøeby konkurentùm objednatele pøepravy.")
				.append(eol)
				.append("(7.4) Dopravce zpøístupní informace pouze omezenému okruhu svých pracovníkù, kteøí jsou urèení ke splnìní  udìlených objednávek, uèiní vhodná opatøení a zajistí, aby jeho pracovníci a pøípadnì jeho subdodavatelé ")
				.append("pøepravních služeb udržovali v tajnosti informace ve stejném rozsahu jako dopravce a používali je pouze pro úèely plnìní objednávek.")
				.append(eol)
				.append("(7.5) Všechny objednatelem poskytnuté podklady jako jsou dodací a balící listy, faktury apod.. jsou brány jako duševní majetek objednatele pøepravy a jako takové nesmìjí být postoupeny jiným tøetím stranám èi osobám vyjma ")
				.append("pøíjemce, a nesmìjí být ani kopírovány za úèelem poskytnutí neoprávnìným stranám èi osobám.")
				.append(eol)
				.append("(7.6) Vznikne-li objednateli pøepravy nedodržením povinnosti mlèenlivosti ze strany dopravce nebo jeho pracovníkù èi subdodavatelù škoda, je dopravce povinen tuto uhradit.");
		return sb.toString();
	}
	
	private String getOdstavec8() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"(8.1) V pøípadì okolností vyluèujících odpovìdnost, tedy povodnì, požáru, pracovních konfliktù, nepokojù, úøedních opatøení, ztráty spojení u objednatele pøepravy nebo v jiných pøípadech vyšší moci, které mají za následek")
				.append(" podstatné omezení èinnosti nebo zastavení práce ve výrobních nebo obchodních provozovnách objednatele pøepravy, mùže objednatel pøepravy pøerušit a zastavit platby, oznámí-li tyto události a uplatnìní klausule vyšší moci")
				.append(" neprodlenì dopravci.")
				.append(eol)
				.append("(8.2) V pøípadì okolností vyluèujících odpovìdnost na stranì dopravce, v dùsledku kterých není objektivnì schopen plnit své závazky z øádné objednávky, je rovnìž povinen oznámit tyto události a uplatnìní klausule vyšší moci neprodlenì objednateli pøepravy.");
		return sb.toString();
	}
	
	private String getOdstavec9() {
		StringBuffer sb = new StringBuffer();
		sb.append("(9.1) Objednatel pøepravy má právo stornovat objednávku pøepravy v den uskuteènìní pøepravy a to bez jakéhokoli nároku dopravce na uhrazení jakékoli èástky za èinnosti spojené se stornem èi marnou jízdou.");
		return sb.toString();
	}
	
	private String getOdstavec10() {
		StringBuffer sb = new StringBuffer();
		sb.append("(10.1) Jakékoli spory vzniklé ve spojení s objednávkou pøepravy a dalšími ujednáními se objednatel i dopravce zavazují øešit v prvé øadì dohodou. Pokud však takové øešení nebude možné, k øešení uvedených sporù bude pøíslušný soud se sídlem v Èeských Budìjovicích.");
		return sb.toString();
	}
	
	private String getOdstavec11() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"(11.1) Právní vztahy neupravené tìmito všeobecnými pøepravními podmínkami a pøíslušnou objednávkou se øídí pøíslušnými ustanoveními zák. è. 513/1991 Sb., obchodního zákoníku v platném znìní.")
				.append(eol)
				.append("(11.2) Je-li nebo stane-li se nìkteré ustanovení z tìchto všeobecných pøepravních podmínek  neplatné èi neúèinné, nedotýká se to ostatních ustanovení tìchto podmínek. Obì strany se v takovém pøípadì zavazují nahradit")
				.append(" ustanovení neplatné a/nebo neúèinné ustanovením novým, které by nejlépe odpovídalo pùvodnì zamýšlenému úèelu pùvodního ustanovení.")
				.append(eol)
				.append("(11.3) Opomenutí nebo neuplatnìní smluvních práv objednatelem pøepravy vyplývajících z objednávky a z všeobecných pøepravních podmínek, nebude považováno za vzdání se tìchto práv vùèi dopravci a nemá za následek zánik tìchto práv ani zánik možnosti tato práva uplatnit.")

				.append("(11.4) Všeobecné pøepravní podmínky nabývají platnosti a úèinnosti pøijetím a potvrzením objednávky pøepravy dopravcem.");

		return sb.toString();
	}
	//	void print(Printer printer) {
//	    if (printer.startJob("Tisk objednavky")) {   // the string is the job name - shows up in the printer's job list
//	      Rectangle clientArea = printer.getClientArea();
//	      Rectangle trim = printer.computeTrim(0, 0, 0, 0);
//	      Point dpi = printer.getDPI();
//	      leftMargin = dpi.x + trim.x; // one inch from left side of paper
//	      rightMargin = clientArea.width - dpi.x + trim.x + trim.width; // one inch from right side of paper
//	      topMargin = dpi.y + trim.y; // one inch from top edge of paper
//	      bottomMargin = clientArea.height - dpi.y + trim.y + trim.height; // one inch from bottom edge of paper
//	      
//	      /* Create a buffer for computing tab width. */
//	      int tabSize = 4; // is tab width a user setting in your UI?
//	      StringBuffer tabBuffer = new StringBuffer(tabSize);
//	      for (int i = 0; i < tabSize; i++) tabBuffer.append(' ');
//	      tabs = tabBuffer.toString();
//
//	      /* Create printer GC, and create and set the printer font & foreground color. */
//	      gc = new GC(printer);
//	      
//	     
//	      
//	      FontData fontData = font.getFontData()[0];
//	      printerFont = new Font(printer, fontData.getName(), fontData.getHeight(), fontData.getStyle());
//	      gc.setFont(printerFont);
//	      tabWidth = gc.stringExtent(tabs).x;
//	      lineHeight = gc.getFontMetrics().getHeight();
//	      
//	      RGB rgb = foregroundColor.getRGB();
//	      printerForegroundColor = new Color(printer, rgb);
//	      gc.setForeground(printerForegroundColor);
//	    
//	      rgb = backgroundColor.getRGB();
//	      printerBackgroundColor = new Color(printer, rgb);
//	      gc.setBackground(printerBackgroundColor);
//	    
//	      /* Print text to current gc using word wrap */
//	      printText();
//	      printer.endJob();
//
//	      /* Cleanup graphics resources used in printing */
//	      printerFont.dispose();
//	      printerForegroundColor.dispose();
//	      printerBackgroundColor.dispose();
//	      gc.dispose();
//	    }
//	  }
	

}
