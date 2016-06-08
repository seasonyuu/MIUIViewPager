package me.seasonyuu.miuiviewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ScrollView;

/**
 * A ViewPager performs item animations while scrolling.
 * Idea from MIUI Rom.
 * <p>
 * Created by seasonyuu on 16-6-2.
 */

public class MIUIViewPager extends ViewPager {
	private MIUIPagerAttachable mPagerAttach;
	private MIUIOnPagerChangeListener mPagerChangeListener;

	private ViewGroup animatedViewGroup = null;
	private int animatedPosition;

	private int itemOffset = -1;
	private float itemOffsetFraction = -1;

	private static final String TAG = MIUIViewPager.class.getSimpleName();

	private int mType;

	public final static int TYPE_IN = 0x0;
	public final static int TYPE_OUT = 0x1;
	public final static int TYPE_BOTH = 0x2;

	private static final int MOVE_TO_LEFT = 0x1;
	private static final int MOVE_TO_RIGHT = 0x2;

	@IntDef({MOVE_TO_LEFT, MOVE_TO_RIGHT})
	@interface MovePosition {

	}

	@IntDef({TYPE_IN, TYPE_OUT, TYPE_BOTH})
	public @interface AnimationType {

	}

	public int getItemOffset() {
		return itemOffset;
	}

	public void setItemOffset(int itemOffset) {
		this.itemOffset = itemOffset;
		if (getMeasuredWidth() != 0) {
			itemOffsetFraction = 1.0f * itemOffset / getMeasuredWidth();
		}
	}

	public MIUIViewPager(Context context) {
		super(context);
		init(null);
	}

	public MIUIViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		if (mPagerChangeListener == null)
			mPagerChangeListener = new MIUIOnPagerChangeListener();
		addOnPageChangeListener(mPagerChangeListener);

		if (attrs != null) {
			TypedArray typedArray = getContext()
					.obtainStyledAttributes(attrs, R.styleable.MIUIViewPager, 0, 0);
			mType = typedArray.getInt(R.styleable.MIUIViewPager_type, TYPE_IN);

			itemOffsetFraction = typedArray.getFraction(R.styleable.MIUIViewPager_item_offsetFraction, 1, 1, -1);

			itemOffset = typedArray.getDimensionPixelOffset(R.styleable.MIUIViewPager_item_offset, -1);

			typedArray.recycle();
		} else {
			mType = TYPE_IN;
			itemOffset = getMeasuredWidth() / 6;
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		if (itemOffset == -1) {
			if (itemOffsetFraction == -1)
				itemOffsetFraction = 0.167f;
			itemOffset = (int) (getMeasuredWidth() * itemOffsetFraction);
		} else if (itemOffsetFraction == -1)
			itemOffsetFraction = 1.0f * itemOffset / getMeasuredWidth();

		if (itemOffsetFraction <= 0 || itemOffsetFraction > 1)
			throw new IllegalArgumentException("Item offset shouldn't be more than parent width or less than 0");
	}

	public void setType(@AnimationType int type) {
		mType = type;
	}

	public int getType() {
		return mType;
	}

	public float getItemOffsetFraction() {
		return itemOffsetFraction;
	}

	public void setItemOffsetFraction(float itemOffsetFraction) {
		this.itemOffsetFraction = itemOffsetFraction;
		if (getMeasuredWidth() != 0) {
			itemOffset = (int) (getMeasuredWidth() * itemOffsetFraction);
		}
	}

	public MIUIPagerAttachable getPagerAttach() {
		return mPagerAttach;
	}

	public void setPagerAttach(MIUIPagerAttachable pagerAttach) {
		mPagerAttach = pagerAttach;
	}

	public void removePagerAttach() {
		mPagerAttach = null;
	}

