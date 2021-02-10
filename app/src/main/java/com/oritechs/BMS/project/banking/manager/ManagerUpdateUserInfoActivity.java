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
import com.oritechs.BMS.project.banking.InputValidator;
import com.oritechs.BMS.security.PasswordHelpers;
import com.oritechs.BMS.R;


public class ManagerUpdateUserInfoActivity extends AppCompatActivity {

  EditText nameView;
  EditText ageView;
  EditText addressView;
  EditText passwordView;
  TextView messageView;
  Intent intent;
  int customerId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_manager_update_user_info);

    // get user id from intent
    intent = getIntent();
    customerId = intent.getIntExtra("customerId", -1);

    messageView = (TextView) findViewById(R.id.message);

    // when update name button is clicked
    Button updateName = (Button) findViewById(R.id.button_update_name);
    updateName.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // get name view
        nameView = (EditText) findViewById(R.id.customer_new_name);

        // if empty then show message saying it shouldn't be
        if (InputValidator.isEmpty(nameView)) {
          messageView.setText(R.string.null_info);

        } else {
          // get new customer name
          String name = nameView.getText().toString();

          // create a new instance of the database and update the user name
          Context context = getApplicationContext();
          DriverHelper db = new DriverHelper(context);
          boolean updated = db.updateUserName(name, customerId);

          if (updated) {
            // show message saying it was updated
            messageView.setText(R.string.msg_name_updated);
            // update the intent so that welcome message gets changed in teller menu
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);

          } else {
            // smthn went wrong in db
            messageView.setText(R.string.msg_update_fail);
          }
        }

      }
    });

    // when update age button is clicked
    Button updateAge = (Button) findViewById(R.id.button_update_age);
    updateAge.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // get age view
        ageView = (EditText) findViewById(R.id.customer_new_age);

        // if empty then show message saying it shouldn't be
        if (InputValidator.isEmpty(ageView)) {
          messageView.setText(R.string.null_info);

        } else {
          // get age
          int age = Integer.parseInt(ageView.getText().toString());

          // create a new instance of the database and update name
          Context context = getApplicationContext();
          DriverHelper db = new DriverHelper(context);
          boolean updated = db.updateUserAge(age, customerId);

          if (updated) {
            // show message saying it was updated if it was updated
            messageView.setText(R.string.msg_age_updated);

          } else {
            // smthn went wrong in update database
            messageView.setText(R.string.msg_update_fail);
          }
        }
      }
    });

    // when update address button is clicked
    Button updateAddress = (Button) findViewById(R.id.button_update_address);
    updateAddress.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // get address view
        addressView = (EditText) findViewById(R.id.customer_new_address);

        // if empty then show message saying it shouldn't be
        if (InputValidator.isEmpty(addressView)) {
          messageView.setText(R.string.null_info);

        } else {
          // get address
          String address = addressView.getText().toString();

          // if address is too long
          if (address.length() > 100) {
            messageView.setText(R.string.address_long);

          } else {
            // create a new instance of the database amd update the user's address
            Context context = getApplicationContext();
            DriverHelper db = new DriverHelper(context);
            boolean updated = db.updateUserAddress(address, customerId);

            if (updated) {
              // show message saying it was updated
              messageView.setText(R.string.msg_address_updated);

              // set result in intent so that welcome message changes
              Intent intent = new Intent();
              setResult(RESULT_OK, intent);

            } else {
              // smthn went wrong in db
              messageView.setText(R.string.msg_update_fail);
            }
          }
        }
      }
    });

    // when update password button is clicked
    Button updatePassword = (Button) findViewById(R.id.button_update_password);
    updatePassword.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        passwordView = (EditText) findViewById(R.id.customer_new_password);

        // check if information is valid, if not,
        if (InputValidator.isEmpty(passwordView)) {
          messageView.setText(R.string.null_info);
        } else {
          String password = passwordView.getText().toString();

          // create a new instance of the database
          Context context = getApplicationContext();
          DriverHelper db = new DriverHelper(context);
          String hashPassword = PasswordHelpers.passwordHash(password);
          boolean updated = db.updateUserPassword(hashPassword, customerId);

          if (updated) {
            // show message
            messageView.setText(R.string.msg_password_updated);
          } else {
            messageView.setText(R.string.msg_update_fail);
          }
        }
      }
    });
  }
}
