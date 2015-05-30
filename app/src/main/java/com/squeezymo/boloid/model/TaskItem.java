package com.squeezymo.boloid.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ru.yandex.yandexmapkit.utils.GeoPoint;

public class TaskItem implements Parcelable {
    @SerializedName("ID") private long id;
    @SerializedName("title") private String title;
    @SerializedName("date") private long date;
    @SerializedName("text") private String text;
    @SerializedName("longText") private String longText;
    @SerializedName("durationLimitText") private String durationLimitText;
    @SerializedName("price") private int price;
    @SerializedName("locationText") private String locationText;
    @SerializedName("location") private GeoPoint location;
    @SerializedName("zoomLevel") private int zoomLevel;
    @SerializedName("prices") private List<Price> prices;
    @SerializedName("translation") private boolean translation;
    @SerializedName("reflink") private String reflink;
    @SerializedName("bingMapImage") private String bingMapImage;

    private TaskItem(
            long id,
            String title,
            long date,
            String text,
            String longText,
            String durationLimitText,
            int price,
            String locationText,
            GeoPoint location,
            int zoomLevel,
            List<Price> prices,
            boolean translation,
            String reflink,
            String bingMapImage
    ) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.text = text;
        this.longText = longText;
        this.durationLimitText = durationLimitText;
        this.price = price;
        this.locationText = locationText;
        this.location = location;
        this.zoomLevel = zoomLevel;
        this.prices = prices;
        this.translation = translation;
        this.reflink = reflink;
        this.bingMapImage = bingMapImage;
    }

    public static class Price implements Parcelable {
        @SerializedName("price") private int price;
        @SerializedName("description") private String description;

        private Price(int price, String description) {
            this.price = price;
            this.description = description;
        }

        public int getPrice() { return price; }
        public String getDescription() { return TextUtils.isEmpty(description) ? "" : description; }

        @Override
        public String toString() {
            return "{" + getPrice() + "|" + getDescription() + "}";
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeInt(getPrice());
            parcel.writeString(getDescription());
        }

        public static final Parcelable.Creator<Price> CREATOR = new Parcelable.Creator<Price>() {
            public Price createFromParcel(Parcel in) {
                return new Price(in.readInt(), in.readString());
            }

            public Price[] newArray(int size) {
                return new Price[size];
            }
        };
    }

    public long getId() { return id; }
    public String getTitle() { return TextUtils.isEmpty(title) ? "" : title; }
    public Date getDate() { return new Date(date); }
    public String getText() { return TextUtils.isEmpty(text) ? "" : text; }
    public String getLongText() { return TextUtils.isEmpty(longText) ? "" : longText; }
    public String getDurationLimitText() { return TextUtils.isEmpty(durationLimitText) ? "" : durationLimitText; }
    public int getPrice() { return price; }
    public String getLocationText() { return TextUtils.isEmpty(locationText) ? "" : locationText; }
    public GeoPoint getGeoPoint() { return location; }
    public int getZoomLevel() { return zoomLevel; }
    public List<Price> getPrices() { return prices == null ? new ArrayList<Price>() : prices; }
    public boolean getTranslation() { return translation; }
    public String getReflink() { return TextUtils.isEmpty(reflink) ? "" : reflink; }
    public String getBingMapImage() { return TextUtils.isEmpty(bingMapImage) ? "" : bingMapImage; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TaskItem))
            return false;

        return getId() == ((TaskItem) obj).getId();
    }

    @Override
    public int hashCode() {
        return (int) getId();
    }

    @Override
    public String toString() {
        return "id: " + getId() + "\n" +
               "title: " + getTitle() + "\n" +
               "date: " + date + "\n" +
               "text: " + getText() + "\n" +
               "longtext: " + getLongText() + "\n" +
               "durationLimitText: " + getDurationLimitText() + "\n" +
               "price: " + getPrice() + "\n" +
               "locationText: " + getLocationText() + "\n" +
               "location: (" + (getGeoPoint() == null ? "NULL" : getGeoPoint().getLat() + ", " + getGeoPoint().getLon()) + ")\n" +
               "zoomLevel: " + getZoomLevel() + "\n" +
               "prices: " + Arrays.toString(getPrices().toArray(new Price[getPrices().size()])) + "\n" +
               "translation: " + getTranslation() + "\n" +
               "reflink: " + getReflink() + "\n" +
               "bingMapImage: " + getBingMapImage();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(getId());
        parcel.writeString(getTitle());
        parcel.writeLong(date);
        parcel.writeString(getText());
        parcel.writeString(getLongText());
        parcel.writeString(getDurationLimitText());
        parcel.writeInt(getPrice());
        parcel.writeString(getLocationText());
        parcel.writeDouble(getGeoPoint().getLat());
        parcel.writeDouble(getGeoPoint().getLon());
        parcel.writeInt(getZoomLevel());
        parcel.writeTypedList(getPrices());
        parcel.writeInt(getTranslation() ? 1 : 0);
        parcel.writeString(getReflink());
        parcel.writeString(getBingMapImage());
    }

    public static final Parcelable.Creator<TaskItem> CREATOR = new Parcelable.Creator<TaskItem>() {
        public TaskItem createFromParcel(Parcel in) {
            long id = in.readLong();
            String title = in.readString();
            long date = in.readLong();
            String text = in.readString();
            String longText = in.readString();
            String durationLimitText = in.readString();
            int price = in.readInt();
            String locationText = in.readString();
            GeoPoint location = new GeoPoint(in.readDouble(), in.readDouble());
            int zoomLevel = in.readInt();

            List<Price> prices = new ArrayList<>();
            in.readTypedList(prices, Price.CREATOR);

            boolean translation = (in.readInt() != 1);
            String reflink = in.readString();
            String bingMapImage = in.readString();

            return new TaskItem(id, title, date, text, longText, durationLimitText, price, locationText, location, zoomLevel, prices, translation, reflink, bingMapImage);
        }

        public TaskItem[] newArray(int size) {
            return new TaskItem[size];
        }
    };
}
