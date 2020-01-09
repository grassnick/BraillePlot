# BraillePlot

A tool to create Braille embosser ready text files for generic diagram representations.

## Build status
![](https://github.com/grassnick/BraillePlot/workflows/Continuos%20Integration/badge.svg)

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

For usage information, please take a look into the [wiki](https://github.com/TUD-INF-IAI-MCI/BraillePlot/wiki/EndUserDocumentation).

### Prerequisites

You will need the following tools to build the project:
* JavaJDK >= 11
* gradle >= 4.4
* git

#### Build preparation

Use the following commands to setup your local source tree:

```bash
git clone ${URL_OF_THIS_REPO}
git submodule update --init --recursive # This will fetch third party dependencies
```

### Installing

Just run
```
gradle jar
```

to create an executable jar file in `build/libs` which will include all runtime dependencies.


### Running the tests

A simple call of
```
gradle test
```

will suffice.


### And coding style tests


```
gradle check
```

will execute the checkstyle plugin which will export any flaws registered.
Results will be available on the command line as well as html exports in `build/reports/checkstyle`.

Please fix all the reported issues before opening merge requests, or start a discussion if you are convinced certain rules are too strict.

## Contributing

There is no research branch - use the wiki for advanced documentation.

This project follows the github workflow [[1](https://guides.github.com/introduction/flow/), [2](http://scottchacon.com/2011/08/31/github-flow.html)].
Please create feature branches off of the `master` branch and open merge requests for reviewing purposes.


Branch names sould follow the following convention:
`$Prefix/$Description-$Issue_number`, where

* `$Prefix` is either
    * `feat` when introducing a new feature
    * `bug` when fixing a bug

* `Description` is a short summary of the applied change (use underscores (`_`) to separate multiple words, if needed)

* `$Issue_number` is the associated issue id



## Authors

In alphabetical order:
* Georg Graßnick
* Leonard Kupper
* Andrey Ruzhanskiy
* Richard Schmidt

## License

Licensed under the GPL-3.0.

See [LICENSE](LICENSE).

## Acknowledgments

No information yet.