# Some utility for Data Engineer

## Usage

### Mongo Import

Import multiple json or json.gz files with Json Path to extract which data to be imported

Commands

```bash
mongo-import
-c, --collection  <arg>
-d, --db  <arg>
    --dir  <arg>
    --drop
-g, --gunzip
-h, --host  <arg>
-j, --json-path  <arg>
    --password  <arg>
-p, --port  <arg>
-u, --username  <arg>
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
