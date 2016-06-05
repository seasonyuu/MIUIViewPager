package me.seasonyuu.miuiviewpager.demo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import me.seasonyuu.miuiviewpager.MIUIPagerAttachable;
import me.seasonyuu.miuiviewpager.MIUIViewPager;

/**
 * Created by seasonyuu on 16-6-2.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
	private MIUIViewPager miuiViewPager;

	private TabLayout mTabLayout;

	private View dialogView;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		miuiViewPager = (MIUIViewPager) findViewById(R.id.view_pager);
		mTabLayout = (TabLayout) findViewById(R.id.tab);


		final MiuiPagerAdapter adapter = new MiuiPagerAdapter();
		miuiViewPager.setOffscreenPageLimit(5);
		miuiViewPager.setAdapter(adapter);

		miuiViewPager.setPagerAttach(new MIUIPagerAttachable() {
			@Override
			public SparseArray<ViewGroup> attachViewGroup() {
				ViewGroup[] viewGroups = adapter.getViewGroups();
				SparseArray<ViewGroup> attach = new SparseArray<>();
				for (int i = 0; i < viewGroups.length; i++) {
					attach.put(i, viewGroups[i]);
				}
				return attach;
			}
		});
		mTabLayout.setupWithViewPager(miuiViewPager);
	}

	/**
	 * Just a simple demo adapter
	 */
	// To show a dialog to config ViewPager
	@Override
	public void onClick(View v) {
		dialogView = LayoutInflater.from(this).inflate(R.layout.config_dialog, null);

		((Spinner) dialogView.findViewById(R.id.spinner_type)).setAdapter(
				new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
						android.R.id.text1,
						new String[]{"TYPE_IN", "TYPE_OUT", "TYPE_BOTH"}));
		((Spinner) dialogView.findViewById(R.id.spinner_type)).setSelection(miuiViewPager.getType());

		((Spinner) dialogView.findViewById(R.id.item_offset_type)).setAdapter(
				new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
						android.R.id.text1, getResources().getStringArray(R.array.item_offset_type)));
		((Spinner) dialogView.findViewById(R.id.item_offset_type)).setOnItemSelectedListener(
				new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						if (position == 0) {
							((EditText) dialogView.findViewById(R.id.et_item_offset))
									.setText(miuiViewPager.getItemOffset() + "");
							((EditText) dialogView.findViewById(R.id.et_item_offset)).setInputType(EditorInfo.TYPE_NUMBER_FLAG_SIGNED);
						} else {
							((EditText) dialogView.findViewById(R.id.et_item_offset))
									.setText(miuiViewPager.getItemOffsetFraction() + "");
							((EditText) dialogView.findViewById(R.id.et_item_offset)).setInputType(EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		new AlertDialog.Builder(this)
				.setTitle(R.string.config)
				.setView(dialogView)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int type = ((Spinner) dialogView.findViewById(R.id.spinner_type)).getSelectedItemPosition();

						miuiViewPager.setType(type == 0 ? MIUIViewPager.TYPE_IN :
								type == 1 ? MIUIViewPager.TYPE_OUT : MIUIViewPager.TYPE_BOTH);
						if (((Spinner) dialogView.findViewById(R.id.item_offset_type)).getSelectedItemPosition() == 0) {
							int itemOffset = Integer.parseInt(
									((EditText) dialogView.findViewById(R.id.et_item_offset)).getText().toString());
							miuiViewPager.setItemOffset(itemOffset);
						} else {
							float itemOffsetFraction = Float.parseFloat(
									((EditText) dialogView.findViewById(R.id.et_item_offset)).getText().toString());
							miuiViewPager.setItemOffsetFraction(itemOffsetFraction);
						}
					}
				})
				.show();
	}

	private class MiuiPagerAdapter extends PagerAdapter {
		private String[] titles = new String[]{"ListView", "LinearLayout", "LinearLayout in ScrollView", "Recycler View"};
		private ViewGroup[] viewGroups;

		private String[] itemTexts = new String[30];

		MiuiPagerAdapter() {
			viewGroups = new ViewGroup[titles.length];

			String test = getResources().getString(R.string.test);
			for (int i = 0; i < itemTexts.length; i++)
				itemTexts[i] = test + (i + 1);

			viewGroups[0] = new ListView(MainActivity.this);
			((ListView) viewGroups[0]).setDivider(null);

			((ListView) viewGroups[0]).setAdapter(
					new ArrayAdapter<>(MainActivity.this,
							R.layout.simple_item, R.id.text1, itemTexts));

			viewGroups[1] = (ViewGroup) View.inflate(MainActivity.this, R.layout.test_linear_layout, null);

			viewGroups[2] = (ViewGroup) View.inflate(MainActivity.this, R.layout.test_scroll_view, null);

			viewGroups[3] = (ViewGroup) View.inflate(MainActivity.this, R.layout.test_recycler_view, null);
			RecyclerView rv = (RecyclerView) viewGroups[3].findViewById(R.id.rv);
			rv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
			rv.setAdapter(new SimpleRVAdapter(MainActivity.this));
		}

		public ViewGroup[] getViewGroups() {
			return viewGroups;
		}

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(viewGroups[position], new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			return viewGroups[position];
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(viewGroups[position]);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	}
}
