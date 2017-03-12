FROM ubuntu:16.10

MAINTAINER eryk "xuqi86@gmail.com"

ADD jdk-8u121-linux-x64.tar.gz /usr/local/
ADD application.conf /home/squant/conf/
ADD squant-assembly-1.0.jar /home/squant/

ENV JAVA_HOME=/usr/local/jdk1.8.0_121
ENV CLASSPATH=$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV PATH=$PATH:$JAVA_HOME/bin

RUN java -version

RUN mkdir -p /home/squant/conf


VOLUME /data

WORKDIR /home/squant

CMD $JAVA_HOME/bin/java -cp conf:squant-assembly-1.0.jar com.squant.cheetah.datasource.Updater