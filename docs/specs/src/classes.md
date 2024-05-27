Schools and classes
===
{% include _menu.md %}

---

Schools have one or more teachers that organise contests for that pupils of that school. 
<span class="new">A teacher is associated to a single school.</span> For all practical purposes, the application makes no
distinction between different teachers of the same school. Information added by one teacher can be seen and edited by every other
teacher of that same school.

A teacher is responsible for a number of _classes_. A class belongs to a single _school_ and a single _year_[^2]. 
Classes have a number of registered pupils. 
A pupil can belong to at most one class in a given year.

Classes must be recreated every year - <span class="new">but class _names_ can be copied from previous years</span>. 
The application enforces no relation between e.g.
class "5ecoB" of 2023–2024 and "5ecoB" of 2024–2025. 
In particular this means that every year, every pupil needs to be registered
anew by a teacher (which might be different from last year, or even from a different school).

The class to which a pupil belongs[^4] determines the contest to which they are allowed to take part - and therefore
also (indirectly) the level at which they participate. However, it remains possible to register a pupil from one class
to be able to participate at another level than the rest of their class[^3].

### Comments

In version 1 schools were uploaded from a national database by an organiser. Teachers then
had to indicate to which school they belonged. This had some disadvantages
* Schools were often mentioned more than once in the database and teachers had to chose correctly. In practice, as long
  as teachers from the same school made the same choice, this was not really a problem
* Each year the database had to be uploaded anew and teachers had to register anew.
* Compare to the size of the database, only a very very small number of schools were actually taking part in Bebras 

Also, the fact that every school/teacher association had to be renewed every year was not always ideal.
* Teachers could not prepare contests for the coming school year before the organizers changed the 'current year' in the database
* Once the 'current year' was changed, teachers could no longer review results from the previous year

### Nice-to-haves?

* Allow the organiser to add school names and addresses in bulk from a spreadsheet. E.g., from a country's official list of schools
* Years are added by an organiser.

#### Footnotes

[^2]:
    The 'year' may also overlap subsequent calendar years (e.g., 2023-2024). The main idea is 
    to distinguish membership of a pupil to a class over the years so that memberships in the past need
    not be deleted. (Although in practice, a pupil will almost always be registered anew each year.)

[^3]: For a given contest, a pupil can only participate at one particular level, as questions are shared between levels.

[^4]: 
     The application does not prohibit that the same pupil belongs to more than one class at the same time, although
     producing such a case will not be made easy in the user interface.