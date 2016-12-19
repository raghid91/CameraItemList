package suchitra.example.com.pluralsightdatabaselist;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ToDo extends AppCompatActivity {

    private List<TodoItems> arrayList;
    protected ListDatabase DBHelper;
    private MyAdapter adapter;
    ListView listview;

    Button add, delete;
    TodoItems items;

    String uriPath;
    private String photoPath;
    private static final int REQUEST_PERMISSION_CAMERA = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private LocationManager locationmanager;
    private LocationListener locationlistener;

    PackageManager packagemanager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        packagemanager = this.getPackageManager();
        if(!(packagemanager.hasSystemFeature(PackageManager.FEATURE_CAMERA)))
        {
            Toast.makeText(this, "This is not a camera device", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }

        add = (Button) findViewById(R.id.addBtn);
        delete = (Button) findViewById(R.id.delBtn);
        items = new TodoItems();
        DBHelper = new ListDatabase(this);

        locationmanager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationlistener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String CoOrdinates = "Latitude: " + location.getLatitude() + " \nLongitude: " + location.getLongitude();
                Toast.makeText(getBaseContext(), CoOrdinates, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Toast.makeText(getBaseContext(), "GPS turned off ", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[] {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET
                }, 1);
                return;
            }
        }

        locationmanager.requestLocationUpdates("gps",5000,0,locationlistener);
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        arrayList = DBHelper.getAllTasks();
        listview = (ListView)findViewById(R.id.todoListView);
        adapter = new MyAdapter(this, R.layout.list, arrayList);
        listview.setAdapter(adapter);
    }

    private class MyAdapter extends ArrayAdapter<TodoItems> {
        Context context;
        List<TodoItems> taskList = new ArrayList<TodoItems>();


        public MyAdapter(Context c, int rId, List<TodoItems> objects) {
            super(c, rId, objects);
            taskList = objects;
            context = c;
        }

        public class imageHolder
        {
            CheckBox isDoneChBx;
            ImageView imgview;
            TextView listText;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            imageHolder holder;

            if (convertView == null) {
                holder = new imageHolder();

                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list, parent, false);

                holder.isDoneChBx = (CheckBox) convertView.findViewById(R.id.checkBox);
                holder.imgview = (ImageView) convertView.findViewById(R.id.photo);
                holder.listText = (TextView) convertView.findViewById(R.id.listText);
                convertView.setTag(holder);
            }
            else {
                holder = (imageHolder) convertView.getTag();
            }
            if(taskList != null) {
                holder.listText.setText(taskList.get(position).getDescription());
                holder.isDoneChBx.setChecked(taskList.get(position).getIs_done());
                holder.isDoneChBx.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(((CheckBox)view).isChecked()) {
                            taskList.get(position).setIs_done(true);
                        }
                        else
                            taskList.get(position).setIs_done(false);
                    }
                });
                holder.imgview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         /* Show photo in pop up window */
                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.showphoto, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setView(promptView);

                        final ImageView image = (ImageView) promptView.findViewById(R.id.displayPciture);
                        /* Get path from To Do Item and display image in pop up window */
                        Bitmap bitmap = BitmapFactory.decodeFile(arrayList.get(position).getUriPath(), resizePhoto());
                        image.setImageBitmap(bitmap);
                        alertDialogBuilder.setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = alertDialogBuilder.create();
                        alert.show();
                    }
                });
            }
            return convertView;
        }
        /* Resize photo to save memory space */
        public BitmapFactory.Options resizePhoto()
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = Math.max(5, 5);
            return options;
        }
    }

    public void toAdd(View view) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setCancelable(false);
        LayoutInflater inflater = getLayoutInflater();
        view = inflater.inflate(R.layout.dialog, null);
        final EditText userInput = (EditText)view.findViewById(R.id.editText);
        adb.setMessage("Enter your To-Do List here");
        adb.setView(view);
        adb.setNegativeButton("CANCEL", null);
        adb.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String input = userInput.getText().toString();
                if (input.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "A TODO task must be entered.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (photoPath != null)
                    {
                        uriPath = photoPath;
                    }
                    TodoItems task = new TodoItems(input, uriPath);
                    DBHelper.addItem(task);
                    adapter.add(task);
                    photoPath = null;
                }
            }
        });
        adb.show();
    }

    public void toDelete(View view)
    {
        DBHelper.deleteTask();
        onResume();
    }

    public void takePhoto(View view) {
        Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File image = createPath();
        cameraintent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
        startActivityForResult(cameraintent, REQUEST_IMAGE_CAPTURE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Toast.makeText(this, "Image Captured", Toast.LENGTH_SHORT).show();
        }
        else if(resultCode == RESULT_CANCELED)
        {
            Toast.makeText(this, "Capturing cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private File createPath()
    {
        File image = null;
        String randomNumber = Integer.toString((int) (Math.random() * 1000)) + Integer.toString((int) (Math.random() * 500)) + Integer.toString((int) (Math.random() * 50));
        String imageFileName = "IMG_" + randomNumber +  "_";
            /* Get directory where to store images */
        File album = getAlbumDirectory();
        try {
            image = File.createTempFile(imageFileName, ".jpg", album);
        } catch (IOException e) {
            e.printStackTrace();
        }
        photoPath = image.getAbsolutePath();
        return image;
    }

    private File getAlbumDirectory()
    {
        File albumDirectory =  getExternalCacheDir();
        if (albumDirectory == null)
        {
            Toast.makeText(this, "Failed to create a directory", Toast.LENGTH_LONG).show();
            Log.e("FILE", "Error: Fail to create directory!");
        }
        return albumDirectory;
    }

    public void requestPermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
        {

            Toast.makeText(this, "Allow this app to use storage. Change it in the Settings/App section!", Toast.LENGTH_LONG).show();
        }

        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
        }
    }
}

