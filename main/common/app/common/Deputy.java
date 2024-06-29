/*
 * Deputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package common;

import be.ugent.caagt.play.binders.PSF;
import be.ugent.caagt.play.binders.StringMap;
import lombok.Getter;
import lombok.Setter;
import play.data.Form;
import play.mvc.Call;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Standard super class for all deputies of this project. Enhances the 'standard' deputy with the following
 * functionality
 * <ul>
 * <li>Stores errors/warnings/... and transfers them to the flash when redirecting</li>
 * <li>Provides helper methods for handling paged tables </li>
 * </ul>
 */
public class Deputy extends be.ugent.caagt.play.deputies.Deputy {

    // Array with error/warning/success/info messages, in that order
    private final String[] statuses = new String[4];

    /**
     * Will flash a success message, unless a warning or error message is already in the flash.
     */
    protected void success(String message, Object... args) {
        statuses[2] = i18n(message, args);
    }

    /**
     * Will flash an error message.
     */
    protected void error(String message, Object... args) {
        statuses[0] = i18n(message, args);
    }

    /**
     * Will flash a warning message, unless an error message is already in the flash.
     */
    protected void warning(String message, Object... args) {
        statuses[1] = i18n(message, args);
    }

    /**
     * Will flash an informational message, unless a success, warning or error message
     * is already in the flash.
     */
    protected void info(String message, Object... args) {
        statuses[3] = i18n(message, args);
    }

    private static final String[] STATUS_STRINGS = {"danger", "warning", "success", "info"};

    /**
     * The type of the most serious error stored with this handler, or null if none.
     *
     * @return String describing the error type, according to Bootstrap 5 conventions.
     */
    public String getStatusType() {
        int i = 0;
        while (i < statuses.length && statuses[i] == null) {
            i++;
        }
        if (i < statuses.length) {
            return STATUS_STRINGS[i];
        } else {
            return null;
        }
    }

    /**
     * The localized message for the most serious error stored with this handler, or null if none.
     */
    public String getStatusMessage() {
        int i = 0;
        while (i < statuses.length && statuses[i] == null) {
            i++;
        }
        if (i < statuses.length) {
            return statuses[i];
        } else {
            return null;
        }
    }

    /**
     * Redirects to the given call, with a result adjusted as follows
     * <ul>
     *     <li>The most serious error message stored in this handler is added to the flash</li>
     * </ul>
     */
    @Override
    protected Result redirect(Call call) {
        Result result = super.redirect(call);
        String type = getStatusType();
        if (type != null) {
            result = result.flashing(type, getStatusMessage());
        }
        return result;
    }

    public Result redirect(String uri) {
        Result result = Results.redirect(uri);
        String type = getStatusType();
        if (type != null) {
            result = result.flashing(type, getStatusMessage());
        }
        return result;
    }

    public Http.Cookie createPageSizeCookie(int size) {
        return Http.Cookie.builder("pageSize", size + "")
                .withMaxAge(Duration.ofDays(365))
                .withPath("/")
                .withSecure(false)
                .withHttpOnly(false)
                .build();
    }

    /**
     * Return the page size (for paged tables) currently preferred by the user, as stored in the 'pageSize' cookie
     */
    private int getPageSizeCookie() {
        try {
            Optional<Http.Cookie> cookie = request.cookie("pageSize");
            return cookie.map(value -> Integer.parseInt(value.value())).orElse(5);
        } catch (Exception ex) { // number format, null pointer, ...
            return 5;
        }
    }

    public List<String> getUILanguages() {
        return config.getStringList("play.i18n.langs");
    }

    protected List<LanguageInfo> getUILanguagesInfo() {
        return getUILanguages().stream().map(LanguageInfo.LANGUAGEINFO_MAP::get).toList();
    }

    @Getter
    @Setter
    public static class SizeData {
        public int pageSize;
    }

    /**
     * Process a form (with field 'size') to set the page size for paged tables. Note that
     * the result of the form still needs to be stored in a cookie.
     */
    protected int getPageSizeFromForm() {
        Form<SizeData> form = formFromRequest(SizeData.class);
        if (form.hasErrors()) {
            return 10;
        } else {
            return form.get().pageSize;
        }
    }

    /**
     * Process a form to set a new filter for paged tables. Only considers the form fields from the listed enumerated type
     */
    protected <E extends Enum<E>> StringMap getStringMapFromForm(Class<E> enumClass) {
        return new StringMap(mapFromRequest(), enumClass);
    }

    /**
     * Return the initial PSF for a paged table. The table is sorted according to the given column.
     */
    private PSF getInitialPSF(String name, boolean ascending) {
        // may need to become protected - or else be inlined
        return new PSF(name, ascending, getPageSizeCookie());
    }

    /**
     * Return the initial PSF for a paged table. The table is sorted according to the given column., in ascending
     * order.
     */
    private PSF getInitialPSF(String name) {
        // may need to become protected - or else be inlined
        return getInitialPSF(name, true);
    }

    protected <E extends Enum<E>> PSF getInitialPSF(E field) {
        return getInitialPSF(field.name(), true);
    }

    /**
     * Retrieves the current UI language
     */
    public String getLanguage() {
        return getLocale().getLanguage();
    }

    protected boolean isLanguageSet() {
        return request.cookie(getMessagesApi().langCookieName()).isPresent();
    }

    public boolean isOrganiser() {
        return getFromSession(Session.ROLE).equals("ORGANISER");
    }
}
