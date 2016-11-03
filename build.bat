set version=6
call get_sources.bat %version%
call compile_aspects.bat %version%
call build_jar.bat %version%