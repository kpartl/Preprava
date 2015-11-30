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
	int tabWidth = 0;
	int leftMargin = 100, lineMargin, rightMargin, topMargin, bottomMargin, titleTopMargin, scaleFactor;
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
		//Point dpi = printer.getDPI();
		
		formHeight = clientArea.height;
		scaleFactor = 3; // scaling factor for logo
		formWidth = clientArea.width - leftMargin;
		leftMargin = trim.x + (int) (formWidth / 24.8);
		lineMargin = leftMargin / 5;
		rightMargin = clientArea.width + trim.x + trim.width; 
		titleTopMargin = trim.y;
		topMargin = titleTopMargin + lineMargin + scaleFactor * kernIcon.height;//dpi.y / 2 + trim.y; // one inch from top edge of paper
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
		addLine(result, "Objedn�vka �.", obj.getCislo_objednavky());
		addLine(result, "F�ze", ObjednanoView.getFazeKey(obj.getFaze()));
		addLine(result, "Dopravce", obj.getDopravce() != null ? obj
				.getDopravce().getNazev() : "");
		addLine(result, "��slo faktury dopravce",
				obj.getCislo_faktury_dopravce());
		addLine(result, "Cena dopravy",
				obj.getCenaFormated() + " " + obj.getMena());
		addLine(result, "Zm�na term�nu nakl�dky", obj.getZmena_nakladky());
		addLine(result, "Datum nakl�dky", obj.getPozadavek()
				.getDatum_nakladky());
		addLine(result, "Datum vykl�dky", obj.getPozadavek()
				.getDatum_vykladky());
		addLine(result, "V�choz� destinace", obj.getPozadavek()
				.getDestinaceZNazevACislo());
		addLine(result, "Kontaktn� osoba ve v�choz� destinaci", obj
				.getPozadavek().getDestinaceZKontaktAOsobu());
		addLine(result, "C�lov� destinace", obj.getPozadavek()
				.getDestinaceDoNazevACislo());
		addLine(result, "Kontaktn� osoba v c�lov� destinaci", obj
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
	
		
		int titleCenter = (kernIcon.width + titleLineHeight) / 2;
		gc.drawString("OBJEDN�VKA P�EPRAVY", leftMargin , titleCenter - titleLineHeight + 15);		
		
		Image printerImage = new Image(printer, kernIcon);
		gc.drawImage(printerImage, 0, 0, kernIcon.width,
				kernIcon.height, formWidth - scaleFactor * kernIcon.width, titleTopMargin, 
                scaleFactor * kernIcon.width,
                scaleFactor * kernIcon.height);
  
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
		gc.drawString("Dodavatel (p�epravce):", col4, y);
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
		gc.drawString("DI�: " + objednavka.getDod_dic(), col4, y);
		newline();
		gc.drawString(objednavka.getObjednavka5(), col1, y);
		gc.drawString("I�: " + objednavka.getDod_ic(), col4, y);
		newline();
		gc.drawString(objednavka.getObjednavka6(), col1, y);
		gc.drawString("Dodavatelsk� ��slo: ", col4, y);
		gc.drawString(objednavka.getDod_sap_cislo(), col5, y);
		newline();
		gc.drawString(objednavka.getObjednavka7(), col1, y);
		newline();
		gc.drawString(objednavka.getObjednavka8(), col1, y);
		newline();
		
		gc.setFont(boldText);
		drawLine();		
		
		gc.drawString("��slo objedn�vky: ", col1, y);
		gc.drawString("Datum objedn�vky: ", col4, y);
		int verticalLineFrom = y;
		gc.setFont(normalText);
		gc.drawString(String.format("%06d %n", objednavka.getCislo_objednavky()), col2, y);
		gc.drawString(getDatumAsText(objednavka.getDatum()), col5, y);
		newline();
		drawLine();
		drawVerticalLine(col4, topMargin, y);
		
		gc.setFont(boldText);
		gc.drawString("Term�n nakl�dky: ", col1, y);
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
		gc.drawString("M�sto nakl�dky: ", col1, y);
		gc.setFont(normalText);
		gc.drawString(objednavka.getNakl_psc() + "  " + objednavka.getNakl_mesto(), col4, y);		
		newline();
		drawShortLine();
		gc.drawString("Kontaktn� osoba:", col2, y);
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
		gc.drawString("Specifikace zbo��:", col1, y);
		gc.setFont(normalText);
		gc.drawString("Hmotnost:", col2, y);
		gc.drawString(objednavka.getPozadavek().getCelkova_hmotnost(), col3, y);
		gc.drawString("ADR:", col5, y);
		gc.drawString(objednavka.getAdr(), col6, y);
		newline();
		drawShortLine();
		gc.drawString("Po�et palet:", col2, y);
		gc.drawString(objednavka.getPozadavek().getPocet_palet(), col3, y);
		gc.drawString("Stohovateln�?", col5, y);
		gc.drawString(objednavka.getPozadavek().getJe_stohovatelne() ? "ano":"ne", col6, y);		
		newline();
		drawLine();
		drawVerticalLine(col3, verticalFrom2, y);
		drawVerticalLine(col5, verticalFrom2, y);
		drawVerticalLine(col6, verticalFrom2, y);
		gc.setFont(boldText);
		gc.drawString("Term�n vykl�dky: ", col1, y);
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
		gc.drawString("M�sto vykl�dky: ", col1, y);
		gc.setFont(normalText);
		gc.drawString(objednavka.getVykl_psc() + " " + objednavka.getVykl_mesto(), col4, y);		
		newline();
		drawShortLine();
		
		gc.drawString("Kontaktn� osoba:", col2, y);
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
		gc.drawString("P�epravn� podm�nky:", col1, y);
		gc.setFont(normalText);
		gc.drawString(objednavka.getPreprav_podminky(), col2, y);
		
		newline();
		drawLine();
		
		gc.setFont(boldText);
		gc.drawString("Pozn�mka:", col1, y);
		gc.setFont(normalText);
		gc.drawString(objednavka.getPozadavek().getPoznamka(), col2, y);
		
		newline();
		drawLine();
		drawVerticalLine(col2, verticalLineFrom, y);
		printPoznamkaLine(getPoznamky());
		
		gc.setFont(boldItalicText);
		newline();
		gc.drawString("P�epravn� p��kaz dle podm�nek KERN-LIEBERS CR spol. s r.o.", col2, y);
		drawLine();
	
		newline();
		gc.drawRectangle(new Rectangle(leftMargin - lineMargin, titleTopMargin, formWidth - leftMargin + lineMargin, y));
		newline();
		newline();
		gc.setFont(boldUnderText);
		gc.drawString("Potvrzen� objedn�vky dodavatelem", col1, y);
		gc.setFont(boldText);
		newline();
		newline();
		newline();
		gc.drawString("Datum: .....................", col1, y);
		gc.drawString("Raz�tko a podpis: ........................................", col4, y);
		
		printSecondPage();
	}
	
	private void printSecondPage() {
		printer.endPage();
		printer.startPage();
		y = titleTopMargin;	
		lineHeightConst = 1.1f;
				
		gc.setFont(nadpisText);
		int center = formWidth /2;
		gc.drawString("V�eobecn� p�epravn� podm�nky Kern-Liebers CR spol. s r.o.", center/2, y);
		newline();
		printOdstavecNadpis("1." + tabs + " Z�kladn� ustanoven�");
		textToPrint = getOdstavec1();
		rightMargin = formWidth - leftMargin + lineMargin;
		printTextCharByChar();
		printOdstavecNadpis("2." + tabs + "N�kladov� list");
		textToPrint = getOdstavec2();
		printTextCharByChar();
		printOdstavecNadpis("3." + tabs + "�kody, ztr�ta, zni�en� z�silky");
		textToPrint = getOdstavec3();
		printTextCharByChar();
		printOdstavecNadpis("4." + tabs + "�plata za p�epravu (p�epravn�) a platebn� podm�nky");
		textToPrint = getOdstavec4();
		printTextCharByChar();
		printOdstavecNadpis("5." + tabs + "Konkurenceschopnost");
		textToPrint = getOdstavec5();
		printTextCharByChar();
		printOdstavecNadpis("6." + tabs + "Smluvn� pokuty a �rok z prodlen�");
		textToPrint = getOdstavec6();
		printTextCharByChar();
		printOdstavecNadpis("7." + tabs + "Povinnost zachovat ml�enlivost");
		textToPrint = getOdstavec7();
		printTextCharByChar();
		printOdstavecNadpis("8." + tabs + "Vy��� moc");
		textToPrint = getOdstavec8();
		printTextCharByChar();
		printOdstavecNadpis("9." + tabs + "Storno objedn�vky p�epravy");
		textToPrint = getOdstavec9();
		printTextCharByChar();
		printOdstavecNadpis("10." + tabs + "�e�en� spor�");
		textToPrint = getOdstavec10();
		printTextCharByChar();
		printOdstavecNadpis("11." + tabs + "Ostatn� ustanoven�");
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
		sb.append("Potvrzen�m objedn�vky na p�epravu zbo�� zaslanou objednatelem Kern-Liebers  se dopravce zavazuje objednateli p�epravy, �e p�eprav� zbo�� (z�silku) z ur�en�ho m�sta odesl�n� (m�sto nakl�dky) do ur�en�ho m�sta vylo�en�");
		sb.append(" (m�sto vykl�dky), z�rove� pln� souhlas� se v�eobecn�mi p�epravn�mi podm�nkami Kern-Liebers a zavazuje se t�mito podm�nkami se bezv�hradn� ��dit. Objednatel p�epravy se zavazuje uhradit dopravci sjednanou cenu p�epravy  (p�epravn�).");
		sb.append(eol);
		sb.append("(1.1) Dopravce potvrzuje objednateli p�epravy fyzick� p�evzet� z�silky v dobr�m stavu v p�epravn�m listu, a objednatel p�epravy potvrzuje dopravci po�adavek na p�epravu svou objedn�vkou p�epravy.");
		sb.append(eol);
		sb.append("(1.2) Objednatel p�epravy v�dy p�ed�v� dopravci ve�ker� nezbytn� doklady k z�silce. Dopravce odpov�d� za proveden� kontroly, �e doklady jsou kompletn� a �pln� a p�eprava m��e b�t realizov�na. Objednatel p�epravy odpov�d� za �kodu zp�sobenou dopravci nespr�vn�mi doklady.");
		sb.append(eol);
		sb.append("(1.3) Dopravce je povinen v�dy vydat objednateli p�epravy p�i p�evzet� z�silky k p�eprav� n�kladov� list, kter� je ��dn� potvrzen �idi�em a m� v�echny n�le�itosti (viz.bod 2).");
		sb.append(eol);
		sb.append("(1.4) Dopravce je povinen p�epravu prov�st do m�sta ur�en� s odbornou p��� ve smluven� lh�t� a bez zbyte�n�ho odkladu.");
		sb.append(eol);
		sb.append("(1.5) Nen�-li p�semn� domluveno jinak, zbo�� nen� mo�no p�ekl�dat na jin� dopravn� prost�edek, ne� na kter� bylo zbo�� nalo�eno v Kern-Liebers C�. Je-li p�semn� domluveno, �e dopravce m��e objednanou p�epravu plnit pomoc� dal��ho dopravce nebo zbo�� bude b�hem p�epravy p�elo�eno na jin� p�epravn� prost�edek, dopravce t�mto pln� odpov�d� za v�echny �kody vznikl� a zp�soben� objednateli p�epravy, jako by p�epravu uskute��oval s�m.");
		sb.append(eol);
		sb.append("(1.6) V p��pad�, �e objednatel p�epravy objedn� u dopravce nakl�dku zbo�� a jeho p�epravu od sv�ho dodavatele, p�i dodac� podm�nce EXW dle Incoterms 2000, dopravce se zavazuje prov�st p�ed a p�i nakl�dce prvotn� kontrolu zdali :");
		sb.append(eol);
		sb.append(tabsDouble + "a) nen� zbo�� na prvn� pohled po�kozen� (zvl�t� u dod�vek p�skovin se kontroluje koroze zbo��),"); 
		sb.append(eol);
		sb.append(tabsDouble + "b) nejsou-li poru�en� �i zni�en� obaly zbo��."); 
		sb.append(eol);
		sb.append(tabsDouble + "Pokud nastane situace dle bodu a / b, pak tuto skute�nost dopravce neprodlen� ozn�m� objednateli p�epravy, kter� stanov� dal�� postup.");
		sb.append(eol);
		sb.append("(1.7) Nen�-li p�semn� domluveno jinak, zbo�� nen� mo�no stohovat."); 
		sb.append(eol);
		sb.append("(1.8) Dopravce (j�m pov��en� �idi�) ru�� a odpov�d� za nakl�dku zbo�� a jeho spr�vnou fixaci (ukotven�) na lo�n� plo�e p�epravn�ho prost�edku, aby b�hem p�epravy nemohlo doj�t k posunu zbo�� na lo�n� plo�e."); 
		sb.append(eol);
		sb.append("(1.9) Tyto v�eobecn� p�epravn� podm�nky jsou nad�azen� v�eobecn�m evropsk�m p�epravn�m podm�nk�m CMR.");
		return sb.toString();		
	}
	
	private String getOdstavec2() {		
		StringBuffer sb = new StringBuffer();
		sb.append("(2.1) Dopravce je povinen z�silku vylo�it a p�edat odpov�dn� osob� p��jemce na m�st� ur�en� podle n�kladov�ho listu, a z�rove� si v n�kladov�m listu nechat p��jemcem potvrdit p�evzet� z�silky.")
		.append(eol).append("(2.2) N�kladov� list je v�dy ��dn� a �iteln� vypln�n.") 
		.append(eol).append("(2.3) Dopravce je povinen v n�kladov�m listu uv�st:")
		.append(eol).append(tabsDouble + "a) spr�vn� a �pln� n�zev firmy dopravce v�etn� adresy, I�O, DI� ,")
		.append(eol).append(tabsDouble).append("b) spr�vn� a �pln� n�zev firmy odes�latele a p��jemce v�etn� adresy,") 
		.append(eol).append(tabsDouble).append("c) ozna�en� p�epravovan�ho zbo��, pop��pad� uv�st odkaz na dodac� list �i fakturu, kter� se k dan� z�silce vztahuj�, a z�rove� i p�esn� po�et a druh p�epravn�ch obal�,") 
		.append(eol).append(tabsDouble).append("d) �daj o p�ed�n� zbo�� p��jemci, v p��pad�, �e z�silka je po�kozen� mus� dopravce toto uv�st do n�kladov�ho listu s popisem a rozsahem po�kozen�,") 
		.append(eol).append(tabsDouble).append("e) m�sto ur�en� (vykl�dky),") 
		.append(eol).append(tabsDouble).append("f) m�sto a datum vystaven� n�kladov�ho listu a podpis dopravce")
		.append(eol).append("(2.4) N�kladov� list se vystavuje ve v�ce stejnopisech (1x dopravce, 1x objednatel p�epravy, 1x p��jemce).") 
		.append(eol).append("(2.5) Za zni�en� nebo ztracen� n�kladov� list je povinen dopravce vydat odes�lateli nov� n�kladov� list s vyzna�en�m, �e jde o duplik�t (list n�hradn�).");
		return sb.toString();
		}
	
	private String getOdstavec3() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"(3.1) Dopravce odpov�d� za ve�ker� �kody zp�soben� na z�silce, je� vznikla po jej�m p�evzet� dopravcem a� do jej�ho �pln�ho vyd�n� p��jemci.")
				.append(eol)
				.append("(3.2) Dopravce se zavazuje prov�d�t p�epravu takov�m zp�sobem, aby nemohlo doj�t k po�kozen� z�silky a d�le, �e p��padn� hroz�c� po�kozen� z�silky odvr�t� odborn�m z�sahem. O tomto rovn� neprodlen� informuje objednatele p�epravy.")
				.append(eol)
				.append("(3.3) P�i ji� vznikl� �kod� na z�silce b�hem p�epravy je dopravce povinen vynalo�it odbornou p��i, aby �koda byla co nejmen��.")
				.append(eol)
				.append("(3.4) Dopravce je povinen bez odkladu (ihned) podat objednateli p�epravy zpr�vu o �kod� na z�silce vznikl� do jej�ho p�ed�n� p��jemci. Jestli�e v�ak p��jemce nabyl pr�va na vyd�n� z�silky, je povinen tuto zpr�vu podat tak� p��jemci. Dopravce odpov�d� za �kodu zp�sobenou objednateli p�epravy poru�en�m t�to povinnosti. Dopravce m� za povinnost rovn� bez odkladu hl�sit objednateli p�epravy i posun zbo�� p�i p�eprav�, i p�esto, �e obal je na pohled neporu�en.")
				.append(eol)
				.append("(3.5) P�i ztr�t� nebo zni�en� z�silky je dopravce povinen hradit objednateli p�epravy plnou cenu zbo��, je� bylo p�edm�tem p�epravy, kterou z�silka m�la v dob�, kdy byla p�ed�na dopravci.")
				.append(eol)
				.append("(3.6) Objednatel p�epravy je povinen poskytnout dopravci spr�vn� �daje o z�silce, pokud si je dopravce vy��d�.");
		return sb.toString();
	}

	private String getOdstavec4() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"(4.1) Dopravci p��slu�� smluven� �plata za realizovanou p�epravu. Ceny p�eprav jsou smluveny p�edem.")
				.append(eol)
				.append("(4.2) Cena je splatn� na z�klad� faktury. Faktura mus� obsahovat n�le�itosti dle � 12.2 z�k. �. 588/1982 Sb. v platn�m zn�n� a d�le:")
				.append(eol)
				.append(tabsDouble)
				.append("- �daje dle p��slu�n�ch da�ov�ch a ��etn�ch z�kon�")
				.append(eol)
				.append(tabsDouble)
				.append("- p�edm�t pln�n� a datum uskute�n�n� p�epravy (dod�n�)")
				.append(eol)
				.append(tabsDouble)
				.append("- ��slo dodac�ho (bal�c�ho) listu")
				.append(eol)
				.append(tabsDouble)
				.append("- lh�tu splatnosti faktury")
				.append(eol)
				.append(tabsDouble)
				.append("- ��slo objedn�vky objednatele p�epravy")
				.append(eol)
				.append(tabsDouble)
				.append("- datum uskute�n�n�ho zdaniteln�ho pln�n�")
				.append(eol)
				.append(tabsDouble)
				.append("- p��lohou je CMR doklad potvrzen� p��jemcem")
				.append(eol)
				.append("(4.3) Dopravce je opr�vn�n vystavit a odeslat fakturu po ��dn�m spln�n� p�epravy (potvrzen� n�kladov� list p��jemcem, kter� bude p��lohou faktury).")
				.append(eol)
				.append("(4.4) V p��pad�, �e faktura m� nedostatky, objednatel p�epravy je opr�vn�n takovou fakturu vr�tit zp�t bez �hrady a s uveden�m d�vodu vr�cen�. Dopravce je povinen podle povahy z�vad fakturu opravit nebo nov� vyhotovit. ")
				.append("Opr�vn�n�m vr�cen�m faktury p�est�v� b�et p�vodn� lh�ta splatnosti. Nov� lh�ta splatnosti b�� znovu ode dne doru�en� opraven� nebo nov� vyhotoven� faktury.")
				.append(eol)
				.append("(4.5) Nem��e-li dopravce dokon�it p�epravu pro skute�nosti, za n� neodpov�d�, m� n�rok na pom�rnou ��st p�epravn�ho s p�ihl�dnut�m k p�eprav� ji� uskute�n�n�.")
				.append(eol)
				.append("(4.6) Objednatel p�epravy se zavazuje uhradit dopravci p�epravn� proti jeho faktu�e a to ve smluven� splatnosti 60 dn� od data vystaven� faktury, nen�-li uvedeno jinak.")
				.append(eol)
				.append("(4.7) Dopravce odpov�d� za jak�koli dan�, poplatky, cla a podobn� platby, kter� souvisej� s p�epravou a kter� je povinen hradit dle platn�ch pr�vn�ch p�edpis�.");
		return sb.toString();
	}

	private String getOdstavec5() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"(5.1) Dopravce se zavazuje vyv�jet trvalou aktivitu sm��uj�c� ke sn�en� cen p�epravy. V p��pad�, �e konkuren�n� dopravci budou nab�zet v�hodn�j�� ceny, bude dopravce p�semn� informovat a poskytne mu p�im��enou lh�tu, kter� ")
				.append("zohledn� rozsah opat�en� u dopravce pot�ebn�ch k tomu, aby byla obnovena jeho konkurenceschopnost. Dopravce neprodlen� vypracuje pl�n opat�en� k obnoven� konkurenceschopnosti a sezn�m� s n�m objednatele p�epravy.")
				.append(eol)
				.append("(5.2) Dopravce se zavazuje, �e u�in� v�echna pot�ebn� opat�en� k tomu, aby udr�el konkurenceschopnost jeho p�epravn�ch slu�eb k objednateli p�epravy. K udr�en� konkurenceschopnosti budou ob� smluvn� strany spolupracovat ")
				.append("tak, aby bylo dosa�eno dal��ho plynul�ho zlep�en� v n�kladech, kvalit� a logistice.");
		return sb.toString();
	}

	private String getOdstavec6() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"(6.1) Objednatel p�epravy m� pr�vo dopravci ��tovat a dopravce je povinen zaplatit smluvn� pokutu za prodlen� s ��dn� objednanou p�epravou zbo�� ve smluven�m term�nu ve v��i 5% z celkov� ceny p�epravy bez DPH za ka�d�")
				.append("zapo�at� kalend��n� den prodlen�, min. v�ak 1000,- K�.")
				.append(eol)
				.append("(6.2) V p��pad�, �e je objednatel p�epravy v prodlen� s �hradou plateb za ��dn� proveden� a p�edan� z�silky dle objedn�vky, m� dopravce pr�vo objednateli p�epravy ��tovat a objednatel p�eprava je povinen zaplatit dopravci")
				.append("�rok z prodlen� ve v��i 0,05% z dlu�n� ��stky za ka�d� den prodlen�.")
				.append(eol)
				.append("(6.3) Uplatn�n�m jak�koli v��e uveden� smluvn� pokuty nezanik� n�rok objednatele p�epravy na n�hradu �kody dle n�sleduj�c�ho odstavce ve v��i p�esahuj�c� r�mec smluvn� pokuty.")
				.append(eol)
				.append("(6.4) Dopravce se zavazuje uhradit ve�kerou �kodu zp�sobenou objednateli p�epravy poru�en�m smluvn�ch povinnost� ze strany dopravce, a to v souladu s ustanoven�m � 373 a n�sl. obchodn�ho z�kon�ku. Hrad� se �koda ve ")
				.append("skute�n� v��i, v�etn� u�l�ho zisku a n�klad�, kter� po�kozen� stran� vznikly jako n�sledek nedodr�en� podm�nek t�to smlouvy a ��dn� objednan� p�epravy �i jin�ho poru�en� povinnosti dopravce. �kodou se rozum� rovn� ")
				.append("ve�ker� smluvn� pokuty �i jin� sankce, kter� jsou uvaleny na objednatele p�epravy jeho z�kazn�ky a kone�n�mi spot�ebiteli.");
		return sb.toString();
	}

	private String getOdstavec7() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"(7.1) Objednatel i dopravce se dohodly, �e ve�ker� skute�nosti, se kter�mi p�ijdou do styku p�i realizaci p��slu�n�ch objedn�vek na p�epravu zbo��, tvo�� p�edm�t obchodn�ho tajemstv�.")
				.append(eol)
				.append("(7.2) Dopravce se zavazuje, �e v�echny z�skan� informace od objednatel p�epravy nebude poskytovat t�et�m stran�m a osob�m.")
				.append(eol)
				.append("(7.3) Informace a s nimi spojen� know-how bude pou��vat pouze pro ��ely spln�n� ud�len�ch objedn�vek, nikoli pro vlastn� pot�ebu nebo pro pot�eby konkurent�m objednatele p�epravy.")
				.append(eol)
				.append("(7.4) Dopravce zp��stupn� informace pouze omezen�mu okruhu sv�ch pracovn�k�, kte�� jsou ur�en� ke spln�n�  ud�len�ch objedn�vek, u�in� vhodn� opat�en� a zajist�, aby jeho pracovn�ci a p��padn� jeho subdodavatel� ")
				.append("p�epravn�ch slu�eb udr�ovali v tajnosti informace ve stejn�m rozsahu jako dopravce a pou��vali je pouze pro ��ely pln�n� objedn�vek.")
				.append(eol)
				.append("(7.5) V�echny objednatelem poskytnut� podklady jako jsou dodac� a bal�c� listy, faktury apod.. jsou br�ny jako du�evn� majetek objednatele p�epravy a jako takov� nesm�j� b�t postoupeny jin�m t�et�m stran�m �i osob�m vyjma ")
				.append("p��jemce, a nesm�j� b�t ani kop�rov�ny za ��elem poskytnut� neopr�vn�n�m stran�m �i osob�m.")
				.append(eol)
				.append("(7.6) Vznikne-li objednateli p�epravy nedodr�en�m povinnosti ml�enlivosti ze strany dopravce nebo jeho pracovn�k� �i subdodavatel� �koda, je dopravce povinen tuto uhradit.");
		return sb.toString();
	}
	
	private String getOdstavec8() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"(8.1) V p��pad� okolnost� vylu�uj�c�ch odpov�dnost, tedy povodn�, po��ru, pracovn�ch konflikt�, nepokoj�, ��edn�ch opat�en�, ztr�ty spojen� u objednatele p�epravy nebo v jin�ch p��padech vy��� moci, kter� maj� za n�sledek")
				.append(" podstatn� omezen� �innosti nebo zastaven� pr�ce ve v�robn�ch nebo obchodn�ch provozovn�ch objednatele p�epravy, m��e objednatel p�epravy p�eru�it a zastavit platby, ozn�m�-li tyto ud�losti a uplatn�n� klausule vy��� moci")
				.append(" neprodlen� dopravci.")
				.append(eol)
				.append("(8.2) V p��pad� okolnost� vylu�uj�c�ch odpov�dnost na stran� dopravce, v d�sledku kter�ch nen� objektivn� schopen plnit sv� z�vazky z ��dn� objedn�vky, je rovn� povinen ozn�mit tyto ud�losti a uplatn�n� klausule vy��� moci neprodlen� objednateli p�epravy.");
		return sb.toString();
	}
	
	private String getOdstavec9() {
		StringBuffer sb = new StringBuffer();
		sb.append("(9.1) Objednatel p�epravy m� pr�vo stornovat objedn�vku p�epravy v den uskute�n�n� p�epravy a to bez jak�hokoli n�roku dopravce na uhrazen� jak�koli ��stky za �innosti spojen� se stornem �i marnou j�zdou.");
		return sb.toString();
	}
	
	private String getOdstavec10() {
		StringBuffer sb = new StringBuffer();
		sb.append("(10.1) Jak�koli spory vznikl� ve spojen� s objedn�vkou p�epravy a dal��mi ujedn�n�mi se objednatel i dopravce zavazuj� �e�it v prv� �ad� dohodou. Pokud v�ak takov� �e�en� nebude mo�n�, k �e�en� uveden�ch spor� bude p��slu�n� soud se s�dlem v �esk�ch Bud�jovic�ch.");
		return sb.toString();
	}
	
	private String getOdstavec11() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"(11.1) Pr�vn� vztahy neupraven� t�mito v�eobecn�mi p�epravn�mi podm�nkami a p��slu�nou objedn�vkou se ��d� p��slu�n�mi ustanoven�mi z�k. �. 513/1991 Sb., obchodn�ho z�kon�ku v platn�m zn�n�.")
				.append(eol)
				.append("(11.2) Je-li nebo stane-li se n�kter� ustanoven� z t�chto v�eobecn�ch p�epravn�ch podm�nek  neplatn� �i ne��inn�, nedot�k� se to ostatn�ch ustanoven� t�chto podm�nek. Ob� strany se v takov�m p��pad� zavazuj� nahradit")
				.append(" ustanoven� neplatn� a/nebo ne��inn� ustanoven�m nov�m, kter� by nejl�pe odpov�dalo p�vodn� zam��len�mu ��elu p�vodn�ho ustanoven�.")
				.append(eol)
				.append("(11.3) Opomenut� nebo neuplatn�n� smluvn�ch pr�v objednatelem p�epravy vypl�vaj�c�ch z objedn�vky a z v�eobecn�ch p�epravn�ch podm�nek, nebude pova�ov�no za vzd�n� se t�chto pr�v v��i dopravci a nem� za n�sledek z�nik t�chto pr�v ani z�nik mo�nosti tato pr�va uplatnit.")

				.append("(11.4) V�eobecn� p�epravn� podm�nky nab�vaj� platnosti a ��innosti p�ijet�m a potvrzen�m objedn�vky p�epravy dopravcem.");

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
