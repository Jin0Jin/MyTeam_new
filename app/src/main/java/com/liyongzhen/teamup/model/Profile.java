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

package com.liyongzhen.teamup.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Profile implements Serializable {

    private String id;
    private String username;
    private String email;
    private String photoUrl;
    private long likesCount;
    private String registrationToken;

    private int gender =0;
    private int chooseSport = 0;
    private String dateofBirth = "";
    private boolean experience1;
    private boolean experience2;
    private boolean experience3;
    private boolean experience4;

    public Profile() {
        // Default constructor required for calls to DataSnapshot.getValue(Profile.class)
    }

    public Profile(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }

    public String getRegistrationToken() {
        return registrationToken;
    }

    public void setRegistrationToken(String registrationToken) {
        this.registrationToken = registrationToken;
    }
    public void setGender(int gender){this.gender = gender;}
    public int getGender(){return gender;}
    public void setChooseSport(int chooseSport){this.chooseSport = chooseSport;}
    public int getChooseSport(){return chooseSport;}
    public void setDateofBirth(String dateofBirth){this.dateofBirth=dateofBirth;}
    public String getDateofBirth(){return dateofBirth;}

    public void setExperience1(boolean experience1){this.experience1 = experience1;}
    public boolean getExperience1(){return experience1;}
    public void setExperience2(boolean experience2){this.experience2=experience2;}
    public boolean getExperience2(){return experience2;}
    public void setExperience3(boolean experience3){this.experience3 = experience3;}
    public boolean getExperience3(){return experience3;}
    public void setExperience4(boolean experience4){this.experience4=experience4;}
    public boolean getExperience4(){return experience4;}
}
