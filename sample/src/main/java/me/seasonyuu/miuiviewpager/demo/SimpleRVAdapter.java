package me.seasonyuu.miuiviewpager.demo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by seasonyuu on 16-6-2.
 */

public class SimpleRVAdapter extends RecyclerView.Adapter<SimpleRVAdapter.SimpleHolder> {
	private Context context;
	private String[] itemTexts = new String[30];

	public SimpleRVAdapter(Context context) {
		this.context = context;

		String test = context.getResources().getString(R.string.test);
		for (int i = 0; i < itemTexts.length; i++)
			itemTexts[i] = test + (i + 1);
	}

	@Override
	public SimpleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new SimpleHolder(
				LayoutInflater.from(context).inflate(R.layout.simple_item, parent, false));
	}

	@Override
	public void onBindViewHolder(SimpleHolder holder, int position) {
		((TextView) holder.itemView.findViewById(R.id.text1)).setText(itemTexts[position]);
	}

	@Override
	public int getItemCount() {
		return itemTexts.length;
	}

	class SimpleHolder extends RecyclerView.ViewHolder {

		SimpleHolder(View itemView) {
			super(itemView);
		}

	}
}
