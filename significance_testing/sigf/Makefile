SIGF_DIR = $(shell pwd)

JAVAC = javac
JAVA_FLAGS = \
-classpath "$(SIGF_DIR)/class" \
-sourcepath "$(SIGF_DIR)/src" \
-d $(SIGF_DIR)/class \
-J-Xmx200m -source 1.5

JAVADOC = javadoc
JAVADOC_FLAGS = -J-Xmx300m

all:	classes docs

classes:	class
	$(JAVAC) $(JAVA_FLAGS) `find src -name '*.java'`

docs:	html
	$(JAVADOC) $(JAVADOC_FLAGS) -classpath "$(SIGF_DIR)/class" -d $(SIGF_DIR)/html -sourcepath $(SIGF_DIR)/src -source 1.5 de.pado.sigf

class:
	mkdir -p class

html:
	mkdir -p html

clean:
	rm -rf $(SIGF_DIR)/class/* $(SIGF_DIR)/html/*
	rm -r $(SIGF_DIR)/src/de/pado/sigf/*~