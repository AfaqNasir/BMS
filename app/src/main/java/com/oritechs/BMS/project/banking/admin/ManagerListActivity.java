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

public class ManagerListActivity extends AppCompatActivity {

  List<Integer> tellerIds = new ArrayList<>();
  List<String> tellerNames = new ArrayList<>();

  // reference: StackOverflow
  // https://stackoverflow.com/questions/18207470/adding-table-rows-dynamically-in-android
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_manager_list);
    Context context = getApplicationContext();
    DriverHelper db = new DriverHelper(context);

    // get userId list and the role id of TELLER
    List<Integer> userIds = db.getUsersIdsList();
    EnumMap<Roles, Integer> roles = db.getRolesEnumMap();
    int roleId = roles.get(Roles.TELLER);

    // check roles for each user
    for (Integer userId : userIds) {
      int currentUserRole = db.getUserRole(userId);
      // if the user is TELLER
      if (roleId == currentUserRole) {
        tellerIds.add(userId);
        // get the name of the admin and add to the list
        String tellerName = db.getUserName(userId);
        tellerNames.add(tellerName);
      }
    }

    init();
  }

  public void init() {
    TableLayout tableLayout = (TableLayout) findViewById(R.id.main_table);
    TableRow row0 = new TableRow(this);
    TableRow.LayoutParams padding = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
        TableLayout.LayoutParams.WRAP_CONTENT);
    row0.setLayoutParams(padding);
    row0.setPadding(24, 24, 24, 8);
    row0.setGravity(Gravity.CENTER_VERTICAL);

    // create table titles
    TextView textView0 = new TextView(this);
    textView0.setText("     Manager ID    ");
    textView0.setTextColor(Color.RED);
    textView0.setTextSize(22);
    TextView textView1 = new TextView(this);
    textView1.setText("     Manager Name   ");
    textView1.setTextColor(Color.RED);
    textView1.setTextSize(22);

    // add table title to first row
    row0.addView(textView0);
    row0.addView(textView1);
    tableLayout.addView(row0);

    // add teller ID and teller name to rows
    // count how many data in tellerIds and tellerNames
    int size = tellerIds.size();
    for (int i = 0; i < size; i++) {
      TableRow row = new TableRow(this);
      row.setLayoutParams(padding);

      // make text view for data
      TextView tvId = new TextView(this);
      tvId.setText(tellerIds.get(i).toString());
      tvId.setTextColor(Color.BLACK);
      tvId.setGravity(Gravity.CENTER);
      tvId.setTextSize(16);
      TextView tvName = new TextView(this);
      tvName.setText(tellerNames.get(i).toString());
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
