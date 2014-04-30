package com.hiof.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hiof.objects.User;
import com.hiof.quizphun.R;

public class CustomUserAdapter extends ArrayAdapter<User> {
	private final Context context;
	private final List<User> userObjects;

	public CustomUserAdapter(Context context, List<User> userObjects) {
		super(context, R.layout.user_listview_row, userObjects);
		this.context = context;
		this.userObjects = userObjects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.user_listview_row, parent, false);
		
		TextView twUserNameId = (TextView) rowView.findViewById(R.id.textview_username_id);
		TextView twUserName = (TextView) rowView.findViewById(R.id.textview_username);
		
		twUserNameId.setText(userObjects.get(position).getId() + ". ");
		twUserName.setText(userObjects.get(position).getUserName());
		
		return rowView;
	}
}