package com.oritechs.BMS.project.banking.admin;

import android.content.Context;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.oritechs.BMS.R;
import com.oritechs.BMS.database.android.DriverHelper;
import com.oritechs.BMS.generics.Roles;
import com.oritechs.BMS.project.banking.InputValidator;

import java.util.EnumMap;

public class NewManagerActivity extends AppCompatActivity {

  EditText nameView;
  EditText ageView;
  EditText addressView;
  EditText passwordView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_manager);

    // get the adminId in integer from admin menu activity.
    final int adminId = getIntent().getIntExtra("adminId", -1);

    // when create button is clicked
    Button create = (Button) findViewById(R.id.button_create_teller);
    create.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // get the values in EditText
        nameView = (EditText) findViewById(R.id.teller_name);
        ageView = (EditText) findViewById(R.id.teller_age);
        addressView = (EditText) findViewById(R.id.teller_address);
        passwordView = (EditText) findViewById(R.id.teller_password);
        long longNewTellerId = -1;

        // check if all the information are there
        if (InputValidator.isEmpty(nameView) || InputValidator.isEmpty(ageView) ||
            InputValidator.isEmpty(addressView) || InputValidator.isEmpty(passwordView)) {
          Toast toast = Toast.makeText(getApplicationContext(),
              R.string.null_info,
              Toast.LENGTH_SHORT);
          toast.show();

        } else {
          Context context = getApplicationContext();
          DriverHelper db = new DriverHelper(context);

          // get the value of all the info
          String name = nameView.getText().toString();
          int age = Integer.parseInt(ageView.getText().toString());
          String address = addressView.getText().toString();
          String password = passwordView.getText().toString();

          // get the role id of teller
          EnumMap<Roles, Integer> roles = db.getRolesEnumMap();
          int roleId = roles.get(Roles.TELLER);

          // insert new teller to db
          longNewTellerId = db.insertNewUser(name, age, address, roleId, password);
          int newTellerId = (int) longNewTellerId;

          // if successful,
          if (longNewTellerId != -1) {
            // pop this msg
            CharSequence text = getString(R.string.manager_created) + newTellerId;
            Toast toast = Toast.makeText(getApplicationContext(),
                text, Toast.LENGTH_LONG);
            toast.show();

            // insert message to the new teller
            String message = "The teller is created by " + db.getUserName(adminId);
            db.insertMessage(newTellerId, message);

          } else {
            // else pop this msg
            Toast toast = Toast.makeText(getApplicationContext(),
                R.string.manager_create_failed,
                Toast.LENGTH_SHORT);
            toast.show();
          }
        }
      }
    });
  }
}
