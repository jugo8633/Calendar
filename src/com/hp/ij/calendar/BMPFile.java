//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BMPFile {    // extends Object{

    // --- Private constants
    private final static int BITMAPFILEHEADER_SIZE = 14;
    private final static int BITMAPINFOHEADER_SIZE = 40;
    private int              bfReserved1           = 0;
    private int              bfReserved2           = 0;
    private int              bfSize                = 0;

    // --- Private variable declaration
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
    private int              biSize          = BITMAPINFOHEADER_SIZE;
    private int              biSizeImage     = 0x030000;
    private int              biWidth         = 0;
    private int              biXPelsPerMeter = 0x0;
    private int              biYPelsPerMeter = 0x0;
    private RandomAccessFile fo              = null;

    // --- Bitmap raw data
    private byte bitmap[];

    // --- Default constructor
    public BMPFile() {}

    public boolean openFile(String fname) {
        try {
            fo = new RandomAccessFile(fname, "rw");
            commitBitmapHeader(10, 10);

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void closeFile() {
        try {
            fo.close();
        } catch (IOException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        fo = null;
    }

    public boolean commitBitmapHeader(int parWidth, int parHeight) {
        try {
            fo.seek(0);
        } catch (IOException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();

            return false;
        }

        convertImage(null, parWidth, parHeight);
        writeBitmapFileHeader();
        writeBitmapInfoHeader();

        return true;
    }

    public boolean writeBitmapContent(byte[] imagePix) {
        try {
            fo.write(imagePix);

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void saveBitmapFile(String fname, byte[] imagePix, int parWidth, int parHeight) {
        try {
            fo = new RandomAccessFile(fname, "rw");
            save(imagePix, parWidth, parHeight);
            fo.close();
        } catch (Exception saveEx) {
            saveEx.printStackTrace();
        }
    }

    /*
     * The saveMethod is the main method of the process. This method
     * will call the convertImage method to convert the memory image to
     * a byte array; method writeBitmapFileHeader creates and writes
     * the bitmap file header; writeBitmapInfoHeader creates the
     * information header; and writeBitmap writes the image.
     *
     */
    private void save(byte[] imagePix, int parWidth, int parHeight) {
        try {
            convertImage(imagePix, parWidth, parHeight);
            writeBitmapFileHeader();
            writeBitmapInfoHeader();
            writeBitmap();
        } catch (Exception saveEx) {
            saveEx.printStackTrace();
        }
    }

    /*
     * convertImage converts the memory image to the bitmap format (BRG).
     * It also computes some information for the bitmap info header.
     *
     */
    private boolean convertImage(byte[] imagePix, int parWidth, int parHeight) {
        bitmap      = imagePix;
        biSizeImage = parWidth * parHeight * 3;
        bfSize      = biSizeImage + BITMAPFILEHEADER_SIZE + BITMAPINFOHEADER_SIZE;
        biWidth     = parWidth;
        biHeight    = parHeight;

        return (true);
    }

    private void writeBitmap() {
        try {
            fo.write(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * writeBitmapFileHeader writes the bitmap file header to the file.
     *
     */
    private void writeBitmapFileHeader() {
        try {
            fo.write(bfType);
            fo.write(intToDWord(bfSize));
            fo.write(intToWord(bfReserved1));
            fo.write(intToWord(bfReserved2));
            fo.write(intToDWord(bfOffBits));
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
            fo.write(intToDWord(biSize));
            fo.write(intToDWord(biWidth));
            fo.write(intToDWord(biHeight));
            fo.write(intToWord(biPlanes));
            fo.write(intToWord(biBitCount));
            fo.write(intToDWord(biCompression));
            fo.write(intToDWord(biSizeImage));
            fo.write(intToDWord(biXPelsPerMeter));
            fo.write(intToDWord(biYPelsPerMeter));
            fo.write(intToDWord(biClrUsed));
            fo.write(intToDWord(biClrImportant));
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
