package io.driden.fishtips.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FishingDataListParcelable implements Parcelable {

    private FishingData[] dataArray;

    public FishingDataListParcelable(){

    }

    protected FishingDataListParcelable(Parcel in) {
        dataArray = in.createTypedArray(FishingData.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(dataArray, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FishingDataListParcelable> CREATOR = new Creator<FishingDataListParcelable>() {
        @Override
        public FishingDataListParcelable createFromParcel(Parcel in) {
            return new FishingDataListParcelable(in);
        }

        @Override
        public FishingDataListParcelable[] newArray(int size) {
            return new FishingDataListParcelable[size];
        }
    };

    public FishingData[] getDataArray() {
        return dataArray;
    }

    public void setDataArray(FishingData[] dataArray) {
        this.dataArray = dataArray;
    }
}
