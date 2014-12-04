DynamicLOD
==========

Source code of Dynamic LOD Cloud based in DataIDs file. More info about dataID please check http://wiki.dbpedia.org/coop/DataIDUnit.

## How to use

#### Starting Jetty server

In order to run the project you need to start the Jetty server using the following command:
`mvn jetty:start`

Maven will then download all dependencies and compile the project. Now the server must be acessible at the address:
`http://localhost:8080/dataid/index.xhtml` .

#### Accessing web interface and adding a dataID file
You can now access `http://localhost:8080/dataid/index.xhtml` and start to adding dataID files. There are four dataID files (English Dbpedia, News-100, RSS-500 and Reuters-128) in the interface page that you can use as examples.

#### Visualizing Cloud
DataID is based on several vocabularies and one of them is the VoID Vocabulary. Thus you can use a VoID Visualize tool (made by Luca Matteis) which  allows you to visualize the generated dataID file.

The dataID file can be accessed at `http://localhost:8080/graph/dataid_graph.ttl` and you can copy and paste at http://lmatteis.github.io/void-graph/ and see the result.
