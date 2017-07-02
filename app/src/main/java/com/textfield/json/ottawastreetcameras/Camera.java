package com.textfield.json.ottawastreetcameras;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jason on 25/04/2016.
 */
public class Camera implements Parcelable {
    private String name, nameFr, owner;
    private double lat, lng;
    private int num, id;

    public Camera(JSONObject vals) {
        try {
            name = vals.getString("description");
            nameFr = vals.getString("descriptionFr");
            owner = vals.getString("type");
            id = vals.getInt("id");
            num = vals.getInt("number");
            if (owner.equals("MTO")) {
                num += 2000;
            }
            lat = vals.getDouble("latitude");
            lng = vals.getDouble("longitude");
        } catch (JSONException e) {
            name = owner = nameFr = "";
            lat = lng = num = id = 0;
        }
    }

    protected Camera(Parcel in) {
        name = in.readString();
        nameFr = in.readString();
        owner = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        id = in.readInt();
        num = in.readInt();
    }

    public static final Creator<Camera> CREATOR = new Creator<Camera>() {
        @Override
        public Camera createFromParcel(Parcel in) {
            return new Camera(in);
        }

        @Override
        public Camera[] newArray(int size) {
            return new Camera[size];
        }
    };

    public Camera(Cursor cursor) {
        name = cursor.getString(cursor.getColumnIndex("name"));
        nameFr = cursor.getString(cursor.getColumnIndex("nameFr"));
        owner = cursor.getString(cursor.getColumnIndex("owner"));
        lat = cursor.getDouble(cursor.getColumnIndex("latitude"));
        lng = cursor.getDouble(cursor.getColumnIndex("longitude"));
        id = cursor.getInt(cursor.getColumnIndex("id"));
        num = cursor.getInt(cursor.getColumnIndex("num"));
    }

    public String getOwner() {
        return owner;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public int getId() {
        return id;
    }

    public int getNum() {
        return num;
    }

    public String getName() {
        return name;
    }

    public String getNameFr() {
        return nameFr;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(nameFr);
        parcel.writeString(owner);
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
        parcel.writeInt(id);
        parcel.writeInt(num);
    }

    @Override
    public String toString() {
        return String.format("camera: {\n\tname: %s\n\tnum: %d\n\tid: %d\n\towner: %s\n}", name, num, id, owner);
    }
}
