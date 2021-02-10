package com.oritechs.BMS.project.banking.user;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.oritechs.BMS.R;
import com.oritechs.BMS.database.android.DriverHelper;
import com.oritechs.BMS.generics.AccountTypes;
import com.oritechs.BMS.project.banking.InputValidator;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;

public class UserDepositActivity extends AppCompatActivity {

  EditText accountIdView;
  EditText depositView;
  TextView messageView;
  Intent intent;
  int customerId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_deposit);

    // Get the user id from the main page
    intent = getIntent();
    customerId = intent.getIntExtra("customerId", -1);

    // when deposit button is clicked
    Button deposit = (Button) findViewById(R.id.button_deposit);
    deposit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // get views
        accountIdView = (EditText) findViewById(R.id.account_id);
        depositView = (EditText) findViewById(R.id.deposit_amount);
        messageView = (TextView) findViewById(R.id.message);

        // Check empty input
        if (InputValidator.isEmpty(accountIdView) || InputValidator.isEmpty(depositView)) {
          messageView.setText(R.string.null_info);

        } else {
          // get the account id
          int accountId = Integer.parseInt(accountIdView.getText().toString());
          BigDecimal deposit = new BigDecimal(depositView.getText().toString());

          // Create instance of database
          Context context = getApplicationContext();
          DriverHelper db = new DriverHelper(context);

          // get account ids for user
          List<Integer> accountIds = db.getAccountIdsList(customerId);

          boolean hasAccess = false;
          // check if account id belongs to the user
          for (Integer i : accountIds) {
            if (accountId == i) {
              hasAccess = true;
            }
          }

          if (hasAccess) {
            // get the balance of the account and add the deposit
            BigDecimal balance = db.getBalance(accountId);
            balance = balance.add(deposit);

            // get account types
            EnumMap<AccountTypes, Integer> accountTypes = db.getAccountTypesEnumMap();
            int balanceOwing = accountTypes.get(AccountTypes.BALANCEOWING);
            int accountType = db.getAccountType(accountId);

            // check if it's a balance owing account
            boolean isBalanceOwing = false;
            if (accountType == balanceOwing) {
              isBalanceOwing = true;
            }

            // if balance owing and balance is positive then don't update
            if (balance.compareTo(BigDecimal.valueOf(0)) > 0 && isBalanceOwing) {
              messageView.setText(R.string.positive_balance);

            } else {
              // update the balance
              boolean updated = db.updateAccountBalance(balance.setScale(2,
                  BigDecimal.ROUND_HALF_UP), accountId);

              if (updated) {
                // balance updated
                messageView.setText(R.string.success);

              } else {
                // smthn wrong in db so can't update
                messageView.setText(R.string.fail_deposit);
              }
            }

          } else {
            // account doesn't exist or customer can't access it
            messageView.setText(R.string.not_existed_bal_msg);
          }
        }

      }
    });
  }
}
