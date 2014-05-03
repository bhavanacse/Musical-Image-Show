package org.bhavmayyin.musicalimageshow;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class InteractiveArrayAdapter extends ArrayAdapter<SlideShow> {

	  private final List<SlideShow> list;
	  private final Activity context;
	  int selectedIndex = -1;
	  public InteractiveArrayAdapter(Activity context, List<SlideShow> list) {
	    super(context, R.layout.grouprow, list);
	    this.context = context;
	    this.list = list;
	  }

	  static class ViewHolder {
		protected CheckBox checkbox;
	    protected TextView text;

	  }

	  @SuppressLint("NewApi")
	@Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    View view = null;
	    if (convertView == null) {
	      LayoutInflater inflator = context.getLayoutInflater();
	      view = inflator.inflate(R.layout.grouprow, null);
	      final ViewHolder viewHolder = new ViewHolder();
	      viewHolder.checkbox = (CheckBox) view.findViewById(R.id.checkbox);
	      viewHolder.checkbox.setTag(list.get(position));
	      viewHolder.text = (TextView) view.findViewById(R.id.shwtitle);
	      view.setTag(viewHolder);
	      
	    } else {
	      view = convertView;
	      ((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
	    }
	    ViewHolder holder = (ViewHolder) view.getTag();
	    holder.checkbox.setChecked(list.get(position).isSelected());
	    
	    if (position %2 == 1) {
	    	view.setBackgroundColor(Color.rgb(204,255,255));
	    }
	    else {
	    	view.setBackgroundColor(Color.rgb(255,255,255));
	    }
	    StringBuffer result = new StringBuffer();// for using arrayList adapter
    	//result.append(list.get(position).getshowName()+System.getProperty("line.separator"));
	    result.append(list.get(position).getshowName());
	    if (!list.get(position).getshowDescription().isEmpty())
	    	result.append(" - " + list.get(position).getshowDescription());
	    holder.text.setText(result.toString());
	    if(list.get(position).getId() == -1){
	    	holder.checkbox.setVisibility(View.GONE);
	    } else {
	    	holder.checkbox.setVisibility(View.VISIBLE);
	    }
	    return view;
	  } 
	} 