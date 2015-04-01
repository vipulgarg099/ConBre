package com.conbre.vipul.conbre;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    private static final String TAG = MainActivity.class.getSimpleName() ;
    public static final int take_photo_request = 0;
    public static final int take_video_request = 1;
    public static final int choose_photo_request = 2;
    public static final int choose_video_request = 3;

    public static final int media_type_image = 4;
    public static final int media_type_video = 5;

    public static final int file_size_limit = 1024*1024*10; //10 MB

    protected Uri mMediaUri;


    protected DialogInterface.OnClickListener mDialogListner =
            new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case 0 : //Take Picture
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(media_type_image);
                    if(mMediaUri==null){
                        //display error
                        Toast.makeText(MainActivity.this,
                                getString(R.string.error_externalStorage),Toast.LENGTH_LONG).show();
                    }
                    else{
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        startActivityForResult(takePhotoIntent, take_photo_request);
                    }
                    break;
                case 1 : //Take Video
                    Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(media_type_video);
                    if(mMediaUri==null){
                        Toast.makeText(MainActivity.this,
                                getString(R.string.error_externalStorage),Toast.LENGTH_LONG).show();
                    }
                    else{
                        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);
                        startActivityForResult(videoIntent,take_video_request);
                    }
                    break;
                case 2 : //Choose Picture
                    Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    choosePhotoIntent.setType("image/*");
                    startActivityForResult(choosePhotoIntent, choose_photo_request);
                    break;
                case 3 : //Choose Video
                    Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    chooseVideoIntent.setType("video/*");
                    Toast.makeText(MainActivity.this, getString(R.string.video_size_warning), Toast.LENGTH_LONG).show();
                    startActivityForResult(chooseVideoIntent, choose_video_request);
                    break;
          }

        }

                private Uri getOutputMediaFileUri(int media_type) {
                    // To be safe, you should check that the SDCard is mounted
                    // using Environment.getExternalStorageState() before doing this.
                    String appName = MainActivity.this.getString(R.string.app_name);
                    if(isExternalStorageAvailable()){

                        File mediaStorageDir = new File(Environment
                                .getExternalStoragePublicDirectory(Environment
                                        .DIRECTORY_PICTURES),appName);
                        if(!mediaStorageDir.exists()){
                            if(!mediaStorageDir.mkdir()) {
                                Log.e(TAG, "Failed to create Directory");
                                return null;
                            }
                        }

                        File mediaFile;
                        Date now = new Date();
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);
                        String path = mediaStorageDir.getPath() + File.separator;
                        if(media_type == media_type_image){
                            mediaFile = new File(path + "IMG_" +timeStamp + ".jpg");
                        }
                        else if(media_type==media_type_video) {
                            mediaFile = new File(path + "VID_" + timeStamp + ".mp4");
                        }
                        else{
                            return null;
                        }
                        Log.d(TAG, "File:"+Uri.fromFile(mediaFile));
                        return Uri.fromFile(mediaFile);
                    }
                    else{
                        return null;
                    }
                }

                private boolean isExternalStorageAvailable(){
                    String state = Environment.getExternalStorageState();
                    if(state.equals(Environment.MEDIA_MOUNTED)){
                        return true;
                    }
                    return false;
                }
            };


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Parse.initialize(this, "UurnvcunD45UE1Cu1KaEpaTH5LWm8cvTJu9pGF0c", "5BMgK9hKxinVE2PTCZZ794ooBiI78NgGApl15xxz");
        ParseUser currentUser = ParseUser.getCurrentUser();

        if(currentUser==null) {
            navigateToLogin();
        }
        else{
            Log.i(TAG, currentUser.getUsername());
        }

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            //add to the gallery
            if(requestCode==choose_photo_request || requestCode==choose_video_request){
                if(data==null){
                    Toast.makeText(this, getString(R.string.media_general_error), Toast.LENGTH_LONG).show();
                }
                else{
                    mMediaUri = data.getData();
                }
                Log.i(TAG,"Media URI:"+mMediaUri);
                if(requestCode==choose_video_request){
                    int fileSize =0;
                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        fileSize = inputStream.available();
                    }
                    catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(this, getString(R.string.error_opening_file), Toast.LENGTH_LONG).show();
                        return;
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, getString(R.string.error_opening_file), Toast.LENGTH_LONG).show();
                        return;
                    }
                    finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) {/*intentionally blank*/ }
                    }
                    if(fileSize>=file_size_limit){
                        Toast.makeText(this, getString(R.string.file_size_too_long), Toast.LENGTH_LONG).show();
                    return;
                    }
                }
            }
            else {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
            }
            Intent recipientsIntent = new Intent(this, RecipientsActivity.class);
            recipientsIntent.setData(mMediaUri);
            startActivity(recipientsIntent);
        }
        else if(resultCode!=RESULT_CANCELED){
            Toast.makeText(this, getString(R.string.media_general_error), Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, Login_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_logOut:
                ParseUser.logOut();
                navigateToLogin();
                break;
            case R.id.action_editFriends:
                Intent intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_camera:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.camera_choices, mDialogListner);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;


        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

}
