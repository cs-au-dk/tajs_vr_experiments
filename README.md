### Building TAJS-VR-Experiments

Make sure you clone not only the TAJS-VR-Experiments repository but also the submodules, for example by running
```
git submodule update --init --recursive
``` 

The simplest way to build TAJS-VR is to run Ant:
```
ant
```
This will build two jar files: `dist/tajsvr.jar` (contains only TAJS-VR itself) and `dist/tajsvr-all.jar` (includes the relevant extra libraries).

### Running TAJS-VR-Experiments
The analysis can be run as follows:
```
java -jar dist/tajsvr-all.jar [EXPERIMENT] [ARGUMENTS]
```
where [EXPERIMENT] can be one of:
 - micro-benchmarks
    - Description: Runs micro-benchmarks with TAJS and TAJS-VR.
    - [ARGUMENTS]: Takes no arguments. 
    - Output: Table similar to table 1 in the paper.
 - library-benchmarks
    - Description: Runs library-benchmarks with TAJS-VR.
    - [ARGUMENTS]: Takes a single argument, which specifies a comma-separated of suites. Valid test-suites are 
    jquery, jsai, prototype, scriptaculous, underscore, lodash3 and lodash4. To run all test-suites, the argument ALL can be used instead.
    - Output: Table similar to table 2, table 3 and the appendix tables for the specified suites.   
 - single-test
    - Description: Runs TAJS-VR on a specified test.
    - [ARGUMENTS]: arguments are directly passed to TAJS, so see the [TAJS readme](submodules/tajs_vr/README.md) for details.
    - Output: Same output as running TAJS without value refinement

## Reproducing results from paper
Note that the paper has run experiments with a JVM with 10GB RAM, so we use the Java option -Xmx10G to enable this.
Reproduce results from Table 1 in the paper (expected time is few seconds):
```
java -Xmx10G -jar dist/tajsvr-all.jar micro-benchmarks
```

Reproduce the rest of the tables (expected time is around 8 hours):
```
java -Xmx10G -jar dist/tajsvr-all.jar library-benchmarks ALL
```

Since recalculating all results takes a long time, smaller versions of the tables can be produced, by only running some of the library test-suites.
```
java -Xmx10G -jar dist/tajsvr-all.jar library-benchmarks scriptaculous,underscore
```

Expected running time for each test-suite:
 - jquery: 1 hour and 14 minutes
 - jsai: 29 minutes
 - prototype: 15 minutes
 - scriptaculous: 5 minutes
 - underscore: 13 minutes
 - lodash3: 44 minutes
 - lodash4: 4 hours and 25 minutes