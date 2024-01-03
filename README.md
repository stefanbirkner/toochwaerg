# Toochwaerg

A command-line too for analyzing a work log that is created with
[jrnl](http://jrnl.sh/). It calculates
- your overtime
- the time you worked on each task
- the time you worked on a topic

Toochwaerg is published under the
[MIT license](http://opensource.org/licenses/MIT).

## Installation

Clone the repository

    git clone git@github.com:stefanbirkner/toochwaerg.git

You need to have a JDK (at least JDK 10) installed. Also you need to have jrnl
installed.

## Usage

Toochwaerg follows the
[Unix philosophy](https://en.wikipedia.org/wiki/Unix_philosophy). It takes the
output of jrnl.sh and does its computations on top of it.

    jrnl <any options> | toochwaerg <command>


### Calculate hours worked

Execute the script

    javac WorkingHours.java; \
    jrnl -from year \
      | java -cp . WorkingHours

You get an output like this

    2017-08-18 07:59 
    2017-08-19 04:40 no pause
    2017-08-20 08:38 
    2017-08-21 08:35

Each line contains the date and the amount of time you spent. Days without an
entry that contain neither the word "lunch" nor the word "pause" have a marker
"no pause" at the end of the line. This should help you to find days where you
missed to add your pause to the log.

### Calculate overtime

Execute the script

    javac WorkingHours.java MissingHours.java; \
    jrnl -from year \
      | java -cp . WorkingHours \
      | java -cp . MissingHours

You get an output like this

    PT1H10M

The script implies a working day of 7 hours. It considers each day with data as
a working day except for Saturday and Sunday. This makes it easy to handle sick
days and holidays because they are simply ignored by the calculator.
