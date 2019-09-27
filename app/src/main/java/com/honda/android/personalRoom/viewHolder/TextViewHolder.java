package com.honda.android.personalRoom.viewHolder;

import android.content.Context;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.honda.android.R;
import com.honda.android.personalRoom.NoteFeedImageView;
import com.honda.android.personalRoom.adapter.NoteCommentFollowUpListAdapter;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.squareup.picasso.Target;

public class TextViewHolder extends RecyclerView.ViewHolder {
    public TextView mExpandButton;
    public ExpandableRelativeLayout mExpandLayout;
    public TextView mOverlayText;
    public TextView name, timestamp, statusMsg, mTotalLoves, mTotalComments, mHiddenComment, mLabelLoves;
    public NoteFeedImageView feedImageView;
    public LinearLayout mLoves, mComments, mLinearHiddenComment, mLoading, mBtNix, mBtLoves, dotA, dotB;
    public Target profilePic;
    public ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;

    public TextViewHolder(View view) {
        super(view);
        this.name = (TextView) view.findViewById(R.id.name);
        this.timestamp = (TextView) view.findViewById(R.id.timestamp);
        this.statusMsg = (TextView) view.findViewById(R.id.txtStatusMsg);
        this.profilePic = (Target) view.findViewById(R.id.profilePic);
        this.mTotalLoves = (TextView) view.findViewById(R.id.totalLoves);
        this.mTotalComments = (TextView) view.findViewById(R.id.totalComments);
        this.mHiddenComment = (TextView) view.findViewById(R.id.hiddenComment);
        this.feedImageView = (NoteFeedImageView) view.findViewById(R.id.feedImage1);
        this.mLoves = (LinearLayout) view.findViewById(R.id.btLoves);
        this.mComments = (LinearLayout) view.findViewById(R.id.btComment);
        this.mLinearHiddenComment = (LinearLayout) view.findViewById(R.id.LinearHiddenComment);
        this.mLoading = (LinearLayout) view.findViewById(R.id.LinearLoading);
        this.mLabelLoves = (TextView) view.findViewById(R.id.labelLoves);
        this.mBtNix = (LinearLayout) view.findViewById(R.id.btNix);
        this.mBtLoves = (LinearLayout) view.findViewById(R.id.btLoves);
        this.dotA = (LinearLayout) view.findViewById(R.id.dotA);
        this.dotB = (LinearLayout) view.findViewById(R.id.dotB);

        mExpandButton = (TextView) view.findViewById(R.id.expandButton);

        mExpandLayout = (ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout);
        mOverlayText = (TextView) view.findViewById(R.id.overlayText);
        mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mExpandLayout.move(mOverlayText.getHeight(), 0, null);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mOverlayText.getViewTreeObserver().removeGlobalOnLayoutListener(mGlobalLayoutListener);
                } else {
                    mOverlayText.getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
                }
            }
        };
    }

    public void bind(Context context, final int position,
                     @Nullable final NoteCommentFollowUpListAdapter.OnButtonClick clickListener) {

        if (clickListener != null) {
            mComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onButtonClick(position);
                }
            });
        }

        if (clickListener != null) {
            mHiddenComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onButtonClick(position);
                }
            });
        }
    }
}

