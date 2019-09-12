package com.honda.android.ui.viewholder;

import android.annotation.TargetApi;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honda.android.R;
import com.honda.android.data.model.Video;
import com.honda.android.ui.adapter.OnItemClickListener;
import com.honda.android.ui.adapter.OnLongItemClickListener;
import com.honda.android.ui.view.SoloCircleProgress;

public class ByonchatVideoTubeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnLongClickListener,
        Video.ProgressListener,
        Video.DownloadingListener {

    public TextView vContents;
    public TextView vInfo;
    public SoloCircleProgress vProgressView;
    public TextView vDescription;
    public RelativeLayout vFrameList;
    public ImageView vAvatar;
    public ImageView vMore;
    public ImageView vCheck;
    public ImageView vDownloaded;
    public View vView;

    protected Drawable selectionBackground;
    protected int selectionChecked;
    protected int downloadedBackground;
    protected Drawable downloadingBackground;

    public OnItemClickListener itemClickListener;
    public OnLongItemClickListener longItemClickListener;

    protected Video video;

    public ByonchatVideoTubeViewHolder(View view,
                                       OnItemClickListener itemClickListener,
                                       OnLongItemClickListener longItemClickListener) {
        super(view);
        vFrameList = (RelativeLayout) view.findViewById(R.id.frame_list_video);
        vDescription = (TextView) view.findViewById(R.id.description);
        vContents = (TextView) view.findViewById(R.id.contents);
        vProgressView = (SoloCircleProgress) view.findViewById(R.id.progress);
        vInfo = (TextView) view.findViewById(R.id.info);
        vMore = (ImageView) view.findViewById(R.id.more);
        vAvatar = (ImageView) view.findViewById(R.id.avatar);
        vCheck = (ImageView) view.findViewById(R.id.check);
        vDownloaded = (ImageView) view.findViewById(R.id.downloaded);
        vView = (View) view.findViewById(R.id.view);

        loadConfig();

        this.itemClickListener = itemClickListener;
        this.longItemClickListener = longItemClickListener;

        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
    }

    protected void loadConfig() {
        selectionBackground = new ColorDrawable(ContextCompat.getColor(itemView.getContext(), R.color.byonchat_divider_light));
        selectionBackground.setAlpha(51);
        selectionChecked = ContextCompat.getColor(itemView.getContext(), android.R.color.black);
        downloadedBackground = ContextCompat.getColor(itemView.getContext(), android.R.color.holo_blue_dark);
        downloadingBackground = new ColorDrawable(ContextCompat.getColor(itemView.getContext(), R.color.byonchat_downloading_transparent));
    }

    @TargetApi(16)
    public void onCommentSelected(Video video) {
        this.video = video;

        video.setProgressListener(this);
        video.setDownloadingListener(this);
        showProgressOrNot(video);

        itemView.setBackground(video.isSelected() ? selectionBackground : null);
        if (video.isSelected())
            vCheck.setColorFilter(selectionChecked, PorterDuff.Mode.SRC_ATOP);
        if (video.isDownloaded())
            vCheck.setColorFilter(downloadedBackground, PorterDuff.Mode.SRC_ATOP);

        vDownloaded.setVisibility(video.isDownloaded() ? View.VISIBLE : View.GONE);
        vCheck.setVisibility(video.isSelected() ? View.VISIBLE : View.GONE);
    }

    @TargetApi(16)
    protected void showProgressOrNot(Video video) {
        if (vProgressView != null) {
            vProgressView.setProgress(video.getProgress());
            vProgressView.setVisibility(
                    video.isDownloading()
                            ? View.VISIBLE : View.GONE
            );

            vFrameList.setBackground(video.isDownloading()
                    ? downloadingBackground : null);
        }
    }

    @Override
    public void onClick(View v) {
        int position = getAdapterPosition();
        if (position >= 0) {
            itemClickListener.onItemClick(v, position);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (longItemClickListener != null) {
            int position = getAdapterPosition();
            if (position >= 0) {
                longItemClickListener.onLongItemClick(v, position);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onProgress(Video video, int percentage) {
        if (video.equals(this.video) && vProgressView != null) {
            vProgressView.setProgress(percentage);
        }
    }

    @TargetApi(16)
    @Override
    public void onDownloading(Video video, boolean downloading) {
        if (video.equals(this.video) && vProgressView != null) {
            itemView.setBackground(video.isSelected() ? selectionBackground : null);
            vCheck.setVisibility(video.isSelected() ? View.VISIBLE : View.GONE);
            vProgressView.setVisibility(downloading ? View.VISIBLE : View.GONE);
        }
    }

}
