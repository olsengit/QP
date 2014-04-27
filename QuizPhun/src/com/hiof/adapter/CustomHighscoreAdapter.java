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
		
		TextView twplayerName = (TextView) rowView.findViewById(R.id.textview_highscore_name);
		TextView twDate = (TextView) rowView.findViewById(R.id.textview_highscore_date);
		TextView twLocation = (TextView) rowView.findViewById(R.id.textview_highscore_location);
		TextView twPoints = (TextView) rowView.findViewById(R.id.textview_highscore_points);
		TextView twPos = (TextView) rowView.findViewById(R.id.textview_highscore_position);
		
		String date = highscoreObjects.get(position).getDate().substring(0,10);
		//System.out.println("Date " + date);
		twPos.setText(highscoreObjects.get(position).getPosition() + ". ");
		twplayerName.setText(highscoreObjects.get(position).getPlayername());
		twDate.setText(date + " ");
		twLocation.setText(highscoreObjects.get(position).getLocation());
		twPoints.setText(highscoreObjects.get(position).getPoints()+ "points");

		return rowView;
	}
}