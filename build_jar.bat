copy libs\argon-0.0.3.jar build\libs\argon-c-rex-0.0.3.jar
cd build\classes\main
rd /s/q de
jar xf ..\..\libs\argon-c-rex.jar
jar uf ..\..\libs\argon-c-rex-0.0.3.jar de
cd ..\..\..