	private void performAnimate(ViewGroup viewGroup, float positionOffset, @MovePosition int movePosition) {
		if (viewGroup == null)
			return;
		if (viewGroup instanceof GridView) {
			performGridView((GridView) viewGroup, positionOffset, movePosition);
			return;
		}
		int start = getFirstVisiblePosition(viewGroup);
		int end = getLastVisiblePosition(viewGroup);

		if (viewGroup instanceof ScrollView || viewGroup instanceof NestedScrollView)
			viewGroup = (ViewGroup) viewGroup.getChildAt(0);

		for (int i = start; i <= end; i++) {
			View childView = viewGroup.getChildAt(i);

			int left = 0;
			if (movePosition == MOVE_TO_LEFT) {
				left = (int) (itemOffset * (i - start) - positionOffset * (end - start) * itemOffset
						+ (1 - positionOffset) * itemOffset);
				if (left < 0)
					left = 0;
			} else {
				left = (int) (-itemOffset * (i - start) - positionOffset * (end - start) * itemOffset
						- positionOffset * itemOffset)
						+ itemOffset * (end - start);
				if (left > 0)
					left = 0;
			}
			childView.layout(left, childView.getTop(), childView.getMeasuredWidth() + left, childView.getBottom());
		}
	}

	private void performGridView(GridView gridView, float positionOffset, @MovePosition int movePosition) {
		int columns = gridView.getNumColumns();
		if (columns == -1)
			return;

		int start = getFirstVisiblePosition(gridView);
		int end = getLastVisiblePosition(gridView);

		int gridItemOffset = itemOffset / 3;

		int childWidth = gridView.getWidth() / columns;

		for (int i = start; i <= end; i += columns) {
			for (int j = i; j < i + columns && j <= end; j++) {
				View childView = gridView.getChildAt(j);

				int left = (j - i) * childWidth;
				if (movePosition == MOVE_TO_LEFT) {
					left += (int) (gridItemOffset * (i - start) - positionOffset * (end - start) * gridItemOffset
							+ (1 - positionOffset) * gridItemOffset);
					if (left < (j - i) * childWidth)
						left = (j - i) * childWidth;
				} else {
					left += (int) (-gridItemOffset * (i - start) - positionOffset * (end - start) * gridItemOffset
							- positionOffset * gridItemOffset)
							+ gridItemOffset * (end - start);
					if (left > (j - i) * childWidth)
						left = (j - i) * childWidth;
				}
				childView.layout(left, childView.getTop(), childWidth + left, childView.getBottom());
			}
		}

	}

	private int getFirstVisiblePosition(ViewGroup viewGroup) {
		int start = -1;

		if (viewGroup instanceof ScrollView || viewGroup instanceof NestedScrollView) {
			ViewGroup content = (ViewGroup) viewGroup.getChildAt(0);
			while (++start < content.getChildCount()) {
				View childView = content.getChildAt(start);
				if ((childView.getBottom() > viewGroup.getScrollY()
						&& childView.getTop() <= viewGroup.getScrollY())
						|| childView.getTop() > viewGroup.getScrollY())
					break;
			}
		} else
			while (++start < viewGroup.getChildCount()) {
				View childView = viewGroup.getChildAt(start);
				if ((childView.getBottom() > 0 && childView.getTop() <= 0)
						|| childView.getTop() > 0)
					break;
			}
		return start;
	}

	private int getLastVisiblePosition(ViewGroup viewGroup) {
		int start = viewGroup.getChildCount();
		if (viewGroup instanceof ScrollView || viewGroup instanceof NestedScrollView) {
			ViewGroup content = (ViewGroup) viewGroup.getChildAt(0);
			start = content.getChildCount();
			while (--start >= 0) {
				View childView = content.getChildAt(start);
				if (childView.getBottom() <= viewGroup.getScrollY() + viewGroup.getHeight() ||
						(childView.getBottom() > viewGroup.getScrollY() + viewGroup.getHeight()
								&& childView.getTop() < viewGroup.getScrollY() + viewGroup.getHeight()))
					break;
			}
		} else
			while (--start >= 0) {
				View childView = viewGroup.getChildAt(start);
				if (childView.getBottom() <= viewGroup.getHeight() ||
						(childView.getBottom() > viewGroup.getHeight()
								&& childView.getTop() < viewGroup.getHeight()))
					break;
			}
		return start;
	}

