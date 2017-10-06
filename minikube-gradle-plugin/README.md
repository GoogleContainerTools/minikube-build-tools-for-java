Minikube Gradle Plugin
======================
This plugin provides tasks and configuration to manage a minikube lifecycle for gradle developers.

Now available on [gradle plugin portal](https://plugins.gradle.org/plugin/com.google.cloud.tools.minikube).

This plugin requires that you have [Minikube installed](https://kubernetes.io/docs/tasks/tools/install-minikube/).
This plugin requires that you have [Docker installed](https://docs.docker.com/engine/installation/).

It exposes the following tasks
- `minikubeStart`
- `minikubeStop`
- `minikubeDelete`
- `prepareMinikubeDockerBuild` - prepares build context and Dockerfile for `minikubeDockerBuild`
- `minikubeDockerBuild` - runs `docker build` in the minikube Docker environment

It exposes the `minikube` configuration extension.

```groovy
minikube {
  minikube = // path to minikube, default is "minikube"
  docker = // path to Docker, default is "docker"
}
```

Task specific flags are configured on the tasks themselves.
 
All `minikube` tasks, except `minikubeDockerBuild`, are of the type `MinikubeTask` and all share the same kind of configuration.
- `flags` (`String[]`) : any minikube flags **this is the only one users should edit for the provided tasks**
- `minikube` (`String`) : path to minikube executable which should be set by using the `minikube` extension
- `command` (`String`) : start/stop/whatever (users probably shouldn't be editing this for default commands)

```groovy
minikubeStart {
  flags = ["--vm-driver=none"]
}
```

`prepareMinikubeDockerBuild` task is of the type `PrepareDockerBuildTask`. This task is required by `minikubeDockerBuild` and can be configured with:
- `context` (`String`) : path to the Docker build context (See 'Extended description' under the [`docker build` Reference](https://docs.docker.com/engine/reference/commandline/build/))
    - Defaults to `build/libs/`
- `dockerfile` (`String`) : path to the Dockerfile (See the [`docker build` Reference](https://docs.docker.com/engine/reference/commandline/build/))
    - Defaults to `src/main/docker/Dockerfile`

`minikubeDockerBuild` task is of the type `DockerBuildTask`. This task requires the successful execution of `prepareMinikubeDockerBuild` and can be configured with:
- `flags` (`String[]`) : any flags to pass to `docker build` (See 'Options' under the [`docker build` Reference](https://docs.docker.com/engine/reference/commandline/build/))
- `minikube` (`String`) : path to minikube executable which should be set by using the `minikube` extension **users should not edit this for this provided task**
- `docker` (`String`) : path to Docker executable which should be set by using the `docker` extension **users should not edit this for this provided task**

```groovy
prepareMinikubeDockerBuild {
  context = "build/libs/"
  dockerfile = "src/main/docker/Dockerfile"
}
minikubeDockerBuild {
  flags = ["--build-arg ARTIFACT_NAME=my_kubernetes_app"]
}
```

This plugin also allows users to add in any custom `minikube` task.

```groovy
task minikubeCustom(type: com.google.cloud.tools.minikube.MinikubeTask) {
  command = "custom"
  flags = ["--some-flag"]
}
```
