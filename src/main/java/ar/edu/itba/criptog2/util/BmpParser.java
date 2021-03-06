package ar.edu.itba.criptog2.util;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * Simple BMP parser. Can read and extract data and metadata from BMP file, and writeImage the file back.
 */
public class BmpParser {

    private final File file;
    private byte[] fileData;
    private String id;
    private int fileSize;
    byte[] reservedBytes;
    int pictureOffset;
    private int infoHeaderLength;
    private int width, height;
    private int numPlanes;
    private int bitsPerPixel;
    private int compressionType;
    private int pictureSize;
    private int horizontalResolution;
    private int verticalResolution;
    private int numUsedColors;
    private int numImportantColors;
    private byte[] pictureData;
    private byte[] extraHeaderBytes;

    public BmpParser(String bitmapImagePath) throws IOException {
        if(bitmapImagePath == null || bitmapImagePath.isEmpty()) {
            throw new IllegalArgumentException("Invalid bitmap image path provided");
        }
        file = new File(bitmapImagePath);
        if (!file.exists()) {
            throw new IllegalArgumentException(bitmapImagePath + " does not exist");
        }
        if (file.isDirectory()) {
            throw new IllegalArgumentException(bitmapImagePath + " is a directory, not a file");
        }
        if (!file.canRead()) {
            throw new IllegalArgumentException(bitmapImagePath + " is not readable");
        }
        parse();
    }

    /**
     * Parses the BMP this parser was initialized with, reading all header data and image data.
     *
     * @throws IOException If an I/O error occurs reading any of the file.
     * @see <a href="https://stackoverflow.com/a/10497780/2333689">Source for converting File to byte[]</a>
     */
    private void parse() throws IOException {
        fileData = Files.readAllBytes(file.toPath());
//        ByteArrayInputStream stream = new ByteArrayInputStream(fileData);
//        ArrayList<Byte> buffer = new ArrayList<>();
        id = new String(Arrays.copyOfRange(fileData, 0, 2));
        fileSize = bytesToInt(Arrays.copyOfRange(fileData, 2, 6));
        reservedBytes = Arrays.copyOfRange(fileData, 6, 10);
        pictureOffset = bytesToInt(Arrays.copyOfRange(fileData, 10, 14));
        infoHeaderLength = bytesToInt(Arrays.copyOfRange(fileData, 14, 18));
        width = bytesToInt(Arrays.copyOfRange(fileData, 18, 22));
        height = bytesToInt(Arrays.copyOfRange(fileData, 22, 26));
        numPlanes = bytesToInt(Arrays.copyOfRange(fileData, 26, 28));

        bitsPerPixel = bytesToInt(Arrays.copyOfRange(fileData, 28, 30));
        if(bitsPerPixel != 8) {
            throw new IllegalArgumentException("Provided BMP does not use 8 bits per pixel. Only 8 bits per pixel is supported.");
        }

        compressionType = bytesToInt(Arrays.copyOfRange(fileData, 30, 34));

        pictureSize = bytesToInt(Arrays.copyOfRange(fileData, 34, 38));
        //Picture size can be 0 if compression is 0. In this case, picture size = width * height * (bits per pixel / 8)
        if(pictureSize == 0) {
            if(compressionType != 0) {
                throw new IllegalStateException("Provided BMP file doesn't specify picture length and is not uncompressed");
            } else {
                pictureSize = width * height * (bitsPerPixel/8);
            }
        }
        // We don't support BMPs that have data after the picture data ends
        if(pictureOffset + pictureSize < fileSize) {
            throw new IllegalArgumentException("Provided BMP has trailing data after picture data, this is not supported");
        }

        horizontalResolution = bytesToInt(Arrays.copyOfRange(fileData, 38, 42));
        verticalResolution = bytesToInt(Arrays.copyOfRange(fileData, 42, 46));
        numUsedColors = bytesToInt(Arrays.copyOfRange(fileData, 46, 50));
        numImportantColors = bytesToInt(Arrays.copyOfRange(fileData, 50, 54));
        extraHeaderBytes = Arrays.copyOfRange(fileData, 54, pictureOffset);
        pictureData = Arrays.copyOfRange(fileData, pictureOffset, pictureOffset + pictureSize);
    }

    public String getId() {
        return id;
    }

    public int getFileSize() {
        return fileSize;
    }

    public byte[] getReservedBytes() {
        return reservedBytes;
    }

    public int getPictureOffset() {
        return pictureOffset;
    }

    public int getInfoHeaderLength() {
        return infoHeaderLength;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getNumPlanes() {
        return numPlanes;
    }

    public int getBitsPerPixel() {
        return bitsPerPixel;
    }

    public int getCompressionType() {
        return compressionType;
    }

    public int getPictureSize() {
        return pictureSize;
    }

    /*
     * We will use the HORIZONTAL and VERTICAL RESOLUTION fields to our convenience, even though they are not reserved.
     * We will store the secret image's dimensions in these fields.
     */
    public int getSecretWidth() {
        return horizontalResolution;
    }

    public int getSecretHeight() {
        return verticalResolution;
    }

    public byte[] getPictureData() {
        return pictureData;
    }

    public int getNumUsedColors() {
        return numUsedColors;
    }

    public int getNumImportantColors() {
        return numImportantColors;
    }

    public byte[] getHeader() {
        return Arrays.copyOfRange(fileData, 0, pictureOffset);
    }

    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    @Override
    public String toString() {
        return "BmpParser, parsed " + width + "x" + height + " bitmap image from " + file.getPath();
    }

    /**
     * Convert a byte array to int.
     *
     * @param bytes The bytes to convert to int.  {@code bytes.length} must be between 1 and 4.
     * @return The converted int.
     * @see <a href="https://stackoverflow.com/a/2383729/2333689">Source</a>
     */
    private int bytesToInt(byte[] bytes) {
        if (bytes.length > 4) {
            throw new IllegalArgumentException("Can only convert up to 4 bytes to int");
        } else if (bytes.length < 1) {
            throw new IllegalArgumentException("Need at least 1 byte to convert");
        }
        byte[] parsedBytes = new byte[4];
        for (int i = 0; i < bytes.length; i++) {
            parsedBytes[i] = bytes[i];
        }
        ByteBuffer bb = ByteBuffer.wrap(parsedBytes);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getInt();
    }

    public int getShadowNumber(){
        return bytesToInt(Arrays.copyOfRange(reservedBytes,2,4));
    }

    public int getSeed(){
        return bytesToInt(Arrays.copyOfRange(reservedBytes,0,2));
    }
}
