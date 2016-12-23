SET current_directory=%~dp0
cd %current_directory%
start java -jar -Xmn1280m pdfviewer_V7.jar %1
exit
