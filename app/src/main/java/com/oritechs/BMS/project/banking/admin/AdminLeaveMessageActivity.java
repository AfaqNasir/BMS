package com.oritechs.BMS.project.banking.admin;

import android.content.Context;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.oritechs.BMS.database.android.DriverHelper;
import com.oritechs.BMS.project.banking.InputValidator;
import com.oritechs.BMS.R;

import java.util.List;

public class AdminLeaveMessageActivity extends AppCompatActivity {

  int adminId = -1;
  TextView messageView;
  EditText userIdView;
  EditText sendMessageView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_leave_message);

    // get admin id
    adminId = getIntent().getIntExtra("adminId", adminId);

    // when send button is clicked
    final Button sendMessage = (Button) findViewById(R.id.send_message);
    sendMessage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // find TextView
        messageView = (TextView) findViewById(R.id.message);
        userIdView = (EditText) findViewById(R.id.userid_leave_msg);
        sendMessageView = (EditText) findViewById(R.id.message_will_be_sent);

        // check if the field is empty
        if (InputValidator.isEmpty(userIdView) || InputValidator.isEmpty(sendMessageView)) {
          Toast toast = Toast.makeText(getApplicationContext(), R.string.null_info,
              Toast.LENGTH_SHORT);
          toast.show();

        } else {
          Context context = getApplicationContext();
          DriverHelper db = new DriverHelper(context);

          // get user ID and message
          int userId = Integer.parseInt(userIdView.getText().toString());
          String sendMessage = sendMessageView.getText().toString();

          // check if the user exists
          List<Integer> userIds = db.getUsersIdsList();
          boolean exists = false;
          for (Integer i : userIds) {
            if (userId == i) {
              exists = true;
            }
          }

          // if user exists
          long inserted = -1;

          if (exists) {
            // check length of message
            if (sendMessage.length() <= 512) {
              // insert message
              inserted = db.insertMessage(userId, sendMessage);

              if (inserted != -1) {
                // message sent
                messageView.setText(R.string.success_sent);

              } else {
                // message cannot be sent
                messageView.setText(R.string.fail_sent);
              }

            } else {
              // message too long
              messageView.setText(R.string.message_long);
            }

          } else {
            // user doesn't exist
            messageView.setText(R.string.invalid_userid);
          }
        }
      }
    });
  }
}
