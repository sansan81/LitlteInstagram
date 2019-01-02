package com.whoami.litlteinstagram;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.whoami.litlteinstagram.lib.InternetTask;
import com.whoami.litlteinstagram.lib.KeyValue;
import com.whoami.litlteinstagram.lib.ListPostAdapter;
import com.whoami.litlteinstagram.lib.OnInternetTaskFinishedListerner;
import com.whoami.litlteinstagram.lib.UtilityHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements OnInternetTaskFinishedListerner, AdapterView.OnItemClickListener
{
    private final static  int ACTIVITY_POST = 1;
    private final static  int ACTIVITY_LOCATION = 2;
    private final static int CHOOSER = 3;
    private final static String API_GET_POST = "get_post";
    private List<com.whoami.litlteinstagram.entities.Post> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        get_post();
    }

    private void show_post_activity()
    {
        showDialog(Home.CHOOSER);
    }

    private void get_post()
    {
        ArrayList<KeyValue> keyValues = new ArrayList<>();
        keyValues.add(new KeyValue("m",Home.API_GET_POST));
        connectApi(Home.API_GET_POST,keyValues);
    }

    private void showAlert(String title, String message)
    {
        UtilityHelper.showAlert(this, title, message);
    }

    private void getBitmapFromCamera()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,Post.USE_CAMERA);
    }

    private void getBitmapFromCamera(Intent data)
    {
        try
        {
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            Intent intent = new Intent(this,Post.class);
            intent.putExtra("image",convertBitmapToByte(bitmap));
            startActivityForResult(intent,Home.ACTIVITY_POST);
        }catch (Exception e){
            showAlert("Error get picture from camera",e.getMessage());
        }
    }

    private void getBitmapFromGallery()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Intent gallery = Intent.createChooser(intent,"Select File");
        startActivityForResult(gallery, Post.USE_GALLERY);
    }

    private void getBitmapFromGallery(Intent data)
    {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        Uri uri = data.getData();
        try
        {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
            Intent intent = new Intent(this, Post.class);
            intent.putExtra("image",convertBitmapToByte(bitmap));
            startActivityForResult(intent,Home.ACTIVITY_POST);
        }catch (Exception e){
            showAlert("Error get photo",e.getMessage());
        }
    }

    private void connectApi(String tag, ArrayList<KeyValue> keyValues)
    {
        InternetTask internetTask = new InternetTask("POST",InternetTask.URI,keyValues);
        internetTask.setOnInternetTaskFinishedListerner(this);
        internetTask.setTag(tag);
        internetTask.execute();
    }

    private byte[] convertBitmapToByte(Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id){
            case Home.CHOOSER:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                final String[] items = {"Take Photo","Choose From Gallery","Cancel"};
                alertDialog.setTitle("Add Photo").setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (items[which]){
                            case "Take Photo":
                                getBitmapFromCamera();
                                break;
                            case "Choose From Gallery":
                                getBitmapFromGallery();
                                break;
                            default:
                                dialog.dismiss();
                                break;
                        }
                    }
                });
                alertDialog.show();
                break;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode==RESULT_OK)
        {
            switch (requestCode) {
                case Home.ACTIVITY_POST:
                    get_post();
                    break;
                case Post.USE_CAMERA:
                    getBitmapFromCamera(data);
                    break;

                case Post.USE_GALLERY:
                    getBitmapFromGallery(data);
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()){
            case R.id.add_post:
                show_post_activity();
                break;
        }
        return true;
    }

    @Override
    public void OnInternetTaskFinished(InternetTask internetTask) throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject(internetTask.getResponseString());
            switch (internetTask.getTag()){
                case Home.API_GET_POST:
                        if(jsonObject.getString("code").equals("200"))
                        {
                            ListView listView = (ListView) findViewById(R.id.list_view);
                            posts = new ArrayList<>();
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i<jsonArray.length(); i++)
                            {
                                com.whoami.litlteinstagram.entities.Post post = new com.whoami.litlteinstagram.entities.Post(jsonArray.getJSONObject(i));
                                posts.add(post);
                            }
                            ListPostAdapter listPostAdapter = new ListPostAdapter(getApplicationContext(), R.layout.post_list, posts);
                            listView.setAdapter(listPostAdapter);
                            listView.setOnItemClickListener(this);
                        }else{
                            showAlert("Error Code",jsonObject.getString("data"));
                        }
                    break;
            }
        }catch (Exception e){
            showAlert("Error Parsing JSON", e.getMessage());
        }
    }

    @Override
    public void OnInternetTaskFailed(InternetTask internetTask)
    {
        showAlert("Error Connection", internetTask.getException().getMessage());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        com.whoami.litlteinstagram.entities.Post post = posts.get(position);
        if(post.getLocation())
        {
            Intent intent = new Intent(this, ShowLocationPhoto.class);
            intent.putExtra("lat",post.getLat());
            intent.putExtra("lang",post.getLang());
            intent.putExtra("name",post.getDesc());
            startActivityForResult(intent, Home.ACTIVITY_LOCATION);
        }else{
            Toast.makeText(this, "No Location for this photo",Toast.LENGTH_SHORT).show();
        }
    }
}
