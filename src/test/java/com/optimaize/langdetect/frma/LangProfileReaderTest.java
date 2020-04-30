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
import java.io.IOException;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import com.optimaize.langdetect.cybozu.util.LangProfile;

public class LangProfileReaderTest {
    private static final File PROFILE_DIR = new File(new File(new File(new File("src"), "main"), "resources"), "languages");

    @Test
    public void readEnFile() throws IOException {
        checkProfileFile("en", 3, 2301);
    }

    @Test
    public void readBnFile() throws IOException {
        checkProfileFile("bn", 3, 2846);
    }

    @Test
    public void readFrFile() throws IOException {
        checkProfileFile("fr", 3, 2232);
    }

    @Test
    public void readNlFile() throws IOException {
        checkProfileFile("nl", 3, 2163);
    }

    private static void checkProfileFile(String language, int nWordSize, int freqSize) throws IOException {
        File profileFile = new File(PROFILE_DIR, language);
        final LangProfile langProfile = new LangProfileReader().read(profileFile);
        assertThat(langProfile, CoreMatchers.is(CoreMatchers.notNullValue()));
        assertThat(langProfile.getName(), CoreMatchers.is(CoreMatchers.equalTo(language)));
        assertThat(langProfile.getNWords(), CoreMatchers.is(CoreMatchers.notNullValue()));
        assertThat(langProfile.getNWords().length, CoreMatchers.is(CoreMatchers.equalTo(nWordSize)));
        assertThat(langProfile.getFreq(), CoreMatchers.is(CoreMatchers.notNullValue()));
        assertThat(langProfile.getFreq().size(), CoreMatchers.is(CoreMatchers.equalTo(freqSize)));
    }

}
