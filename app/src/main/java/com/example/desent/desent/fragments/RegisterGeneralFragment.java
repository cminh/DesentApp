package com.example.desent.desent.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.desent.desent.R;
import com.example.desent.desent.utils.AESHelper;
import com.example.desent.desent.utils.Utility;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileNotFoundException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by celine on 06/07/17.
 */

public class RegisterGeneralFragment extends Fragment {

    private final static String CIPHER_KEY = "p2m8j0DgoqjJGxnDYfq70fV92h7sCg0N";

    private boolean isProfileValid;
    private ImageView profilePic;
    private EditText nameTextView;
    private EditText emailTextView;
    private EditText passwordTextView;
    private EditText confirmPasswordTextView;
    private SharedPreferences sharedPreferences;
    private Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().getTheme().applyStyle(R.style.AppTheme_NoActionBar_AccentColorGreen, true);

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_register_general, container, false);

        profilePic = rootView.findViewById(R.id.profile_pic);
        nameTextView = rootView.findViewById(R.id.name);
        emailTextView = rootView.findViewById(R.id.email);
        passwordTextView = rootView.findViewById(R.id.password);
        confirmPasswordTextView = rootView.findViewById(R.id.confirm_password);

        profilePic.setOnClickListener(new View.OnClickListener(){ //TODO:request permission
            @Override
            public void onClick(View arg0) {
                pickImage();
            }});

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        restorePreferences();

        setErrors();

        return rootView;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();


        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("pref_key_profile_picture", imageUri.toString());
        editor.putString("pref_key_personal_name", String.valueOf(nameTextView.getText()));
        editor.putString("pref_key_personal_email", String.valueOf(emailTextView.getText()));

        try {
            editor.putString("pref_key_personal_password", AESHelper.encrypt(CIPHER_KEY, String.valueOf(passwordTextView.getText())));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        editor.commit();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }

    private void setErrors(){

        nameTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (String.valueOf(nameTextView.getText()).equals(""))
                    nameTextView.setError(getString(R.string.error_invalid_name));
                else
                    nameTextView.setError(null);

            }
        });

        emailTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                String email = String.valueOf(emailTextView.getText());
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    emailTextView.setError(getString(R.string.error_invalid_email));
                else
                    emailTextView.setError(null);

            }
        });

        passwordTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                String password = String.valueOf(passwordTextView.getText());
                if (password.length() < 8)
                    passwordTextView.setError(getString(R.string.error_password_too_short));
                else
                    passwordTextView.setError(null);

            }
        });

        confirmPasswordTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!(String.valueOf(confirmPasswordTextView.getText()).equals(String.valueOf(passwordTextView.getText()))))
                    confirmPasswordTextView.setError(getString(R.string.error_password_not_match));
                else
                    passwordTextView.setError(null);

            }
        });

    }

    private void pickImage() {
        Crop.pickImage(getContext(), this, Crop.REQUEST_PICK);
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(getContext(), this, Crop.REQUEST_CROP);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            imageUri = Crop.getOutput(result);
            profilePic.setImageURI(Crop.getOutput(result));
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                profilePic.setImageBitmap(Utility.getCroppedBitmap(bitmap));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(getActivity(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void restorePreferences(){

        try {
            imageUri = Uri.parse(sharedPreferences.getString("pref_key_profile_picture", "android.resource://com.example.desent.desent/drawable/earth"));
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
            profilePic.setImageBitmap(Utility.getCroppedBitmap(bitmap));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        nameTextView.setText(sharedPreferences.getString("pref_key_personal_name", ""), TextView.BufferType.EDITABLE);
        emailTextView.setText(sharedPreferences.getString("pref_key_personal_email", ""), TextView.BufferType.EDITABLE);

        try {
            passwordTextView.setText(AESHelper.decrypt(CIPHER_KEY, sharedPreferences.getString("pref_key_personal_password", "")), TextView.BufferType.EDITABLE);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
