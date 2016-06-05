package me.seasonyuu.miuiviewpager;

import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.HashMap;

/**
 * Created by seasonyuu on 16-6-2.
 */

public interface MIUIPagerAttachable {

	/**
	 * Integer means the page position,
	 * ViewGroup means the ViewGroup which need to perform animation.
	 *
	 * @return A map to keep relation between page and the view group to animate.
	 */
	SparseArray<ViewGroup> attachViewGroup();
}
