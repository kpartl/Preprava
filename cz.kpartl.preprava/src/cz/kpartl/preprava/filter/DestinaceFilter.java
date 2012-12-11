package cz.kpartl.preprava.filter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import cz.kpartl.preprava.model.Destinace;

public class DestinaceFilter extends ViewerFilter {
	
	private String searchString;

	  public void setSearchText(String s) {
	    // Search must be a substring of the existing value
	    this.searchString = ".*" + s + ".*";
	  }

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {		  
			    if (searchString == null || searchString.length() == 0) {
			      return true;
			    }
			    
			    Destinace destinace = (Destinace)element;
			    if(destinace.getNazev().matches(searchString)) 	return true;
			    if(destinace.getMesto() != null && destinace.getMesto().matches(searchString)) 	return true;
			    if(destinace.getPSC() != null && String.valueOf(destinace.getPSC()).matches(searchString)) 	return true;
			    
			    return false;
			    
	}

}
