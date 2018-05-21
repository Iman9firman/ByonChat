package lb.library;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;


public abstract class BasePinnedHeaderListViewAdapter extends BaseAdapter implements SectionIndexer, OnScrollListener,
    PinnedHeaderListView.PinnedHeaderAdapter
  {
	private SectionIndexer _sectionIndexer;
	private boolean mHeaderViewVisible = true;
	public void setSectionIndexer(final SectionIndexer sectionIndexer) {
		_sectionIndexer = sectionIndexer;
	}

	/** remember to call bindSectionHeader(v,position); before calling return */
	@Override
	public abstract View getView(final int position, final View convertView, final ViewGroup parent);

	public abstract CharSequence getSectionTitle(int sectionIndex);

	protected void bindSectionHeader(final View AKA,final TextView headerView, final View dividerView,final View dividerContact, final int position) {
			final int sectionIndex = getSectionForPosition(position);
			if (getPositionForSection(getSectionForPosition(position+1)) == position+1) {
				dividerContact.setVisibility(View.GONE);
				AKA.setVisibility(View.GONE);
			}else{
				AKA.setVisibility(View.VISIBLE);
				dividerContact.setVisibility(View.VISIBLE);
			}


			if (getPositionForSection(sectionIndex) == position) {
				final CharSequence title = getSectionTitle(sectionIndex);
				headerView.setText(title);

				if(position == 0 && title.toString().startsWith(" ")) {
					headerView.setText("Me");
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
						headerView.setAllCaps(false);
					}
				}
				headerView.setVisibility(View.VISIBLE);
				if (dividerView != null)
					dividerView.setVisibility(View.VISIBLE);
			} else {
				if (getPositionForSection(sectionIndex+1) == position+1) {
					dividerContact.setVisibility(View.GONE);
				}

				headerView.setVisibility(View.GONE);
				if (dividerView != null)
					dividerView.setVisibility(View.GONE);
			}
			// move the divider for the last item in a section
		/*if (dividerView != null)
			if (getPositionForSection(sectionIndex + 1) - 1 == position)
				dividerView.setVisibility(View.GONE);
			else
				dividerView.setVisibility(View.GONE);
		if (!mHeaderViewVisible)
			headerView.setVisibility(View.VISIBLE);*/

	}

	@Override
	public int getPinnedHeaderState(final int position) {
		if (_sectionIndexer == null || getCount() == 0 || !mHeaderViewVisible)
			return PINNED_HEADER_GONE;
		if (position < 0)
			return PINNED_HEADER_GONE;
		// The header should get pushed up if the top item shown
		// is the last item in a section for a particular letter.
		final int section = getSectionForPosition(position);
		final int nextSectionPosition = getPositionForSection(section + 1);
		if (nextSectionPosition != -1 && position == nextSectionPosition - 1)
			return PINNED_HEADER_PUSHED_UP;
		return PINNED_HEADER_VISIBLE;
	}

	public void setHeaderViewVisible(final boolean isHeaderViewVisible) {
		mHeaderViewVisible = isHeaderViewVisible;
	}

	public boolean isHeaderViewVisible() {
		return this.mHeaderViewVisible;
	}

	@Override
	public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount,
			final int totalItemCount) {
		((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);
	}

	@Override
	public void onScrollStateChanged(final AbsListView arg0, final int arg1) {
	}

	@Override
	public int getPositionForSection(final int sectionIndex) {
		if (_sectionIndexer == null)
			return -1;
		return _sectionIndexer.getPositionForSection(sectionIndex);
	}

	@Override
	public int getSectionForPosition(final int position) {
		if (_sectionIndexer == null)
			return -1;
		return _sectionIndexer.getSectionForPosition(position);
	}

	@Override
	public Object[] getSections() {
		if (_sectionIndexer == null)
			return new String[] { " " };
		return _sectionIndexer.getSections();
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}
}
