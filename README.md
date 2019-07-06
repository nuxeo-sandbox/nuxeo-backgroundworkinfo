# Nuxeo Background Work Info
<hr>

**WARNING**: This is **W**ork **I**n **P**rogress

Getting info about the background work: Mainly Workers, Bulk Action Framework and asynchronous Event Handlers (aka Listeners).

One important concept to understand is that the info returned is valid only at the exact time it was fetched. This is the intrinsic way asynchronous/background jobs work: The info can return "10 workers" when it is called, and return 100 or 0 one millisecond later.


## WARNINGS
* **W**ork **I**n **P**rogress, as stated above (but we say it again here)
* This work is **not supported** until it is written it is supported


## Build

    cd /path/to/nuxeo-backgroundworkinfo
    mvn clean install



## Known limitations
This plugin is a work in progress.



## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)



## About Nuxeo
Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris. More information is available at [www.nuxeo.com](http://www.nuxeo.com).
