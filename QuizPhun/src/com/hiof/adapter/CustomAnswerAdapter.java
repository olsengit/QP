package com.hiof.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hiof.objects.Answer;
import com.hiof.quizphun.R;

/*
 * Adapter for the answers gridview
 */
public class CustomAnswerAdapter extends BaseAdapter {

	private Context mContext;
	private List<Answer> answers;

	public CustomAnswerAdapter(Context c, List<Answer> answers) {
		mContext = c;
		this.answers = answers;
	}

	@Override
	public int getCount() {
		return 4;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.quiz_gridview_item, parent,
				false);

		TextView nameTxt = (TextView) itemView
				.findViewById(R.id.textview_gridview_customitem);

		nameTxt.setText(answers.get(position).getAnswer());
		return itemView;
	}

}
