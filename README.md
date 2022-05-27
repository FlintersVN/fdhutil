# Some utilities for Data Engineer

## Installation

Prerequisite java 8+

```cli
$> curl -sL https://github.com/FlintersVN/fdhutil/releases/download/v0.0.3/cli-0.0.3.zip -o cli.zip
$> unzip cli.zip
$> mv cli-0.0.3 cli
$> ./cli/bin/fdhutil --help
```

## Usage

For windows users, use fdhutil.bat instead

### Mongo Import

Import multiple json or json.gz files in streaming manner using [Json Path](<https://github.com/jsurfer/JsonSurfer#what-is-jsonpath>) to extract which data to be imported

Commands

```cli
$> ./cli/bin/fdhutil mongo-import
    -a, --auth-source  <arg>   auth source for authentication, default: admin
    -c, --collection  <arg>    collection to use, required
    -d, --db  <arg>            database to use, required
        --dir  <arg>           Absolute path, required
        --drop                 drop collection before inserting documents, default: false
    -g, --gunzip               only json.gz file is processed, default: false
    -h, --host  <arg>          mongodb host to connect to, default: localhost
    -j, --json-path  <arg>     json path to data to be imported, default: $
        --password  <arg>      password for authentication, required
    -p, --port  <arg>          server port, default: 27017
    -u, --username  <arg>      username for authentication, required
    -v, --verbose              more detailed log output, default: false
        --help                 Show help message
```

Example

```cli
$ ./cli/bin/fdhutil mongo-import --db test \
--collection testCollection \
--drop \
--username test \
--password test \
--dir ./ad-platform-client/media-data/campaign/2020/12/ \
--gunzip \
--json-path $.data
```
