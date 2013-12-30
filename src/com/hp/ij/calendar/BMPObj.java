//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar;


import org.apache.http.util.ByteArrayBuffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Bitmap.Config;

public class BMPObj {

    // --- Private constants
    private final static int BITMAPFILEHEADER_SIZE = 14;
    private final static int BITMAPINFOHEADER_SIZE = 40;
    private int              bfReserved1           = 0;
    private int              bfReserved2           = 0;
    private int              bfSize                = 0;

    // --- Bitmap file header
    private byte bfType[]       = { (byte) 'B', (byte) 'M' };
    private int  bfOffBits      = BITMAPFILEHEADER_SIZE + BITMAPINFOHEADER_SIZE;
    private int  biBitCount     = 24;
    private int  biClrImportant = 0;
    private int  biClrUsed      = 0;
    private int  biCompression  = 0;
    private int  biHeight       = 0;
    private int  biPlanes       = 1;

    // --- Bitmap info header
    private int biSize          = BITMAPINFOHEADER_SIZE;
    private int biSizeImage     = 0x030000;
    private int biWidth         = 0;
    private int biXPelsPerMeter = 0x0;
    private int biYPelsPerMeter = 0x0;
    private int biByteCount     = biBitCount / 8;

    // --- Bitmap raw data
    private Bitmap          bitmap = null;
    private ByteArrayBuffer bmpBuf = new ByteArrayBuffer(0);
    private ByteArrayBuffer tmpBuf = new ByteArrayBuffer(0);

    // --- Default constructor
    public BMPObj() {}

    private void setSize(int width, int height) {
        biWidth     = width;
        biHeight    = height;
        biSizeImage = biWidth * biHeight * biByteCount;
        bfSize      = biSizeImage + BITMAPFILEHEADER_SIZE + BITMAPINFOHEADER_SIZE;
    }

    public void writeBitmapContent(byte[] pixels, int len) {
        int    index = len - 1;
        byte[] temp  = new byte[len];

        if (temp != null) {

            // Mirror image.
            for (int i = 0; i < len; i += biByteCount) {
                temp[i]     = pixels[index];
                temp[i + 1] = pixels[index - 1];
                temp[i + 2] = pixels[index - 2];
                index       -= biByteCount;
            }

            tmpBuf.append(temp, 0, len);
        }
    }

    public boolean buildBitmap(int parWidth, int parHeight) {
        return buildBitmap(parWidth, parHeight, 0, 0);
    }

    public boolean buildBitmap(int parWidth, int parHeight, int marginLeft, int marginTop) {

        // if (bitmap != null) {
        // bitmap.recycle();
        // bitmap = null;
        // }
        setSize(parWidth, parHeight);

        if (bmpBuf != null) {
            bmpBuf.clear();
            writeHeader();

            if (tmpBuf != null) {
                bmpBuf.append(tmpBuf.buffer(), 0, tmpBuf.length());

                Bitmap bmp = BitmapFactory.decodeByteArray(bmpBuf.buffer(), 0, bmpBuf.length());

                if (bmp != null) {

                    // rotate image.
                    Matrix matrix = new Matrix();

                    matrix.postRotate(180);
                    bmp = Bitmap.createBitmap(bmp, 0, 0, biWidth, biHeight, matrix, true);

                    if (bmp != null) {

                        // append top and left margin.
                        bitmap = Bitmap.createBitmap(biWidth + marginLeft, biHeight + marginTop, Config.RGB_565);

                        if (bitmap != null) {
                            Canvas canvas = new Canvas(bitmap);

                            if (canvas != null) {
                                canvas.drawColor(Color.WHITE);
                                canvas.drawBitmap(bmp, marginTop + 1, marginLeft + 1, null);
                            }
                        }

                        bmp = null;
                    }
                }

                tmpBuf.clear();
                System.gc();

                return true;
            }
        }

        return false;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void release() {
        if (bmpBuf != null) {
            bmpBuf.clear();
            bmpBuf = null;
        }

        if (tmpBuf != null) {
            tmpBuf.clear();
            tmpBuf = null;
        }

        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }

        System.gc();
    }

    private void writeHeader() {
        writeBitmapFileHeader();
        writeBitmapInfoHeader();
    }

    /*
     * writeBitmapFileHeader writes the bitmap file header to the file.
     *
     */
    private void writeBitmapFileHeader() {
        try {
            bmpBuf.append(bfType, 0, 2);
            bmpBuf.append(intToDWord(bfSize), 0, 4);
            bmpBuf.append(intToWord(bfReserved1), 0, 2);
            bmpBuf.append(intToWord(bfReserved2), 0, 2);
            bmpBuf.append(intToDWord(bfOffBits), 0, 4);
        } catch (Exception wbfh) {
            wbfh.printStackTrace();
        }
    }

    /*
     *
     * writeBitmapInfoHeader writes the bitmap information header
     * to the file.
     *
     */
    private void writeBitmapInfoHeader() {
        try {
            bmpBuf.append(intToDWord(biSize), 0, 4);
            bmpBuf.append(intToDWord(biWidth), 0, 4);
            bmpBuf.append(intToDWord(biHeight), 0, 4);
            bmpBuf.append(intToWord(biPlanes), 0, 2);
            bmpBuf.append(intToWord(biBitCount), 0, 2);
            bmpBuf.append(intToDWord(biCompression), 0, 4);
            bmpBuf.append(intToDWord(biSizeImage), 0, 4);
            bmpBuf.append(intToDWord(biXPelsPerMeter), 0, 4);
            bmpBuf.append(intToDWord(biYPelsPerMeter), 0, 4);
            bmpBuf.append(intToDWord(biClrUsed), 0, 4);
            bmpBuf.append(intToDWord(biClrImportant), 0, 4);
        } catch (Exception wbih) {
            wbih.printStackTrace();
        }
    }

    /*
     *
     * intToWord converts an int to a word, where the return
     * value is stored in a 2-byte array.
     *
     */
    private byte[] intToWord(int parValue) {
        byte retValue[] = new byte[2];

        retValue[0] = (byte) (parValue & 0x00FF);
        retValue[1] = (byte) ((parValue >> 8) & 0x00FF);

        return (retValue);
    }

    /*
     *
     * intToDWord converts an int to a double word, where the return
     * value is stored in a 4-byte array.
     *
     */
    private byte[] intToDWord(int parValue) {
        byte retValue[] = new byte[4];

        retValue[0] = (byte) (parValue & 0x00FF);
        retValue[1] = (byte) ((parValue >> 8) & 0x000000FF);
        retValue[2] = (byte) ((parValue >> 16) & 0x000000FF);
        retValue[3] = (byte) ((parValue >> 24) & 0x000000FF);

        return (retValue);
    }
}
