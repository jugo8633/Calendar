//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.photo.data;

//~--- non-JDK imports --------------------------------------------------------

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * @author , Denny @ Foxconn
 *
 */
public class PhotoBundle implements Parcelable {
    public static final Parcelable.Creator<PhotoBundle> CREATOR = new Parcelable.Creator<PhotoBundle>() {
        public PhotoBundle createFromParcel(Parcel in) {
            return new PhotoBundle(in);
        }
        public PhotoBundle[] newArray(int size) {
            return new PhotoBundle[size];
        }
    };

    // public int[] brightRGB = new int[3];
    public int[] brightRGB = { 0, 0, 0 };

    // public float[] cropRect = new float[4];
    public float[] cropRect    = { 0L, 0L, 0L, 0L };
    public int     effectIdx   = 0;
    public int     prtNum      = 0;    // default 0
    public boolean bCrop       = false;
    public int     rotateAngle = 0;
    public boolean bBright     = false;

    public PhotoBundle() {}

    public PhotoBundle(Parcel in) {
        boolean[] bc = { false, false };

        prtNum = in.readInt();
        in.readFloatArray(cropRect);
        in.readBooleanArray(bc);
        bCrop       = bc[0];
        bBright     = bc[1];
        rotateAngle = in.readInt();
        effectIdx   = in.readInt();
        in.readIntArray(brightRGB);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        boolean[] bc = { bCrop, bBright };

        dest.writeInt(prtNum);
        dest.writeFloatArray(cropRect);
        dest.writeBooleanArray(bc);
        dest.writeInt(rotateAngle);
        dest.writeInt(effectIdx);
        dest.writeIntArray(brightRGB);
    }
}
