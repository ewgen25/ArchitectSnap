package com.example.archirectureobjects;
import android.os.Parcel;
import android.os.Parcelable;
import org.checkerframework.checker.nullness.qual.NonNull;

public class HistoryBuildTitle implements Parcelable {
    public static final Creator<HistoryBuildTitle> CREATOR = new Creator<HistoryBuildTitle>() {
        @Override
        public HistoryBuildTitle createFromParcel(Parcel in) {
            return new HistoryBuildTitle(in);
        }

        @Override
        public HistoryBuildTitle[] newArray(int size) {
            return new HistoryBuildTitle[size];
        }
    };
    private String title;
    public HistoryBuildTitle(String mBuildTitle) {
        title = mBuildTitle;
    }
    public HistoryBuildTitle() {
    }
    protected HistoryBuildTitle(Parcel in) {
        title = in.readString();
    }
    public String gettitle() {
        return title;
    }
    public void setBuildTitle(String buildTitle) {
        this.title = buildTitle;
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(title);
    }
}
