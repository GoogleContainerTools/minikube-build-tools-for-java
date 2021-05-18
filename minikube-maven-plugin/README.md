| ⛔⛔⛔ DEPRECATED ⛔⛔⛔ |
| :----- |
| These plugins are not maintained, no more updates or releases will be made. Users may fork and update this project on their own. |

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.google.cloud.tools/minikube-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.google.cloud.tools/minikube-maven-plugin)

Minikube Maven Plugin
======================
This plugin provides tasks and configuration to manage a minikube lifecycle for Maven developers.

Now available on the [Central repository](https://maven-badges.herokuapp.com/maven-central/com.google.cloud.tools/minikube-maven-plugin).

This plugin requires that you have Minikube [installed](https://kubernetes.io/docs/tasks/tools/install-minikube/).

In your Maven Java project, add the plugin to your `pom.xml`:

```xml
<plugin>
  <groupId>com.google.cloud.tools</groupId>
  <artifactId>minikube-maven-plugin</artifactId>
  <version>1.0.0-alpha.1</version>
</plugin>
```

It exposes the following goals:

- `minikube:start` : Starts a local kubernetes cluster
- `minikube:stop` : Stops a local kubernetes cluster
- `minikube:delete` : Deletes a local kubernetes cluster

Configure additional plugin options:

Field | Default | Description
--- | --- | ---
`minikube`|`minikube`|Path to minikube executable
`flags`|*None*|Flags to pass to minikube
`start`|*None*|Configuration for `start` goal
`stop`|*None*|Configuration for `start` goal
`delete`|*None*|Configuration for `delete` goal

Example configuration:

```xml
<plugin>
  ...
  <configuration>
    <minikube>/path/to/minikube</minikube>
    <flags>
      <flag>flags to pass to minikube</flag>
    </flags>
    <start>
      <flags>
        <flag>flags for the start task</flag>
      </flags>
    </start>
    <stop>
      <flags>
        <flag>flags for the stop task</flag>
      </flags>    
    </stop>
    <delete>
      <flags>
        <flag>flags for the delete task</flag>
      </flags>    
    </delete>
  </configuration>
</plugin>
```
