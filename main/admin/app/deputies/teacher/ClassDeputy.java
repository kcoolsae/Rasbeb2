/*
 * ClassDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.teacher;

import be.ugent.caagt.dao.DataAccessException;
import be.ugent.rasbeb2.db.dao.ClassesDao;
import be.ugent.rasbeb2.db.dto.ClassWithPupils;
import deputies.TeacherOnlyDeputy;
import lombok.Getter;
import lombok.Setter;
import play.data.Form;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.mvc.Result;

public class ClassDeputy extends TeacherOnlyDeputy {

    public Result getClasses() {
        ClassesDao dao = dac().getClassesDao();
        Iterable<ClassWithPupils> classesWithPupils = dao.getClassesWithPupils(getCurrentYearId());
        if (isActiveYear()) {
            return ok(views.html.school.classes.render(
                    emptyForm(ClassesData.class),
                    classesWithPupils,
                    this));
        } else {
            return ok(views.html.school.classes_inactive.render(
                    classesWithPupils,
                    this));
        }
    }


    @Getter
    @Setter
    public static class ClassesData {
        @Constraints.Required
        @Formats.NonEmpty
        public String classNames;
    }

    public Result newClasses() {
        Form<ClassesData> form = formFromRequest(ClassesData.class);
        if (form.hasErrors()) {
            // should not happen
            return badRequest();
        } else {
            // TODO split into separate class names here, not in DAO
            // TODO signal if class already existed
            dac().getClassesDao().addClasses(form.get().classNames, getCurrentYearId());
            success("school.classes.success-added");
            return redirect(controllers.home.routes.TeacherHomeController.index());
        }
    }

    @Getter
    @Setter
    public static class UpdateDeleteData {
        // validation must be done in the deputy method
        public String className;
        public String action;
    }

    public Result updateDeleteClass(int classId) {
        // similar to YearDeputy.updateDeleteYear
        Form<UpdateDeleteData> form = formFromRequest(UpdateDeleteData.class);
        if (form.hasErrors()) {
            return badRequest(); // this should not happen
        } else {
            UpdateDeleteData data = form.get();
            if ("delete".equals(data.action)) {
                deleteClass(classId);
            } else {
                updateClass(classId, data.className);
            }
            return redirect(controllers.home.routes.TeacherHomeController.index());
        }
    }

    private void deleteClass(int classId) {
        try {
            dac().getClassesDao().removeClass(classId);
            success("school.classes.success-deleted");
        } catch (DataAccessException ex) {
            error("school.classes.error-deleted");
        }
    }

    private void updateClass(int classId, String className) {
        if (className == null || className.isBlank()) {
            // this should not happen
        } else {
            dac().getClassesDao().editClass(className, classId);
            success("school.classes.success-updated");
        }
    }

}
