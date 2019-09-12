package com.honda.android.personalRoom.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lukmanpryg on 5/12/2016.
 */
public class CommentModel implements Parcelable {
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_ATT_SINGLE = 2;
    public static final int TYPE_ATT_MULTIPLE = 3;
    public static final int TYPE_ATT_BEFORE_AFTER = 4;
    public static final int TYPE_HEADER = 5;

    private String idRoomTab, id_note, headerColor, id_comment, myuserid, userid, profileName, profile_photo, jumlahLove, jumlahNix, jumlahComment, content_comment, timeStamp, parent_id, userLike, userDislike, name2, comment2;
    private int level;
    private Boolean flag;
    private String photos, photoBefore, photoAfter;
    private int type;
    private String api_url;

    public CommentModel() {
    }

    public CommentModel(String id_note, String id_comment, String myuserid, String userid, String profileName, String profile_photo, String jumlahLove, String jumlahNix, String jumlahComment, String content_comment, String timeStamp, String parent_id, String userLike, String userDislike, String name2, String comment2, int level, String idRoomTab, String headerColor, Boolean flag) {
        super();
        this.id_note = id_note;
        this.id_comment = id_comment;
        this.myuserid = myuserid;
        this.userid = userid;
        this.profileName = profileName;
        this.profile_photo = profile_photo;
        this.jumlahLove = jumlahLove;
        this.jumlahNix = jumlahNix;
        this.jumlahComment = jumlahComment;
        this.content_comment = content_comment;
        this.timeStamp = timeStamp;
        this.parent_id = parent_id;
        this.userLike =
                this.name2 = name2;
        this.comment2 = comment2;
        this.level = level;
        this.idRoomTab = idRoomTab;
        this.headerColor = headerColor;
        this.flag = flag;
    }

    public boolean areContentsTheSame(CommentModel commentModel) {
        return idRoomTab.equals(commentModel.idRoomTab)
                && headerColor.equals(commentModel.headerColor)
                && id_note.equals(commentModel.id_note)
                && id_comment.equals(commentModel.id_comment)
                && myuserid.equals(commentModel.myuserid)
                && userid.equals(commentModel.userid)
                && profileName.equals(commentModel.profileName)
                && photos.equals(commentModel.photos)
                && photoBefore.equals(commentModel.photoBefore)
                && photoAfter.equals(commentModel.photoAfter)
                && profile_photo.equals(commentModel.profile_photo)
                && jumlahLove.equals(commentModel.jumlahLove)
                && jumlahNix.equals(commentModel.jumlahNix)
                && jumlahComment.equals(commentModel.jumlahComment)
                && content_comment.equals(commentModel.content_comment)
                && timeStamp.equals(commentModel.timeStamp)
                && name2.equals(commentModel.name2)
                && comment2.equals(commentModel.comment2)
                && api_url.equals(commentModel.api_url);
    }

    public String getApi_url() {
        return api_url;
    }

    public void setApi_url(String api_url) {
        this.api_url = api_url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public String getPhotoBefore() {
        return photoBefore;
    }

    public String getPhotoAfter() {
        return photoAfter;
    }

    public void setPhotoBefore(String photoBefore) {
        this.photoBefore = photoBefore;
    }

    public void setPhotoAfter(String photoAfter) {
        this.photoAfter = photoAfter;
    }

    public String getHeaderColor() {
        return headerColor;
    }

    public void setHeaderColor(String headerColor) {
        this.headerColor = headerColor;
    }

    public String getIdRoomTab() {
        return idRoomTab;
    }

    public void setIdRoomTab(String idRoomTab) {
        this.idRoomTab = idRoomTab;
    }

    public String getMyuserid() {
        return myuserid;
    }

    public void setMyuserid(String myuserid) {
        this.myuserid = myuserid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getId_note() {
        return id_note;
    }

    public void setId_note(String id_note) {
        this.id_note = id_note;
    }

    public String getId_comment() {
        return id_comment;
    }

    public void setId_comment(String id_comment) {
        this.id_comment = id_comment;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getJumlahLove() {
        return jumlahLove;
    }

    public void setJumlahLove(String jumlahLove) {
        this.jumlahLove = jumlahLove;
    }

    public String getJumlahNix() {
        return jumlahNix;
    }

    public void setJumlahNix(String jumlahNix) {
        this.jumlahNix = jumlahNix;
    }

    public String getJumlahComment() {
        return jumlahComment;
    }

    public void setJumlahComment(String jumlahComment) {
        this.jumlahComment = jumlahComment;
    }

    public String getContent_comment() {
        return content_comment;
    }

    public void setContent_comment(String content_comment) {
        this.content_comment = content_comment;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getUserLike() {
        return userLike;
    }

    public void setUserLike(String userLike) {
        this.userLike = userLike;
    }

    public String getUserDislike() {
        return userDislike;
    }

    public void setUserDislike(String userDislike) {
        this.userDislike = userDislike;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getComment2() {
        return comment2;
    }

    public void setComment2(String comment2) {
        this.comment2 = comment2;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    protected CommentModel(Parcel in) {
        idRoomTab = in.readString();
        id_note = in.readString();
        headerColor = in.readString();
        id_comment = in.readString();
        myuserid = in.readString();
        userid = in.readString();
        profileName = in.readString();
        profile_photo = in.readString();
        jumlahLove = in.readString();
        jumlahNix = in.readString();
        jumlahComment = in.readString();
        content_comment = in.readString();
        timeStamp = in.readString();
        parent_id = in.readString();
        userLike = in.readString();
        userDislike = in.readString();
        name2 = in.readString();
        comment2 = in.readString();
        photos = in.readString();
        photoBefore = in.readString();
        photoAfter = in.readString();
        type = in.readInt();
        api_url = in.readString();
    }

    public static final Creator<CommentModel> CREATOR = new Creator<CommentModel>() {
        @Override
        public CommentModel createFromParcel(Parcel in) {
            return new CommentModel(in);
        }

        @Override
        public CommentModel[] newArray(int size) {
            return new CommentModel[size];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idRoomTab);
        dest.writeString(id_note);
        dest.writeString(headerColor);
        dest.writeString(id_comment);
        dest.writeString(myuserid);
        dest.writeString(userid);
        dest.writeString(profileName);
        dest.writeString(profile_photo);
        dest.writeString(jumlahLove);
        dest.writeString(jumlahNix);
        dest.writeString(jumlahComment);
        dest.writeString(content_comment);
        dest.writeString(timeStamp);
        dest.writeString(parent_id);
        dest.writeString(userLike);
        dest.writeString(userDislike);
        dest.writeString(name2);
        dest.writeString(comment2);
        dest.writeString(photos);
        dest.writeString(photoBefore);
        dest.writeString(photoAfter);
        dest.writeInt(type);
        dest.writeString(api_url);
    }
}
