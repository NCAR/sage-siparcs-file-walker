# End of Summer Notes for 2024

A lot was learned this year about Elasticsearch:
* Indexes are much more performant than updates due to the fact that Lucine records are immutable.  Meaning that every update
we do will cause a new record to be written and then the original record is the marked for deletion.  This means that there
are records in the index, taking up space, but not being used, and they won't be cleaned up until there is an index merge
which Elasticsearch does naturally on its own.
* Elasticsearch has a high watermark where it will stop writing to disk if there isn't enough disk space remaining.  Generally speaking
this number is set to 90%, but can be changed.  When this value is reached then the index is searchable, but we can no longer
write to it and the merge index behavior of Elasticsearch also stops.
* It is easy to add a new field to an Elasticsearch schema, but it is not possible to remove unless the data is moved
from the current index to a new index that has the schema that you want.

## Future Ideas

Change the workflow to happen prior to inserting a record into Elasticsearch.  Figure out what data we want, media-type, 
scientific metadata (variables, model run, temporal rangs, spatial range, etc...), checksum, and anything else, prior to
the initial write to Elasticsearch because updates (see above for deleted records) is quite painful.

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