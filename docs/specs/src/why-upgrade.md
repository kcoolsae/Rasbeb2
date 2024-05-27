Why do we upgrade Rasbeb?
==

**Features**. We want 
* to support _interactive_ questions, preferably in a way compatible with [Bebras Lodge](https://www.bebras.org/node_56.html) 
* to improve support for organisers. Currently too many things still need to be done by interacting directly with the database.
* a more modern look and feel, with support for tablets and maybe smart phones.

Some features which were in version 1 were never used in practice - or turned to be not practical in usage (e.g., keeping track of a student's history)
and can be removed, which will simplify the use of the system by teachers and students. 

**Technical**
* Rasbeb 1 is based on software libraries that already existed 10 years ago. Although some upgrades
have been done during the past years, to conform to the latest versions (especially of the Play framework) 
needs a major rewrite. 
* We are not 100% sure that Rasbeb 1 is perfectly thread safe - and for that reason we run it
  as a single process, which does not scale well
* We'd like a better separation between running the contest and managing the contest, using different URLs for both, possibly even on
  different servers.
* Also a better decoupling of database and presentation layers, making it easier to adapt to changes in the future

### Why not switch to a system in use by other countries?

The main reason is money. Belgium has no sufficient funds to let the contest be run by an external
organisation that needs to be paid. Courtesy of our university we have a server on which we can run the contest and can
do software development and maintenance during normal office hours.

We have looked at one other system that is used by quite a few Bebras countries, but it does not satisfy our needs.