/*
 * YearController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.other;

import controllers.OrganiserOnlyController;
import play.mvc.Http;
import play.mvc.Result;
import deputies.other.YearDeputy;

public class YearController extends OrganiserOnlyController<YearDeputy> {

    public YearController(){
        super(YearDeputy::new);
    }

    public Result newYear (Http.Request request) {
        return createDeputy(request).newYear();
    }

    public Result listYears (Http.Request request) {
        return createDeputy(request).listYears();
    }

    public Result updateDeleteYear (Http.Request request, int yearId) {
        return createDeputy(request).updateDeleteYear(yearId);
    }
}
