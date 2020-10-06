package com.example.uberforhotels.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.uberforhotels.HotelProfileActivity;
import com.example.uberforhotels.Other.DBHelper;
import com.example.uberforhotels.Other.Helper;
import com.example.uberforhotels.R;
import com.example.uberforhotels.models.Hotel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HotelSettings extends Fragment {
    @BindView(R.id.nameEdit)
    TextView nameEdit;
    @BindView(R.id.statusSwitch)
    SwitchCompat statusSwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragmentsettings, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        nameEdit.setText(Helper.getHotelNameFromPrefs(getContext()));
        statusSwitch.setChecked(Helper.getIsHotelOpenFromPrefs(getContext()));

        toggleSwitch(statusSwitch.isChecked());

        statusSwitch.setOnCheckedChangeListener((compoundButton, b) -> toggleSwitch(b));
    }

    @OnClick({R.id.editPen, R.id.saveBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.editPen:
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Edit Hotel Name");
                final EditText editText = new EditText(getContext());
                alertDialog.setView(editText);
                editText.setText(nameEdit.getText());
                alertDialog.setPositiveButton("OK", (dialogInterface, i) -> {
                    String newName = editText.getText().toString().trim();
                    if (newName.isEmpty())
                        Helper.toast(getContext(), "Invalid input");
                    else {
                        nameEdit.setText(newName);
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
                alertDialog.show();
                break;

            case R.id.saveBtn:
                DBHelper.addHotel(new Hotel(
                        Helper.getHotelIdFromPreference(getContext()),
                        nameEdit.getText().toString(),
                        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(),
                        Helper.getPrefsCoverImgUrl(getContext()),
                        statusSwitch.isChecked(),
                        null
                ));
                Helper.setHotelNameInPrefs(nameEdit.getText().toString(), getContext());
                Helper.setHotelOpenInPrefs(statusSwitch.isChecked(), getContext());
                ((HotelProfileActivity) Objects.requireNonNull(getActivity())).fillNavigationView();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    private void switchOn(){
        statusSwitch.setText("Open");
        statusSwitch.setTextColor(R.color.colorPrimary);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    private void switchOff(){
        statusSwitch.setText("Closed");
        statusSwitch.setTextColor(R.color.red);
    }

    private void toggleSwitch(boolean b){
        if (b) switchOn();
        else switchOff();
    }
}
