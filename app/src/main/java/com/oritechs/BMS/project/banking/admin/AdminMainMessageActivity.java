package com.oritechs.BMS.project.banking.admin;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.oritechs.BMS.R;


public class AdminMainMessageActivity extends AppCompatActivity {

  int adminId = -1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_main_message);

    // get the adminId in integer.
    adminId = getIntent().getIntExtra("adminId", adminId);

    // when view your message button is clicked
    Button ownMessage = (Button) findViewById(R.id.button_admin_message);
    ownMessage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // add admin id to intent
        Intent intent = new Intent(AdminMainMessageActivity.this, AdminMessageActivity.class);
        intent.putExtra("adminId", adminId);
        startActivity(intent);
      }
    });

    // when view any message button is clicked
    Button anyMessage = (Button) findViewById(R.id.button_view_any_message);
    anyMessage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // add admin id to intent
        Intent intent = new Intent(AdminMainMessageActivity.this, AdminAnyMessageActivity.class);
        intent.putExtra("adminId", adminId);
        startActivity(intent);
      }
    });

    // when leave message button is clicked
    Button leaveMessage = (Button) findViewById(R.id.button_leave_message);
    leaveMessage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // add admin id to intent
        Intent intent = new Intent(AdminMainMessageActivity.this, AdminLeaveMessageActivity.class);
        intent.putExtra("adminId", adminId);
        startActivity(intent);
      }
    });
  }
}
