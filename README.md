# Nuxeo Background Work Info
<hr>

QA status<br/>
[![Build Status](https://qa.nuxeo.org/jenkins/buildStatus/icon?job=Sandbox/sandbox_nuxeo-nuxeo-backgroundworkinfo-master)](https://qa.nuxeo.org/jenkins/view/Sandbox/job/Sandbox/job/sandbox_nuxeo-backgroundworkinfo-master/)

**WARNING**: This is **W**ork **I**n **P**rogress

Getting info about the background work: Workers, Bulk Action Framework, Streams, Events, ...

Current implantation handles only Workers and Bulk Actions (That said, this covers almost everything: most asynchronous listeners use the WorkManager to handle their tasks, so they are handled)

## About Cluster of Nuxeo

* Information returned about Bulk Action Framework is global, compatible with a cluster deployment of nuxeo, listing all the actions running/scheduled/etc. on all the nodes.

* For workers, it depends on the way the persistence of works is configured. With Kafka, for example, the information returned is global.

## WARNINGS:
### Such Info is Highly Ephemeral
One important concept to understand is that the info returned is valid only at the exact time it was fetched. This is the intrinsic way asynchronous/background jobs work: The info can return "10 workers" when it is called, and returns 100 or 0 a millisecond later.

### There can be duplicates in the list
A typical example would be an asynchronous worker, say "Worker1", that launches a Bulk Action ("Bulk1") that runs in a single thread. The worker then waits until completion of the BAF. In this case, there will be two running activities ("Worker1" and "Bulk1") while there really is only one, somehow (even if it uses several threads)


## Install
The plugin is available on the public [Nuxeo marketplace](https://doc.nuxeo.com/studio/nuxeo-marketplace/). So, for example, it can be installed with `nuxeoctl`:

```
./nuxeoctl mp-install nuxeo-backgroundwork-info -s
```

(`-s` means "allow for installing SNAPSHOT. The plugin version is a snapshot.)


## Usage
The plugin:

* Implements the `InfoFetcher` class that, well, fetches the info
* Contributes the `BackgroundWork.Overview` operation for easy usage from UI
* Contributes a WebUI Admin slot tab in "Analytics"
  * *Pull requests welcome to make it look a bit better :-)*

#### As of today-now the plugin fetches only:
* Workers
* Bulk Action Framework


### Operation: BackgroundWork.Overview

<div style="margin-left:50px; padding:5px; background-color:#eeeeee">
<span style="font-weight:bold">IMPORTANT</span>: This operation is filtered and can be called only by users belonging to the "administrators" group. See below "Operations REST filtering" for more details.
</div>

* Input/output: `void`
* Parameter(s)
  * `infoType`, string. Possible values: `"Basic"` and `"Overview"`. Default is `"Basic"`
* The operation returns a JSON string whose value depends on `infoType`. From JS Automation, you can then call `JSON.Parse` on the result and handle it as you wish.

##### => If `infoType` is `"Basic"`...
...then the operation returns JSON object (as string):

```
{
  scheduled: number,
  running: number,
  completed: number,
  aborted: number
}
```
These are global values, no details, they are the total number of background activities.

##### => Else, if `infoType` is `"Overview"`...
...then the operation returns a JSON Array (as string). Each object in the array is:

```
{
  type: string,
  name: string,
  scheduled: number,
  running: number,
  completed: number,
  aborted: number
}
```

The type will be "Worker" or "BAF".

The `name` is the info declared in the configuration. For example, for workers, it is the ID of the Queue they belong to.

Here is an example of simple output:

```
[
  {
    "name": "default",
    "running": 2,
    "scheduled": 12,
    "aborted": 0,
    "completed": 421
  },
  {
    "name": "fulltextUpdater",
    "running": 2,
    "scheduled": 18,
    "aborted": 0,
    "completed": 234
  },
  {
    "name": "updateACEStatus",
    "running": 0,
    "scheduled": 0,
    "aborted": 0,
    "completed": 0
  },
  {
    "name": "myCustomWorker",
    "running": 4,
    "scheduled": 0,
    "aborted": 0,
    "completed": 27
  },
  {
    "name": "myCustomBAFAction",
    "running": 2,
    "scheduled": 1,
    "aborted": 0,
    "completed": 14
  }
  . . .
]
```

## Operations REST filtering
The opérations that are filtered for REST calls and restricted to administrators are listed in the `backgroundworkinfo-operations-contrib.xml` file. We are using the recommended mechanism for the filtering, as described [here](https://doc.nuxeo.com/nxdoc/filtering-exposed-operations/). `backgroundworkinfo-operations-contrib.xml` contains, for example:

```
  <extension target="org.nuxeo.ecm.automation.server.AutomationServer" point="bindings">
    <binding name="BackgroundWork.Overview">
      <groups>administrators</groups>
    </binding>
  </extension>
```

This contribution makes sure these operations, when called via REST, can only be called by a user belonging to the administrators group. If you need to make them available via REST for other users/groups, you can:

* Override them in a Studio XML extension
* Use them in an Automation Chain that you call from REST


## WARNINGS
* **W**ork **I**n **P**rogress, as stated above (but we say it again here). So far only Workers and Bulk Actions are handled.
* This work is **not supported** until it is explicitly written it is supported


## Build

    cd /path/to/nuxeo-backgroundworkinfo
    mvn clean install



## Known limitations
See _WARNINGS_, above.



## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)



## About Nuxeo
Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris. More information is available at [www.nuxeo.com](http://www.nuxeo.com).
