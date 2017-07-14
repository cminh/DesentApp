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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.desent.desent.R;

import java.io.FileNotFoundException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by celine on 06/07/17.
 */

public class RegisterGeneralFragment extends Fragment {

    private ImageView profilePic;
    private EditText nameTextView;
    private EditText emailTextView;
    private EditText passwordTextView;
    private SharedPreferences sharedPreferences;

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


        profilePic.setOnClickListener(new View.OnClickListener(){ //TODO:request permission
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }});

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        restorePreferences();

        return rootView;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();


        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("pref_key_personal_name", String.valueOf(nameTextView.getText()));
        editor.putString("pref_key_personal_email", String.valueOf(emailTextView.getText()));
        editor.putString("pref_key_personal_password", String.valueOf(passwordTextView.getText()));

        editor.commit();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(targetUri));
                profilePic.setImageBitmap(getCroppedBitmap(bitmap));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void restorePreferences(){

        nameTextView.setText(sharedPreferences.getString("pref_key_personal_name", ""), TextView.BufferType.EDITABLE);
        emailTextView.setText(sharedPreferences.getString("pref_key_personal_email", ""), TextView.BufferType.EDITABLE);
        passwordTextView.setText(sharedPreferences.getString("pref_key_personal_password", ""), TextView.BufferType.EDITABLE);

    }

    //TODO: let the user crop the image
    //TODO: utils
    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

}
