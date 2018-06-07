FROM tomcat:8.0-jre8
MAINTAINER Rosco Kalis <roscokalis@gmail.com>
LABEL Vendor="Eurocommercial"

#COPY backend/webapp/target/contactapp.war /usr/local/tomcat/webapps/ROOT.war
#RUN cd /usr/local/tomcat/webapps/ && rm -rf ROOT && unzip contacts.war -d ROOT && rm contacts.war

RUN rm -rf /usr/local/tomcat/webapps/ROOT
COPY backend/webapp/target/contactapp-webapp-1.2.0-SNAPSHOT/ /usr/local/tomcat/webapps/ROOT/

