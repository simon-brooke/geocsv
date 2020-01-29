# geocsv

A wee tool to show comma-separated value data on a map.

The CSV file must have

* column names in the first row;
* data in all other rows;
* a column whose name is `name`, which always contains data;
* a column whose name is `latitude`, whose value is always a number between -90.0 and 90.0;
* a column whose name is `longitude`, whose value is always a number between -180.0 and 180.90

Additionally, the value of the column `category`, if present, will be used to select map pins from the map pins folder, if a suitable pin is present. Thus is the value of `category` is `foo`, a map pin image with the name `Foo-pin.png` will be selected.

## Passing CSV files to the app

### Loading them onto the server

If you run the server running **geocsv**, the simplest way to add CSV files is simply to copy them into the directory `resourcs/data`. The default file is the one named `data.csv`, which is the one that will be served if nothing else is specified. Other files can be specifiec by appending `?file=filename` to the URL; so if the URL of your geocsv service is

    https://geocsv.example.com/

and the file you want to view is `myfile.csv`, then you would specify this as

    https://geocsv.example.com/?file=myfile.csv

### Using a Google spreadsheet

If you use [Google Sheets](https://www.google.co.uk/sheets/about/), then every sheet has a 'document id', a long string of characters which uniquely identifies that sheet. Suppose your Google spreadsheet has a document id of `abcdefghijklmnopqrstuvwxyz-12345`, then you could pull data from this spreadsheet by specifying:

    https://geocsv.example.com/?docid=abcdefghijklmnopqrstuvwxyz-12345

The spreadsheet **must** be publicly readable.

## Not yet working

GeoCSV is at an early stage of development, and some features are not yet working.

### Missing map pin images

At the current stage of development, if no appropriate image exists in the `resources/public/img/map-pins` folder, that's your problem. **TODO:** I intend at some point to make missing pin images default to `unknown-pin.png`, which does exist.

### Doesn't scale and centre the map to show the data in the sheet

Currently the map is initially centred roughly on the centre of Scotland, and scaled arbitrarily. It should compute an appropriate centre and scale from the data provided, but currently doesn't.

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein npm install
    lein run

## License

Copyright © 2020 Simon Brooke

Licensed under the GNU General Public License, version 2.0 or (at your option) any later version.

**NOTE THAT** files which are directly created by the Luminus template do not currently have a GPL header
at the top; files which are new in this project or which have been substantially modified for this project should have a GPL header at the top.
