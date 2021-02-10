package com.oritechs.BMS.project.banking.manager;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.oritechs.BMS.database.android.DriverHelper;
import com.oritechs.BMS.R;

import java.math.BigDecimal;
import java.util.List;

public class ManagerViewTotalBalanceActivity extends AppCompatActivity {

  TextView messageView;
  Intent intent;
  int customerId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_manager_view_total_balance);

    // get customer id from intent
    intent = getIntent();
    customerId = intent.getIntExtra("customerId", -1);

    // create a new instance of the database
    Context context = getApplicationContext();
    DriverHelper db = new DriverHelper(context);
    // get account ids for user
    List<Integer> accountIds = db.getAccountIdsList(customerId);

    messageView = (TextView) findViewById(R.id.total_balance);

    // check if customer has no accounts
    if (accountIds.size() == 0) {
      messageView.setText(R.string.check_no_account);
    } else {
      // get the total balance for the customer and show total balance message
      BigDecimal totalBalance = new BigDecimal(0);
      for (Integer i : accountIds) {
        BigDecimal balance = db.getBalance(i);
        totalBalance = totalBalance.add(balance);
      }

      // get view and set the text
      messageView.setText("$" + totalBalance.setScale(2, BigDecimal.ROUND_HALF_UP));
    }
  }
}
