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

public class ManagerDepositActivity extends AppCompatActivity {

  EditText accountIdView;
  EditText depositView;
  TextView messageView;
  Intent intent;
  int customerId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_manager_deposit);

    // get customer id
    intent = getIntent();
    customerId = intent.getIntExtra("customerId", -1);

    // when button is pressed
    Button deposit = (Button) findViewById(R.id.button_deposit);
    deposit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // get views
        accountIdView = (EditText) findViewById(R.id.account_id);
        depositView = (EditText) findViewById(R.id.deposit_amount);
        messageView = (TextView) findViewById(R.id.message);

        // check if input is empty
        if (InputValidator.isEmpty(accountIdView) || InputValidator.isEmpty(depositView)) {
          // show message to not leave blank
          messageView.setText(R.string.null_info);

        } else {
          // else get the account id and deposit amount
          int accountId = Integer.parseInt(accountIdView.getText().toString());
          BigDecimal deposit = new BigDecimal(depositView.getText().toString());

          // create a new instance of the database
          Context context = getApplicationContext();
          DriverHelper db = new DriverHelper(context);

          // get account ids for user
          List<Integer> accountIds = db.getAccountIdsList(customerId);

          boolean hasAccess = false;

          // check if accountid passed in is inside user's list of accounts
          for (Integer i : accountIds) {
            if (accountId == i) {
              hasAccess = true;
            }
          }

          if (hasAccess) {
            // customer has access to account so add balance
            BigDecimal balance = db.getBalance(accountId);
            balance = balance.add(deposit);

            // check if balance owing account
            EnumMap<AccountTypes, Integer> accountTypes = db.getAccountTypesEnumMap();
            int balanceOwing = accountTypes.get(AccountTypes.BALANCEOWING);
            int accountType = db.getAccountType(accountId);
            boolean isBalanceOwing = false;
            if (accountType == balanceOwing) {
              isBalanceOwing = true;
            }

            // check if overall balance is positive for a balance owing account
            if (balance.compareTo(BigDecimal.valueOf(0)) > 0 && isBalanceOwing) {
              messageView.setText(R.string.positive_balance);

            } else {
              // update account balance
              boolean updated = db.updateAccountBalance(balance.setScale(2,
                  BigDecimal.ROUND_HALF_UP), accountId);

              if (updated) {
                // successful deposit
                messageView.setText(R.string.success);

              } else {
                // something wrong with deposit (to do with database)
                messageView.setText(R.string.fail_deposit);
              }
            }

          } else {
            // customer doesn't have access to account or doesn't exist
            messageView.setText(R.string.not_existed_bal_msg);

          }
        }
      }
    });
  }
}
