# geocsv

GeoCSV is a wee tool to show comma-separated value data on a map.

The CSV file must have

* column names in the first row;
* data in all other rows;
* a column whose name is `name`, which always contains data;
* a column whose name is `latitude`, whose value is always a number between -90.0 and 90.0;
* a column whose name is `longitude`, whose value is always a number between -180.0 and 180.90

Additionally, the value of the column `category`, if present, will be used to select map pins from the map pins folder, if a suitable pin is present. Thus is the value of `category` is `foo`, a map pin image with the name `Foo-pin.png` will be selected.

## Not yet working

GeoCSV is at an early stage of development, and some features are not yet working.

### Doesn't actually interpret CSV

I haven't yet found an easy way to parse CSV into EDN client side, so I've written a [separate library](https://github.com/simon-brooke/csv2edn) to do it server side. However, that library is not yet integrated. Currently the client side actually interprets JSON.

### Missing map pin images

At the current stage of development, if no appropriate image exists in the `resources/public/img/map-pins` folder, that's your problem. **TODO:** I intend at some point to make missing pin images default to `unknown-pin.png`, which does exist.

### Doesn't scale and centre the map to show the data in the sheet

Currently the map is initially centred roughly on the centre of Scotland, and scaled arbitrarily. It should compute an appropriate centre and scale from the data provided, but currently doesn't.

### There's no way of linking your own data feed

Currently, the data is taken from the file `resources/public/data/data.json`. What I intend is that you should have a form which allows you to either

1. enter [the `DOCID` of your own (publicly readable) Google Sheets spreadsheet](https://stackoverflow.com/questions/33713084/download-link-for-google-spreadsheets-csv-export-with-multiple-sheets);
2. enter the URL of a CSV file publicly available on the web;
3. upload a CSV file to the server.

### There's no way of shareing the map of your own data with other people

Currently, the data that is shared is just the data that's present when the app is compiled. Ideally, there should be a way of generating a URL, which might take the form:

    https://server.name/geocsv/docid/564747867

To show data from the first sheet of the Google Sheets spreadsheet whose `DOCID` is 564747867; or

    https://server.name/geocsv?uri=https://address.of.another.server/path/to/csv-file.csv

to show the content of a publicly available CSV file.

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
