/*
 * PupilDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.teacher;

import be.ugent.caagt.dao.DataAccessException;
import be.ugent.rasbeb2.db.dto.Gender;
import be.ugent.rasbeb2.db.dto.PupilInClass;
import be.ugent.rasbeb2.db.poi.DataOrError;
import be.ugent.rasbeb2.db.poi.PupilSheetReader;
import be.ugent.rasbeb2.db.poi.PupilSheetWriter;
import controllers.teacher.routes;
import deputies.TeacherOnlyDeputy;
import lombok.Getter;
import lombok.Setter;
import play.data.Form;
import play.libs.Files;
import play.mvc.Http;
import play.mvc.Result;
import views.html.notuploaded;

import java.util.List;

public class PupilDeputy extends TeacherOnlyDeputy {
    @Getter
    @Setter
    public static class PupilData {
        public String name;
        public Gender gender;
        public String password;
    }

    public Result addPupil(int classId) {
        Form<PupilData> form = formFromRequest(PupilData.class);
        if (form.hasErrors()) {
            error("school.new-pupil.failed"); // this should not happen
        } else {
            PupilData data = form.get();
            dac().getClassesDao().addPupil(classId, data.name, data.gender, data.password);
            success("school.new-pupil.success");
        }
        return redirect(routes.ClassController.getClasses());
    }

    public Result editPupil(int pupilId) {
        Form<PupilData> form = formFromRequest(PupilData.class);
        if (form.hasErrors()) {
            error("school.edit-pupil.failed"); // this should not happen
        } else {
            PupilData data = form.get();
            dac().getClassesDao().editPupil(pupilId, data.name, data.gender, data.password);
            success("school.edit-pupil.success");
        }
        return redirect(routes.ClassController.getClasses());
    }

    public Result removePupil(int pupilId) { // not used anymore
        try {
            dac().getClassesDao().removePupil(pupilId);
            success("school.delete-pupil.success");
        } catch (DataAccessException ex) {
            error("school.delete-pupil.failed");
        }
        return redirect(routes.ClassController.getClasses());
    }

    private Gender translateGender(String genderString) {
        for (Gender gender : Gender.values()) {
            if (i18n("enum.gender-short." + gender.name()).equalsIgnoreCase(genderString)) {
                return gender;
            }
        }
        return null;
    }

    public Result uploadPupils() {
        Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<Files.TemporaryFile> part = body.getFile("students");
        int yearId = getCurrentYearId();

        try {
            List<DataOrError<PupilInClass>> pupils = new PupilSheetReader(dac().getClassesDao(), yearId, this::translateGender).read(part.getRef().path());
            for (DataOrError<PupilInClass> p : pupils) {
                if (p.hasError()) {
                    return badRequest(notuploaded.render(pupils, this));
                }
            }
            // no errors
            dac().getClassesDao().addPupils(pupils, yearId);
            success("spreadsheet.pupils.success");
        } catch (Exception exception) {
            error("spreadsheet.pupils.error");
        }
        return redirect(routes.ClassController.getClasses());
    }

    public Result downloadPupils() {
        List<PupilInClass> pupils = dac().getClassesDao().getPupilsByClass(getCurrentYearId());
        return ok(new PupilSheetWriter(this::i18n).write(pupils))
                .as("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .withHeader(
                        "Content-Disposition",
                        "attachment; filename=" + i18n("spreadsheet.filename.pupils") + ".xlsx"
                );
    }

}
