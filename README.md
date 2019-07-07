# Nuxeo Background Work Info
<hr>

**WARNING**: This is **W**ork **I**n **P**rogress

Getting info about the background work: Mainly Workers. WIP is trying to get also info from Bulk Action Framework/Streams and asynchronous Event Handlers (aka Listeners). Most asynchronous listeners actually run in workers, but still, we want to get more info if possible.

Current implantation handles Workers and Bulk Actions.

## WARNINGS:
### Such Info is Highly Ephemeral
One important concept to understand is that the info returned is valid only at the exact time it was fetched. This is the intrinsic way asynchronous/background jobs work: The info can return "10 workers" when it is called, and return 100 or 0 0.2 millisecond later.

### There can be duplicates
A typical example would be an asynchronous worker, say "Worker1" that launches a Bulk Action ("Bulk1") and waits until the completion of the bulk computation. In this case, there will be, say, 2 running activities while it really is only one, somehow (even if it uses several threads)

## Usage
The plugin exposes a class, `InfoFetcher` that, well, fetches the info.

#### As of today-now the plugin fetches only:
* Workers
* Bulk Action Framework running and scheduled actions
  * We don't list aborted/completed actions

It also contributes a new operation: `BackgroundWork.Overview`

### Operation: BackgroundWork.Overview

* Input/output: `void`
* Parameter(s)
  * `infoType`, string. Possible values: `"Basic"` and `"Overview"`. Default is `"Basic"`
* The operation fills the `BackgroundWorkOverview` context variable with a JSON string whose value depends on `infoType`. From JS Automation, you can then call `JSON.Parse(ctx. BackgroundWorkOverview)` and handle the result as you wish.

##### => If `infoType` is `"Basic"`...
...then `BackgroundWorkOverview` is a JSON object (as string):

```
{
  scheduled: number,
  running: number,
  completed: number,
  aborted: number
}
```
These are global values, no details, they are the total number of background activities. (**WARNING**: As this is Work In Progress, we have only Workers info, not BAF/Streams/etc.)

##### => Else, if `infoType` is `"Overview"`...
...then `BackgroundWorkOverview` is a is a JSON Array (as string). Each object in the array is:

```
{
  name: string
  scheduled: number,
  running: number,
  completed: number,
  aborted: number
}
```

Again, **WARNING**: As this is Work In Progress, we have only Workers info, not BAF/Streams/etc.

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
    "completed": 0
  }
  . . .
]
```




## WARNINGS
* **W**ork **I**n **P**rogress, as stated above (but we say it again here). SO far only Workers are handled.
* This work is **not supported** until it is written it is supported


## Build

    cd /path/to/nuxeo-backgroundworkinfo
    mvn clean install



## Known limitations
This plugin is a work in progress and handles only Workers, not the Bulk Action Framework, the streams, ...



## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)



## About Nuxeo
Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris. More information is available at [www.nuxeo.com](http://www.nuxeo.com).
