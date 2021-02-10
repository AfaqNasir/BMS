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
import com.oritechs.BMS.generics.AccountTypes;
import com.oritechs.BMS.generics.Roles;
import com.oritechs.BMS.project.banking.admin.AdminMenuActivity;
import com.oritechs.BMS.project.banking.user.UserMenuActivity;
import com.oritechs.BMS.security.PasswordHelpers;
import com.oritechs.BMS.R;
import com.oritechs.BMS.project.banking.manager.ManagerMenuActivity;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  EditText userIdView;
  EditText passwordView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Context context = getApplicationContext();
    final DriverHelper db = new DriverHelper(context);

    // get list of roles, account types, users and check if empty
    List<Integer> roles = db.getRolesList();
    List<Integer> accountTypes = db.getAccountTypesIdsList();
    List<Integer> users = db.getUsersIdsList();

    // if roles are empty then add roles
    if (roles.size() == 0) {
      db.insertRole(Roles.ADMIN.toString());
      db.insertRole(Roles.TELLER.toString());
      db.insertRole(Roles.CUSTOMER.toString());
    }

    // if account types are empty then add account types
    if (accountTypes.size() == 0) {
      db.insertAccountType(AccountTypes.CHEQUING.toString(), new BigDecimal(0.15));
      db.insertAccountType(AccountTypes.SAVING.toString(), new BigDecimal(0.2));
      db.insertAccountType(AccountTypes.CURRENTACCOUNT.toString(), new BigDecimal(0.25));
      db.insertAccountType(AccountTypes.RESTRICTEDSAVING.toString(), new BigDecimal(0.3));
      db.insertAccountType(AccountTypes.BALANCEOWING.toString(), new BigDecimal(0.35));
    }

    // if there are no users then add an admin
    if (users.size() == 0) {
      db.insertNewUser("Admin", 20, "UTSC", 1, "admin");
    }

    // action when log in button is clicked
    Button button = (Button) findViewById(R.id.button_login);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent;
        // get the userID and password
        userIdView = (EditText) findViewById(R.id.login_id);
        passwordView = (EditText) findViewById(R.id.login_pw);

        // if the userID or password is null
        if (InputValidator.isEmpty(userIdView) || InputValidator.isEmpty(passwordView)) {
          // pop up message to tell user to enter all fields
          Toast toast = Toast.makeText(getApplicationContext(), R.string.null_field,
              Toast.LENGTH_SHORT);
          toast.show();

        } else {
          // else, check role of user by ID and do authenticate
          // get integer userId
          int userId = Integer.parseInt(userIdView.getText().toString());
          String password = passwordView.getText().toString();

          // check if user id exists
          List<Integer> userIds = db.getUsersIdsList();
          boolean exists = false;
          for (Integer i : userIds) {
            if (userId == i) {
              exists = true;
            }
          }

          // try authenticate user
          boolean auth = false;
          if (exists) {
            auth = PasswordHelpers.comparePassword(db.getPassword(userId), password);
          }

          // if authenticated
          if (auth) {
            // get user details (role)
            int userRoleId = db.getUserRole(userId);

            EnumMap<Roles, Integer> roles = db.getRolesEnumMap();

            if (userRoleId == -1) {
            } else if (userRoleId == roles.get(Roles.ADMIN)) {
              // user is an admin so go to admin terminal
              intent = new Intent(MainActivity.this, AdminMenuActivity.class);
              intent.putExtra("adminId", userId);
              startActivity(intent);

            } else if (userRoleId == roles.get(Roles.TELLER)) {
              // user is a teller so go to teller terminal
              intent = new Intent(MainActivity.this, ManagerMenuActivity.class);
              intent.putExtra("tellerId", userId);
              startActivity(intent);

            } else if (userRoleId == roles.get(Roles.CUSTOMER)) {
              // user is a customer so go to atm
              intent = new Intent(MainActivity.this, UserMenuActivity.class);
              intent.putExtra("customerId", userId);
              startActivity(intent);
            }

          } else if (!exists) {
            // user id doesn't exist
            Toast toast = Toast.makeText(getApplicationContext(),
                R.string.invalid_userid, Toast.LENGTH_SHORT);
            toast.show();

          } else {
            // password is wrong
            Toast toast = Toast.makeText(getApplicationContext(),
                R.string.incorrect_password, Toast.LENGTH_SHORT);
            toast.show();
          }
        }
      }
    });

    // when forget password is clicked
    Button forgetPassword = (Button) findViewById(R.id.button_forget_password);
    forgetPassword.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(MainActivity.this, ForgetPasswordActivity.class));
      }
    });
  }

  // if back key is pressed
  @Override
  public void onBackPressed() {
    // leave the app
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_HOME);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }

}
