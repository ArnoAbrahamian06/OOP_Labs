FROM tomcat:9.0.111-jre17

# Очищаем стандартные приложения Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*


COPY target/Lab-2-1.0-SNAPSHOT.war D:/Java/apache-tomcat-9.0.113/webapps/ROOT.war

# Порт 8080 открыт по умолчанию в образе Tomcat
EXPOSE 8080
