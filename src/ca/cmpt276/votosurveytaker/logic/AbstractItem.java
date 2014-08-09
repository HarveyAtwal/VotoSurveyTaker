package ca.cmpt276.votosurveytaker.logic;

import android.view.LayoutInflater;
import android.view.View;
/**
 * 
 * item class that stores the title
 *
 */
public abstract class AbstractItem {

    protected String title;
    
	public abstract View fillView(View convertView, LayoutInflater mInflater);

    public String getTitle(){
    	return title;
    }
    
    
}
