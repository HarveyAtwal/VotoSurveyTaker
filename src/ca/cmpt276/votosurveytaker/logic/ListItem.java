package ca.cmpt276.votosurveytaker.logic;

import ca.cmpt276.votosurveytaker.R;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 
 * Inflates layout for drawer items
 *
 */
public class ListItem extends AbstractItem {

    private int id;
 
    public ListItem (String title, int id) {
        super.title = title;
        this.id = id;
        
    }
 
    @Override
    public View fillView(View view, LayoutInflater inflater) {
        if (view == null) {
            view = inflater.inflate(R.layout.drawer_list_item, null);
        }
 
        TextView textView = (TextView) view.findViewById(R.id.drawerListItemTitle);
        if(textView != null) {
            textView.setText(this.title);
        }
        
        ImageView imgView = (ImageView) view.findViewById(R.id.drawerListItemImg);
        if(imgView != null)
        	
        	imgView.setBackgroundResource(id);
        
        return view;
    }
}