	private class MIUIOnPagerChangeListener implements OnPageChangeListener {
		private int lastPosition = 0;
		private int position = 0;
		private float offset = 0;
		private int offsetPixels = 0;


		@Override
		public void onPageScrolled(int position, float offset, int offsetPixels) {
			this.offset = offset;
			this.offsetPixels = offsetPixels;
			this.position = position;

			if (mPagerAttach == null || mPagerAttach.attachViewGroup() == null)
				return;

			if (mType == TYPE_BOTH) {
				ViewGroup preViewGroup = null;
				ViewGroup nextViewGroup = null;
				if (position < getAdapter().getCount() - 1) {
					nextViewGroup = mPagerAttach.attachViewGroup().get(position + 1);
					preViewGroup = mPagerAttach.attachViewGroup().get(position);
				} else {
					nextViewGroup = mPagerAttach.attachViewGroup().get(position);
					preViewGroup = mPagerAttach.attachViewGroup().get(position - 1);
				}

				performAnimate(preViewGroup, offset, MOVE_TO_RIGHT);
				if (!(position == getAdapter().getCount() - 1 && offset == 0))
					performAnimate(nextViewGroup, offset, MOVE_TO_LEFT);
			} else {
				if (getWidth() * (getAdapter().getCount() - 1) == getScrollX() || getScrollX() == 0)
					return;
				SparseArray<ViewGroup> mViewGroups = mPagerAttach.attachViewGroup();
				ViewGroup preViewGroup = null;
				ViewGroup nextViewGroup = null;
				if (mType == TYPE_IN) {
					if (position < getAdapter().getCount() - 1) {
						preViewGroup = mViewGroups.get(position);
						nextViewGroup = mViewGroups.get(position + 1);
						animatedViewGroup = lastPosition < position + offset ? nextViewGroup : preViewGroup;
						animatedPosition = lastPosition < position + offset ? MOVE_TO_LEFT : MOVE_TO_RIGHT;
					} else if (position == getAdapter().getCount() - 1) {
						animatedViewGroup = mViewGroups.get(position - 1);
						animatedPosition = MOVE_TO_RIGHT;
					}
				} else if (mType == TYPE_OUT) {
					if (position < getAdapter().getCount() - 1) {
						preViewGroup = mViewGroups.get(position);
						nextViewGroup = mViewGroups.get(position + 1);
						animatedViewGroup = lastPosition >= position + offset ? nextViewGroup : preViewGroup;
						animatedPosition = lastPosition >= position + offset ? MOVE_TO_LEFT : MOVE_TO_RIGHT;
					} else if (position == getAdapter().getCount() - 1) {
						animatedViewGroup = mViewGroups.get(position);
						animatedPosition = MOVE_TO_LEFT;
					}
				}
				performAnimate(animatedViewGroup, offset, animatedPosition);
			}
		}

		@Override
		public void onPageSelected(int position) {
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (mType == TYPE_BOTH) {
				if (state == SCROLL_STATE_IDLE)
					mPagerAttach.attachViewGroup().get(position).requestLayout();
				return;
			}
			if (state == SCROLL_STATE_DRAGGING) {
				if (animatedViewGroup != null) {
					animatedViewGroup.requestLayout();
					animatedViewGroup = null;
				}
				lastPosition = position;
			} else if (state == SCROLL_STATE_IDLE) {
				if (animatedViewGroup != null) {
					animatedViewGroup.requestLayout();
					animatedViewGroup = null;
				}
			} else if (state == SCROLL_STATE_SETTLING) {

			}

		}
	}

}
