FROM jeanblanchard/tomcat:8
LABEL maintainer="Rosco Kalis <roscokalis@gmail.com>"
LABEL version="1.2.0-SNAPSHOT"

ENV MAVEN_VERSION 3.5.2
ENV MAVEN_HOME /usr/lib/mvn
ENV PATH $MAVEN_HOME/bin:$PATH

RUN wget http://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz && \
    tar -zxvf apache-maven-$MAVEN_VERSION-bin.tar.gz && \
    rm apache-maven-$MAVEN_VERSION-bin.tar.gz && \
    mv apache-maven-$MAVEN_VERSION /usr/lib/mvn

COPY . /contactapp

RUN cd /contactapp && \
    mvn clean install -DskipTests && \
    rm -rf /opt/tomcat/webapps/ROOT && \
    cp -r /contactapp/webapp/target/contactapp-webapp-1.2.0-SNAPSHOT/ /opt/tomcat/webapps/ROOT
