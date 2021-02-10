package com.oritechs.BMS.project.banking.admin;

import android.content.Context;
import android.graphics.Color;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.oritechs.BMS.R;
import com.oritechs.BMS.database.android.DriverHelper;
import com.oritechs.BMS.generics.Roles;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class AdminListActivity extends AppCompatActivity {

  List<Integer> adminIds = new ArrayList<>();
  List<String> adminNames = new ArrayList<>();


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_list);

    Context context = getApplicationContext();
    DriverHelper db = new DriverHelper(context);

    // get userId list and the role id of ADMIN
    List<Integer> userIds = db.getUsersIdsList();
    EnumMap<Roles, Integer> roles = db.getRolesEnumMap();
    int roleId = roles.get(Roles.ADMIN);

    // check roles for each user
    for (Integer userId : userIds) {
      int currentUserRole = db.getUserRole(userId);

      // if the user is ADMIN
      if (roleId == currentUserRole) {
        // add user id
        adminIds.add(userId);
        // get the name of the admin and add to the list
        String adminName = db.getUserName(userId);
        adminNames.add(adminName);
      }
    }

    // create table layout
    init();
  }

  public void init() {
    TableLayout tableLayout = (TableLayout) findViewById(R.id.main_table);
    TableRow row0 = new TableRow(this);
    row0.setPadding(24, 24, 24, 8);
    row0.setGravity(Gravity.CENTER_VERTICAL);

    // create table titles
    TextView textView0 = new TextView(this);
    textView0.setText("     Admin ID       ");
    textView0.setTextColor(Color.RED);
    textView0.setTextSize(22);
    TextView textView1 = new TextView(this);
    textView1.setText("   Admin Name     ");
    textView1.setTextColor(Color.RED);
    textView1.setTextSize(22);

    // add table title to first row
    row0.addView(textView0);
    row0.addView(textView1);
    tableLayout.addView(row0);

    // add admin ID and admin name to rows
    // count how many data in adminIds and adminNames
    int size = adminIds.size();
    for (int i = 0; i < size; i++) {
      TableRow row = new TableRow(this);

      // make text view for data
      TextView tvId = new TextView(this);
      tvId.setText(adminIds.get(i).toString());
      tvId.setTextColor(Color.BLACK);
      tvId.setGravity(Gravity.CENTER);
      tvId.setTextSize(16);
      TextView tvName = new TextView(this);
      tvName.setText(adminNames.get(i).toString());
      tvName.setTextColor(Color.BLACK);
      tvName.setGravity(Gravity.CENTER);
      tvName.setTextSize(16);

      // add the data to current row
      row.addView(tvId);
      row.addView(tvName);
      tableLayout.addView(row);
    }

  }
}
