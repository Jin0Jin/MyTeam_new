/*
 *  Copyright 2017 Rozdoum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.liyongzhen.teamup.adapters.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.liyongzhen.teamup.Constants;
import com.liyongzhen.teamup.R;
import com.liyongzhen.teamup.controllers.LikeController;
import com.liyongzhen.teamup.managers.PostManager;
import com.liyongzhen.teamup.managers.ProfileManager;
import com.liyongzhen.teamup.managers.listeners.OnObjectChangedListener;
import com.liyongzhen.teamup.managers.listeners.OnObjectExistListener;
import com.liyongzhen.teamup.model.Like;
import com.liyongzhen.teamup.model.Post;
import com.liyongzhen.teamup.model.Profile;
import com.liyongzhen.teamup.utils.FormatterUtil;
import com.liyongzhen.teamup.utils.Utils;

/**
 * Created by alexey on 27.12.16.
 */

public class PostViewHolder extends RecyclerView.ViewHolder {
    public static final String TAG = PostViewHolder.class.getSimpleName();

    private Context context;
    private ImageView postImageView;
    private TextView titleTextView;
    private TextView detailsTextView;
    private TextView likeCounterTextView;
    private ImageView likesImageView;
    private TextView commentsCountTextView;
    private TextView watcherCounterTextView;
    private TextView dateTextView;
    private TextView nameTextView;
    private ImageView authorImageView;
    private ViewGroup likeViewGroup;
    private ImageView imageViewLevel;
    private TextView textViewSport;

    private ProfileManager profileManager;
    private PostManager postManager;

    private LikeController likeController;

    public PostViewHolder(View view, final OnClickListener onClickListener) {
        this(view, onClickListener, true);
    }

    public PostViewHolder(View view, final OnClickListener onClickListener, boolean isAuthorNeeded) {
        super(view);
        this.context = view.getContext();

        postImageView = (ImageView) view.findViewById(R.id.postImageView);
        likeCounterTextView = (TextView) view.findViewById(R.id.likeCounterTextView);
        likesImageView = (ImageView) view.findViewById(R.id.likesImageView);
        commentsCountTextView = (TextView) view.findViewById(R.id.commentsCountTextView);
        watcherCounterTextView = (TextView) view.findViewById(R.id.watcherCounterTextView);
        dateTextView = (TextView) view.findViewById(R.id.dateTextView);
        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        detailsTextView = (TextView) view.findViewById(R.id.detailsTextView);
        nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        authorImageView = (ImageView) view.findViewById(R.id.authorImageView);
        likeViewGroup = (ViewGroup) view.findViewById(R.id.likesContainer);
        imageViewLevel = (ImageView) view.findViewById(R.id.imageViewLevel);
        textViewSport = (TextView) view.findViewById(R.id.textViewSport);

        authorImageView.setVisibility(isAuthorNeeded ? View.VISIBLE : View.GONE);
        nameTextView.setVisibility(isAuthorNeeded ? View.VISIBLE : View.GONE);
        imageViewLevel.setVisibility(isAuthorNeeded ? View.VISIBLE : View.GONE);
        textViewSport.setVisibility(isAuthorNeeded ? View.VISIBLE : View.GONE);

        profileManager = ProfileManager.getInstance(context.getApplicationContext());
        postManager = PostManager.getInstance(context.getApplicationContext());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (onClickListener != null && position != RecyclerView.NO_POSITION) {
                    onClickListener.onItemClick(getAdapterPosition(), v);
                }
            }
        });

        likeViewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                if (onClickListener != null && position != RecyclerView.NO_POSITION) {
                    onClickListener.onLikeClick(likeController, position);
                }
            }
        });

        authorImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (onClickListener != null && position != RecyclerView.NO_POSITION) {
                    onClickListener.onAuthorClick(getAdapterPosition(), v);
                }
            }
        });
    }

    public void bindData(Post post) {

        likeController = new LikeController(context, post, likeCounterTextView, likesImageView, true);

        String title = removeNewLinesDividers(post.getTitle());
        titleTextView.setText(title);
        String description = removeNewLinesDividers(post.getDescription());
        detailsTextView.setText(description);
        likeCounterTextView.setText(String.valueOf(post.getLikesCount()));
        commentsCountTextView.setText(String.valueOf(post.getCommentsCount()));
        watcherCounterTextView.setText(String.valueOf(post.getWatchersCount()));

        CharSequence date = FormatterUtil.getRelativeTimeSpanStringShort(context, post.getCreatedDate());
        dateTextView.setText(date);

        String imageUrl = post.getImagePath();
        int width = Utils.getDisplayWidth(context);

        int height = (int) context.getResources().getDimension(R.dimen.post_detail_image_height);
        if(title.equals(""))
            titleTextView.getLayoutParams().height = 0;
        else
            titleTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        if(description.equals(""))
            detailsTextView.getLayoutParams().height = 0;
        else
            detailsTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

        if(imageUrl.equals("")) {
            postImageView.getLayoutParams().height = 0;
        }
        else {
            postImageView.getLayoutParams().height = height;
            // Displayed and saved to cache image, as needs for post detail.
            Glide.with(context)
                    .load(imageUrl)
                    .centerCrop()
                    .override(width, height)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .error(R.drawable.ic_stub)
                    .into(postImageView);
        }
        if (post.getAuthorId() != null) {
            profileManager.getProfileSingleValue(post.getAuthorId(), createProfileChangeListener(authorImageView, nameTextView, textViewSport, imageViewLevel));
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            postManager.hasCurrentUserLikeSingleValue(post.getId(), firebaseUser.getUid(), createOnLikeObjectExistListener());
        }
    }

    private String removeNewLinesDividers(String text) {
        int decoratedTextLength = text.length() < Constants.Post.MAX_TEXT_LENGTH_IN_LIST ?
                text.length() : Constants.Post.MAX_TEXT_LENGTH_IN_LIST;
        return text.substring(0, decoratedTextLength).replaceAll("\n", " ").trim();
    }

    private OnObjectChangedListener<Profile> createProfileChangeListener(final ImageView authorImageView, final TextView nameTextView, final TextView textViewSport, final ImageView imageViewLevel) {
        return new OnObjectChangedListener<Profile>() {
            @Override
            public void onObjectChanged(final Profile obj) {
                if (obj.getPhotoUrl() != null) {

                    Glide.with(context)
                            .load(obj.getPhotoUrl())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .crossFade()
                            .into(authorImageView);
                }
                if(obj.getUsername() != null) {
                    nameTextView.setText(obj.getUsername());

                    String[] testArray = context.getResources().getStringArray(R.array.sports);
                    textViewSport.setText(testArray[obj.getChooseSport()]);
                    if(obj.getExperience1()){
                        imageViewLevel.setImageResource(R.drawable.icon_level_red);
                    }
                    else if(obj.getExperience2()){
                        imageViewLevel.setImageResource(R.drawable.icon_level_blue);
                    }
                    else if(obj.getExperience3()){
                        imageViewLevel.setImageResource(R.drawable.icon_level_green);
                    }
                    else{
                        imageViewLevel.setImageResource(R.drawable.icon_level_yellow);
                    }
                }

            }
        };
    }

    private OnObjectExistListener<Like> createOnLikeObjectExistListener() {
        return new OnObjectExistListener<Like>() {
            @Override
            public void onDataChanged(boolean exist) {
                likeController.initLike(exist);
            }
        };
    }

    public interface OnClickListener {
        void onItemClick(int position, View view);

        void onLikeClick(LikeController likeController, int position);

        void onAuthorClick(int position, View view);
    }
}