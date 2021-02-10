package com.oritechs.BMS.project.banking.admin;

import android.content.Context;

import java.math.BigDecimal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.oritechs.BMS.database.android.DriverHelper;
import com.oritechs.BMS.generics.Roles;
import com.oritechs.BMS.project.banking.InputValidator;
import com.oritechs.BMS.R;

import java.util.EnumMap;
import java.util.List;

public class AdminViewTotalBalanceActivity extends AppCompatActivity {

  TextView messageView;
  EditText idView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_view_total_balance);

    messageView = (TextView) findViewById(R.id.message);

    // when check button is clicked
    Button check = (Button) findViewById(R.id.button_check);
    check.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // variable of user ID
        idView = (EditText) findViewById(R.id.user_id);

        // check field is not null
        if (InputValidator.isEmpty(idView)) {
          Toast toast = Toast.makeText(getApplicationContext(),
              R.string.null_info,
              Toast.LENGTH_SHORT);
          toast.show();

        } else {
          Context context = getApplicationContext();
          DriverHelper db = new DriverHelper(context);

          // if there is such user (customer), show this (with balance at the end)
          // get a list of accounts of user
          int userId = Integer.parseInt(idView.getText().toString());

          // check if user id exists and it's a customer
          EnumMap<Roles, Integer> roles = db.getRolesEnumMap();
          List<Integer> userIds = db.getUsersIdsList();

          boolean exists = false;
          for (Integer i : userIds) {
            if (userId == i) {
              int userRoleId = db.getUserRole(userId);
              if (userRoleId == roles.get(Roles.CUSTOMER)) {
                exists = true;
              }
            }
          }

          // if customer exists
          if (exists) {
            // get customer's accounts
            List<Integer> accounts = db.getAccountIdsList(userId);

            if (accounts.size() == 0) {
              // no accounts
              messageView.setText(R.string.check_no_account);

            } else {
              // loop through accounts and add balance to total balance
              BigDecimal totalBalance = new BigDecimal(0);
              for (Integer i : accounts) {
                BigDecimal balance = db.getBalance(i);
                totalBalance = totalBalance.add(balance);
              }

              // set text to show total balance
              totalBalance = totalBalance.setScale(2, BigDecimal.ROUND_HALF_UP);
              CharSequence text =
                  getResources().getString(R.string.checked_total_msg) + totalBalance;
              messageView.setText(text);
            }
          } else {
            // else, show this
            messageView.setText(R.string.checked_fail_msg);
          }
        }
      }
    });
  }
}
