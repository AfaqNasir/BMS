package com.oritechs.BMS.project.banking.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oritechs.BMS.R;
import com.oritechs.BMS.database.android.DriverHelper;
import com.oritechs.BMS.generics.AccountTypes;
import com.oritechs.BMS.project.banking.InputValidator;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;

public class UserSendMoneyActivity extends AppCompatActivity {

    EditText senderAccountId,receverAccountId;
    EditText enterAmount;
    TextView messageView;
    Intent intent;
    int senderId,receverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_send_money);

        // get customer id from intent
        intent = getIntent();
        senderId = intent.getIntExtra("senderId", -1);

        // when withdraw button is clicked
        Button sendMoney = findViewById(R.id.button_sendMoney);
        sendMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get views
                senderAccountId = (EditText) findViewById(R.id.sender_account_id);
                receverAccountId = (EditText) findViewById(R.id.recever_account_id);
                enterAmount = (EditText) findViewById(R.id.enter_amount);
                messageView = (TextView) findViewById(R.id.message);

                // check if input is empty
                if (InputValidator.isEmpty(senderAccountId) || InputValidator.isEmpty(enterAmount)) {
                    messageView.setText(R.string.null_info);

                } else {
                    // get the account id and withdraw amount
                    int senderAccount = Integer.parseInt(senderAccountId.getText().toString());
                    int receverAccount = Integer.parseInt(receverAccountId.getText().toString());
                    BigDecimal amount = new BigDecimal(enterAmount.getText().toString());

                    // create instance of the database
                    Context context = getApplicationContext();
                    DriverHelper db = new DriverHelper(context);
                    // get account ids for user
                    List<Integer> senderAccountIds = db.getAccountIdsList(senderId);
                    // get account ids for user
                    List<Integer> receverAccountIds = db.getAccountIdsList(receverId);
                    boolean hasAccess = false;

                    // check if the account belongs to the user
                    for (Integer i : senderAccountIds) {
                        if (senderAccount == i) {
                            hasAccess = true;
                        }
                    }

                    for (Integer i : receverAccountIds) {
                        if (receverAccount == i) {
                            hasAccess = true;
                        }
                    }

                    if (hasAccess) {
                        // subtract balance
                        BigDecimal sendBalance = db.getBalance(senderAccount);
                        sendBalance = sendBalance.subtract(amount);
                        //add balance
                        BigDecimal receverBalance = db.getBalance(receverAccount);
                        receverBalance = receverBalance.add(amount);

                        // get all account types to check
                        EnumMap<AccountTypes, Integer> accountTypes = db.getAccountTypesEnumMap();
                        int accountType = db.getAccountType(senderAccount);
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

                        } else if (accountType == savings && amount.compareTo(BigDecimal.valueOf(1000)) < 0) {
                            // check if savings and new balance is less than 1000
                            convert = true;

                        } else if (accountType == restrictedSavings) {
                            // check if restricted savings
                            restricted = true;
                        }

                        // check if balance would be less than 0 and not balance owing
                        if (amount.compareTo(BigDecimal.valueOf(0)) < 0 && !isBalanceOwing) {
                            // can't withdraw
                            messageView.setText(R.string.insufficient_funds);

                        } else if (restricted) {
                            // customer can't withdraw from restricted savings
                            messageView.setText(R.string.restrict_saving);

                        } else {
                            // update balance
                            boolean updated = db.updateAccountBalance(amount.setScale(2,
                                    BigDecimal.ROUND_HALF_UP), senderAccount);

                            // check if account needs to convert the type from savings to chequing
                            if (updated && convert) {
                                messageView.setText(R.string.saving_chequing);
                                // update account type and send a message
                                db.updateAccountType(chequing, senderAccount);
                                db.insertMessage(senderId, getResources().getString(R.string.convert1) + " "
                                        + senderAccountId + " " + getResources().getString(R.string.convert2));

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