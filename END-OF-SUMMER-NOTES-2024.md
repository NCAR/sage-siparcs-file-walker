# End of Summer Notes for 2024

A lot was learned this year about Elasticsearch:
* Indexes are much more performant than updates due to the fact that Lucine records are immutable.  Meaning that every update
we do will cause a new record to be written and then the original record is the marked for deletion.  This means that there
are records in the index, taking up space, but not being used, and they won't be cleaned up until there is an index merge
which Elasticsearch does naturally on its own.
* Elasticsearch has a high watermark where it will stop writing to disk if there isn't enough disk space remaining.  Generally speaking
this number is set to 90%, but can be changed.  When this value is reached then the index is no longer accessible until the
disk space situation is improved.
* It is easy to add a new field to an Elasticsearch schema, but it is not possible to remove unless the data is moved
from the current index to a new index that has the schema that you want.

## Future Ideas

Change the workflow to happen prior to inserting a record into Elasticsearch.  Figure out what data we want, media-type, 
scientific metadata (variables, model run, temporal ranges, spatial ranges, etc...), checksum, and anything else, prior to
the initial write to Elasticsearch because updates (see above for deleted records) is quite painful.

FWIW, these ideas are in response to having difficulties with the size of the Elasticsearch index on disk.  Figure out what
types of files do we want?  Should we have a Strategy (pattern) that picks what style of file or directory we want to add
to the index?  Ideas include, no directories (we haven't really queried for directories yet and there are a lot of them > 8
million for the directories we picked this summer), don't index zero sized files (we had to add behavior to our scientific
file workflow query this summer to exclude zero length files due to a netcdf library exception), only index scientific files
(this depends on the use case of the system for sure, for this summer we really focused on the find-ability of scientific
data, however we were approached by NRIT to perhaps deploy this application on numerous VMs to help get an audit of what was
on those VMs prior to deletion).

By default, Elasticsearch will allow up to 20% of the index to be taken up by deleted records, it will then perform a merge
on its own (this cannot be triggered by us) and it will start to merge segments removing deleted records.  The deletes_pct_allowed
value can be set between 5.0% and 50.0%.  The lower the value, the more cpu is used to merge segments, but the index size
on disk will be smaller.

You've tried to change this value by sending the following in the Kibana UI "Edit index settings" mode:
```
{
  "index.merge.policy.deletes_pct_allowed": "5.0"
}
```

But for some reason it didn't seem to take, but by the time you set that value, the index itself was quite large and you were
experiencing disk space issues.  Try setting this value earlier.  You tried to set this value via Java code during index
creation, but there just didn't seem to be a way to do it, check this again in the future.

Following up on the idea above, if we decide to add any new fields to our schema, we would need to add it to the current workflow(s)
and/or create a back-fill workflow to fill in all the data that is missing from the records that are currently in Elasticsearch.
Keeping in mind that we will have to let Elasticsearch rest and merge indexes to remove deleted files caused by updates.

There are two states of the file-walker, the initial state of just get the files into Elasticsearch (only insert) vs maintenance mode which
is used after the initial run has been completed, and then we would transfer to updating/inserting files (a combination of
insert/update or as Elasticsearch calls it upsert).

Consider:  Only searching specific scientific files that have specific extensions or only curated directories.  During this
summer we really decided that the Use Case for this system is to help find scientific data that can be used for science.  While
the system CAN be used for more system administration work (what files are there on the disk, who is over their disk allotment, etc...),
we decided it really should be about finding data.  Some of the choices of directories we chose this summer weren't great, and it caused
a lot of home directories and things like that to be scanned, which is most likely a lot of scratch data and really
shouldn't be indexed if our goal is to help find useful scientific data.