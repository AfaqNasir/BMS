package com.oritechs.BMS.project.banking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.oritechs.BMS.database.android.DriverHelper;
import com.oritechs.BMS.security.PasswordHelpers;
import com.oritechs.BMS.R;

import java.util.List;

public class ForgetPasswordActivity extends AppCompatActivity {

  EditText idView;
  EditText nameView;
  EditText addressView;
  EditText passwordView;
  EditText confirmPasswordView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_forget_password);

    // when the update button is clicked
    Button update = (Button) findViewById(R.id.button_update_password);
    update.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // find views by id
        idView = (EditText) findViewById(R.id.userid);
        nameView = (EditText) findViewById(R.id.name);
        addressView = (EditText) findViewById(R.id.address);
        passwordView = (EditText) findViewById(R.id.new_password);
        confirmPasswordView = (EditText) findViewById(R.id.confirm_password);

        // check if there are empty fields, if yes
        if (InputValidator.isEmpty(idView) || InputValidator.isEmpty(passwordView) ||
            InputValidator.isEmpty(addressView) || InputValidator.isEmpty(nameView) ||
            InputValidator.isEmpty(confirmPasswordView)) {
          // pop message
          Toast toast = Toast.makeText(getApplicationContext(), R.string.null_info,
              Toast.LENGTH_SHORT);
          toast.show();

        } else {
          Context context = getApplicationContext();
          DriverHelper db = new DriverHelper(context);

          // else, get the value of those input
          int id = Integer.parseInt(idView.getText().toString());
          String name = nameView.getText().toString();
          String address = addressView.getText().toString();
          String password = passwordView.getText().toString();

          // check if the id exists in the database
          List<Integer> userIds = db.getUsersIdsList();
          boolean exists = false;
          for (Integer i : userIds) {
            if (id == i) {
              exists = true;
            }
          }

          // if user exists
          if (exists) {
            // check the information whether they match the user details
            String dbName = db.getUserName(id);
            String dbAddress = db.getUserAddress(id);
            // if the info match
            if (dbName.equals(name) && dbAddress.equals(address)) {
              // check if the password and confirm password are match
              // if match
              if (passwordView.getText().toString()
                  .equals(confirmPasswordView.getText().toString())) {
                // update the password

                String hashPassword = PasswordHelpers.passwordHash(password);
                boolean updated = db.updateUserPassword(hashPassword, id);

                if (updated) {
                  // pop message and return to main login page
                  Toast toast = Toast.makeText(getApplicationContext(),
                      R.string.msg_password_updated, Toast.LENGTH_SHORT);
                  toast.show();
                  startActivity(new Intent(ForgetPasswordActivity.this,
                      MainActivity.class));
                } else {
                  Toast toast = Toast.makeText(getApplicationContext(),
                      R.string.msg_update_fail, Toast.LENGTH_SHORT);
                  toast.show();
                }
              } else {
                // if password does not match
                // pop message
                Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.password_not_match, Toast.LENGTH_SHORT);
                toast.show();
              }

            } else {
              // else if the info doesn't match
              Toast toast = Toast.makeText(getApplicationContext(),
                  R.string.not_match_into, Toast.LENGTH_SHORT);
              toast.show();
            }
          } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                R.string.invalid_userid, Toast.LENGTH_SHORT);
            toast.show();
          }
        }
      }
    });
  }

  // if back key is pressed
  @Override
  public void onBackPressed() {
    // go back to main Activity
    startActivity(new Intent(ForgetPasswordActivity.this, MainActivity.class));
  }
}
