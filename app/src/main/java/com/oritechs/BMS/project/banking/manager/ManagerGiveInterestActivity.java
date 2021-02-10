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
import com.oritechs.BMS.generics.AccountTypes;
import com.oritechs.BMS.project.banking.InputValidator;
import com.oritechs.BMS.R;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;


public class ManagerGiveInterestActivity extends AppCompatActivity {

  EditText accountIdView;
  TextView messageView;
  Intent intent;
  int customerId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_manager_give_interest);

    // get customer id
    intent = getIntent();
    customerId = intent.getIntExtra("customerId", -1);

    // when button is pressed
    Button giveInterest = (Button) findViewById(R.id.button_give_interest);
    giveInterest.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // get views
        accountIdView = (EditText) findViewById(R.id.interest_account_id);
        messageView = (TextView) findViewById(R.id.message);

        // check if input is empty
        if (InputValidator.isEmpty(accountIdView)) {
          // tell them to not leave it empty
          messageView.setText(R.string.null_info);

        } else {
          // get the account id
          int accountId = Integer.parseInt(accountIdView.getText().toString());

          // create a new instance of the database
          Context context = getApplicationContext();
          DriverHelper db = new DriverHelper(context);

          // get account ids for user
          List<Integer> accountIds = db.getAccountIdsList(customerId);

          boolean hasAccess = false;

          // check if accountid passed in is inside customer's list of accounts
          for (Integer i : accountIds) {
            if (accountId == i) {
              hasAccess = true;
            }
          }

          if (hasAccess) {
            // get interest rate for specific account
            BigDecimal balance = db.getBalance(accountId);
            int accountType = db.getAccountType(accountId);
            BigDecimal interestRate = db.getInterestRate(accountType);
            BigDecimal interest = balance.multiply(interestRate);

            // check if balance owing account
            EnumMap<AccountTypes, Integer> accountTypes = db.getAccountTypesEnumMap();
            int balanceOwing = accountTypes.get(AccountTypes.BALANCEOWING);

            if (accountType == balanceOwing) {
              // subtract interest if it's a balance owing account
              balance = balance.subtract(interest);

            } else {
              // otherwise add interest normally
              balance = balance.add(interest);
            }

            // update the balance
            boolean updated = db.updateAccountBalance(balance.setScale(2,
                BigDecimal.ROUND_HALF_UP), accountId);

            if (updated) {
              // successful deposit
              messageView.setText(R.string.success);

            } else {
              // unsuccessful deposit (to do with database)
              messageView.setText(R.string.fail_interest);
            }

          } else {
            // account doesn't exist or customer doesn't have access to it
            messageView.setText(R.string.not_existed_bal_msg);
          }
        }
      }
    });
  }
}
