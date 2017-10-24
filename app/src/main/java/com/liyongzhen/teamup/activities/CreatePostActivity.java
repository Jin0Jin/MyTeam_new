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

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.liyongzhen.teamup.R;
import com.liyongzhen.teamup.managers.PostManager;
import com.liyongzhen.teamup.managers.listeners.OnPostCreatedListener;
import com.liyongzhen.teamup.model.Post;
import com.liyongzhen.teamup.utils.LogUtil;
import com.liyongzhen.teamup.utils.ValidationUtil;

public class CreatePostActivity extends PickImageActivity implements OnPostCreatedListener {
    private static final String TAG = CreatePostActivity.class.getSimpleName();
    public static final int CREATE_NEW_POST_REQUEST = 11;

    protected ImageView imageView;
    protected ProgressBar progressBar;
    protected EditText descriptionEditText;

    protected PostManager postManager;
    protected boolean creatingPost = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_post_activity);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        postManager = PostManager.getInstance(CreatePostActivity.this);

        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        imageView = (ImageView) findViewById(R.id.imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
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
        loadImageToImageView();
    }

    protected void attemptCreatePost() {
        // Reset errors.
        descriptionEditText.setError(null);

        String description = descriptionEditText.getText().toString().trim();

        View focusView = null;
        boolean cancel = true;

        if (TextUtils.isEmpty(description)) {
//            descriptionEditText.setError(getString(R.string.warning_empty_description));
//            focusView = descriptionEditText;
//            cancel = true;
        }
        else
            cancel = false;


        if (!(this instanceof EditPostActivity) && imageUri == null) {
//            showWarningDialog(R.string.warning_empty_image);
//            focusView = imageView;
//            cancel = true;
        } else
            cancel = false;

        if (!cancel) {
            creatingPost = true;
            hideKeyboard();
            savePost("", description);
        } else {
            showWarningDialog(R.string.warning_empty_image);
//            focusView = imageView;
//            cancel = true;
//            focusView.requestFocus();
        }
    }

    protected void savePost(String title, String description) {
        showProgress(R.string.message_creating_post);
        Post post = new Post();
        post.setTitle(title);
        post.setDescription(description);
        post.setAuthorId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if(imageUri != null)
            postManager.createOrUpdatePostWithImage(imageUri, CreatePostActivity.this, post);
        else
            postManager.createOrUpdatePostWithoutImage(CreatePostActivity.this, post);
    }

    @Override
    public void onPostSaved(boolean success) {
        hideProgress();

        if (success) {
            setResult(RESULT_OK);
            CreatePostActivity.this.finish();
            LogUtil.logDebug(TAG, "Post was created");
        } else {
            creatingPost = false;
            showSnackBar(R.string.error_fail_create_post);
            LogUtil.logDebug(TAG, "Failed to create a post");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.post:
                if (!creatingPost) {
                    if (hasInternetConnection()) {
                        attemptCreatePost();
                    } else {
                        showSnackBar(R.string.internet_connection_failed);
                    }
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
