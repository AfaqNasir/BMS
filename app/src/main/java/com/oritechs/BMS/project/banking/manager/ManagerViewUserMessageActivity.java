package com.oritechs.BMS.project.banking.manager;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.oritechs.BMS.database.android.DriverHelper;
import com.oritechs.BMS.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManagerViewUserMessageActivity extends AppCompatActivity {

  TextView messageView;
  ListView messagesView;
  Intent intent;
  int customerId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_manager_view_user_message);

    // get customer id
    intent = getIntent();
    customerId = intent.getIntExtra("customerId", -1);

    // create a new instance of the database
    Context context = getApplicationContext();
    DriverHelper db = new DriverHelper(context);

    // set the header of the page using customer's name
    String customerName = db.getUserName(customerId);
    messageView = (TextView) findViewById(R.id.message);
    messageView.setText(getResources().getString(R.string.message_owner) + " " + customerName);

    // update the list
    updateList();

    messagesView = (ListView) findViewById(R.id.messages_list_view);
    messagesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // get the message clicked on
        Object item = messagesView.getItemAtPosition(position);
        // get the id of the message
        HashMap<String, String> itemInfo = (HashMap<String, String>) item;
        String messageIdString = itemInfo.get("id");
        int messageId = Integer.parseInt(messageIdString);

        // move to new page to read message
        Intent intentResult = new Intent(ManagerViewUserMessageActivity.this,
            ManagerViewMessageActivity.class);
        // add message id to intent to read it
        intentResult.putExtra("messageId", messageId);
        startActivityForResult(intentResult, 1);
      }
    });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // after viewing a message
    if (requestCode == 1) {
      if (resultCode == RESULT_OK) {
        // set message id from intent as read
        int messageId = data.getIntExtra("messageId", -1);
        Context context = getApplicationContext();
        DriverHelper db = new DriverHelper(context);
        boolean updated = db.updateUserMessageState(messageId);

        // update list to show its read
        updateList();
      }
    }
  }

  public void updateList() {
    // create a new instance of the database
    Context context = getApplicationContext();
    DriverHelper db = new DriverHelper(context);

    // get messages for user
    HashMap<Integer, Boolean> messages = db.getAllMessagesHashMap(customerId);

    ArrayList<HashMap<String, String>> messagesInfo = new ArrayList<>();

    for (Map.Entry<Integer, Boolean> entry : messages.entrySet()) {
      HashMap<String, String> newMessage = new HashMap<>();
      // get message info and convert to string
      int messageIdInt = entry.getKey();
      String messageId = String.valueOf(messageIdInt);
      String messageStringLong = db.getSpecificMessage(messageIdInt);
      String messageString = messageStringLong
          .substring(0, Math.min(messageStringLong.length(), 35));
      boolean messageViewedBool = entry.getValue();
      String messageViewed = "UNREAD";

      if (messageViewedBool) {
        messageViewed = "READ";
      }

      // add message info to a hashmap
      newMessage.put("id", messageId);
      newMessage.put("message", messageString + "...");
      newMessage.put("viewed", messageViewed);

      // add hashmap to arraylist
      messagesInfo.add(newMessage);
    }

    // basically keys for the message info and their respective textviews (rows)
    String[] rows = {"id", "message", "viewed"};
    int[] textViews = {R.id.message_id, R.id.message_string, R.id.message_viewed};

    // add messages to adapter
    SimpleAdapter adapter = new SimpleAdapter(this, messagesInfo,
        R.layout.content_message_list_view,
        rows, textViews);

    // set the adapter to the list view
    messagesView = (ListView) findViewById(R.id.messages_list_view);
    messagesView.setAdapter(adapter);
  }
}
