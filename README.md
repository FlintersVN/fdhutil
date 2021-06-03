# Some utilities for Data Engineer

## Installation
Prerequisite java 8+
```cli
$ curl -sL https://github.com/FlintersVN/fdhutil/releases/download/v0.0.1/cli-0.0.1.zip -o cli.zip
$ unzip cli.zip
$ mv cli-0.0.1 cli
$ ./cli/bin/fdhutil --help
```
## Usage

### Mongo Import

Import multiple json or json.gz files with [Json Path](<https://github.com/jsurfer/JsonSurfer#what-is-jsonpath>) to extract which data to be imported

Commands

```cli
$ ./cli/bin/fdhutil mongo-import
-c, --collection  <arg>   collection to be imported, required

-d, --db  <arg>           database name, required
    --dir  <arg>          Absolute path, required
    --drop                drop collection if exist, default: false

-g, --gunzip              gunzip files in directory, if enable only json.gz files will be processed, default: false
-h, --host  <arg>         database host, default: localhost
-j, --json-path  <arg>    json path to extract, default: $

    --password  <arg>     database password to be imported, required
-p, --port  <arg>         database host, default: 27017
-u, --username  <arg>     database username to be imported, required
-v, --verbose             verbose logging, default: false
--help                Show help message
```

Example

```cli
$ ./cli/bin/fdhutil mongo-import --db test \
--collection testCollection \
--drop \
--username test \
--password test \
--dir /Users/huy_nq/Projects/kyotsu/ad-platform-client/media-data/smartnews/campaign/2020/12/ \
--gunzip \
--json-path $.data
```
