package com.oritechs.BMS.project.banking.admin;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.oritechs.BMS.database.android.DriverHelper;
import com.oritechs.BMS.R;

public class AdminViewMessageActivity extends AppCompatActivity {

  TextView messageView;
  Intent intent;
  int messageId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_view_message);

    // get message id to read
    intent = getIntent();
    messageId = intent.getIntExtra("messageId", -1);

    // create a new instance of the database
    Context context = getApplicationContext();
    DriverHelper db = new DriverHelper(context);

    // get message
    String message = db.getSpecificMessage(messageId);

    // show message in text view
    messageView = (TextView) findViewById(R.id.message_view);
    messageView.setText(message);

    // send result to show it's read
    Intent intentResult = new Intent();
    intentResult.putExtra("messageId", messageId);
    setResult(RESULT_OK, intentResult);
  }
}
