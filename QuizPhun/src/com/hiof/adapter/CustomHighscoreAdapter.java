package com.hiof.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hiof.objects.Category;
import com.hiof.objects.Highscore;
import com.hiof.quizphun.R;

public class CustomHighscoreAdapter extends ArrayAdapter<Highscore> {
	private final Context context;
	private final List<Highscore> highscoreObjects;

	public CustomHighscoreAdapter(Context context, List<Highscore> highscoreObjects) {
		super(context, R.layout.highscore_listview_row, highscoreObjects);
		this.context = context;
		this.highscoreObjects = highscoreObjects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.highscore_listview_row, parent, false);
		
		TextView tw = (TextView) rowView.findViewById(R.id.test);
		//System.out.println("obj" + highscoreObjects.get(0).getPlayername());
		tw.setText(highscoreObjects.get(position).getPlayername());

		return rowView;
	}
}