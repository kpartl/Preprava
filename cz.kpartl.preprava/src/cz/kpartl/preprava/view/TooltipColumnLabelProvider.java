package cz.kpartl.preprava.view;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Point;

public class TooltipColumnLabelProvider extends ColumnLabelProvider {
		 	 
	String tooltip;
	
	public TooltipColumnLabelProvider(){
		new TooltipColumnLabelProvider("");
	}
	
	public TooltipColumnLabelProvider(String tooltip){
		super();
		this.tooltip = tooltip;
	}
	
	 @Override
	  public String getToolTipText(Object element) {
	    return tooltip.concat(": ").concat(getText(element));
	  }

	  @Override
	  public Point getToolTipShift(Object object) {
	    return new Point(5, 5);
	  }

	  @Override
	  public int getToolTipDisplayDelayTime(Object object) {
	    return 100; //msec
	  }

	  @Override
	  public int getToolTipTimeDisplayed(Object object) {
	    return 5000; //msec
	  }
	
}
