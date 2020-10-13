@echo on

cd github/minikube-build-tools-for-java

rem there is no minikube installed on this system, so any integration test will probably not work
rem we porbably also have to install VirtualBox or some other VM driver, it not even clear to me
rem that kokoro will allow us to run a VM.

cd minikube-gradle-plugin && call gradlew.bat --console=plain clean build && ^
cd ../minikube-maven-plugin && call mvnw.bat -B -U clean verify
exit /b %ERRORLEVEL%
