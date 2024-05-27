## Additional libraries

The application uses two additional libraries (Play Utilities / DAO Helper) which are currently not (yet) directly
available but need to be built from source. The source for these libraries is available on `github.com`

* [Play Utilities](https://github.com/kcoolsae/play-utils)
* [DAO Helper](https://github.com/kcoolsae/daoHelper)

Both libraries can be built using the `sbt` build tool. In both cases, the Linux command

    sbt clean publishLocal
when executed from the top level project directory, builds the library and installs it in the local IVY repository, ready
for use by the present rasbeb2 application.