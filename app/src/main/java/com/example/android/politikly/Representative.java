/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.politikly;

/**
 * {@Event} represents an earthquake event.
 */
public class Representative {

    /** Title of the earthquake event */
    public final String representative;
    public final String party;
    public final String description;
    public final String website;

    /**
     * Constructs a new {@link District}.
     *
     * @param eventRep is the title of the earthquake event
     */
    public Representative(String eventRep, String eventParty, String eventDescription, String eventWebsite) {
        representative = eventRep;
        party = eventParty;
        description = eventDescription;
        website = eventWebsite;
    }
}
