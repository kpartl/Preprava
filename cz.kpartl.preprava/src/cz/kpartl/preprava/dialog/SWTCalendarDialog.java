package cz.kpartl.preprava.dialog;

import org.eclipse.swt.SWT;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import org.eclipse.swt.widgets.Shell;



import cz.kpartl.preprava.calendar.SWTCalendarListener;

import cz.kpartl.preprava.calendar.SWTCalendar;



import java.util.Calendar;

import java.util.Date;



public class SWTCalendarDialog {

    private Shell shell;

    private SWTCalendar swtcal;

    private Display display;



    public SWTCalendarDialog(Display display) {

        this.display = display;

        shell = new Shell(display, SWT.APPLICATION_MODAL | SWT.CLOSE);

        shell.setLayout(new RowLayout());

        swtcal = new SWTCalendar(shell);
              
    }



    public void open() {

        shell.pack();

        shell.open();

        while (!shell.isDisposed()) {

            if (!display.readAndDispatch()) display.sleep();

        }

    }



    public Calendar getCalendar() {

        return swtcal.getCalendar();

    }



    public void setDate(Date date) {

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);

        swtcal.setCalendar(calendar);

    }



    public void addDateChangedListener(SWTCalendarListener listener) {

        swtcal.addSWTCalendarListener(listener);

    }
    
    

}