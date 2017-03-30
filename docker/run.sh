#!/bin/bash

/usr/local/hbase-1.3.0/bin/start-hbase.sh
/usr/local/jdk1.8.0_121/bin/java -cp /home/squant/conf:/home/squant/squant-assembly-1.1.jar com.squant.cheetah.datasource.Updater
