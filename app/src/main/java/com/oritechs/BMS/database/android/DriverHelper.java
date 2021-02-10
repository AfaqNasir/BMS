package com.oritechs.BMS.database.android;

import android.content.Context;
import android.database.Cursor;


import com.oritechs.BMS.generics.AccountTypes;
import com.oritechs.BMS.generics.Roles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

public class DriverHelper extends DatabaseDriverA {

  public DriverHelper(Context context) {
    super(context);
  }

  public long insertRole(String role) {
    long id = super.insertRole(role);
    return id;
  }

  public long insertNewUser(String name, int age, String address, int roleId, String password) {
    long id = super.insertNewUser(name, age, address, roleId, password);
    return id;
  }

  public long insertAccountType(String name, BigDecimal interestRate) {
    long id = super.insertAccountType(name, interestRate);
    return id;
  }

  public long insertNewAccount(String name, BigDecimal balance, int typeId) {
    long id = super.insertAccount(name, balance, typeId);
    return id;
  }

  public long insertUserAccount(int userId, int accountId) {
    long id = super.insertUserAccount(userId, accountId);
    return id;
  }

  public long insertMessage(int userId, String message) {
    long id = super.insertMessage(userId, message);
    return id;
  }

  public Cursor getRoles() {
    Cursor roles = super.getRoles();
    return roles;
  }


  public List<Integer> getRolesList() {
    List<Integer> roles = new ArrayList<Integer>();
    Cursor cursor = this.getRoles();

    while (cursor.moveToNext()) {
      Integer role = cursor.getInt(cursor.getColumnIndex("ID"));
      roles.add(role);
    }

    return roles;
  }


  public EnumMap<Roles, Integer> getRolesEnumMap() {
    EnumMap<Roles, Integer> roleEnumMap = new EnumMap<>(Roles.class);

    // Get the list of roleIds and add them into the enumMaps
    List<Integer> roleIds = this.getRolesList();

    for (Integer i : roleIds) {
      String roleName = this.getRole(i);
      roleEnumMap.put(Roles.valueOf(roleName), i);
    }

    return roleEnumMap;
  }

  public String getRole(int id) {
    String role = super.getRole(id);
    return role;
  }

  public int getUserRole(int userId) {
    int roleId = super.getUserRole(userId);
    return roleId;
  }

  public Cursor getUsersDetails() {
    Cursor users = super.getUsersDetails();
    return users;
  }


  public List<Integer> getUsersIdsList() {
    List<Integer> usersIds = new ArrayList<Integer>();
    Cursor cursor = this.getUsersDetails();

    while (cursor.moveToNext()) {
      Integer userId = cursor.getInt(cursor.getColumnIndex("ID"));
      usersIds.add(userId);
    }

    return usersIds;
  }

  public Cursor getUserDetails(int userId) {
    Cursor user = super.getUserDetails(userId);
    return user;
  }


  public String getUserName(int userId) {
    String name = null;
    Cursor cursor = this.getUserDetails(userId);

    while (cursor.moveToNext()) {
      name = cursor.getString(cursor.getColumnIndex("NAME"));
    }
    return name;
  }


  public String getUserAddress(int userId) {
    String address = null;
    Cursor cursor = this.getUserDetails(userId);

    while (cursor.moveToNext()) {
      address = cursor.getString(cursor.getColumnIndex("ADDRESS"));
    }
    return address;
  }

  public String getPassword(int userId) {
    String password = super.getPassword(userId);
    return password;
  }

  public Cursor getAccountIds(int userId) {
    Cursor accountIds = super.getAccountIds(userId);
    return accountIds;
  }


  public List<Integer> getAccountIdsList(int userId) {
    List<Integer> accountIds = new ArrayList<Integer>();
    Cursor cursor = this.getAccountIds(userId);

    while (cursor.moveToNext()) {
      Integer accountId = cursor.getInt(cursor.getColumnIndex("ACCOUNTID"));
      accountIds.add(accountId);
    }

    return accountIds;
  }

  public Cursor getAccountDetails(int accountId) {
    Cursor account = super.getAccountDetails(accountId);
    return account;
  }

  public String getAccountName(int accountId) {
    String accountName = null;
    Cursor cursor = this.getAccountDetails(accountId);

    while (cursor.moveToNext()) {
      accountName = cursor.getString(cursor.getColumnIndex("NAME"));
    }

    return accountName;
  }

