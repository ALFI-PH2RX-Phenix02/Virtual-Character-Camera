package com.example.virtual_character_camera;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CharChange extends AppCompatActivity {

    //the string that contains the name of the characters. the name should be the same as those in the folder live2d
    private String []ids={"nepnep",
            "noir",
            "nepgear",
            "haru",
            "blanc_classic",
            "vert_classic",
            "uni",
            "histoire"
            };

    //icons of the characters saved in drawable. the name should be char+i
    private int[]images=new int[]{R.drawable.char1,
            R.drawable.char2,
            R.drawable.char3,
            R.drawable.char4,
            R.drawable.char5,
            R.drawable.char6,
            R.drawable.char7,
            R.drawable.char8
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);

        ListView listview=(ListView) findViewById(R.id.listview);
        MyAdapter myAdapter=new MyAdapter();
        listview.setAdapter(myAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle bundle = new Bundle();

                bundle.putString("character", ids[position]);

                //send the name of the selected character to CharacterView to change
                Intent intent = new Intent(CharChange.this,CharacterView.class);
                intent.putExtras(bundle);
                startActivity(intent);
                //Terminate();

            }
        });
    }

    private class MyAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return ids.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view=getLayoutInflater().inflate(R.layout.list_layout,null);
            ImageView image=(ImageView) view.findViewById(R.id.imageview);
            TextView text=(TextView) view.findViewById(R.id.textview);

            image.setImageResource(images[position]);
            text.setText(ids[position]);
            return view;
        }
    }

    public void Terminate()
    {
        this.finish();
    }
}
