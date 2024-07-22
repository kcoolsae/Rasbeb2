Database setup
===

To set up an initial database you may use the `create-tables.sh` 
in the `database` subdirectory of the `main` directory.

The same script can be used to create the database `rasbeb2test` used during development
for unit testing.

    DATABASE=rasbeb2test ./create-tables.sh

However, the test database also needs to be initialized with the test data:

    ./init-test-data.sh


