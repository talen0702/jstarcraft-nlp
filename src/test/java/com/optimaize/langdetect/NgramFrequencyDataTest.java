/*
 * Copyright 2011 Fabian Kessler
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

package com.optimaize.langdetect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableSet;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;

/**
 * Some rudimentary tests for NgramFrequencyData.
 *
 * @author Fabian Kessler
 */
public class NgramFrequencyDataTest {

    private static NgramFrequencyData allThreeGrams;

    @BeforeAll
    public static void init() throws IOException {
        allThreeGrams = forAll(3);
    }

    private static NgramFrequencyData forAll(int gramSize) throws IOException {
        List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();
        return NgramFrequencyData.create(languageProfiles, ImmutableSet.of(gramSize));
    }

    @Test
    public void size() throws Exception {
        // update the number when adding built-in languages
        assertEquals(allThreeGrams.getLanguageList().size(), 71);
    }

    @Test
    public void constantOrder() throws Exception {
        // expect constant order:
        int pos = 0;
        for (Locale locale : allThreeGrams.getLanguageList()) {
            assertEquals(allThreeGrams.getLanguage(pos), locale);
            pos++;
        }
    }

    @Test
    public void expectGram() throws Exception {
        // this must exist in many languages
        double[] probabilities = allThreeGrams.getProbabilities("dam");
        assert probabilities != null;
        assertTrue(probabilities.length >= 5 && probabilities.length <= allThreeGrams.getLanguageList().size());
    }

    @Test
    public void forbidGramOfWrongSize() throws Exception {
        // we said 3-grams, not 2 grams
        assertEquals(allThreeGrams.getProbabilities("da"), null);
    }

}
