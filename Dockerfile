FROM tomcat:8.0-jre8
MAINTAINER Rosco Kalis <roscokalis@gmail.com>
LABEL Vendor="Eurocommercial"

# COPY backend/webapp/target/contacts.war /usr/local/tomcat/webapps/contacts.war
# RUN cd /usr/local/tomcat/webapps/ && rm -rf ROOT && unzip contacts.war -d ROOT && rm contacts.war

RUN rm -rf /usr/local/tomcat/webapps/ROOT
COPY backend/webapp/target/contactapp-webapp-1.1.0/ /usr/local/tomcat/webapps/ROOT/

COPY entrypoint.sh /root/

ENTRYPOINT ["/root/entrypoint.sh"]
