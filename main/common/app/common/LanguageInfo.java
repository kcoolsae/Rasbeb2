/*
 * LanguageInfo.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.io.InputStream;

@SuppressWarnings("ClassCanBeRecord")
public class LanguageInfo {
    // the scalac compiler really does not like records (yet)
    public final String code;
    public final String name;

    public LanguageInfo(String code, String name) {
        this.code = code;
        this.name = name;
    }

    static final Map<String, LanguageInfo> LANGUAGEINFO_MAP = new HashMap<>();

    static {
        Properties props = new Properties();
        try (InputStream in = LanguageInfo.class.getResourceAsStream("/resources/languages.properties")) {
            props.load(in);
            props.forEach((k, v) -> LANGUAGEINFO_MAP.put(k.toString(), new LanguageInfo(k.toString(), v.toString())));
        } catch (Exception ex) {
            // TODO log
        }
    }

    public static LanguageInfo get(String code) {
        return LANGUAGEINFO_MAP.get(code);
    }

    public static List<LanguageInfo> list(List<String> languages) {
        return languages.stream().map(LanguageInfo::get).toList();
    }
}
