package com.oritechs.BMS.project.banking.manager;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.oritechs.BMS.R;


public class ManagerMainMessageActivity extends AppCompatActivity {

  Intent intent;
  int customerId;
  int tellerId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_manager_main_message);

    // get customer id and teller id
    intent = getIntent();
    customerId = intent.getIntExtra("customerId", -1);
    tellerId = intent.getIntExtra("tellerId", -1);

    // view teller's messages
    Button viewOwnMessage = (Button) findViewById(R.id.button_view_own_msg);
    viewOwnMessage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // add teller id to intent and go to new activity
        Intent intent = new Intent(ManagerMainMessageActivity.this,
            ManagerViewItsMessagesActivity.class);
        intent.putExtra("tellerId", tellerId);
        startActivity(intent);
      }
    });

    // view customer's messages
    Button viewCustomerMessage = (Button) findViewById(R.id.button_view_cus_msg);
    viewCustomerMessage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // if there is no current customer
        if (customerId == -1) {
          // pop a message saying there's no current customer
          Toast toast = Toast.makeText(getApplicationContext(),
              R.string.no_current_user,
              Toast.LENGTH_SHORT);
          toast.show();

        } else {
          // else go to next activity and add customer id to intent
          Intent intent = new Intent(ManagerMainMessageActivity.this,
              ManagerViewUserMessageActivity.class);
          intent.putExtra("customerId", customerId);
          startActivity(intent);
        }
      }
    });

    // leave a message
    Button leaveMessage = (Button) findViewById(R.id.button_leave_msg);
    leaveMessage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // add the teller's id to intent and start activity
        Intent intent = new Intent(ManagerMainMessageActivity.this,
                ManagerLeaveMessageActivity.class);
        intent.putExtra("tellerId", tellerId);
        startActivity(intent);
      }
    });
  }
}
