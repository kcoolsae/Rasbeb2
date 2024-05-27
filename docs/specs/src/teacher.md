The teacher
===
{% include _menu.md %}

---

A teacher is registered with the application
by an organiser (after sending a request by e-mail) or by a colleague of the same school (this is the preferred way). 
<span class="new">The teacher is linked to a (single) school at registration time.</span>
After registration the application sends an e-mail to the teacher which they can use to choose an initial password.
The e-mail address serves as the teacher's user ID. 

A teacher can register
classes (i.e., class names), pupils, and assign pupils to classes. To facilitate this registration, a spreadsheet with
the necessary information can be uploaded into the application. Small additions and corrections can also
be done 'manually'.

To participate in a competition a teacher must organise _local events_ for that competition (see [Competitions and local events](competition.html)), register
classes and/or individual pupils for those local events, and open the events and close them at the appropriate times.
After a competition is closed the marks of all participants can be downloaded as a spreadsheet. (For the official
competition, as long as it is also officially closed.)

For more details, see
* [Contest management system](management-system.html) (teacher's view)

**Important** Different teachers of the _same school_ see
and manipulate the _same data_. To the system a teacher is simply a means for a school to access the application.

### Nice-to-haves
* A teacher can change their e-mail address. There might be some security issues involved here... maybe we should
  restrict this privilege to organisers?

* A teacher can monitor pupils during a local event (whether they are logged in, how much time
  they have left, whether they already hit the 'finished' button, ... ).

### Removed

* Teachers could be associated to more than one school at the same time.

  In practice
  this never occurred, and it made the user interface slightly more complicated. Workaround: the same teacher can
  be registered to the application with different ids (= e-mail addresses), each for a different school.

* Teacher's preferred interface language is not stored in the database but rather in a cookie
