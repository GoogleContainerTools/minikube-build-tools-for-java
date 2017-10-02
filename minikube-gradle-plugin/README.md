Minikube Gradle Plugin
======================

Now available on [gradle plugin portal](https://plugins.gradle.org/plugin/com.google.cloud.tools.minikube).

This plugin provides tasks and configuration to manage a minikube lifecycle for gradle developers.

It exposes the following tasks
- `minikubeStart`
- `minikubeStop`
- `minikubeDelete`

It exposes the `minikube` configuration extension.

```groovy
minikube {
  minikube = // path to minikube, default is "minikube"
}
```

Task specific flags are configured on the tasks themselves, all minikube tasks are of the type `MinikubeTask` and all share the same kind of configuration.
- `flags` (`String[]`) : any minikube flags **this is the only one users should edit for the provided tasks**
- `minikube` (`String`) : path to minikube executable which should be set by using the `minikube` extension
- `command` (`String`) : start/stop/whatever (users probably shouldn't be editing this for default commands)

```groovy
minikubeStart {
  flags = ["--vm-driver=none"]
}
```

This plugin also allows users to add in any custom minikube task.

```groovy
task minikubeCustom(type: com.google.cloud.tools.minikube.MinikubeTask) {
  command = "custom"
  flags = ["--some-flag"]
}
```
