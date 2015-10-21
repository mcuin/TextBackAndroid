package com.mykal.textback;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReplyList extends AppCompatActivity {

    ListView replies;
    ArrayList<HashMap<String, String>> entries;
    HashMap<String, String> reply = new HashMap<>();
    SimpleAdapter adapter;
    DatabaseHandler db;
    String nameOut, messageOut;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_list);

        replies = (ListView) findViewById(R.id.replyList);
        replies.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        entries = new ArrayList<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReplyList.this, AddReply.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reply_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_feedback) {
            Intent intent = new Intent(ReplyList.this, Feedback.class);
            startActivityForResult(intent, 2);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        db = new DatabaseHandler(this);

        final List<Replies> reps = db.getAllReplies();
        entries = new ArrayList<>();

        for (Replies rep : reps) {
            String name = "" + rep.getName();
            String message = "" + rep.getMessage();

            reply.put(name, message);
            entries.add(reply);

            adapter = new SimpleAdapter(this, entries, android.R.layout.simple_list_item_1, new String[] {name},
                    new int[] {android.R.id.text1});
            replies.setAdapter(adapter);

        }

        replies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ReplyList.this, AddReply.class);
                Replies reply = db.getReply(id+1);
                intent.putExtra("id", reply.getId());
                intent.putExtra("name", reply.getName());
                intent.putExtra("message", reply.getMessage());

                startActivity(intent);
            }
        });

        replies.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Replies reply = db.getReply(id+1);
                db.deleteReply(reply.getId());
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }

}
