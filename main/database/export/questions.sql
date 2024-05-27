\copy questions(question_id,question_type,question_type_xtra,question_external_id, question_magic_q, question_magic_f) TO 'questions.dat' DELIMITER '|' ENCODING 'UTF-8'
\copy questions_i18n(question_id,lang,question_title,question_correct_answer,question_uploaded_q,question_uploaded_f) TO 'questions_i18n.dat' DELIMITER '|' ENCODING 'UTF-8'
\copy questions_in_set(contest_id,age_group_id,question_id,question_seq_nr,question_marks_if_correct,question_marks_if_incorrect) TO 'questions_in_set.dat' DELIMITER '|' ENCODING 'UTF-8'
--  questions.sql
--  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
--  Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
--
--  This software is distributed under the MIT License - see files LICENSE and AUTHORS
--  in the top level project directory.

-- marks not exported - immutable
