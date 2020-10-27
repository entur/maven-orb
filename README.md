# maven-orb
This orb clones the official [CircleCI Maven] orb interface, but applies a different caching strategy. 

In a nutshell, it __detects previous build file changes via the git history__, so that it can restore the previous Maven cache regardless of what changed in the latest commit.

In contrast, the official CircleCI orb restores the previous Maven cache via a hash of the build files, so all changes to the build files (even whitespace changes) result in a cache miss. 

Typical use-case:

 * projects with frequent tweaks to the Maven build, i.e.
   * functional changes in the build files themselves
   * dependency bumping

Advantages:

 * Improves build time when the Maven build files themselves are updated, as (most) dependencies are already in the cache
 * Less traffic for artifact repositories (i.e. Maven central, JCenter, your own private Artifactory etc.)

Disadvantages:

 *  Instruments Maven itself to record POM files in use
      * The cache job might break on a future version of Maven or the JVM. If so, it is trivial to (temporarily) revert to the official Maven orb.
      
Bugs, feature suggestions and help requests can be filed with the [issue-tracker].

## Usage
Import the orb

```yaml
orbs:
  owasp: entur/maven-orb@0.0.x
```

where `x` is the latest version from [the orb registry](https://circleci.com/orbs/registry/orb/entur/maven-orb).

Note that the official orb goes offline before running the build. This does noes work well for multi-module builds, and so this orb detects whether the root `pom.xml` has any `<modules>` tag and if so skips going offline.

### Default executor
To use the default executor, [Docker Hub credentials](https://circleci.com/docs/2.0/private-images/) must be set as the environment variables `$DOCKERHUB_LOGIN` and `$DOCKERHUB_PASSWORD`.

## Caching strategy
For a detailed caching strategy walkthrough, see our [Gradle orb](https://github.com/entur/gradle-orb).

## Troubleshooting
If the cache is corrupted, update the cache key, so that the previous state is not restored - as in the official Maven orb.

[issue-tracker]:               https://github.com/entur/maven-orb
[CircleCI Maven]:             https://circleci.com/orbs/registry/orb/circleci/maven
