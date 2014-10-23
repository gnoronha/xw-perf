package com.collabora.xwperf.social.java;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends Activity {

	String[] names = { "abba", "bebba", "cucca", "toffo" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ListView list = (ListView)findViewById(R.id.list);

		ArrayList<String> items = new ArrayList<String>();
		for (int i = 0; i < 100; i++) {
			items.add(String.format("%s%d", names[i % names.length], i));
		}

		SpecialAdapter adapter = new SpecialAdapter(this, items);
		list.setAdapter(adapter);
	}

	static class ViewHolder {
		TextView avatar;
		TextView text;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class SpecialAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private ArrayList<String> data;

		public SpecialAdapter(Context context, ArrayList<String> items) {
			mInflater = LayoutInflater.from(context);
			this.data = items;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			String name;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.row, parent, false);

				holder = new ViewHolder();
				holder.text = (TextView)convertView.findViewById(R.id.headline);
				holder.avatar = (TextView)convertView.findViewById(R.id.avatar);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}

			name = data.get(position);
			holder.text.setText(name);
			holder.avatar.setText(name.substring(0, 1).toUpperCase(Locale.getDefault()));

			return convertView;
		}
	}

}
