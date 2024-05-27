/*
 * QuestionUploadDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.question;

import be.ugent.rasbeb2.db.dao.QuestionDao;
import be.ugent.rasbeb2.db.dto.QuestionWithTranslations;
import be.ugent.rasbeb2.db.dto.Translation;
import be.ugent.rasbeb2.db.poi.DataOrError;
import be.ugent.rasbeb2.db.poi.QuestionSheetReader;
import controllers.question.routes;
import deputies.OrganiserOnlyDeputy;
import lombok.Getter;
import lombok.Setter;
import play.data.Form;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.libs.Files;
import play.mvc.Http;
import play.mvc.Result;
import views.html.question.upload_questions;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class QuestionUploadDeputy extends OrganiserOnlyDeputy {

    public Result uploadPages(int questionId, String lang) {
        boolean uploadOK = true;

        Http.MultipartFormData<Files.TemporaryFile> formData = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<Files.TemporaryFile> part = formData.getFile("questionFile");
        if (part != null) {
            uploadOK = uploadToServer(part.getRef().path(), questionId, lang, QuestionDao.FileType.QUESTION);
        }
        part = formData.getFile("feedbackFile");
        if (part != null) {
            uploadOK &= uploadToServer(part.getRef().path(), questionId, lang, QuestionDao.FileType.FEEDBACK);
        }
        if (uploadOK) {
            success("question.upload.success");
        } else {
            error("question.upload.error");
        }
        return redirect(routes.QuestionDetailController.getQuestion(questionId, lang));
    }

    private boolean uploadToServer(Path path, int questionId, String lang, QuestionDao.FileType fileType) {
        String script = config.getString("rasbeb2.upload-script");
        if (script == null) {
            throw new RuntimeException("No upload script configured (rasbeb2.upload-script)");
        }
        QuestionDao dao = dac().getQuestionDao();

        ProcessBuilder builder = new ProcessBuilder(
                script, dao.getMagic(questionId, fileType), lang, Integer.toString(questionId)
        );
        builder.redirectInput(path.toFile());
        try {
            Process process = builder.start();
            process.waitFor(); // TODO: do this asynchronously?
            if (process.exitValue() == 0) {
                dao.setUploaded(questionId, lang, fileType);
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            throw new RuntimeException("Problems with upload script", ex);
        }
    }

    @Getter
    @Setter
    public static class LanguageData {
        @Constraints.Required
        @Formats.NonEmpty
        public String langs;

        public List<String> languages() {
            return Arrays.asList(langs.split("\\s*,\\s*"));
        }
    }

    public Result uploadQuestionsForm() {
        return ok(upload_questions.render(emptyForm(LanguageData.class), this));
    }

    public Result uploadQuestions() {
        Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<Files.TemporaryFile> part = body.getFile("questionsFile");

        Form<LanguageData> form = formFromRequest(LanguageData.class);
        if (!form.hasErrors()) {
            // check language format?
            List<String> languages = form.get().languages();
            int i = 0;
            while (i < languages.size() && languages.get(i).length() == 2) {
                i++;
            }
            if (i < languages.size()) {
                form = form.withError("langs", "question.language-format-error");
            }
            // errors fall through
        }

        if (!form.hasErrors() && part != null) {
            try {
                List<DataOrError<QuestionWithTranslations>> list = new QuestionSheetReader(form.get().languages()).read(part.getRef().path());
                QuestionDao dao = dac().getQuestionDao();
                boolean warnings = false;
                for (DataOrError<QuestionWithTranslations> item : list) {
                    warnings |= item.hasError();
                    QuestionWithTranslations data = item.getData();
                    if (dao.questionExists(data.externalId())) {
                        warnings = true;
                        item.addError("question.already-exists");
                    } else {
                        int questionId = dao.createQuestion(data.answerType(), data.typeExtra(), data.externalId());
                        for (Translation translation : data.translations()) {
                            dao.addTranslation(questionId, translation);
                        }
                    }
                }
                if (warnings) {
                    error("question.upload-errors");
                    return badRequest(views.html.poi.questions_warnings.render(list, this));
                }
                success("question.uploaded");
            } catch (IOException ex) {
                error("question.no-file");
            }
        }
        // errors fall through
        return badRequest(upload_questions.render(form, this));
    }

}

