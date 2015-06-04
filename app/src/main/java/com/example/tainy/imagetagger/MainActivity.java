package com.example.tainy.imagetagger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;


import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    public static final String APPLICATION_ID = "aw5b9j4i4f41fgwj1s7z97u6q3588fa0afulvgvcz7kaz0id";
    public static final String APP_KEY        = "e8e9q0v471yad51mwerfv43or9suv8aalh8cwhtyiy5una0s";
    public static final String MASTER_KEY     = "ur5xkd716zu3xidwc8sky6amkdrsv8behfq4tjp8t577fbbq";

    LinearLayout ll_tags;
    TagImageAdapter adapter;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        init3rdParty();

        setContentView(R.layout.activity_main);
        EditText et_tag = (EditText) this.findViewById(R.id.search_tag_name);
        final String name = et_tag.getText().toString();
        Button btn_search = (Button) this.findViewById(R.id.search_tag_button);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                search(name);
            }
        });
        ll_tags = (LinearLayout) this.findViewById(R.id.tag_container);
        list = (ListView) this.findViewById(R.id.image_list);
        showTags();

    }

    private void init3rdParty()
    {
        AVOSCloud.initialize(this, APPLICATION_ID, APP_KEY);
    }

    private void search(String name)
    {
//        SharedPreferences sp = this.getSharedPreferences("TagActivity",MODE_PRIVATE);
        retrieveDataFromCloud();

    }

    private void showTags()
    {
        SharedPreferences sp = this.getSharedPreferences("TagActivity",MODE_PRIVATE);
        Map<String,?> values = sp.getAll();
        TextView[] tvs = new TextView[values.size()];
        int i=0;
        for(String tag:values.keySet())
        {
            Object path = values.get(tag);
            TextView tv_tag = new TextView(this);
            tv_tag.setTag(tag);
            tv_tag.setText(tag+"       ");
            tv_tag.setOnClickListener(this);
            tvs[i] = tv_tag;

            i++;
        }
        populateText(ll_tags,tvs,this);

    }

    @Override
    public void onResume()
    {
        super.onResume();
        showTags();

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateText(LinearLayout ll, View[] views , Context mContext) {
        Display display = getWindowManager().getDefaultDisplay();
        ll.removeAllViews();
        int maxWidth = display.getWidth() - 20;

        LinearLayout.LayoutParams params;
        LinearLayout newLL = new LinearLayout(mContext);
        newLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        newLL.setGravity(Gravity.LEFT);
        newLL.setOrientation(LinearLayout.HORIZONTAL);

        int widthSoFar = 0;

        for (int i = 0 ; i < views.length ; i++ ){
            LinearLayout LL = new LinearLayout(mContext);
            LL.setOrientation(LinearLayout.HORIZONTAL);
            LL.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
            LL.setLayoutParams(new ListView.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            //my old code
            //TV = new TextView(mContext);
            //TV.setText(textArray[i]);
            //TV.setTextSize(size);  <<<< SET TEXT SIZE
            //TV.measure(0, 0);
            views[i].measure(0,0);
            params = new LinearLayout.LayoutParams(views[i].getMeasuredWidth(),
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            //params.setMargins(5, 0, 5, 0);  // YOU CAN USE THIS
            //LL.addView(TV, params);
            LL.addView(views[i], params);
            LL.measure(0, 0);
            widthSoFar += views[i].getMeasuredWidth();// YOU MAY NEED TO ADD THE MARGINS
            if (widthSoFar >= maxWidth) {
                ll.addView(newLL);

                newLL = new LinearLayout(this);
                newLL.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                newLL.setOrientation(LinearLayout.HORIZONTAL);
                newLL.setGravity(Gravity.LEFT);
                params = new LinearLayout.LayoutParams(LL
                        .getMeasuredWidth(), LL.getMeasuredHeight());
                newLL.addView(LL, params);
                widthSoFar = LL.getMeasuredWidth();
            } else {
                newLL.addView(LL);
            }
        }
        ll.addView(newLL);
    }

    @Override
    public void onClick(View v)
    {
        String tag = v.getTag().toString();
        SharedPreferences sp = this.getSharedPreferences("TagActivity",MODE_PRIVATE);
        String pathList = sp.getString(tag,"");
        pathList = pathList.substring(1);
        String[] pathArray = pathList.split(",");
        adapter = new TagImageAdapter(pathArray);
        list.setAdapter(adapter);

    }

    public class TagImageAdapter extends BaseAdapter
    {
        String[] pathArray;
        public TagImageAdapter(String[] tags)
        {
            this.pathArray = tags;
        }
        @Override
        public int getCount()
        {
            return pathArray.length;
        }

        @Override
        public Object getItem(int position) {
            return pathArray[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            View row = inflater.inflate(R.layout.tag_image_row,null);
            TextView tv_path = (TextView) row.findViewById(R.id.image_path);
            ImageView iv_snap = (ImageView) row.findViewById(R.id.image_snap);

            tv_path.setText(pathArray[position]);
            if(!pathArray[position].isEmpty())
            {
                final File file = new File(pathArray[position]);
                iv_snap.setImageDrawable(Drawable.createFromPath(pathArray[position]));
                iv_snap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        Uri uri = Uri.fromFile(file);
                        intent.setDataAndType(uri, "image/*");
                        startActivity(intent);
                    }
                });

            }

            return row;

        }
    }

    private void doSomethingonCloud()
    {
        AVObject testObject = new AVObject("Tainy_Test_The_Object");
        testObject.put("key1", "value1");
        testObject.put("key2", "value2");
        testObject.put("key3", "value3");
        testObject.put("key4", "value4");
        testObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e)
            {

            }
        });
    }

    private void retrieveDataFromCloud()
    {
        AVQuery<AVObject> query = new AVQuery<>("Tainy_Test_The_Object");
        query.whereEqualTo("key2","anotherValue2");

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException e)
            {
                for(AVObject obj:avObjects)
                {
                    String id = obj.getString("objectId");
                    String value2 = obj.getString("key2");
                }

            }
        });
/*        try {
            query.find
            List<AVObject> list = query.find();
            for(AVObject obj:list)
            {
                String id = obj.getString("objectId");
                String key1 = obj.getString("key1");
                Date createDate = obj.getDate("createdAt");
                Date updateDate = obj.getDate("updatedAt");
            }

        } catch (AVException e) {
            e.printStackTrace();
        }*/


    }
}
