Contest system
===
What a pupil sees when they use the system.

{% include _menu.md %}
* [Management system](management-system.html) (teacher's view)
* [Management system](organisers-system.html) (organiser's view)

---

When first accessing the application from a specific browser you indicate your preferred interface language. 
This language can be changed at any time. 

After logging in with a teacher provided username and password you arrive at the _landing page_. That page
contains list of contests in which you can participate or already have participated. Each entry has one of the following
options
* The contest can not yet be taken
* The contest is open. Clicking brings you to the _start contest_ page (cf. below)
* You have already participated in the contest but feedback (marks, correct answers, ...) is not yet available
* You have participated and feedback is available. In this case also your score for the contest is printed. Clicking brings you to the _feedback page_ (cf. below)

### Participating

The _start contest_ page gives you a warning that you are about to start a contest. There is a
button to opt out (return to the landing page) and a button to proceed, bringing you to the contest proper.
Once you have pressed the latter button, the clock starts running and the contest begins. You can then no longer take
the contest at a later time.

A contest page shows
* the question text (consisting of text and images - full HTML, possibly including
  interactive parts) 
* the '_interface_' for answering that question. This interface consists of
  * a set of buttons labeled A, B, C, ..., for a multiple choice question
  * a text field, for an open integer or open text question
  * a submit button, for certain types of interactive questions
  The interface also contains a button to reset/clear your answer - not giving an answer is less costly than
  giving an incorrect answer.
* the title of the current question
* the marks assigned to that question - when answered correct vs. incorrect
* a clock (counting down) that shows you the time left to finish the contest
* a list of (the titles of) all questions that you need to answer. Normally
the contest starts with the first question and proceeds to the next every time you submit an answer, but
using that list it is also possible to jump back and forward between questions.

There is also a button by which you can indicate that you have finished (early). This shows a warning page - once finished
you cannot restart - and gives you the choice of either returning to the contest
or jumping back to the landing page.

You also automatically return to the landing page (with an intermediate explanation page) when you submit an answer
after the deadline.

### Feedback

On the landing page, when you click on a contest to which you participated and for which feedback is already unlocked,
you jump to a general overview page that contains your total score for the contest and a table of all questions, with, for each question
* The title
* Whether you answered correctly, incorrectly or left the answer blank
* How much marks you gained/lost for this question

Clicking on a question in this table brings you to a detailed feedback page.
This feedback page has a similar setup as a contest page
* in addition to the question text you now also see the corresponding feedback text (correct answer, why this is correct, 
  the 'it's informatics' section, the 'it's computational thinking', ...) 
* whether you answered this question correctly and how much marks this has earned/lost you
* you can again use the list of all questions to jump back and forward between pages

From this detailed feedback page you can return to the general overview and from there 
go back to the landing page.

### Logged in twice

It may happen that you are logged in twice at the same time, e.g., because your PC malfunctioned during a contest
and you had to move to another PC to continue. Although in principle this does not need to lead to problems for the application, some extra actions are
taken in case to avoid confusion.

If you started a contest which was not already closed by the teacher or for which you did not press the 'finished' button, then
* logging in or performing any other action while already logged in, brings you to a page that warns you that you are in the middle of a contest and allows you
  to resume that contest
* if this happens from a different PC, different browser window or browser tab, then an additional warning is given that you are trying to participat from two different
  places at the same time. You are then given the possibility of taking 'ownership' of that participation session
* if you submit an answer to a contest from a browser tab which does not have ownership, that answer will not be registered and a corresponding warning is given

Logging out does not finish a contest. In that case you can log in to resume participation.

In case of technical problems, the teacher can extend your participation deadline by some 'grace minutes'.

### Anonymous participation

The application provides a list of _public competitions_ (quizzes) in which you can participate anonymously[^2].
Anonymous participation is very similar to regular participations, except that
* you do not login
* after you finish the contest you are taken immediately to the feedback page (general overview)
* there is no way to return to the feedback pages once you have left them. (A corresponding warning is given.)

#### Footnotes

[^2]:
     In Rasbeb 1 this list was on the home page of the application to ensure maximum visibility. Unfortunately,
     that meant that occasionally a pupil would 'forget' to log in and take a public contest instead of the 
     official contest they were supposed to.

