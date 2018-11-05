package com.example.ahmed.qusat.Fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ahmed.qusat.Model.User;
import com.example.ahmed.qusat.NetworikConntection;
import com.example.ahmed.qusat.Presenter.RegisterPresenter;
import com.example.ahmed.qusat.R;
import com.example.ahmed.qusat.View.RegisterView;
import com.fourhcode.forhutils.FUtilsValidation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Register extends Fragment implements RegisterView {


    public Register() {
        // Required empty public constructor
    }
    RegisterPresenter userPresenter;
    EditText firstName,lastName,userEmail,
            userPassword,userNationalId,userAdress,userPhone;
    CheckBox maleCheckBox,femaleCheckBox;
    String gender;
    Button  registerButton;
    private ProgressBar progressBar;
    Button selectImage;
    ImageView imageView;
    public String encImage;
    // private int REQUEST_CODE=1;
    private final int PICK_IMAGE_REQUEST = 71;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;

   View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_register, container, false);
        firstName=view.findViewById( R.id.E_firstName );
        lastName=view.findViewById( R.id.E_lastName );
        userEmail=view.findViewById( R.id.E_Email );
        userPassword=view.findViewById( R.id.E_Password );
        userNationalId=view.findViewById( R.id.E_ssn );
        userAdress=view.findViewById( R.id.E_Address );
        userPhone=view.findViewById( R.id.E_Phone );
        maleCheckBox=view.findViewById( R.id.maleCheckBox);
        femaleCheckBox=view.findViewById( R.id.femaleCheckBox );
        registerButton=view.findViewById( R.id.btn_register);
        progressBar=view.findViewById( R.id.reg_ProgressBar );
        selectImage=view.findViewById( R.id.reg_select_image );
        imageView=view.findViewById( R.id.reg_imageview );
        userPresenter= new RegisterPresenter(getContext(), (RegisterView) this);

        checkPermission();
        registerButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        } );

        selectImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectNationailIdFromGallery();

            }
        } );
        maleCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender="Male";
                femaleCheckBox.setChecked(false);
            }
        });
        femaleCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender="Female";
                maleCheckBox.setChecked(false);
            }
        });



    return view;
    }
    private void selectNationailIdFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);

    }
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + "com.example.ahmed.qusat"));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
        /*Intent intent=new Intent( );
        intent.setType( "image/*" );
        intent.setAction( Intent.ACTION_GET_CONTENT );
        startActivityForResult( Intent.createChooser( intent,"Select Picture" ),REQUEST_CODE );*/
    }
    @Override
    public void openMain() {
        Toast.makeText( getContext(),R.string.waitingemail, Toast.LENGTH_LONG ).show();
        getFragmentManager().beginTransaction().replace(R.id.flContent,new Login()).commitAllowingStateLoss();
        progressBar.setVisibility( View.GONE );

    }

    @Override
    public void showError(String error) {
        Toast.makeText( getContext(), error, Toast.LENGTH_LONG ).show();
        progressBar.setVisibility( View.GONE );

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult( requestCode, resultCode, data );
        if(requestCode==PICK_IMAGE_REQUEST &&resultCode==RESULT_OK &&data!=null &&data.getData()!=null)
        {
            Uri uri = data.getData();
            if(uri!=null)
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                    imageView.setImageBitmap( bitmap );
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, bytes);
                    byte[] dat = bytes.toByteArray();
                    encImage = Base64.encodeToString(dat, Base64.DEFAULT);
                    imageView.setVisibility( View.VISIBLE );

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText( getContext(), "failed", Toast.LENGTH_LONG ).show();
                }
        }

    }
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private void register() {
        FUtilsValidation.isEmpty( firstName,getResources().getString( R.string.userfirstame ) );
        FUtilsValidation.isEmpty( lastName,getResources().getString( R.string.userlastname ) );
        FUtilsValidation.isEmpty(userEmail, getResources().getString( R.string.invalideemail ));
        FUtilsValidation.isEmpty( userPassword,getResources().getString( R.string.userpassword ) );
        FUtilsValidation.isEmpty( userNationalId,getResources().getString( R.string.usernationalid ) );
        FUtilsValidation.isEmpty( userAdress,getResources().getString( R.string.useradress ) );
        ValidateEmail();
        NetworikConntection checkgbsAndNetwork=new NetworikConntection(getContext());
        if (checkgbsAndNetwork.isNetworkAvailable(getContext())) {

            if (!firstName.getText().toString().equals( "" ) && !lastName.getText().toString().equals( "" )
                    && !userEmail.getText().toString().equals( "" )
                    && !userNationalId.getText().toString().equals( "" )
                    && !userAdress.getText().toString().equals( "" )
                    &&encImage!=null&&
                    (maleCheckBox.isChecked() || femaleCheckBox.isChecked())
                    && FUtilsValidation.isLengthCorrect( userPassword.getText().toString(), 4, 16 )
                    && ValidateEmail()) {

                User userRegister = new User();
                userRegister.setFirst_name( firstName.getText().toString() );
                userRegister.setLast_name( lastName.getText().toString() );
                userRegister.setEmail( userEmail.getText().toString() );
                userRegister.setPassword( userPassword.getText().toString() );
                userRegister.setSsn( userNationalId.getText().toString() );
                userRegister.setAddress( userAdress.getText().toString() );
                userRegister.setPhone( userPhone.getText().toString() );
                userRegister.setUsername( "Shimaa" );
                userRegister.setGender( gender );
                userRegister.setApi_token( "100" );
                userRegister.setBase64(encImage);
                progressBar.setVisibility( View.VISIBLE );
                userPresenter.register( userRegister );

            }else
            {
                Toast.makeText( getContext(), R.string.fillinformation, Toast.LENGTH_LONG ).show();
            }


        }
        else {
            Toast.makeText( getContext(), R.string.checkinternetconnection, Toast.LENGTH_SHORT ).show();
        }

    }
    private Boolean ValidateEmail(){
        String EMAIL=userEmail.getText().toString().trim();
        if (EMAIL.isEmpty()||!isValidEmail(EMAIL)){
            userEmail.setError(getResources().getString(R.string.invalidemail));

            return false;
        }else if(!userEmail.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){
            userEmail.setError(getResources().getString(R.string.invalidemail));
            return false;
        }
        return true;
    }

}
