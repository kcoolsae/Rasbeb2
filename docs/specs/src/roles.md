Roles and ids
===
{% include _menu.md %}

---

An _anonymous user_ can simply access the application, choose one of the public 'contests'
from a list and start it. When finished they get their marks and can also see what the correct
answers were supposed to be. Once logged out there is no way for an anonymous user to revisit
their results and the correct answers.  

A _pupil_ belongs to a certain class in a certain school and has been registered through their teacher.
They can take part in all competitions (restricted and official) that were opened for their class by their
teacher.

A _teacher_ can register classes and pupils
and organize contests using the restricted question sets.
A teacher can supervise the official competition for their pupils during the official competition week.
<span class="new">Teachers are linked to a (single) school at registration time.</span>
Teachers are registered by an organiser (by e-mail outside the system) or by another teacher of
the same school, the latter being the preferred way.

An _organiser_, of which there are only few, can register teachers with the application, upload and
organize question sets, and do some general housekeeping. Organisers can mimic teachers or pupils: they
can instruct the system to behave as if a specific user was logged in. <span class="new">After they log out, they
then automatically resume their role as organiser.</span>

Question _authors_ are the people that write questions — as HTML pages (see below). Question
authors do not have direct access to the application, their questions are uploaded by an organiser.
In practice often organisers will also be authors.

{: .new }
And of course there is always an _administrator_ who can create user accounts for organisers, and
is all powerful in general. An administrator can mimic both organisers, teachers and pupils.[^1]

Except for the anonymous users and the question authors, everybody is identified with a user ID
and a password. An initial password is assigned at the moment of registration and
should then be changed by the user. Teachers can reset passwords for the pupils of their classes.
Teacher/organisers and administrators can change their password and for them there is a 'password forgotten?' link.
Their user ID must be a valid e-mail address.

### Nice-to-haves?

* Allow a teacher to build their own restricted contests by selecting a question set 
  from the available questions. Currently only the organisers can do this.
* Users can be temporarily disabled. (Never needed this in the past.)

#### Footnotes
[^1]: 
    In version 1 there was no distinction between organiser and administrator. The web interface
    for organisers/administrators was never fully implemented and sometimes 
    actions had to be performed directly on the database using SQL commands.