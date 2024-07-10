# StockMarket_Project
Workshop on Software Engineering Project -- 2024

![Screenshot of a main page on our website.](https://github.com/inbarbc/StockMarket_Project/assets/102466931/6249ea04-49d0-4ffb-a95f-9c70aa1d75aa)


## Initialization
There are 2 configuration files for our system.

First, the system configuration file, who choose the way to initialize the external services & database.

The text in the file should be in the format:
##
    external_services:<-option1>  
    database:<-option2>


#### 1. External Services
The external services will contain one of the following values:
* tests
* real
* 
For tests, we will not connect to the external systems and managed the requests locally.

For real time, we will connect to the external systems.


#### 2. Database
The database will contain one of the following values:
* tests
* real_init
The real_init option is for running the application with data that will load from the data configuration file(see up next about format).

The tests option is for running tests with no database, and make no writes and reads to database. The tests option is for running tests with clean database. the tests will read and write to a demo database.


The configuration file path should be:
##
    ../Server/Configuration/test_config.txt


The second configuration file contains instructions for initialized data for the market when we load the system with demo.

This file is a text file who contains instructions in the next format :
##
    <-instruction name>#<-param1>#<-param2>#<-param3>..


The configuration file path should be:
##
    ../Server/Configuration/instructions_config.txt


> [!NOTE]
> you can create some versions of instructions_config as you would like to initiate the system with your objects. Please view documentaion of function `run_instruction` in `MarketSystem.java` class to understand better.

> [!NOTE]
> to change to initiate of the external services and databases from real to test (and back) - go to class `MarketSystem.java` -> on the last line in the constractor -> put the fields `tests_config_file_path` or `real_system_config_path` as you would like.

this.init_market(real_system_config_path);


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

© Copyright


> [!CAUTION]
> Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
>
> The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
>
> THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
