package com.oritechs.BMS.project.banking.admin;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.oritechs.BMS.R;
import com.oritechs.BMS.database.android.DriverHelper;
import com.oritechs.BMS.project.banking.MainActivity;


public class AdminMenuActivity extends AppCompatActivity {

  int adminId = -1;
  TextView messageView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_menu);

    if (adminId == -1) {
      // get the adminId in integer.
      adminId = getIntent().getIntExtra("adminId", adminId);

      // get admin name
      Context context = getApplicationContext();
      DriverHelper db = new DriverHelper(context);
      String name = db.getUserName(adminId);

      // set welcome message
      messageView = (TextView) findViewById(R.id.admin_welcome);
      messageView.setText("Welcome, " + name + ".");
    }

    // action when create new admin button is clicked (go to create admin page)
    Button newAdmin = (Button) findViewById(R.id.button_admin_new_ad);
    newAdmin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // add admin id to intent
        Intent intentNewAdmin = new Intent(AdminMenuActivity.this, NewAdminInfoActivity.class);
        intentNewAdmin.putExtra("adminId", adminId);
        startActivity(intentNewAdmin);
      }
    });

    // go to create teller page
    Button newTeller = (Button) findViewById(R.id.button_admin_new_teller);
    newTeller.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // add admin id to intent
        Intent intentNewTeller = new Intent(AdminMenuActivity.this, NewManagerActivity.class);
        intentNewTeller.putExtra("adminId", adminId);
        startActivity(intentNewTeller);
      }
    });

    // go to admin list
    Button adminList = (Button) findViewById(R.id.button_admin_list_admin);
    adminList.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(AdminMenuActivity.this, AdminListActivity.class));
      }
    });

    // go to teller list page
    Button tellerList = (Button) findViewById(R.id.button_admin_list_teller);
    tellerList.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(AdminMenuActivity.this, ManagerListActivity.class));
      }
    });

    // go to customer list page
    Button customerList = (Button) findViewById(R.id.button_admin_list_cus);
    customerList.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(AdminMenuActivity.this, UserListActivity.class));
      }
    });

    // go to view total balance of customer page
    Button viewTotalBalance = (Button) findViewById(R.id.button_admin_view_total_bal);
    viewTotalBalance.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(AdminMenuActivity.this, AdminViewTotalBalanceActivity.class));
      }
    });

    // go to view total balance of bank page
    Button viewTotalBalanceBank = (Button) findViewById(R.id.button_admin_view_total_money);
    viewTotalBalanceBank.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(AdminMenuActivity.this,
            AdminBankTotalMoneyActivity.class));
      }
    });

    // go to promote page
    Button promote = (Button) findViewById(R.id.button_admin_promote);
    promote.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // add admin id to intent
        Intent intentPromote = new Intent(AdminMenuActivity.this,
            AdminPromoteActivity.class);
        intentPromote.putExtra("adminId", adminId);
        startActivity(intentPromote);
      }
    });

    // go to message menu page
    Button messageMenu = (Button) findViewById(R.id.button_message);
    messageMenu.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // add admin id to intent
        Intent intentMessage = new Intent(AdminMenuActivity.this, AdminMainMessageActivity.class);
        intentMessage.putExtra("adminId", adminId);
        startActivity(intentMessage);
      }
    });

    // log out is clicked
    Button logout = (Button) findViewById(R.id.button_admin_logout);
    logout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        adminId = -1;
        // do log out and back to previous page
        startActivity(new Intent(AdminMenuActivity.this, MainActivity.class));
      }
    });
  }

  // if back key is pressed
  @Override
  public void onBackPressed() {
    // log out admin
    adminId = -1;
    startActivity(new Intent(AdminMenuActivity.this, MainActivity.class));
  }
}


