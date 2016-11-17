CP=lib/banner.jar
CP=${CP}:lib/dragontool.jar
CP=${CP}:lib/heptag.jar
CP=${CP}:lib/commons-collections-3.2.1.jar
CP=${CP}:lib/commons-configuration-1.6.jar
CP=${CP}:lib/commons-lang-2.4.jar
CP=${CP}:lib/mallet-deps.jar
CP=${CP}:lib/mallet.jar
CP=${CP}:lib/commons-logging-1.1.1.jar
CP=${CP}:lib/bioc.jar
CP=${CP}:lib/stax-utils.jar
CP=${CP}:lib/woodstox-core-asl-4.2.0.jar
CP=${CP}:lib/stax2-api-3.1.1.jar
CONFIG=$1
INPUT=$2
OUTPUT=$3
# echo $CP
java -cp ${CP} BANNER_BioC $CONFIG $INPUT $OUTPUT

