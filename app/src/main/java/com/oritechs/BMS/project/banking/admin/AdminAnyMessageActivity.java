package com.oritechs.BMS.project.banking.admin;

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
import java.util.List;
import java.util.Map;

public class AdminAnyMessageActivity extends AppCompatActivity {

  TextView messageView;
  ListView messagesView;
  Intent intent;
  int adminId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_any_message);

    intent = getIntent();
    adminId = intent.getIntExtra("adminId", -1);

    // create a new instance of the database
    Context context = getApplicationContext();
    DriverHelper db = new DriverHelper(context);

    // set the header of the page
    messageView = (TextView) findViewById(R.id.message);
    messageView.setText(getResources().getString(R.string.all_messages));

    // update the list
    updateList();

    messagesView = (ListView) findViewById(R.id.messages_list_view);
    messagesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // get the message clicked on
        Object item = messagesView.getItemAtPosition(position);
        HashMap<String, String> itemInfo = (HashMap<String, String>) item;
        String messageIdString = itemInfo.get("id");
        int messageId = Integer.parseInt(messageIdString);

        // move to new page to read message
        Intent intentResult = new Intent(AdminAnyMessageActivity.this,
            AdminViewMessageActivity.class);
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
        int messageId = data.getIntExtra("messageId", -1);

        Context context = getApplicationContext();
        DriverHelper db = new DriverHelper(context);

        // check whether this message belongs to admin
        List<Integer> messageIdList = db.getMessageIds(adminId);
        for (Integer eachMessageId : messageIdList) {
          // if so, update the status of the message
          if (eachMessageId == messageId) {
            boolean updated = db.updateUserMessageState(messageId);
          }
        }

        updateList();
      }
    }
  }

  public void updateList() {
    // create a new instance of the database
    Context context = getApplicationContext();
    DriverHelper db = new DriverHelper(context);

    // get messages for user
    List<Integer> userIds = db.getUsersIdsList();
    ArrayList<HashMap<String, String>> messagesInfo = new ArrayList<>();

    for (Integer i : userIds) {
      HashMap<Integer, Boolean> messages = db.getAllMessagesHashMap(i);

      for (Map.Entry<Integer, Boolean> entry : messages.entrySet()) {
        HashMap<String, String> newMessage = new HashMap<>();
        // get message info
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

        // add info to a hashmap
        newMessage.put("id", messageId);
        newMessage.put("message", messageString + "...");
        newMessage.put("viewed", messageViewed);

        // add hashmap to arraylist
        messagesInfo.add(newMessage);
      }
    }

    // basically keys for the message info
    String[] rows = {"id", "message", "viewed"};
    int[] textViews = {R.id.message_id, R.id.message_string, R.id.message_viewed};

    SimpleAdapter adapter = new SimpleAdapter(this, messagesInfo,
        R.layout.content_message_list_view,
        rows, textViews);

    // set the list
    messagesView = (ListView) findViewById(R.id.messages_list_view);
    messagesView.setAdapter(adapter);
  }

}
