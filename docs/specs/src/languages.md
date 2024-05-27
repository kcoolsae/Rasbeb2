Language support
===
{% include _menu.md %}

---

The application supports multiple languages. Not only do we wish to support schools in different parts of a country that 
use a different language, but also the case where in a school with base language X, a pupil who is more familiar with
language Y, wants to participate in language Y.

The choice of language is used in two different contexts
* The _user interface_
* The _contents_: tasks (question and feedback pages), titles of tasks, titles of contests, ...

(There are also a few items that are not translated: names of schools, names of classes, ...)

In practice the language used for the user interface is the same as the language used for the contents - 
although in principle they are independent.

A user's language is determined when they first sign on, but can also be changed later.

### Comments

* Support for right-to-left languages might be hard. Especially if both left-to-right and right-to-left must
  be available at the same time. (And what about: left-to-right interface + right-to-left content?)
* What to do when a user interface language is supported for which there is no translated content?
* Should we go as far as allowing a user to change content language in mid-contest? (Better not: that would yield
a slight advantage for questions that have different answers depending on the language, and sounds unnecessary complicated.)



