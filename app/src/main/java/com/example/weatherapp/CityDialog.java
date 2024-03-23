package com.example.weatherapp;

import static android.view.View.VISIBLE;

import static com.example.weatherapp.MainActivity.cityList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class CityDialog {
    private Context context;
    private Activity activity;
    private Dialog cityDialog;

    private OnDialogResultListener resultListener;
    private boolean isDialogOpen = false;

    public CityDialog(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void setOnDialogResultListener(OnDialogResultListener listener) {
        this.resultListener = listener;
    }

    private ListView cityListView;

    ImageView  clear_text, search_icon;
    EditText searchEdit;

    TextView cancel_button;

    private ArrayAdapter<String> cityListAdapter;
    private ArrayList<String> suggestions;

    public void showDialog() {

        // Check if the dialog is already open
        if (isDialogOpen) {
            return;
        }

        cityDialog = new Dialog(context);
        cityDialog.setContentView(R.layout.dialog_city_search);
        cityDialog.setCancelable(true);
        cityDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        cityDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        cityDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {

                    dismissDialog();
                }
                return true;
            }
        });

        clear_text = cityDialog.findViewById(R.id.clear_text);
        search_icon = cityDialog.findViewById(R.id.serch_icon);
        searchEdit = cityDialog.findViewById(R.id.searchEdit);
        cancel_button = cityDialog.findViewById(R.id.cancel_button);
        cityListView = cityDialog.findViewById(R.id.cityListView);


        // Load JSON data into suggestions ArrayList
        suggestions = new ArrayList<>();
        for (int i = 0; i < cityList.size(); i++) {
            String name = cityList.get(i).getName();
            String country = cityList.get(i).getCountry();
            suggestions.add(name + "," + country);
        }


        // Set up ArrayAdapter for city list
        cityListAdapter = new ArrayAdapter<>(activity, R.layout.list_item, suggestions);
        cityListView.setAdapter(cityListAdapter);

        // Set up item click listener for city list
        cityListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            resultListener.onCitySelect(selectedItem);
            dismissDialog();
        });
        cityListView.setVisibility(View.GONE);

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0) {
                    // Filter suggestions only when search bar is not empty
                    cityListView.setVisibility(View.VISIBLE);
                    cityListAdapter.getFilter().filter(s.toString());
                } else {
                    cityListView.setVisibility(View.GONE);
                }

            }
        });


        clear_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityListView.setVisibility(View.GONE);
                searchEdit.getText().clear();
                clear_text.setVisibility(View.GONE);
                search_icon.setVisibility(View.VISIBLE);


            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });


        cityDialog.show();

        // Set the flag to indicate that the dialog is open
        isDialogOpen = true;
    }




    public void dismissDialog() {
        if (cityDialog != null && cityDialog.isShowing()) {
            cityDialog.dismiss();
            isDialogOpen = false;
        }
    }

    public interface OnDialogResultListener {
        void onCitySelect( String make);

    }
}
