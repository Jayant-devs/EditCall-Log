package com.calllogadderprank;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    DatePickerDialog datePickerDialog;
    Button timepick, datepick;
    int hour, minute;
    String ap, strmonth;
    int arYear, arMonth, arDay;
    int type = 0; // 1==missed 2==outgoing 3==incomming 0==error
    int duration_min = 5;

    RadioButton missed_btn, out_btn, incomming_btn;
    Button numberpick_btn, durationpick_btn;

    CardView call_btn;
    String mobilenumber;
    AdView adView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timepick = findViewById(R.id.main_Timepick_button);
        datepick = findViewById(R.id.main_Datepick_button);
        missed_btn = findViewById(R.id.missed_btn);
        out_btn = findViewById(R.id.outgoing_btn);
        incomming_btn = findViewById(R.id.incomming_btn);
        numberpick_btn = findViewById(R.id.main_numberpick_button);
        call_btn = findViewById(R.id.main_addcall_btn);
        durationpick_btn = findViewById(R.id.main_durationpick_button);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        // Create a new ad view.
        adView = findViewById(R.id.main_adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                super.onAdLoaded();
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                super.onAdFailedToLoad(adError);
                adView.loadAd(adRequest);
            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                super.onAdLoaded();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                super.onAdOpened();
            }
        });



        askForPermission(Manifest.permission.WRITE_CALL_LOG, 1);

        durationpick_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPicker np = new NumberPicker(MainActivity.this);
                np.setMinValue(1);
                np.setMaxValue(120);

                AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
                ab.setTitle("Duration of call(minutes)");
                ab.setView(np);
                ab.setNegativeButton("cancel", null);
                ab.setPositiveButton("set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        duration_min = np.getValue();
                        durationpick_btn.setText(duration_min+" minutes");
                    }
                });
                ab.create().show();
            }
        });

        numberpick_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText num = new EditText(MainActivity.this);
                num.setHint("Enter name/number");

                AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
                ab.setView(num);
                ab.setNegativeButton("cancel", null);
                ab.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mobilenumber = num.getText().toString().trim() + "";

                        numberpick_btn.setText(mobilenumber + "");
                    }
                });
                ab.create().show();
            }
        });

        timepick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popTimePicker();
            }
        });

        datepick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });


        missed_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 1;
            }
        });

        out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 2;
            }
        });


        incomming_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 3;
            }
        });


        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(arYear == 0 || ap == null || mobilenumber == null){
                    Toast.makeText(MainActivity.this, "fill all necessary details", Toast.LENGTH_SHORT).show();
                } else {
                    Calendar cal = Calendar.getInstance();

                    cal.set(Calendar.YEAR, arYear);
                    cal.set(Calendar.MONTH, arMonth - 1); // month is from 0 to 11
                    cal.set(Calendar.DAY_OF_MONTH, arDay); // date is like normal
                    cal.set(Calendar.HOUR_OF_DAY, hour); // 16 pe 4am and 10 pe 10pm
                    cal.set(Calendar.MINUTE, minute);
                    cal.set(Calendar.SECOND, 0);

                    ContentValues values = new ContentValues();
                    values.put(android.provider.CallLog.Calls.NUMBER, mobilenumber);
                    values.put(android.provider.CallLog.Calls.DATE, cal.getTimeInMillis());
                    values.put(android.provider.CallLog.Calls.DURATION, duration_min * 60);


                    if (type == 0) {
                        Toast.makeText(MainActivity.this, "Select Call-Type", Toast.LENGTH_SHORT).show();
                    } else if (type == 1) {
                        values.put(android.provider.CallLog.Calls.TYPE, CallLog.Calls.MISSED_TYPE);

                        getApplicationContext().getContentResolver().insert(android.provider.CallLog.Calls.CONTENT_URI, values);
                        Toast.makeText(getApplicationContext(), "Successfully Added Missed-Call to Call Logs", Toast.LENGTH_LONG).show();
                    } else if (type == 2) {
                        values.put(android.provider.CallLog.Calls.TYPE, CallLog.Calls.OUTGOING_TYPE);

                        getApplicationContext().getContentResolver().insert(android.provider.CallLog.Calls.CONTENT_URI, values);
                        Toast.makeText(getApplicationContext(), "Successfully Added Outgoing-Call to Call Logs", Toast.LENGTH_LONG).show();
                    } else if (type == 3) {
                        values.put(android.provider.CallLog.Calls.TYPE, CallLog.Calls.INCOMING_TYPE);

                        getApplicationContext().getContentResolver().insert(android.provider.CallLog.Calls.CONTENT_URI, values);
                        Toast.makeText(getApplicationContext(), "Successfully Added Incomming-Call to Call Logs", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(MainActivity.this, "error\ntry Again.", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

    }

    //                theCaller utility = new theCaller();
    //                    int calltype = CallLog.Calls.MISSED_TYPE;
//                    utility.AddNumToCallLog(getApplicationContext().getContentResolver(), mobilenumber,
//                            calltype, cal.getTimeInMillis());


    public void popTimePicker() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;

                int arhour = hour;

                if (hour >= 12) {
                    arhour=arhour-12;
                    ap = "pm";
                } else if (hour < 12) {
                    ap = "am";
                }

                String strhour;
                String strmin;

                if (arhour < 10) {
                    strhour = "0" + arhour;
                } else {
                    strhour = "" + arhour;
                }

                if (minute < 10) {
                    strmin = "0" + minute;
                } else {
                    strmin = "" + minute;
                }


                timepick.setText(strhour + " : " + strmin + " " + ap);
            }
        };

        int style = AlertDialog.THEME_HOLO_LIGHT;
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, style, onTimeSetListener, hour, minute, false);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                arYear = year;
                arMonth = month;
                arDay = day;

                if (month == 1)
                    strmonth = "JAN";
                if (month == 2)
                    strmonth = "FEB";
                if (month == 3)
                    strmonth = "MAR";
                if (month == 4)
                    strmonth = "APR";
                if (month == 5)
                    strmonth = "MAY";
                if (month == 6)
                    strmonth = "JUN";
                if (month == 7)
                    strmonth = "JUL";
                if (month == 8)
                    strmonth = "AUG";
                if (month == 9)
                    strmonth = "SEP";
                if (month == 10)
                    strmonth = "OCT";
                if (month == 11)
                    strmonth = "NOV";
                if (month == 12)
                    strmonth = "DEC";

                datepick.setText(day + " " + strmonth + " " + year);

            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);


        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show();

    }


    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {
                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
//            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }


}