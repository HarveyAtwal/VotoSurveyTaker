package ca.cmpt276.votosurveytaker.logic;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import ca.cmpt276.votosurveytaker.R;
/**
 * 
 * Inflates layout for drawer headers
 *
 */
public class HeaderItem extends AbstractItem {

    public HeaderItem (String title) {
        super.title = title;
    }
 
    @Override
    public View fillView(View view, LayoutInflater inflater) {
        if (view == null) {
            view = inflater.inflate(R.layout.drawer_header_list_item, null);
        }
 
        TextView textView = (TextView) view.findViewById(R.id.drawerHeaderListItemTitle);
        if(textView != null) {
            textView.setText(this.title);
        }
        view.setClickable(false);
        return view;
    }
}
