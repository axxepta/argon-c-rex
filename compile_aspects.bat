@IF "%1"=="" GOTO NoVersion
@set version=%1
set CLASSPATH=e:\Programme\aspectj1.8\lib\aspectjrt.jar;libs\argon-0.0.%version%.jar;libs\basex-8.5.3.jar;libs\oxygen.jar;libs\log4j-core-2.3.jar;libs\log4j-api-2.3.jar
ajc -source 1.8 -target 1.8 -outjar build\libs\argon-c-rex.jar @argon-c-rex.lst
@GOTO Done
:NoVersion
ECHO No version parameter given
:Done