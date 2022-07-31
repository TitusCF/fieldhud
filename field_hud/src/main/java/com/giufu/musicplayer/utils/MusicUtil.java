package com.giufu.musicplayer.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import java.io.File;

public class MusicUtil {
    //Glass resolution constants, Our artwork will take up the full screen
    private static final int GLASS_WIDTH = 640;

    private static final int GLASS_HEIGHT = 360;

    /**
     * Gets an image for the Album Art Work
     *
     * @param filePath Path of the file to be decoded
     * @return Bitmap for the album art work, null if none exists
     */
    public static Bitmap getAlbumArtWork(String filePath) {
        try {
            MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
            metaRetriver.setDataSource(filePath);
            byte[] album = metaRetriver.getEmbeddedPicture();
            if (album != null) {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(album, 0, album.length, opts);
                opts.inSampleSize = calculateInSampleSize(opts);
                opts.inJustDecodeBounds = false;
                return BitmapFactory.decodeByteArray(album, 0, album.length, opts);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        //Our height and width will always be the same since all glass has the same resolution, for now...
        if (height > GLASS_HEIGHT || width > GLASS_WIDTH) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > GLASS_HEIGHT && (halfWidth / inSampleSize) > GLASS_WIDTH) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static String getAlbum(String filePath) {
        try {
            MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
            metaRetriver.setDataSource(filePath);
            String album = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            if (album == null) throw new Exception();
            return album;
        } catch (Exception e) {
            return "Unknown";
        }
    }

    public static String getSongTitle(String filePath) {
        try {
            MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
            metaRetriver.setDataSource(filePath);
            String title = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            if (title == null) throw new Exception();
            return title;
        } catch (Exception e) {
            return new File(filePath).getName();
        }
    }

    public static int getDuration(String filePath) {
        try {
            MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
            metaRetriver.setDataSource(filePath);
            return Integer.valueOf(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception e) {
            return -1;
        }
    }

    public static String getArtist(String filePath) {
        try {
            MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
            metaRetriver.setDataSource(filePath);
            String artist = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            if (artist == null) throw new Exception();
            return artist;
        } catch (Exception e) {
            return "Unknown";
        }
    }

    public static String getHumanReadableTime(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        return finalTimerString;
    }
}