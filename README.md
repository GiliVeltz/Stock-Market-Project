# StockMarket_Project
Workshop on Software Engineering Project -- 2024

## Initialization
There are 2 configuration files for our system.

First, the system configuration file, who choose the way to initialize the external services & database.

The text in the file should be in the format:


  external_services:<-option1>  
  database:<-option2>


1. The external services will contain one of the following values:
* tests
* real
For tests, we will not connect to the external systems and managed the requests locally.

For real time, we will connect to the external systems.


2. The database will contain one of the following values:
* tests
* real_init
The real_init option is for running the application with data that will load from the data configuration file(see up next about format).

The tests option is for running tests with no database, and make no writes and reads to database. The tests option is for running tests with clean database. the tests will read and write to a demo database.


The configuration file path should be:


   ../Server/Configuration/test_config.txt


The second configuration file contains instructions for initialized data for the market when we load the system with demo.

This file is a text file who contains instructions in the next format :


   <-instruction name>#<-param1>#<-param2>#<-param3>..


The configuration file path should be:


   ../Server/Configuration/instructions_config.txt


** Note: you can create some versions of instructions_config as you would like to initiate the system with your objects. Please view documentaion of function run_instruction in MarketSystem.java class to understand better.

** Note: to change to initiate of the external services and databases from real to test (and back) - go to class "MarketSystem.java" -> on the last line in the constractor -> put the fields "tests_config_file_path" or "real_system_config_path" as you would like.


## Running the application
The project is a standard Maven project.

To run the application:
1. Right click on Server.java file (in the Server folder).
2. Right clikc on Application.java file (in the UI folder).

Notice: http://localhost:8080 in your browser.

## Relevant Links
Link to drawio: https://drive.google.com/file/d/1QwmOQ_Z2IBAZtaPuWoCLUYUFDy6u0PF5/view?usp=drive_link

Link to exel Acceptance-Tests: https://docs.google.com/spreadsheets/d/11zTxZ4zcfAvLORkBvPt3BZ4AmSn7EMx0/edit?usp=drive_link&ouid=106302452385150567875&rtpof=true&sd=true

Link to exel Use-Cases: https://docs.google.com/spreadsheets/d/1-20lpZteCvqh29Lt1lmqYKUh4ZyNEZbTjioOIZ-S6GU/edit#gid=0


## Contrubutors and Copyright (c) :
Inbar Ben-Chaim 209289081

Gili Veltz 318916384

Vladislav Shembel 322126350

Or Saada 207161605

Tal Koren 213904972

Amit levints 208671990

Metar Bachar 206892317

