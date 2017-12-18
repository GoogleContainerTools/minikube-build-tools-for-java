@echo on

cd github/minikube-build-tools-for-java

rem there is no minikube installed on this system, so any integration test will probably not work
rem we porbably also have to install VirtualBox or some other VM driver, it not even clear to me
rem that kokoro will allow us to run a VM.

call gcloud.cmd gccomponents install docker-credential-gcr

cd minikube-gradle-plugin && call gradlew.bat clean build && ^
cd ../minikube-maven-plugin && call mvnw.bat clean install && ^
cd ../crepecake && call gradlew.bat clean build integrationTest --info
exit /b %ERRORLEVEL%
