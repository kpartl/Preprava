package cz.kpartl.preprava.util;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
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

	  Font font;
	  Color foregroundColor, backgroundColor;
	  
	  Printer printer;
	  GC gc;
	  Font printerFont;
	  Color printerForegroundColor, printerBackgroundColor;

	  int lineHeight = 0;
	  int tabWidth = 0;
	  int leftMargin, rightMargin, topMargin, bottomMargin;
	  int x, y;
	  int index, end;
	  String textToPrint;
	  String tabs;
	  StringBuffer wordBuffer;
	  
	  final String separator = "\t";
	  final char eol = 0x0d;
	  
	  public PrintHelper(Shell shell){
		  this.shell = shell;
	  }
	
	public void tiskVybraneObjednavky(Objednavka selectedObjednavka){
		
		 PrintDialog dialog = new PrintDialog(shell, SWT.NULL);
		    PrinterData data = dialog.open();
		    if (data == null) return;
		    if (data.printToFile) {
		      data.fileName = "print.out"; // you probably want to ask the user for a filename
		    }
		    
		    /* Get the text to print from the Text widget (you could get it from anywhere, i.e. your java model) */
		    textToPrint = getObjednavkaAsText(selectedObjednavka);
		    printer = new Printer(data);
		    font = new Font(shell.getDisplay(), "Times New Roman", 10, SWT.NORMAL);
		      foregroundColor = shell.getDisplay().getSystemColor(SWT.COLOR_BLACK);
		      backgroundColor = shell.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		    Thread printingThread = new Thread("Printing") {
		      public void run() {		    	 
		        print(printer);
		        printer.dispose();
		      }
		    };
		    printingThread.start();
	}
	
	private String getObjednavkaAsText(Objednavka obj){				
		StringBuffer result = new StringBuffer();	
		addLine(result,"Objednávka è.", obj.getCislo_objednavky());		
		addLine(result,"Fáze", ObjednanoView.getComboItems(true)[obj.getFaze()]);
		addLine(result,"Dopravce", obj.getDopravce() != null ? obj.getDopravce().getNazev() : "");
		addLine(result,"Èíslo faktury dopravce", obj.getCislo_faktury_dopravce());
		addLine(result,"Cena dopravy", obj.getCenaFormated() + " " + obj.getMena());
		addLine(result,"Zmìna termínu nakládky", obj.getZmena_nakladky());
		addLine(result,"Datum nakládky", obj.getPozadavek().getDatum_nakladky());
		addLine(result,"Datum vykládky", obj.getPozadavek().getDatum_vykladky());
		addLine(result,"Výchozí destinace", obj.getPozadavek().getDestinaceZNazevACislo());
		addLine(result,"Kontaktní osoba ve výchozí destinaci", obj.getPozadavek().getDestinaceZKontaktAOsobu());
		addLine(result,"Cílová destinace", obj.getPozadavek().getDestinaceDoNazevACislo());
		addLine(result,"Kontaktní osoba v cílové destinaci", obj.getPozadavek().getDestinaceDoKontaktAOsobu());
	
		
		return result.toString();
	}
	
	private void addLine(final StringBuffer sb, final String key, final Object value){
		sb.append(key).append(separator).append(value).append(eol);
	}
	
	void print(Printer printer) {
	    if (printer.startJob("Tisk objednavky")) {   // the string is the job name - shows up in the printer's job list
	      Rectangle clientArea = printer.getClientArea();
	      Rectangle trim = printer.computeTrim(0, 0, 0, 0);
	      Point dpi = printer.getDPI();
	      leftMargin = dpi.x + trim.x; // one inch from left side of paper
	      rightMargin = clientArea.width - dpi.x + trim.x + trim.width; // one inch from right side of paper
	      topMargin = dpi.y + trim.y; // one inch from top edge of paper
	      bottomMargin = clientArea.height - dpi.y + trim.y + trim.height; // one inch from bottom edge of paper
	      
	      /* Create a buffer for computing tab width. */
	      int tabSize = 4; // is tab width a user setting in your UI?
	      StringBuffer tabBuffer = new StringBuffer(tabSize);
	      for (int i = 0; i < tabSize; i++) tabBuffer.append(' ');
	      tabs = tabBuffer.toString();

	      /* Create printer GC, and create and set the printer font & foreground color. */
	      gc = new GC(printer);
	      
	     
	      
	      FontData fontData = font.getFontData()[0];
	      printerFont = new Font(printer, fontData.getName(), fontData.getHeight(), fontData.getStyle());
	      gc.setFont(printerFont);
	      tabWidth = gc.stringExtent(tabs).x;
	      lineHeight = gc.getFontMetrics().getHeight();
	      
	      RGB rgb = foregroundColor.getRGB();
	      printerForegroundColor = new Color(printer, rgb);
	      gc.setForeground(printerForegroundColor);
	    
	      rgb = backgroundColor.getRGB();
	      printerBackgroundColor = new Color(printer, rgb);
	      gc.setBackground(printerBackgroundColor);
	    
	      /* Print text to current gc using word wrap */
	      printText();
	      printer.endJob();

	      /* Cleanup graphics resources used in printing */
	      printerFont.dispose();
	      printerForegroundColor.dispose();
	      printerBackgroundColor.dispose();
	      gc.dispose();
	    }
	  }
	
	void printText() {
	    printer.startPage();
	    wordBuffer = new StringBuffer();
	    x = leftMargin;
	    y = topMargin;
	    index = 0;
	    end = textToPrint.length();
	    while (index < end) {
	      char c = textToPrint.charAt(index);
 	      index++;
	      if (c != 0) {
	        if (c == 0x0a || c == 0x0d) {
	          if (c == 0x0d && index < end && textToPrint.charAt(index) == 0x0a) {
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
