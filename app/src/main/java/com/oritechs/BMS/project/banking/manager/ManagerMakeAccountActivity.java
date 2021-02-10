package com.oritechs.BMS.project.banking.manager;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.oritechs.BMS.database.android.DriverHelper;
import com.oritechs.BMS.generics.AccountTypes;
import com.oritechs.BMS.project.banking.InputValidator;
import com.oritechs.BMS.R;

import java.math.BigDecimal;
import java.util.EnumMap;

public class ManagerMakeAccountActivity extends AppCompatActivity implements
    AdapterView.OnItemSelectedListener {

  // the type of the account where user selected
  String accountType = null;
  EditText nameView;
  EditText balanceView;
  TextView messageView;
  Intent intent;
  int customerId;

  // (spinner) cite from Android Developers:
  // https://developer.android.com/guide/topics/ui/controls/spinner.html
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_manager_make_account);

    // get the spinner for the account types
    Spinner spin = (Spinner) findViewById(R.id.account_types);
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.types_array, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    // apply the adapter to the spinner
    spin.setAdapter(adapter);
    spin.setOnItemSelectedListener(this);

    // get the message view
    messageView = (TextView) findViewById(R.id.message);

    // get the customer's id
    intent = getIntent();
    customerId = intent.getIntExtra("customerId", -1);

    // when create button is clicked
    Button create = (Button) findViewById(R.id.button_create);
    create.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // get the views
        nameView = (EditText) findViewById(R.id.account_name);
        balanceView = (EditText) findViewById(R.id.account_balance);

        // check if fields are empty
        if (InputValidator.isEmpty(nameView) || InputValidator.isEmpty(balanceView)) {
          // show message to not leave it blank
          messageView.setText(R.string.null_info);

        } else {
          // get account info
          String name = nameView.getText().toString();
          BigDecimal balance = new BigDecimal(balanceView.getText().toString());
          String type = accountType.replaceAll("\\s+", "");

          // create a new instance of the database
          Context context = getApplicationContext();
          DriverHelper db = new DriverHelper(context);

          // get account types
          EnumMap<AccountTypes, Integer> accountTypes = db.getAccountTypesEnumMap();
          int accountTypeId = accountTypes.get(AccountTypes.valueOf(type));
          // get balance owing and savings type ids
          int balanceOwing = accountTypes.get(AccountTypes.BALANCEOWING);
          int savings = accountTypes.get(AccountTypes.SAVING);

          boolean invalidBalance = false;
          if (accountTypeId == balanceOwing) {
            // if it's a balance owing account the balance will be negative
            balance = balance.negate();

          } else if (accountTypeId == savings && balance.compareTo(BigDecimal.valueOf(1000)) < 0) {
            // if it's a savings account the balance should be greater than 1000
            invalidBalance = true;
          }

          if (invalidBalance) {
            // not a high enough balance so show message
            messageView.setText(R.string.savings_balance);

          } else {
            // add the account to the database
            balance = balance.setScale(2, BigDecimal.ROUND_HALF_UP);
            long inserted = db.insertNewAccount(name, balance, accountTypeId);

            // if successfully inserted
            if (inserted != -1) {
              // add the account to the current customer
              long connected = db.insertUserAccount(customerId, (int) inserted);

              // show that account is created and new account id number
              Toast toast = Toast.makeText(getApplicationContext(),
                  R.string.account_created,
                  Toast.LENGTH_SHORT);
              toast.show();
              messageView
                  .setText(getResources().getString(R.string.new_account_id) + " " + inserted);

            } else {
              // for some reason account cannot be created
              messageView.setText(R.string.account_create_failed);
            }
          }
        }
      }
    });
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    // get item selected with spinner
    accountType = parent.getItemAtPosition(position).toString();
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
  }
}
