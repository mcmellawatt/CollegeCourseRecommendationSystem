The Course Recommendation application uses a layered, client-server architecture built on 
the Play framework to deliver optimized recommendations to students based on availability and other constraints.  It is 
based on the design in the included PDF "Team33_Project3-final.pdf" and represents a combination of each team members' 
individual designs.  The application uses Play framework, Gurobi optimizer, and Ebeans with an 
in-memory H2 SQL database to demonstrate the concept, which could be replaced with a full fledged SQL database in a production setting.

Assumptions
-----------
The following assumes that the machine is running Ubuntu x64, Java 7,
and already has Gurobi 6.5 installed with a valid license.


Compiling and Running the Course Recommender
----------------------------------------------
1. Download Typesafe Activator from http://www.typesafe.com/activator/download
    and extract it to the desired install directory.
    -Note: This file is 477MB so it was decided to not include it with
           the submitted source code.

2. Add export PATH=$PATH:<fullpathtoactivator> to your .bashrc file,
    where <fullpathtoactivator> is the full path to the installed
    directory in step 1.

3. Open terminal and set the current directory to the Course Recommender
    root directory (Project4CourseRecommender).

4. Run the command "activator run".
    -Note: This will compile and run the application in one step.

5. Open a browser and navigate to "http://localhost:9000/".

6. To login as a student, enter the following credentials:

    username:   john
    password:   adams
    
    -Note: The initial-data.yml file in the /conf/ directory is used
           to prepopulate the database with users, so additional
           credentials can be found in that file.

7. To login as an admin, enter the following credentials:

    username:   admin
    password:   admin
    