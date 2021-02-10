package com.oritechs.BMS.project.banking.admin;

import android.content.Context;
import java.math.BigDecimal;

import android.os.Bundle;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.oritechs.BMS.database.android.DriverHelper;
import com.oritechs.BMS.generics.Roles;
import com.oritechs.BMS.R;

import java.util.EnumMap;
import java.util.List;

public class AdminBankTotalMoneyActivity extends AppCompatActivity {

  TextView messageView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_bank_total_money);

    Context context = getApplicationContext();
    DriverHelper db = new DriverHelper(context);

    // find TextView of message
    messageView = (TextView) findViewById(R.id.message);

    // initialise total balance of bank
    BigDecimal totalBalance = new BigDecimal(0);

    // get all the users and roles
    List<Integer> userIds = db.getUsersIdsList();
    EnumMap<Roles, Integer> roles = db.getRolesEnumMap();
    int userRoleId;

    // go through all the users
    for (Integer userId : userIds) {
      userRoleId = db.getUserRole(userId);

      // check if user is a customer
      if (userRoleId == roles.get(Roles.CUSTOMER)) {
        // get the customer's accounts
        List<Integer> accounts = db.getAccountIdsList(userId);

        // loop through accounts and add their balance to the total balance
        for (Integer i : accounts) {
          BigDecimal balance = db.getBalance(i);
          totalBalance = totalBalance.add(balance);
        }
      }
    }

    // display total balance of the bank
    totalBalance = totalBalance.setScale(2, BigDecimal.ROUND_HALF_UP);
    CharSequence text = getResources().getString(R.string.check_total_money_in_bank) + totalBalance;
    messageView.setText(text);
  }
}
