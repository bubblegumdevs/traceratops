/*
 * Copyright 2015 Bubblegum Developers
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
 */

package com.bubblegum.traceratops.app.profiles;

public abstract class ProfileUpdateNotifier implements AppProfile.OnEntryListUpdatedListener {

    AppProfile profile;

    public ProfileUpdateNotifier(AppProfile currentProfile) {
        profile = currentProfile;
    }

    public void setProfile(AppProfile profile) {
        AppProfile oldProfile = this.profile;
        if(oldProfile!=null) {
            oldProfile.removeOnEntryListUpdatedListener(this);
        }
        this.profile = profile;
        if(this.profile!=null) {
            this.profile.addOnEntryListUpdatedListener(this);
        }
        onProfileChanged(profile, oldProfile);
    }

    protected abstract void onProfileChanged(AppProfile newProfile, AppProfile oldProfile);

    public void onNewProfileAdded(AppProfile profile) {

    }
}
