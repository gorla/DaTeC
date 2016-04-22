## These are the only paths to change
JAVA=/tools/jdk1.6.0_22/bin/java
JAVAC=/tools/jdk1.6.0_22/bin/javac
JAVA_RT_JAR=/tools/jdk1.6.0_22/jre/lib/rt.jar

PRJ_TO_ANALYZE=target/test-classes/coffeemaker.zip
SAMPLE_TEST_DIR=sample-test
TEST_CASE=SimpleTestCaseCM
DATEC_JAR=target/DaTeC-1.0-SNAPSHOT.jar

package:$(DATEC_JAR)

install-dependencies:
	@echo '** Installing DaTeC dependencies... '
	@echo '** Installing Soot... '
	mvn install:install-file \
		-DgroupId=sable \
		-DartifactId=soot \
		-Dversion=2.3.0 \
		-Dpackaging=jar \
		-Dfile=lib/sootclasses-2.3.0.jar
	@echo '** Installing polyglot... '
	mvn install:install-file \
		-DgroupId=sable \
		-DartifactId=soot-polyglot \
		-Dversion=2.3.0 \
		-Dpackaging=jar \
		-Dfile=lib/polyglot.jar
	@echo '** Installing java_cup... '
	mvn install:install-file \
		-DgroupId=sable \
		-DartifactId=soot-javacup \
		-Dversion=2.3.0 \
		-Dpackaging=jar \
		-Dfile=lib/java_cup.jar

$(DATEC_JAR):
	@echo '** Building DaTeC package... '
	mvn package


runAnalysis: $(DATEC_JAR)
	@echo "** Running Data flow analysis on $(PRJ_TO_ANALYZE).."
	$(JAVA) -Xmx2000m \
	 -cp $(DATEC_JAR):lib/sootclasses-2.3.0.jar:lib/polyglot.jar:lib/java_cup.jar:lib/sqlite-jdbc-3.6.4.jar \
	 ch.unisi.inf.datec.Main $(PRJ_TO_ANALYZE)


instrument: datec.db
	@echo "** Instrumenting $(PRJ_TO_ANALYZE).."
	$(JAVA) -Xmx2000m \
	 -cp $(JAVA_RT.JAR):$(DATEC_JAR):lib/sootclasses-2.3.0.jar:lib/polyglot.jar:lib/java_cup.jar:lib/sqlite-jdbc-3.6.4.jar:lib/jasminclasses-2.3.0.jar:$(PRJ_TO_ANALYZE) \
	 ch.unisi.inf.datec.Instrument

runSampleTest: instrument.jar
	@echo "** Compiling test case"
	cp $(SAMPLE_TEST_DIR)/$(TEST_CASE).java .
	$(JAVAC) -cp instrument.jar:$(DATEC_JAR):lib/sqlite-jdbc-3.6.4.jar:lib/junit.jar:. \
	  $(TEST_CASE).java
	@echo "** Running test case"
	$(JAVA) -cp instrument.jar:$(DATEC_JAR):lib/sqlite-jdbc-3.6.4.jar:lib/junit.jar:. \
	  org.junit.runner.JUnitCore $(TEST_CASE)

generateReport: $(TEST_CASE).class
	$(JAVA) -Xmx2000m -cp $(DATEC_JAR):lib/sqlite-jdbc-3.6.4.jar \
	  ch.unisi.inf.datec.Report
	cp -R style/* report/

clean:
	-rm -rf datec.db
	-rm $(TEST_CASE).class
	-rm $(TEST_CASE).java

cleanAll: clean
	-rm -rf target
	-rm -rf instrument.jar
	-rm -rf report
