@IF "%1"=="" GOTO NoVersion
@set version=%1
del build\libs\argon-c-rex-0.0.*.jar
copy libs\argon-0.0.%version%.jar build\libs\argon-c-rex-0.0.%version%.jar
cd build\classes\main
rd /s/q de
jar xf ..\..\libs\argon-c-rex.jar
jar uf ..\..\libs\argon-c-rex-0.0.%version%.jar de
cd ..\..\..
@GOTO Done
:NoVersion
ECHO No version parameter given
:Done