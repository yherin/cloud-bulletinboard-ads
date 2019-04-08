FROM tomcat:7-jre8-alpine
ADD /target/bulletinboard-ads.war /usr/local/tomcat/webapps/ROOT.war