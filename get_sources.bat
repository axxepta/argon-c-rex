@IF "%1"=="" GOTO NoVersion
@set version=%1
set sourcefolder=E:\Dateien\code\Java\project-argon\
set aspectfolder=E:\Dateien\code\Java\argon-c-rex\
copy %sourcefolder%src\main\java\de\axxepta\oxygen\core\ClassFactory.java %aspectfolder%src\main\java\de\axxepta\oxygen\core\
copy %sourcefolder%src\main\java\de\axxepta\oxygen\utils\WorkspaceUtils.java %aspectfolder%src\main\java\de\axxepta\oxygen\utils\
del %aspectfolder%libs\argon-0.0.*.jar
copy %sourcefolder%build\libs\argon-0.0.%version%.jar %aspectfolder%libs\
@GOTO Done
:NoVersion
ECHO No version parameter given
:Done