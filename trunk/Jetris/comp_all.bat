@REM this batch file will compile all the files needed to make jetris run.
@REM ***remember to add a command to this file for every java file you add
@REM 
@REM for more batch file commands - http://www.cs.ntu.edu.au/homepages/bea/home/subjects/ith305/description.html
@REM @echo off

@TITLE Compiling All Files...

javac "net\sourceforge\jetris\Figure.java"
javac "net\sourceforge\jetris\Figures.java"
javac "net\sourceforge\jetris\HTMLLink.java"
javac "net\sourceforge\jetris\SplashScreen.java"
javac "net\sourceforge\jetris\FigureFactory.java"
javac "net\sourceforge\jetris\HelpDialog.java"
javac "net\sourceforge\jetris\TetrisGrid.java"
javac "net\sourceforge\jetris\JetrisMainFrame.java"
javac "res\ResClass.java"
javac "JetrisMain.java"
javac "net\sourceforge\jetris\io\HiScore.java"
javac "net\sourceforge\jetris\io\PublishHiScore.java"
javac "net\sourceforge\jetris\NewGame.java"
javac "net\sourceforge\jetris\OnePlayerGame.java" 
javac "net\sourceforge\jetris\TwoPlayerGame.java"
javac "net\sourceforge\jetris\Player.java"
javac "net\sourceforge\jetris\PlayerOptions.java"

@PAUSE