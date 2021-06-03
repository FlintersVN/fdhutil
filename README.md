# Some utility for Data Engineer

## Usage

### Mongo Import

Import multiple json or json.gz files with [Json Path](<https://github.com/jsurfer/JsonSurfer#what-is-jsonpath>) to extract which data to be imported

Commands

```bash
mongo-import
-c, --collection  <arg>   collection to be imported, required
-d, --db  <arg>           database name, required
    --dir  <arg>          Absolute path, required
    --drop                drop collection if exist, default: false
-g, --gunzip              gunzip files in directory, if enable only json.gz
                          file will be processed, default: false
-h, --host  <arg>         database host, default: localhost
-j, --json-path  <arg>    json path to extract, default: $
    --password  <arg>     database password to be imported, required
-p, --port  <arg>         database host, default: 27017
-u, --username  <arg>     database username to be imported, required
    --help                Show help message
```

Example

```cli
mongo-import --db test \
--collection testCollection \
--drop \
--username test \
--password test \
--dir /Users/huy_nq/Projects/kyotsu/ad-platform-client/media-data/smartnews/campaign/2020/12/\
--gunzip \
--json-path $.data
```
