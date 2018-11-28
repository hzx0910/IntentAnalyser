package com.jersey.intentanalyser;

import java.lang.reflect.Field;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	TextView tv;
	Button bto;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView) findViewById(R.id.textview);
		bto = (Button) findViewById(R.id.bto);
		intent = getIntent();
		bto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				intent.setComponent(null);
				intent.setPackage(null);
				try {
					startActivity(Intent.createChooser(intent, ""));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		handleIntent();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.handle, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_info:
		case R.id.action_blog:
			Uri uri = Uri.parse("http://blog.csdn.net/jerseyho");
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			try {
				it.setClassName("com.android.browser",
						"com.android.browser.BrowserActivity");
				startActivity(it);
			} catch (Exception e) {
				it.setComponent(null);
				it.setPackage(null);
				startActivity(it);
			}
			break;
		case R.id.action_share:
			if (tv.getText().length() > 0) {
				Intent shareIntent = new Intent(Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent
						.putExtra(Intent.EXTRA_TEXT, tv.getText().toString());
				startActivity(Intent.createChooser(shareIntent, ""));
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		this.intent = intent;
		handleIntent();
	}

	private String getBolderTitleHtml(String str) {

		return "<br/><b>" + str + "</b>";
	}

	private void handleIntent() {
		StringBuilder sb = new StringBuilder("<b>Intent:</b>");
		try {
			if (intent.getAction() != null)
				sb.append(getBolderTitleHtml("Action:") + intent.getAction());
			if (intent.getDataString() != null)
				sb.append(getBolderTitleHtml("DataString:")
						+ intent.getDataString());
			// if(intent.getPackage()!=null)sb.append("\n" + "Package:"
			// +intent.getPackage());
			if (intent.getScheme() != null)
				sb.append(getBolderTitleHtml("Scheme:") + intent.getScheme());
			if (intent.getType() != null)
				sb.append(getBolderTitleHtml("Type:") + intent.getType());

			// sb.append("\n" + "FLAGS:"+
			// toFullBinaryString(intent.getFlags()));
			Class<?> intentClass = Class.forName("android.content.Intent");
			Field[] fileds = intentClass.getDeclaredFields();
			for (Field filed : fileds) {
				if (filed.getName().startsWith("FLAG_")
						&& (filed.getInt(intentClass) & intent.getFlags()) != 0
						&& (!filed.getName().startsWith("FLAG_RECEIVER_"))) {
					sb.append(getBolderTitleHtml("FLAG:") + filed.getName());
				}
			}
			if (intent.getCategories() != null)
				sb.append(getBolderTitleHtml("Categories:")
						+ intent.getCategories());
			// if(intent.getComponent()!=null)sb.append("\n" + "Component:"+
			// intent.getComponent());
			if (intent.getData() != null)
				sb.append(getBolderTitleHtml("Data:") + intent.getData());
			if (intent.getClipData() != null)
				sb.append(getBolderTitleHtml("ClipData:")
						+ intent.getClipData());
			if (intent.getSourceBounds() != null)
				sb.append(getBolderTitleHtml("SourceBounds:")
						+ intent.getSourceBounds());

			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Set<String> set = bundle.keySet();
				if (set != null) {
					for (String ss : set) {
						sb.append(getBolderTitleHtml(ss + ":") + "\n"
								+ bundle.get(ss));
					}
				}
			}
			if (intent.getAction().equals("android.intent.action.MAIN")
					&& intent.getCategories().toString()
							.contains("android.intent.category.LAUNCHER")) {
				bto.setVisibility(View.GONE);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		tv.setText(Html.fromHtml(sb.toString()));
	}

}
