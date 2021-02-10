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

public class ManagerWithdrawActivity extends AppCompatActivity {

  EditText accountIdView;
  EditText withdrawView;
  TextView messageView;
  Intent intent;
  int customerId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_manager_withdraw);

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

        // check if any fields are empty and show message showing it shouldn't be
        if (InputValidator.isEmpty(accountIdView) || InputValidator.isEmpty(withdrawView)) {
          messageView.setText(R.string.null_info);

        } else {
          // get the account id and withdraw amount
          int accountId = Integer.parseInt(accountIdView.getText().toString());
          BigDecimal withdraw = new BigDecimal(withdrawView.getText().toString());

          // create a new instance of the database
          Context context = getApplicationContext();
          DriverHelper db = new DriverHelper(context);
          // get account ids for user
          List<Integer> accountIds = db.getAccountIdsList(customerId);

          boolean hasAccess = false;

          // check if accountid passed in is inside user's accounts
          for (Integer i : accountIds) {
            if (accountId == i) {
              hasAccess = true;
            }
          }

          // if customer has account
          if (hasAccess) {
            // subtract balance
            BigDecimal balance = db.getBalance(accountId);
            balance = balance.subtract(withdraw);

            // get all the account types necessary
            EnumMap<AccountTypes, Integer> accountTypes = db.getAccountTypesEnumMap();
            int accountType = db.getAccountType(accountId);
            int balanceOwing = accountTypes.get(AccountTypes.BALANCEOWING);
            int savings = accountTypes.get(AccountTypes.SAVING);
            int chequing = accountTypes.get(AccountTypes.CHEQUING);

            boolean isBalanceOwing = false;
            boolean convert = false;
            if (accountType == balanceOwing) {
              // check if balance owing
              isBalanceOwing = true;

            } else if (accountType == savings && balance.compareTo(BigDecimal.valueOf(1000)) < 0) {
              // check if account is savings and balance is now less than 1000
              convert = true;
            }

            // check if balance would be less than 0 and it's not a balance owing account
            if (balance.compareTo(BigDecimal.valueOf(0)) < 0 && !isBalanceOwing) {
              // not enough funds
              messageView.setText(R.string.insufficient_funds);

            } else {
              // update balance
              boolean updated = db.updateAccountBalance(balance.setScale(2,
                  BigDecimal.ROUND_HALF_UP), accountId);

              // check if savings needs to be converted to a chequing
              if (updated && convert) {
                messageView.setText(R.string.saving_chequing);

                // update account type and send a message
                db.updateAccountType(chequing, accountId);
                db.insertMessage(customerId, getResources().getString(R.string.convert1) + " "
                    + accountId + " " + getResources().getString(R.string.convert2));

              } else if (updated) {
                // balance is updated
                messageView.setText(R.string.success);

              } else {
                // couldn't update due to db
                messageView.setText(R.string.fail_withdraw);
              }
            }

          } else {
            // customer doesn't have access to account
            messageView.setText(R.string.not_existed_bal_msg);
          }
        }
      }
    });
  }
}
