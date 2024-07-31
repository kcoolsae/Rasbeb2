/*
 * LanguagesWithCode.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package util;

import common.LanguageInfo;

import java.util.Iterator;
import java.util.List;

/**
 * List of (all) languages with one code singled out
 */
public record LanguagesWithSelection(List<LanguageInfo> list, LanguageInfo selected) implements Iterable<LanguageInfo> {

    public LanguagesWithSelection(List<LanguageInfo> list, String lang) {
        this(list, LanguageInfo.get(lang));
    }

    public boolean isSelected(LanguageInfo languageInfo) {
        return languageInfo.code.equals(selected.code);
    }

    public boolean isNotShort() {
        return list.size() > 1;
    }

    @Override
    public Iterator<LanguageInfo> iterator() {
        return list.iterator();
    }

    public String code() {
        return selected.code;
    }

    public String name() {
        return selected.name;
    }
}
