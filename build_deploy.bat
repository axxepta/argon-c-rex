set versionnumber=4
copy extension.xml roll-out\
copy plugin.xml roll-out\argon-c-rex\
copy build\libs\lib\*.* roll-out\argon-c-rex\libs\lib\
copy build\libs\argon-c-rex-0.0.%versionnumber%.jar roll-out\argon-c-rex\libs\
xcopy /S/Y build\resources\main\api roll-out\argon-c-rex\resources\api\
xcopy /S/Y build\resources\main\images roll-out\argon-c-rex\resources\images\
copy build\resources\main\*.* roll-out\argon-c-rex\resources\
cd roll-out
jar cvf Argon-C-Rex-Plugin-0.0.%versionnumber%.zip argon-c-rex
cd ..