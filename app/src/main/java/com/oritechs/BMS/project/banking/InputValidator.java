package com.oritechs.BMS.project.banking;

import android.widget.EditText;


public class InputValidator {


  public static boolean isEmpty(EditText editText) {
    return editText.getText().toString().trim().length() == 0;
  }

}
