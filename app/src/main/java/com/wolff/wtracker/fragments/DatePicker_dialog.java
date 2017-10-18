package com.wolff.wtracker.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;


import java.util.Calendar;

/**
 * Created by wolff on 07.06.2017.
 */

public class DatePicker_dialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public static final String TAG_PERIOD_ID = "period_id";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int _year = calendar.get(Calendar.YEAR);
        int _month = calendar.get(Calendar.MONTH);
        int _day = calendar.get(Calendar.DAY_OF_MONTH);
        Dialog picker = new DatePickerDialog(getActivity(),this,_year,_month,_day);
        picker.setTitle("Дата операции");
        return picker;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
     //   Button btn = (Button) getActivity().findViewById(R.id.imCreditItem_Period);
     //   btn.setText(dayOfMonth+"-"+month+"-"+year);
        //DateUtils dateUtils = new DateUtils();
        //Intent intent = new Intent();
        //intent.putExtra(TAG_PERIOD_ID,dateUtils.addZero(dayOfMonth)+"-"+dateUtils.addZero(month)+"-"+year);
        //getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,intent);
        dismiss();

    }
}
