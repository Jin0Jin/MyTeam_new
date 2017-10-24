/*
 * Copyright 2017 Rozdoum
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.liyongzhen.teamup.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.liyongzhen.teamup.R;
import com.liyongzhen.teamup.managers.ProfileManager;
import com.liyongzhen.teamup.managers.listeners.OnObjectChangedListener;
import com.liyongzhen.teamup.managers.listeners.OnProfileCreatedListener;
import com.liyongzhen.teamup.model.Profile;
import com.liyongzhen.teamup.utils.CommonValuables;
import com.liyongzhen.teamup.utils.ValidationUtil;

public class EditProfileActivity extends PickImageActivity implements OnProfileCreatedListener , DatePickerDialog.OnDateSetListener {
    private static final String TAG = EditProfileActivity.class.getSimpleName();

    // UI references.
    private EditText nameEditText;
    private ImageView imageView;
    private TextView textViewBirthday;
    private RadioGroup radioGroupGender;
    private CheckBox checkBox1, checkBox2,checkBox3,checkBox4;
    private Spinner spinner;

    private ProgressBar progressBar;

    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set up the login form.
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageView = (ImageView) findViewById(R.id.imageView);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        textViewBirthday = (TextView) findViewById(R.id.textViewBithday);
        radioGroupGender = (RadioGroup) findViewById(R.id.radio_group);
        checkBox1 = (CheckBox) findViewById(R.id.checkBox_experience1);
        checkBox2 = (CheckBox) findViewById(R.id.checkBox_experience2);
        checkBox3 = (CheckBox) findViewById(R.id.checkBox_experience3);
        checkBox4 = (CheckBox) findViewById(R.id.checkBox_experience4);
        spinner = (Spinner) findViewById(R.id.spinner_sport);

        showProgress();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ProfileManager.getInstance(this).getProfileSingleValue(firebaseUser.getUid(), createOnProfileChangedListener());

        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClick(v);
            }
        });
    }

    @Override
    public ProgressBar getProgressView() {
        return progressBar;
    }

    @Override
    public ImageView getImageView() {
        return imageView;
    }

    @Override
    public void onImagePikedAction() {
        startCropImageActivity();
    }

    private OnObjectChangedListener<Profile> createOnProfileChangedListener() {
        return new OnObjectChangedListener<Profile>() {
            @Override
            public void onObjectChanged(Profile obj) {
                profile = obj;
                fillUIFields();
            }
        };
    }
    public void onClickDatePicker(View view){
        String str = textViewBirthday.getText().toString();
        int[] intDate = CommonValuables.convertDateStringToInt(str);
        DatePickerDialog dialog = new DatePickerDialog(this, this, intDate[0], intDate[1], intDate[2]);
        dialog.show();
    }

    private void fillUIFields() {
        if (profile != null) {
            nameEditText.setText(profile.getUsername());

            textViewBirthday.setText(profile.getDateofBirth());
            ((RadioButton)radioGroupGender.getChildAt(profile.getGender())).setChecked(true);
            checkBox1.setChecked(profile.getExperience1());
            checkBox2.setChecked(profile.getExperience2());
            checkBox3.setChecked(profile.getExperience3());
            checkBox4.setChecked(profile.getExperience4());
            spinner.setSelection(profile.getChooseSport());
            if (profile.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(profile.getPhotoUrl())
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .crossFade()
                        .error(R.drawable.ic_stub)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(imageView);
            }
        }
        hideProgress();
        nameEditText.requestFocus();
    }

    private void attemptCreateProfile() {

        // Reset errors.
        nameEditText.setError(null);

        // Store values at the time of the login attempt.
        String name = nameEditText.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(name)) {
            nameEditText.setError(getString(R.string.error_field_required));
            focusView = nameEditText;
            cancel = true;
        } else if (!ValidationUtil.isNameValid(name)) {
            nameEditText.setError(getString(R.string.error_profile_name_length));
            focusView = nameEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            showProgress();
            String birthday = textViewBirthday.getText().toString();
            int gender = 0;
            if(radioGroupGender.getCheckedRadioButtonId() == R.id.radio_female)
                gender = 1;
            boolean check1 = checkBox1.isChecked();
            boolean check2 = checkBox2.isChecked();
            boolean check3 = checkBox3.isChecked();
            boolean check4 = checkBox4.isChecked();
            int sport = spinner.getSelectedItemPosition();
            profile.setUsername(name);
            profile.setDateofBirth(birthday);
            profile.setGender(gender);
            profile.setExperience1(check1);
            profile.setExperience2(check2);
            profile.setExperience3(check3);
            profile.setExperience4(check4);
            profile.setChooseSport(sport);
            ProfileManager.getInstance(this).createOrUpdateProfile(profile, imageUri, this);
        }
    }


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Log.d(CommonValuables.TAG, i + ":" + i1 + ":"+ i2);
        String str = i+"/"+ i1+"/"+i2;
        textViewBirthday.setText(str);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle result of pick image chooser
        super.onActivityResult(requestCode, resultCode, data);
        handleCropImageResult(requestCode, resultCode, data);
    }



    @Override
    public void onProfileCreated(boolean success) {
        hideProgress();

        if (success) {
            finish();
        } else {
            showSnackBar(R.string.error_fail_update_profile);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.save:
                if (hasInternetConnection()) {
                    attemptCreateProfile();
                } else {
                    showSnackBar(R.string.internet_connection_failed);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

