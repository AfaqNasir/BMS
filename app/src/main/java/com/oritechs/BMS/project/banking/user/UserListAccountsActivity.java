package com.oritechs.BMS.project.banking.user;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.oritechs.BMS.R;
import com.oritechs.BMS.database.android.DriverHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserListAccountsActivity extends AppCompatActivity {

  TextView messageView;
  ListView accountsView;
  Intent intent;
  int customerId;

  // tutorial on how to create custom list view cited from:
  // http://saigeethamn.blogspot.ca/2010/04/custom-listview-android-developer.html
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_list_accounts);

    // get customer id from intent
    intent = getIntent();
    customerId = intent.getIntExtra("customerId", -1);

    // create a new instance of the database
    Context context = getApplicationContext();
    DriverHelper db = new DriverHelper(context);

    // set header for customer
    String customerName = db.getUserName(customerId);
    messageView = (TextView) findViewById(R.id.message);
    messageView.setText(getResources().getString(R.string.account_owner) + " " + customerName);

    // get account ids for user
    List<Integer> accountIds = db.getAccountIdsList(customerId);

    ArrayList<HashMap<String, String>> accountsInfo = new ArrayList<>();

    for (Integer i : accountIds) {
      HashMap<String, String> newAccount = new HashMap<>();
      // get account info and convert to string
      int accountTypeId = db.getAccountType(i);
      String accountId = i.toString();
      String accountType = db.getAccountTypeName(accountTypeId);
      String accountName = db.getAccountName(i);
      BigDecimal accountBalanceNumber = db.getBalance(i);
      accountBalanceNumber = accountBalanceNumber.setScale(2, BigDecimal.ROUND_HALF_UP);
      String accountBalance = accountBalanceNumber.toPlainString();

      // add info to a hashmap
      newAccount.put("id", accountId);
      newAccount.put("type", accountType);
      newAccount.put("name", accountName);
      newAccount.put("balance", "$" + accountBalance);

      // add hashmap to arraylist
      accountsInfo.add(newAccount);
    }

    // represents info to show on list view and their textviews
    String[] rows = {"type", "name", "balance"};
    int[] textViews = {R.id.account_type, R.id.account_name, R.id.account_balance};

    // add account info to an adapter
    SimpleAdapter adapter = new SimpleAdapter(this, accountsInfo,
        R.layout.content_account_list_view,
        rows, textViews);

    // set the adapter to the list view
    accountsView = (ListView) findViewById(R.id.accounts_list_view);
    accountsView.setAdapter(adapter);

    // when user clicks on an account
    accountsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object item = accountsView.getItemAtPosition(position);
        // get the account id
        HashMap<String, String> itemInfo = (HashMap<String, String>) item;
        String accountId = itemInfo.get("id");

        // show what the account id is
        Toast toast = Toast.makeText(getApplicationContext(),
            getResources().getString(R.string.account_id) + " " + accountId + ".",
            Toast.LENGTH_SHORT);
        toast.show();
      }
    });

  }
}
