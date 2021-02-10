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

public class UserWithdrawActivity extends AppCompatActivity {

  EditText accountIdView;
  EditText withdrawView;
  TextView messageView;
  Intent intent;
  int customerId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_withdraw);

    // get customer id from intent
    intent = getIntent();
    customerId = intent.getIntExtra("customerId", -1);

    // when withdraw button is clicked
    Button withdraw = (Button) findViewById(R.id.button_withdraw);
    withdraw.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // get views
        accountIdView = (EditText) findViewById(R.id.account_id);
        withdrawView = (EditText) findViewById(R.id.withdraw_amount);
        messageView = (TextView) findViewById(R.id.message);

        // check if input is empty
        if (InputValidator.isEmpty(accountIdView) || InputValidator.isEmpty(withdrawView)) {
          messageView.setText(R.string.null_info);

        } else {
          // get the account id and withdraw amount
          int accountId = Integer.parseInt(accountIdView.getText().toString());
          BigDecimal withdraw = new BigDecimal(withdrawView.getText().toString());

          // create instance of the database
          Context context = getApplicationContext();
          DriverHelper db = new DriverHelper(context);
          // get account ids for user
          List<Integer> accountIds = db.getAccountIdsList(customerId);

          boolean hasAccess = false;

          // check if the account belongs to the user
          for (Integer i : accountIds) {
            if (accountId == i) {
              hasAccess = true;
            }
          }

          if (hasAccess) {
            // subtract balance
            BigDecimal balance = db.getBalance(accountId);
            balance = balance.subtract(withdraw);

            // get all account types to check
            EnumMap<AccountTypes, Integer> accountTypes = db.getAccountTypesEnumMap();
            int accountType = db.getAccountType(accountId);
            int balanceOwing = accountTypes.get(AccountTypes.BALANCEOWING);
            int savings = accountTypes.get(AccountTypes.SAVING);
            int chequing = accountTypes.get(AccountTypes.CHEQUING);
            int restrictedSavings = accountTypes.get(AccountTypes.RESTRICTEDSAVING);

            boolean isBalanceOwing = false;
            boolean convert = false;
            boolean restricted = false;
            if (accountType == balanceOwing) {
              // check if balance owing
              isBalanceOwing = true;

            } else if (accountType == savings && balance.compareTo(BigDecimal.valueOf(1000)) < 0) {
              // check if savings and new balance is less than 1000
              convert = true;

            } else if (accountType == restrictedSavings) {
              // check if restricted savings
              restricted = true;
            }

            // check if balance would be less than 0 and not balance owing
            if (balance.compareTo(BigDecimal.valueOf(0)) < 0 && !isBalanceOwing) {
              // can't withdraw
              messageView.setText(R.string.insufficient_funds);

            } else if (restricted) {
              // customer can't withdraw from restricted savings
              messageView.setText(R.string.restrict_saving);

            } else {
              // update balance
              boolean updated = db.updateAccountBalance(balance.setScale(2,
                  BigDecimal.ROUND_HALF_UP), accountId);

              // check if account needs to convert the type from savings to chequing
              if (updated && convert) {
                messageView.setText(R.string.saving_chequing);
                // update account type and send a message
                db.updateAccountType(chequing, accountId);
                db.insertMessage(customerId, getResources().getString(R.string.convert1) + " "
                    + accountId + " " + getResources().getString(R.string.convert2));

              } else if (updated) {
                // balance updated
                messageView.setText(R.string.success);

              } else {
                // can't be updated for some reason
                messageView.setText(R.string.fail_withdraw);
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
