//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.util;


public class LittleEndian {

  private LittleEndian() {
  }

  public static int readInt(byte[] arr, int offset) {
    int b1, b2, b3, b4;
    b1 = arr[offset + 0] & 0xff;
    b2 = arr[offset + 1] & 0xff;
    b3 = arr[offset + 2] & 0xff;
    b4 = arr[offset + 3] & 0xff;
    return (b1 | (b2 << 8) +
        (b3 << 16) + (b4 << 24));
  }

  // decode the integer value into bytes in the byte array
  public static void writeInt(byte[] barray, int offset, int value) {
    barray[offset + 3] = (byte) ((value >> 24) & 0xff);
    barray[offset + 2] = (byte) ((value >> 16) & 0xff);
    barray[offset + 1] = (byte) ((value >> 8) & 0xff);
    barray[offset + 0] = (byte) (value & 0xff);
  }

  public static void writeFloat(byte[] barray, int offset, float value) {
    writeInt(barray, offset, Float.floatToIntBits(value));
  }

  public static float readFloat(byte[] arr, int offset) {
    return Float.intBitsToFloat(readInt(arr, offset));
  }
  
}
