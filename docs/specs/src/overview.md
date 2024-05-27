Overview
===
{% include _menu.md %}

---

The web application supports three different kinds of contest:

* The _official_ competition, which can only be taken during the official Bebras contest weeks.
* _Restricted_ contests, which can only be taken by registered pupils. Such competitions can
be run under supervision of a teacher. These contests can serve as a tryout for the official
competition, or can be used in the context of a computer science or computational thinking lesson.
* _Public_ quizzes, which anyone can do anonymously, without the need for registration.

Each contest consists of a series of questions which must be answered online, within a restricted
time period. Question sets maybe provided in more than one language, and also the user interface is multilingual. 
A language is chosen when the user first
enters the application[^1].

Question sets are different for pupils of different age. Question sets often overlap, and
sometimes the same question is given less or more weight according to the age of the contestant.
Questions can be shared among different contests.

Questions can have different formats:
* Multiple choice format. The number of answer choices is usually 4, but can be any number.
* Simple open answer (text). 
* {:.new} The answer is a single integer.
* {:.new} *[Interactive questions](interactive.html)*.

Note that the correct answer need not be the same across different translations of the same question[^2].

Questions are essentially HTML pages, possibly with some additional JavaScript. These
pages are prepared *outside the application* and are uploaded separately.

The application allows for a large number of simultaneous users. For that reason it must be possible to store the
questions and answer pages on a (cluster of) web server(s) that is separate from the application
server.

{:.new}
The contest proper and the contest management system are separate web applications which again can
be run on distinct servers. The database server can also be separate from the web server(s).

Pupils can only register through their teachers.  Teachers register by e-mail to the organisers. 
Teachers can register other teachers <span class="new">of the same school</span>
(and should be encouraged to do so). See also: [roles](roles.html).

### Nice-to-haves?

Question formats
* Additional question formats, e.g., questions with two separate answers which must both be correct ('What is the minimum and maximum number of boxes...')
* Restrict the possible open text answers, e.g., by supplying a regular expression which the answer must match.
* Allow variants for the answer, e.g., by storing the answer as a regular expression which must be matched. There are two problems with this: ideally, 
  checking whether an answer is correct should be done at the level of the database. Authors should ot have to write these regular expressions themselves.

#### Footnotes

[^1]: 
    The user interface language need not necessarily be the same as the language in 
    which the questions are set. See [language support](languages.html).

[^2]: 
    For multiple choice questions the number of answer options must be the same
    in different translations of the same question.