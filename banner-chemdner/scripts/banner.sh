CP=${CP}:../build/classes
CP=${CP}:../lib/BANNER-CHEMDNER.jar
CP=${CP}:../lib/dragontool.jar
CP=${CP}:../lib/heptag.jar
CP=${CP}:../lib/commons-collections-3.2.1.jar
CP=${CP}:../lib/commons-configuration-1.6.jar
CP=${CP}:../lib/commons-lang-2.4.jar
CP=${CP}:../lib/mallet-deps.jar
CP=${CP}:../lib/mallet.jar
CP=${CP}:../lib/commons-logging-1.1.1.jar
CP=${CP}:../lib/biolemmatizer-core-1.1-jar-with-dependencies.jar
java -cp ${CP} banner.eval.BANNERCHEMDNER $1 $2 $3

