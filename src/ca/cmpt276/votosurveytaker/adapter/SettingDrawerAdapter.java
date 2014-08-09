package ca.cmpt276.votosurveytaker.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import ca.cmpt276.votosurveytaker.logic.AbstractItem;
import ca.cmpt276.votosurveytaker.logic.HeaderItem;

/**
 * 
 * Fills in the views with an array of strings for Notification drawer
 *
 */

public class SettingDrawerAdapter extends BaseAdapter  {
    List<AbstractItem> items;
    
    private LayoutInflater mInflater;
 
    public SettingDrawerAdapter (Context context, List<AbstractItem> items) {
        this.items = items;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	AbstractItem item = items.get(position);
        View view = item.fillView(convertView, mInflater);
        return view;
    }

	@Override
	public int getCount() {
        return items.size();
	}

	@Override
	public Object getItem(int i) {
        return items.get(i);
	}

	@Override
	public long getItemId(int position) {
        return position;
	}

	@Override
    public boolean areAllItemsEnabled() 
    {
        return false;
    }
	
	@Override
    public boolean isEnabled(int position) 
    {
        AbstractItem item = (AbstractItem) getItem(position);
        if (item != null)
        {
            if (item.getClass() == HeaderItem.class)
            	return false;
        }
        return true;
    }
}
