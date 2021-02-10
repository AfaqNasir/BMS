package com.oritechs.BMS.project.banking.manager;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.oritechs.BMS.database.android.DriverHelper;
import com.oritechs.BMS.generics.Roles;
import com.oritechs.BMS.project.banking.InputValidator;
import com.oritechs.BMS.R;

import java.util.EnumMap;
import java.util.List;

public class ManagerLeaveMessageActivity extends AppCompatActivity {

  EditText userIdView;
  EditText messageSendView;
  TextView messageView;
  Intent intent;
  int tellerId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_manager_leave_message);

    // get teller id
    intent = getIntent();
    tellerId = intent.getIntExtra("tellerId", -1);

    // when send button is clicked
    Button sendMessage = (Button) findViewById(R.id.send_message);
    sendMessage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // get views
        userIdView = (EditText) findViewById(R.id.userid_leave_msg);
        messageSendView = (EditText) findViewById(R.id.message_will_be_sent);
        messageView = (TextView) findViewById(R.id.message);

        // check if input is empty
        if (InputValidator.isEmpty(userIdView) || InputValidator.isEmpty(messageSendView)) {
          // show message to not leave empty
          messageView.setText(R.string.null_info);

        } else {
          // get user id and message
          int userId = Integer.parseInt(userIdView.getText().toString());
          String message = messageSendView.getText().toString();

          // create a new instance of the database
          Context context = getApplicationContext();
          DriverHelper db = new DriverHelper(context);

          // check if user id exists
          List<Integer> userIds = db.getUsersIdsList();
          boolean exists = false;
          for (Integer i : userIds) {
            if (userId == i) {
              exists = true;
            }
          }

          boolean isCustomer = false;
          long inserted = -1;

          if (exists) {
            // check if id is customer
            EnumMap<Roles, Integer> roles = db.getRolesEnumMap();
            int customerRoleId = roles.get(Roles.CUSTOMER);
            int roleId = db.getUserRole(userId);
            if (roleId == customerRoleId) {
              isCustomer = true;
            }

            // check length of message
            if (isCustomer && message.length() <= 512) {
              // insert message if not long
              inserted = db.insertMessage(userId, message);

              if (inserted != -1) {
                // message is sent
                messageView.setText(R.string.success_sent);

              } else {
                // message couldn't be sent for some reason
                messageView.setText(R.string.fail_sent);
              }

            } else if (!isCustomer) {
              // tellers can only leave messages for customers
              messageView.setText(R.string.userid_is_not_avaliable);

            } else if (message.length() > 512) {
              // message is too long
              messageView.setText(R.string.message_long);
            }

          } else {
            // user id doesn't exist
            messageView.setText(R.string.invalid_userid);
          }
        }
      }
    });
  }
}
