Android-Data-Logger
===================

* Simple Android Application to log sensor data for motion based activity recognition. Logs data as a timestamped .csv file

* The output file can be found in [sdcard]/Download/AllData_[timestamp].csv

* Two buttons 'EXIT' and 'ENTER' are given in the application to enter break points, Data given by these buttons are essentially empty data marked by ENTER/EXIT indentifer

* New sensor data is written whenever there is a change in data, thus the resolution of data is a minimum of 10milliseconds, and maximum of how much ever time it takes for the sensor to detect a change in the data


Issues
------
* Timestamp is the raw timestamp returned by System.getmilliseconds() in Java

