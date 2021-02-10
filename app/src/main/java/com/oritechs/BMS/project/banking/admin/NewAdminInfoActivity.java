package com.oritechs.BMS.project.banking.admin;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.oritechs.BMS.database.android.DriverHelper;
import com.oritechs.BMS.generics.Roles;
import com.oritechs.BMS.project.banking.InputValidator;
import com.oritechs.BMS.R;

import java.util.EnumMap;

public class NewAdminInfoActivity extends AppCompatActivity {

  // set up DriverHelper Connection
  EditText nameView;
  EditText ageView;
  EditText addressView;
  EditText passwordView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_admin_info);

    // get the adminId in integer from admin menu activity.
    final int adminId = getIntent().getIntExtra("adminId", -1);

    // when create button is clicked
    Button create = (Button) findViewById(R.id.button_create_admin);
    create.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // get views in EditText
        nameView = (EditText) findViewById(R.id.admin_name);
        ageView = (EditText) findViewById(R.id.admin_age);
        addressView = (EditText) findViewById(R.id.admin_address);
        passwordView = (EditText) findViewById(R.id.admin_password);
        long longNewAdminId = -1;

        // check if all the information are there
        if (InputValidator.isEmpty(nameView) || InputValidator.isEmpty(ageView) ||
            InputValidator.isEmpty(addressView) || InputValidator.isEmpty(passwordView)) {
          // if not, pop msg: please fill in all the info
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

          // get the role id of ADMIN
          EnumMap<Roles, Integer> roles = db.getRolesEnumMap();
          int roleId = roles.get(Roles.ADMIN);

          // insert new admin to db and get the new admin ID
          longNewAdminId = db.insertNewUser(name, age, address, roleId, password);
          int newAdminId = (int) longNewAdminId;

          // if successful,
          if (longNewAdminId != -1) {
            // pop this message
            CharSequence text = getString(R.string.admin_created) + newAdminId;
            Toast toast = Toast.makeText(getApplicationContext(),
                text, Toast.LENGTH_LONG);
            toast.show();

            // insert message to the new admin
            String message = "The admin is created by " + db.getUserName(adminId);
            db.insertMessage(newAdminId, message);

          } else {
            // else pop this msg
            Toast toast = Toast.makeText(getApplicationContext(), R.string.admin_create_failed,
                Toast.LENGTH_SHORT);
            toast.show();
          }

        }
      }
    });
  }

}
