package com.example.tainy.imagetagger;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EditText;


public class TagActivity extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tag, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private String imagePath = null;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tag, container, false);
            Log.d("in TagActivity", "onCreateView");
            return rootView;
        }


        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onResume() {
            super.onResume();
            Intent intent = this.getActivity().getIntent();
            Uri uri = intent.getClipData().getItemAt(0).getUri();
            this.imagePath = getRealPathFromURI(this.getActivity(), uri);
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            LayoutInflater inflater = LayoutInflater.from(this.getActivity());
            View view = inflater.inflate(R.layout.tag_input_view, null);
            final EditText et_tag = (EditText) view.findViewById(R.id.tag_name);
            builder.setTitle("input the tag");
            builder.setView(view);
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String tag = et_tag.getText().toString();
                    saveTag(tag);
                }
            });
            builder.create().show();
        }

        /**
         * @param tag save the tag:imagePath pair in below format:
         *            tag1:path1,path2,path3...
         *            tag2:path4,path1,path6
         *            tag3:path1,path10...
         */
        private void saveTag(String tag) {
            SharedPreferences sp = this.getActivity().getSharedPreferences("TagActivity", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            String newPath = sp.getString(tag, "") + "," + imagePath;
            editor.putString(tag, newPath);
            editor.commit();
        }

        public String getRealPathFromURI(Context context, Uri contentUri) {
            Cursor cursor = null;
            try {
                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = context.getContentResolver().query(contentUri, proj, null,
                        null, null);
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }
}
