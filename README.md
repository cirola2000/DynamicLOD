DynamicLOD
==========

Source code of Dynamic LOD Cloud based in DataIDs file. More info about DataID Unit please check http://wiki.dbpedia.org/coop/DataIDUnit.


## Requirements
This project uses external tools that you must install before start using.
We use MongoDB to save relevant metadata for the creation of linksets. Thus, for MongoDB the default installation is sufficient: `sudo apt-get install mongodb-server`. We also need rapper tool to parse files and you can install via apt-get `sudo apt-get install raptor-utils`.

In order to compile and download dependencies you must have Maven3 installed. You can easily install it via apt-get `sudo apt-get install maven`.

Important!!! After cloning the project from this repository, please access the folder /resources and edit the properties configuration file.

## How to use

#### Instalation process

After cloning the project, open the project root folder and type: `mvn clean install`. Maven will then download all dependencies and compile the project.


#### Starting Jetty server

In order to run the project you need to start the Jetty server using the following command:
`mvn jetty:start`

 Now the server must be acessible at the address:
`http://localhost:8080/dataid/index.xhtml` .

#### Accessing web interface and adding a dataID file
You can now access `http://localhost:8080/dataid/index.xhtml` and start to adding dataID files. There are four dataID files (English Dbpedia, News-100, RSS-500 and Reuters-128) in the interface page that you can use as examples.

#### Visualizing Cloud
DataID is based on several vocabularies and one of them is the VoID Vocabulary. Thus you can use a VoID Visualize tool (made by Luca Matteis) which  allows you to visualize the cloud using the generated DataID file.

The output  DataID file can be accessed at `http://localhost:8080/output` and you can copy and paste to  http://lmatteis.github.io/void-graph/ and see the generated cloud.
