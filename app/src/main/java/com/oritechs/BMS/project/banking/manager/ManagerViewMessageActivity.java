package com.oritechs.BMS.project.banking.manager;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.oritechs.BMS.database.android.DriverHelper;
import com.oritechs.BMS.R;

public class ManagerViewMessageActivity extends AppCompatActivity {

  TextView messageView;
  Intent intent;
  int messageId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_manager_view_message);

    // get the message to read from intent
    intent = getIntent();
    messageId = intent.getIntExtra("messageId", -1);

    // create a new instance of the database
    Context context = getApplicationContext();
    DriverHelper db = new DriverHelper(context);
    // get message
    String message = db.getSpecificMessage(messageId);

    // get text view and set the message
    messageView = (TextView) findViewById(R.id.message_view);
    messageView.setText(message);

    // message is read so put this into the intent so it gets updated
    Intent intentResult = new Intent();
    intentResult.putExtra("messageId", messageId);
    setResult(RESULT_OK, intentResult);
  }
}
