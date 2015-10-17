package cz.kpartl.preprava.util;

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
	Font normalFont, boldFont, titleFont;
	Color foregroundColor, backgroundColor;

	Printer printer;
	GC gc;
	Font titleText, normalText, boldText;
	Color printerForegroundColor, printerBackgroundColor;

	int lineHeight = 0;
	int tabWidth = 0;
	int leftMargin, rightMargin, topMargin, bottomMargin, titleTopMargin;
	int x, y;
	int index, end;
	int col1 = 0;
	int col2 = 300;
	int col3 = 600;
	int col4 = 900;
	int col5 = 1200;
	int col6 = 1500;
	String textToPrint;
	ImageData kernIcon;
	String tabs;
	StringBuffer wordBuffer;
	IEclipseContext context;

	final String separator = "\t";
	final char eol = 0x0d;

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
		normalFont = new Font(shell.getDisplay(), "Times New Roman", 12, SWT.NORMAL);
		boldFont = new Font(shell.getDisplay(), "Times New Roman", 12, SWT.BOLD);
		titleFont = new Font(shell.getDisplay(), "Times New Roman", 16, SWT.BOLD);
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
			Rectangle clientArea = printer.getClientArea();
			Rectangle trim = printer.computeTrim(0, 0, 0, 0);
			Point dpi = printer.getDPI();
			leftMargin = dpi.x + trim.x; // one inch from left side of paper
			col1 += leftMargin;
			col2 += leftMargin;
			col3 += leftMargin;
			col4 += leftMargin;
			col5 += leftMargin;
			col6 += leftMargin;
			rightMargin = clientArea.width - dpi.x + trim.x + trim.width; // one
																			// inch
																			// from
																			// right
																			// side
																			// of
																			// paper
			topMargin = dpi.y + trim.y + 15; // one inch from top edge of paper
			titleTopMargin =30;
			bottomMargin = clientArea.height - dpi.y + trim.y + trim.height; // one
																				// inch
																				// from
																				// bottom
																				// edge
																				// of
																				// paper

			/* Create a buffer for computing tab width. */
			int tabSize = 4; // is tab width a user setting in your UI?
			StringBuffer tabBuffer = new StringBuffer(tabSize);
			for (int i = 0; i < tabSize; i++)
				tabBuffer.append(' ');
			tabs = tabBuffer.toString();

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
		int scaleFactor = 3;
		int titleCenter = (kernIcon.width + titleLineHeight) / 2;
		gc.drawString("OBJEDNÁVKA PØEPRAVY", leftMargin, titleCenter - titleLineHeight);
		
		Image printerImage = new Image(printer, kernIcon);
		gc.drawImage(printerImage, 0, 0, kernIcon.width,
				kernIcon.height, 1500, titleTopMargin, 
                scaleFactor * kernIcon.width,
                scaleFactor * kernIcon.height);
  
              // Clean up
              printerImage.dispose();
	}
	
	void printBody() {
		y = topMargin;
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
		gc.drawString(objednavka.getDod_psc(), col4, y);
		gc.drawString(objednavka.getDod_mesto(), col4, y);
		newline();
		gc.drawString(objednavka.getObjednavka4(), col1, y);
		gc.drawString("DIÈ: " + objednavka.getDod_dic(), col4, y);
		newline();
		gc.drawString(objednavka.getObjednavka5(), col1, y);
		gc.drawString("IÈ: " + objednavka.getDod_ic(), col4, y);
		newline();
		gc.drawString(objednavka.getObjednavka6(), col1, y);
		gc.drawString("Dodavatelské èíslo: ", col4, y);
		gc.drawString(objednavka.getDod_sap_cislo(), col6, y);
		newline();
		gc.drawString(objednavka.getObjednavka7(), col1, y);
		newline();
		gc.drawString(objednavka.getObjednavka8(), col1, y);
		
	}
	
	
	void printTextOld() {
		
		
		wordBuffer = new StringBuffer();
		x = leftMargin;
		y = topMargin;
		
		gc.setFont(normalText);
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
			printer.endPage();
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
		lineHeight = (int) (1.5 * (gc.getFontMetrics().getHeight()));
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

}
