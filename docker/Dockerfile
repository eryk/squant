FROM ubuntu:16.10

MAINTAINER eryk "xuqi86@gmail.com"

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai  /etc/localtime

RUN mkdir -p /home/squant/conf /data/hbase/logs

ADD jdk-8u121-linux-x64.tar.gz /usr/local/

ADD squant-assembly-1.0.jar /home/squant/
ADD application.conf /home/squant/conf/
ADD logback.xml /home/squant/conf/

ADD hbase-1.3.0-bin.tar.gz /usr/local/
ADD hbase-site.xml /usr/local/hbase-1.3.0/conf/
ADD hbase-env.sh /usr/local/hbase-1.3.0/conf/

ENV JAVA_HOME=/usr/local/jdk1.8.0_121
ENV HBASE_HOME=/usr/local/hbase-1.3.0
ENV CLASSPATH=$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV PATH=$PATH:$JAVA_HOME/bin:$HBASE_HOME/bin

RUN /usr/local/hbase-1.3.0/bin/start-hbase.sh

VOLUME /data

WORKDIR /home/squant

CMD $JAVA_HOME/bin/java -cp conf:squant-assembly-1.0.jar com.squant.cheetah.datasource.Updater