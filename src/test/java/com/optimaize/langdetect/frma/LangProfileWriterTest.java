/*
 * Copyright 2011 Francois ROLAND
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

package com.optimaize.langdetect.frma;

import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import com.optimaize.langdetect.cybozu.util.LangProfile;

public class LangProfileWriterTest {
    private static final File PROFILE_DIR = new File(new File(new File(new File("src"), "main"), "resources"), "languages");

    @Test
    public void writeEnProfile() throws IOException {
        checkProfileCopy("en");
    }

    @Test
    public void writeFrProfile() throws IOException {
        checkProfileCopy("fr");
    }

    @Test
    public void writeNlProfile() throws IOException {
        checkProfileCopy("nl");
    }

    protected void checkProfileCopy(String language) throws IOException {
        File originalFile = new File(PROFILE_DIR, language);
        final LangProfile originalProfile = new LangProfileReader().read(originalFile);
        File newFile = File.createTempFile("profile-copy-", null);
        try (FileOutputStream output = new FileOutputStream(newFile)) {
            new LangProfileWriter().write(originalProfile, output);
            LangProfile newProfile = new LangProfileReader().read(newFile);
            assertThat(newProfile.getFreq().size(), CoreMatchers.is(CoreMatchers.equalTo(originalProfile.getFreq().size())));
            assertThat(newProfile.getFreq(), CoreMatchers.is(CoreMatchers.equalTo(originalProfile.getFreq())));
            assertThat(newProfile.getNWords(), CoreMatchers.is(CoreMatchers.equalTo(originalProfile.getNWords())));
            assertThat(newProfile.getName(), CoreMatchers.is(CoreMatchers.equalTo(originalProfile.getName())));
        } finally {
            // noinspection ResultOfMethodCallIgnored
            newFile.delete();
        }
    }

}
