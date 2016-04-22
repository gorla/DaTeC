#run this script to instrument the classes
#provide the zip or jar files (containing the classes to be analyzed) as a parameter
java -Xmx2000m -cp /System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Classes/classes.jar:target/DaTeC-1.0-SNAPSHOT.jar:lib/sootclasses-2.3.0.jar:lib/polyglot.jar:lib/java_cup.jar:lib/sqlite-jdbc-3.6.4.jar:lib/jasminclasses-2.3.0.jar:$1 ch.unisi.inf.datec.Instrument
