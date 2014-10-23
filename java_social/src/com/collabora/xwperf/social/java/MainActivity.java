package com.collabora.xwperf.social.java;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

class Tweet {
	public String who;
	public Date when;
	public int avatar_color;
	public String message;
}

class TweetFactory {
	private static String[] names = { "Amelia", "Olivia", "Emily", "Jessica",
			"Ava", "Isla", "Poppy", "Isabella", "Sophie", "Mia", "Oliver",
			"Jack", "Harry", "Jacob", "Charlie", "Thomas", "Oscar", "William",
			"James", "George" };

	private static String[] emoticons = { "^_^", "o_O", "\\o/", "/o\\", ":)",
			":(", ":/", ":D", "<3", "O:-}" };

	private static String[] colors = { "#d01716", "#c2185b", "#7b1fa2",
			"#512da8", "#303f9f", "#455ede", "#0288d1", "#0097a7", "#00796b",
			"#0a7e07", "#689f38", "#afb42b", "#fbc02d", "#ffa000", "#f57c00",
			"#e64a19", "#5d4037", "#616161", "#455a64" };

	private static String letters = "qwrtyzxcvbnmsdfghjkl";

	private Random random = new Random();

	private String randomName() {
		return names[random.nextInt(names.length)];
	}

	private String randomEmoticon() {
		return emoticons[random.nextInt(emoticons.length)];
	}

	private int randomColor() {
		return Color.parseColor(colors[random.nextInt(colors.length)]);
	}

	private String randomWord() {
		int n = random.nextInt(10) + 3;
		String s = new String();

		for (int j = 0; j < n; j++)
			s += letters.charAt(random.nextInt(letters.length()));

		return s;
	}

	public Tweet fakeOne() {
		Tweet t = new Tweet();
		int nWords = random.nextInt(10) + 3;
		String msg = new String();

		for (int i = 0; i < nWords; i++) {
			if (random.nextInt(100) < 10) {
				msg += " @" + randomName();
			} else if (random.nextInt(100) < 5) {
				msg += " " + randomEmoticon();
			} else {
				msg += " " + randomWord();
			}
		}

		nWords = random.nextInt(4);
		for (int i = 0; i < nWords; i++)
			msg += " #" + randomWord();

		t.who = randomName();
		t.avatar_color = randomColor();
		t.when = new Date();
		t.message = msg.substring(1);

		return t;
	}

	public ArrayList<Tweet> fakeList(int n) {
		Calendar cal = new GregorianCalendar();
		ArrayList<Tweet> arr = new ArrayList<Tweet>();

		cal.add(Calendar.SECOND, -42 * n);
		for (; n > 0; n--) {
			Tweet t = fakeOne();
			cal.add(Calendar.SECOND, 42);
			t.when = cal.getTime();
			arr.add(t);
		}

		return arr;
	}
}

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ListView list = (ListView) findViewById(R.id.list);

		TweetFactory fac = new TweetFactory();

		SpecialAdapter adapter = new SpecialAdapter(this, fac.fakeList(100));
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
		private ArrayList<Tweet> data;

		public SpecialAdapter(Context context, ArrayList<Tweet> items) {
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

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.row, parent, false);

				holder = new ViewHolder();
				holder.text = (TextView) convertView
						.findViewById(R.id.headline);
				holder.avatar = (TextView) convertView
						.findViewById(R.id.avatar);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Tweet t = data.get(position);
			holder.text.setText(t.who);
			holder.avatar.setText(t.who.substring(0, 1).toUpperCase(
					Locale.getDefault()));

			return convertView;
		}
	}

}
