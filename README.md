DynamicLOD
==========

Source code of Dynamic LOD Cloud based in DataIDs file. More info about dataID please check http://wiki.dbpedia.org/coop/DataIDUnit.

### Requirements

Current version of DynamicLOD runs only on linux and uses a set of tools must be installed. 
Bzip2 must be installed in order to uncompress .bz2 distributions and raptor-utils to convert rdf files. You can easily install it on Debian based distributions running `apt-get install bzip2` and `install raptor-utils`.


### Starting Jetty server

In order to run the project you need to start the Jetty server using the following command:
`mvn jetty:start`

Maven will then download all dependencies and compile the project. Now the server must be acessible in the address:
`http://localhost:8080/dataid/index.xhtml`

