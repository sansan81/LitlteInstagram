package com.whoami.litlteinstagram.lib;

import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class FormData
{
    public static final String CONTENT_TYPE_MULTIPART_FILE = "multipart/form-data";
    public static final String CONTENT_TYPE_PLAIN_TEXT = "text/plain";
    public static final String FILE_FORMAT_JPEG = "jpg";
    public static final String FILE_FORMAT_PNG = "png";

    ArrayList<KeyValue> data;
    ArrayList<String> contentTypes;
    ArrayList<String> fileFormats;

    public FormData()
    {
        this.data = new ArrayList<>();
        this.contentTypes = new ArrayList<>();
        this.fileFormats = new ArrayList<>();
    }

    public void add(String key, String value)
    {
        KeyValue kv = new KeyValue(key, value);
        this.data.add(kv);
        this.contentTypes.add(FormData.CONTENT_TYPE_PLAIN_TEXT);
        this.fileFormats.add("");
    }

    public void addImage(String key, Bitmap image, String format)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Bitmap.CompressFormat compressFormat = format.equals(FormData.FILE_FORMAT_JPEG) ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG;
        image.compress(compressFormat, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        KeyValue kv = new KeyValue(key, imageBytes);
        this.data.add(kv);
        this.contentTypes.add(FormData.CONTENT_TYPE_MULTIPART_FILE);
        this.fileFormats.add(format);
    }

    public ArrayList<KeyValue> getData(String contentType)
    {
        ArrayList<KeyValue> result = new ArrayList<>();
        for(int i = 0; i < this.data.size(); i++)
        {
            String format = this.fileFormats.get(i);
            if(contentType.equals(FormData.CONTENT_TYPE_MULTIPART_FILE))
            {
                if (!format.isEmpty())
                    result.add(this.data.get(i));
            }
            else
            {
                if (format.isEmpty())
                    result.add(this.data.get(i));
            }
        }
        return result;
    }

    public String fileNameFor(KeyValue fileData)
    {
        int index = this.data.indexOf(fileData);
        if(index > -1)
        {
            String fileName = fileData.getKey() + "." + this.fileFormats.get(index);
            return fileName;
        }
        return null;
    }
}