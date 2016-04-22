#run this script to perform the static analysis. 
#Provide the jar or zip (containing all the classes to be analyzed) as a parameter. 
java -Xmx2000m -cp target/DaTeC-1.0-SNAPSHOT.jar:lib/sootclasses-2.3.0.jar:lib/polyglot.jar:lib/java_cup.jar:lib/sqlite-jdbc-3.6.4.jar ch.unisi.inf.datec.Main $1
