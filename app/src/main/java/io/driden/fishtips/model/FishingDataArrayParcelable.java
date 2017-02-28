package io.driden.fishtips.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FishingDataArrayParcelable implements Parcelable {

    private FishingData[] dataArray;

    public FishingDataArrayParcelable(){

    }

    protected FishingDataArrayParcelable(Parcel in) {
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

    public static final Creator<FishingDataArrayParcelable> CREATOR = new Creator<FishingDataArrayParcelable>() {
        @Override
        public FishingDataArrayParcelable createFromParcel(Parcel in) {
            return new FishingDataArrayParcelable(in);
        }

        @Override
        public FishingDataArrayParcelable[] newArray(int size) {
            return new FishingDataArrayParcelable[size];
        }
    };

    public FishingData[] getDataArray() {
        return dataArray;
    }

    public void setDataArray(FishingData[] dataArray) {
        this.dataArray = dataArray;
    }
}
