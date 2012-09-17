REM @ECHO OFF
TITLE AutoSimmune

REM Repast Simphony Model Starter
REM By Michael J. North
REM 
REM Please note that the paths given below use
REM a unusual Linux-like notation. This is a
REM unfortunate requirement of the Java Plugin
REM framework application loader.

REM Note the Repast Simphony Directories.
REM set REPAST_SIMPHONY_ROOT=../repast.simphony/repast.simphony.runtime_$REPAST_VERSION/
set REPAST_SIMPHONY_ROOT=C:/RepastSimphony/eclipse/plugins/repast.simphony.runtime_1.2.0/
set REPAST_SIMPHONY_LIB=%REPAST_SIMPHONY_ROOT%lib/

REM Define the Core Repast Simphony Directories and JARs
SET CP=
SET CP=%CP%;%REPAST_SIMPHONY_ROOT%bin
SET CP=%CP%;%REPAST_SIMPHONY_ROOT%bin-groovy
SET CP=%CP%;%REPAST_SIMPHONY_LIB%saf.core.runtime.jar
SET CP=%CP%;%REPAST_SIMPHONY_LIB%commons-logging-1.0.4.jar
SET CP=%CP%;%REPAST_SIMPHONY_LIB%groovy-all-1.5.7.jar
SET CP=%CP%;%REPAST_SIMPHONY_LIB%javassist-3.7.0.GA.jar
SET CP=%CP%;%REPAST_SIMPHONY_LIB%jpf.jar
SET CP=%CP%;%REPAST_SIMPHONY_LIB%jpf-boot.jar
SET CP=%CP%;%REPAST_SIMPHONY_LIB%log4j-1.2.13.jar
SET CP=%CP%;%REPAST_SIMPHONY_LIB%xpp3_min-1.1.4c.jar
SET CP=%CP%;%REPAST_SIMPHONY_LIB%xstream-1.3.jar
SET CP=%CP%;C:/RepastSimphony/eclipse/plugins/repast.simphony.core_1.2.0/bin/repast/simphony/engine/schedule/


REM Change to the Default Repast Simphony Directory
CD C:/RepastSimphony/workspace/AutoSimmune/

REM Start the Model
REM START javaw -Xss10M -Xmx400M -cp %CP% repast.simphony.runtime.RepastMain autosimmune.rs
javaw -Xss10M -Xmx400M -cp %CP% repast.simphony.runtime.RepastMain autosimmune.rs

