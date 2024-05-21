@echo off
echo Deleting old class files...
del *.class

echo Compiling Java files...
javac *.java
javac -cp ".;c:/Software/mysql-connector-j-8.4.0/mysql-connector-j-8.4.0.jar" SnakeGame.java

echo Starting the game...
java -cp ".;c:/Software/mysql-connector-j-8.4.0/mysql-connector-j-8.4.0.jar" SnakeGame
pause
