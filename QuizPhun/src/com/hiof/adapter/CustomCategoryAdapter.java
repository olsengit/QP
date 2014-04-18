package com.hiof.adapter;

import java.text.DecimalFormat;
import java.util.List;
import com.hiof.objects.Category;
import com.hiof.quizphun.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomCategoryAdapter extends ArrayAdapter<Category> {
	private final Context context;
	private final List<Category> categoryObjects;

	public CustomCategoryAdapter(Context context, List<Category> categoryObjects) {
		super(context, R.layout.category_listview_row, categoryObjects);
		this.context = context;
		this.categoryObjects = categoryObjects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.category_listview_row, parent, false);
		
		ImageView picture = (ImageView) rowView.findViewById(R.id.imageview_customcategory_image);
		TextView category = (TextView) rowView.findViewById(R.id.textview_customcategory_name);

		int imageid = categoryObjects.get(position).getCategoryid();
		switch(imageid){
			case 1:
				picture.setImageResource(R.drawable.icon_android);
				break;
			case 2:
				picture.setImageResource(R.drawable.icon_film);
				break;
			case 3: 
				picture.setImageResource(R.drawable.icon_music);
				break;
			case 4:
				picture.setImageResource(R.drawable.icon_game);
				break;
			case 5:
				picture.setImageResource(R.drawable.icon_sport);
				break;
			default:
				picture.setImageResource(R.drawable.ic_launcher);
				break;
		}
		category.setText(categoryObjects.get(position).getCategoryname());
	
		return rowView;
	}
}
