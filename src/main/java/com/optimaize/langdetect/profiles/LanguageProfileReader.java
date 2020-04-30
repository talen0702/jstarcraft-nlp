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

package com.optimaize.langdetect.profiles;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.NotNull;

import com.optimaize.langdetect.frma.LangProfileReader;

/**
 * Reads {@link LanguageProfile}s.
 *
 * @author Fabian Kessler
 */
public class LanguageProfileReader {

    private static final LangProfileReader internalReader = new LangProfileReader();
    private static final String PROFILES_DIR = "languages";

    /**
     * Reads a {@link LanguageProfile} from a File in UTF-8.
     */
    public LanguageProfile read(File profileFile) throws IOException {
        return OldLangProfileConverter.convert(internalReader.read(profileFile));
    }

    /**
     * Reads a {@link LanguageProfile} from an InputStream in UTF-8.
     */
    public LanguageProfile read(InputStream inputStream) throws IOException {
        return OldLangProfileConverter.convert(internalReader.read(inputStream));
    }

    /**
     * Load profiles from the classpath in a specific directory.
     *
     * <p>
     * This is usually used to load built-in profiles, shipped with the jar.
     * </p>
     *
     * @param classLoader      the ClassLoader to load the profiles from. Use {@code MyClass.class.getClassLoader()}
     * @param profileDirectory profile directory path inside the classpath. The default profiles are in "languages".
     * @param profileFileNames for example ["en", "fr", "de"].
     */
    public List<LanguageProfile> read(ClassLoader classLoader, String profileDirectory, Collection<String> profileFileNames) throws IOException {
        List<LanguageProfile> loaded = new ArrayList<>(profileFileNames.size());
        for (String profileFileName : profileFileNames) {
            String path = makePathForClassLoader(profileDirectory, profileFileName);
            try (InputStream in = classLoader.getResourceAsStream(path)) {
                if (in == null) {
                    throw new IOException("No language file available named " + profileFileName + " at " + path + "!");
                }
                loaded.add(read(in));
            }
        }
        return loaded;
    }

    private String makePathForClassLoader(String profileDirectory, String fileName) {
        // WITHOUT slash before the profileDirectory when using the classloader!
        return profileDirectory + '/' + fileName;
    }

    /**
     * Same as {@link #read(ClassLoader, String, java.util.Collection)} using the class loader of this class.
     */
    public List<LanguageProfile> read(String profileDirectory, Collection<String> profileFileNames) throws IOException {
        return read(LanguageProfileReader.class.getClassLoader(), profileDirectory, profileFileNames);
    }

    /**
     * Same as {@link #read(ClassLoader, String, java.util.Collection)} using the class loader of this class, and the default profiles directory of this library.
     */
    public List<LanguageProfile> read(Collection<String> profileFileNames) throws IOException {
        return read(LanguageProfileReader.class.getClassLoader(), PROFILES_DIR, profileFileNames);
    }

    @NotNull
    public LanguageProfile readBuiltIn(@NotNull Locale locale) throws IOException {
        String filename = makeProfileFileName(locale);
        String path = makePathForClassLoader(PROFILES_DIR, filename);
        try (InputStream in = LanguageProfileReader.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IOException("No language file available named " + filename + " at " + path + "!");
            }
            return read(in);
        }
    }

    @NotNull
    private String makeProfileFileName(@NotNull Locale locale) {
        return locale.toLanguageTag();
    }

    @NotNull
    public List<LanguageProfile> readBuiltIn(@NotNull Collection<Locale> languages) throws IOException {
        List<String> profileNames = new ArrayList<>();
        for (Locale locale : languages) {
            profileNames.add(makeProfileFileName(locale));
        }
        return read(LanguageProfileReader.class.getClassLoader(), PROFILES_DIR, profileNames);
    }

    /**
     * @deprecated renamed to readAllBuiltIn()
     */
    public List<LanguageProfile> readAll() throws IOException {
        return readAllBuiltIn();
    }

    /**
     * Reads all built-in language profiles from the "languages" folder (shipped with the jar).
     */
    public List<LanguageProfile> readAllBuiltIn() throws IOException {
        List<LanguageProfile> loaded = new ArrayList<>();
        for (Locale locale : BuiltInLanguages.getLanguages()) {
            loaded.add(readBuiltIn(locale));
        }
        return loaded;
    }

    /**
     * Loads all profiles from the specified directory.
     *
     * Do not use this method for files distributed within a jar.
     *
     * @param path profile directory path
     * @return empty if there is no language file in it.
     */
    public List<LanguageProfile> readAll(File path) throws IOException {
        if (!path.exists()) {
            throw new IOException("No such folder: " + path);
        }
        if (!path.canRead()) {
            throw new IOException("Folder not readable: " + path);
        }
        File[] listFiles = path.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return looksLikeLanguageProfileFile(pathname);
            }
        });
        if (listFiles == null) {
            throw new IOException("Failed reading from folder: " + path);
        }

        List<LanguageProfile> profiles = new ArrayList<>(listFiles.length);
        for (File file : listFiles) {
            if (!looksLikeLanguageProfileFile(file)) {
                continue;
            }
            profiles.add(read(file));
        }
        return profiles;
    }

    private boolean looksLikeLanguageProfileFile(File file) {
        if (!file.isFile()) {
            return false;
        }
        return looksLikeLanguageProfileName(file.getName());
    }

    private boolean looksLikeLanguageProfileName(String fileName) {
        if (fileName.contains(".")) {
            return false;
        }
        try {
            Locale.forLanguageTag(fileName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
