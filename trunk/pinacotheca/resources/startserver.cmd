rem set JAVA="C:\Programme\Java\jre1.5.0_12\bin\java.exe"
set JAVA="java"

set PTINST="."
set PTSERVERJAR=%PTINST%/pinacotheca.jar
set PTPROPERTIES=
set PTSSLCERT=
set PTSSLPASS=

%JAVA% -Djavax.net.ssl.keyStore=%PTSSLCERT% -Djavax.net.ssl.keyStorePassword=%PTSSLPASS% -jar %PTSERVERJAR% %PTPROPERTIES%
PAUSE