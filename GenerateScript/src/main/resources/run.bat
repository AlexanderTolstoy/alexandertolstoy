@echo off
start javaw -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=2345 -jar CheckModule.jar
