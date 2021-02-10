package com.oritechs.BMS.project.banking.admin;

import android.content.Context;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.oritechs.BMS.R;
import com.oritechs.BMS.database.android.DriverHelper;
import com.oritechs.BMS.generics.Roles;
import com.oritechs.BMS.project.banking.InputValidator;

import java.util.EnumMap;
import java.util.List;

public class AdminPromoteActivity extends AppCompatActivity {

  TextView messageView;
  EditText idView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_promote);

    // get admin id from intent
    final int adminId = getIntent().getIntExtra("adminId", -1);

    // when the promote button is clicked
    Button promote = (Button) findViewById(R.id.button_promote);
    promote.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // find the view of EditText and TextView
        idView = (EditText) findViewById(R.id.teller_id);
        messageView = (TextView) findViewById(R.id.message);

        // check if the field is empty
        if (InputValidator.isEmpty(idView)) {
          Toast toast = Toast.makeText(getApplicationContext(), R.string.null_info,
              Toast.LENGTH_SHORT);
          toast.show();

        } else {
          Context context = getApplicationContext();
          DriverHelper db = new DriverHelper(context);

          // get the id from the EditView
          int id = Integer.parseInt(idView.getText().toString());

          // check if the user exist
          List<Integer> userIds = db.getUsersIdsList();
          boolean exists = false;
          for (Integer i : userIds) {
            if (id == i) {
              exists = true;
            }
          }

          // if exists
          if (exists) {
            // check if it is teller by role
            // get the role id of TELLER
            EnumMap<Roles, Integer> roles = db.getRolesEnumMap();
            int roleId = roles.get(Roles.TELLER);
            int currentIdRole = db.getUserRole(id);

            // if it's a teller, promote it (update).
            if (roleId == currentIdRole) {
              db.updateUserRole(roles.get(Roles.ADMIN), id);
              // send message to that teller
              String message = "You are promoted to admin by " + db.getUserName(adminId);
              db.insertMessage(id, message);
              // if success, give msg ->
              messageView.setText(R.string.promote_success);

            } else {
              // else show msg fail->
              messageView.setText(R.string.promote_fail);

            }
          } else {
            // else, show there's no such user
            messageView.setText(R.string.checked_fail_msg);
          }
        }
      }
    });
  }
}