  public BigDecimal getBalance(int accountId) {
    BigDecimal balance = super.getBalance(accountId);
    return balance;
  }

  public int getAccountType(int accountId) {
    int accountType = super.getAccountType(accountId);
    return accountType;
  }

  public String getAccountTypeName(int accountTypeId) {
    String accountTypeName = super.getAccountTypeName(accountTypeId);
    return accountTypeName;
  }

  public Cursor getAccountTypesIds() {
    Cursor accountTypes = super.getAccountTypesId();
    return accountTypes;
  }


  public List<Integer> getAccountTypesIdsList() {
    List<Integer> accountTypes = new ArrayList<Integer>();
    Cursor cursor = this.getAccountTypesIds();

    while (cursor.moveToNext()) {
      Integer accountType = cursor.getInt(cursor.getColumnIndex("ID"));
      accountTypes.add(accountType);
    }

    return accountTypes;
  }


  public EnumMap<AccountTypes, Integer> getAccountTypesEnumMap() {
    EnumMap<AccountTypes, Integer> accountTypesEnumMap = new EnumMap<>(AccountTypes.class);

    // Get the list of roleIds and add them into the enumMaps
    List<Integer> accountTypesIds = this.getAccountTypesIdsList();

    for (Integer i : accountTypesIds) {
      String accountType = this.getAccountTypeName(i);
      accountTypesEnumMap.put(AccountTypes.valueOf(accountType), i);
    }

    return accountTypesEnumMap;
  }

  public BigDecimal getInterestRate(int accountType) {
    BigDecimal interestRate = super.getInterestRate(accountType);
    return interestRate;
  }

  public Cursor getAllMessages(int userId) {
    Cursor messages = super.getAllMessages(userId);
    return messages;
  }

  public List<Integer> getMessageIds(int userId) {
    List<Integer> messageIdList = new ArrayList<>();
    Cursor cursor = this.getAllMessages(userId);

    while (cursor.moveToNext()) {
      messageIdList.add(cursor.getInt(cursor.getColumnIndex("ID")));
    }
    return messageIdList;
  }

  public HashMap<Integer, Boolean> getAllMessagesHashMap(int userId) {
    HashMap<Integer, Boolean> messages = new HashMap<>();
    Cursor cursor = this.getAllMessages(userId);

    while (cursor.moveToNext()) {
      Integer messageId = cursor.getInt(cursor.getColumnIndex("ID"));
      Integer viewedInt = cursor.getInt(cursor.getColumnIndex("VIEWED"));
      boolean viewed = false;
      if (viewedInt == 1) {
        viewed = true;
      }
      messages.put(messageId, viewed);
    }

    return messages;
  }

  public String getSpecificMessage(int messageId) {
    String message = super.getSpecificMessage(messageId);
    return message;
  }

  public boolean updateRoleName(String name, int id) {
    boolean updated = super.updateRoleName(name, id);
    return updated;
  }

  public boolean updateUserName(String name, int id) {
    boolean updated = super.updateUserName(name, id);
    return updated;
  }

  public boolean updateUserAge(int age, int id) {
    boolean updated = super.updateUserAge(age, id);
    return updated;
  }

  public boolean updateUserRole(int roleId, int id) {
    boolean updated = super.updateUserRole(roleId, id);
    return updated;
  }

  public boolean updateUserAddress(String address, int id) {
    boolean updated = super.updateUserAddress(address, id);
    return updated;
  }

  public boolean updateAccountName(String name, int id) {
    boolean updated = super.updateAccountName(name, id);
    return updated;
  }

  public boolean updateAccountBalance(BigDecimal balance, int id) {
    boolean updated = super.updateAccountBalance(balance, id);
    return updated;
  }

  public boolean updateAccountType(int typeId, int id) {
    boolean updated = super.updateAccountType(typeId, id);
    return updated;
  }

  public boolean updateAccountTypeName(String name, int id) {
    boolean updated = super.updateAccountTypeName(name, id);
    return updated;
  }

  public boolean updateAccountTypeInterestRate(BigDecimal interestRate, int id) {
    boolean updated = super.updateAccountTypeInterestRate(interestRate, id);
    return updated;
  }

  public boolean updateUserPassword(String password, int id) {
    boolean updated = super.updateUserPassword(password, id);
    return updated;
  }

  public boolean updateUserMessageState(int id) {
    boolean updated = super.updateUserMessageState(id);
    return updated;
  }
}
