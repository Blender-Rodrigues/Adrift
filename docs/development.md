# Development guidelines

## Project setup locally

Hopefully Intellij is smart enough to detect gradle files and offer to use .gradlew that's included in the repo
on the next startup, but just in case gradle can also be installed on your machine via
https://docs.gradle.org/current/userguide/installation.html

## Commit messages

Start your commit message with `#x` (where x is the issue number in gitlab) and then add a sentence 
describing the content of the commit. See https://www.freecodecamp.org/news/writing-good-commit-messages-a-practical-guide/
or a similar guide for to get the ideas what should be and shouldn't be covered in that message.
 
But P.S. - do keep the issue reference at the start of the message please, makes much more sense to have a fixed position
for that. 

## Log file formatting

Settings > Log Highlighting (Ideolog) > Add

* Message pattern: `^(\[[A-Z ]+\]) (.{23}) (\[.+\]) (\w+) - (.*)$`
* Message start: `^\[`
* Time capture group: 2
* Severity capture group: 1
* Category capture group: 4

And after that you can start adding formatting in the box left below,
 
E.g. `^\[ERROR]` highlight line, foreground red etc

## Tests

Gitlab is configured to run tests on every branch, don't merge branches which have failing tests.

## Deploy

Merge to development branch triggers an update for the test game server (port 8888)
Merge to master branch triggers an update for the live game server (port 8880) and static web page

## To see logs or restart game server

```bash
ssh username@104.248.243.136 docker logs -f escape-from-eros
ssh username@104.248.243.136 docker restart escape-from-eros
```

## Gitlab runner setup

```
docker build -t jaakkytt/runner:latest -f RunnerDockerfile .
docker push jaakkytt/runner:latest
docker run -d --name gitlab-runner --restart always -v /var/run/docker.sock:/var/run/docker.sock jaakkytt/runner:latest
```
