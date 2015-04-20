package com.example.tainy.imagetagger;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    LinearLayout ll_tags;
    TagImageAdapter adapter;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private void search(String name)
    {
        SharedPreferences sp = this.getSharedPreferences("TagActivity",MODE_PRIVATE);

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
        String[] pathArray = pathList.split(",");
        adapter = new TagImageAdapter(pathArray);
        list.setAdapter(adapter);

    }

    public class TagImageAdapter extends BaseAdapter
    {
        List<String> pathArray = new ArrayList<String>();
        public TagImageAdapter(String[] tags)
        {
            for(String tag:tags)
            {
                if(tag !=null && !tag.isEmpty())
                    pathArray.add(tag);
            }
        }
        @Override
        public int getCount()
        {
            return pathArray.size();
        }

        @Override
        public Object getItem(int position) {
            return pathArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            View row = inflater.inflate(R.layout.tag_image_row,null);
            TextView tv_path = (TextView) row.findViewById(R.id.image_path);
            ImageView iv_snap = (ImageView) row.findViewById(R.id.image_snap);

            tv_path.setText(pathArray.get(position));
            if(!pathArray.get(position).isEmpty())
            {
                Drawable drawable = Drawable.createFromPath(pathArray.get(position));
                iv_snap.setImageDrawable(drawable);

            }

            return row;

        }
    }
}
