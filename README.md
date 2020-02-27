# geocsv

A wee tool to show comma-separated value data on a map.

[![Clojars Project](https://img.shields.io/clojars/v/geocsv.svg)](https://clojars.org/geocsv)

## Other variants

This is a little project I've played about with, and there are now three variants:

1. [geocsv](https://github.com/simon-brooke/geocsv) is a fairly heavyweight web-app with both client-side and serverside components. It was the first version, and is the only version which meets the original requirement of being able to present data from [Google Sheets](https://www.google.co.uk/sheets/about/), but it's a remarkably heavyweight solution to what should be a simple problem.
2. [geocsv-lite](https://github.com/simon-brooke/geocsv-lite) is a much lighter, client-side only reworking of the problem, in ClojureScript. I still wasn't satisfied that this was light enough.
3. [geocsv-js](https://github.com/simon-brooke/geocsv-js) is a reworking in native JavaScript without any frameworks or heave libraries, except Leaflet. It is vastly lighter, and probably the one to use in most applications.

## Overview

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

and the file you want to view is `myfile.csv`, then you would specify this as the value of `file` in the query part of the URL.

    https://geocsv.example.com/?file=myfile.csv

### Loading CSV file onto another public server

If you're not running the **geocsv** server yourself, you can upload the CSV to another server which is accessible by the **geocsv** server. You can then map data from the CSV file by specifying the URL of the file as the value of `uri` in the query part of the URL:

    https://geocsv.example.com/?uri=http://my.other.server/path/to/myfile.csv

### Using a Google spreadsheet

If you use [Google Sheets](https://www.google.co.uk/sheets/about/), then every sheet has a 'document id', a long string of characters which uniquely identifies that sheet. Suppose your Google spreadsheet has a document id of `abcdefghijklmnopqrstuvwxyz-12345`, then you could pull data from this spreadsheet by specifying this as the value of `docid` in the query part of the URL:

    https://geocsv.example.com/?docid=abcdefghijklmnopqrstuvwxyz-12345

The spreadsheet **must** be publicly readable.

### Precedence

Nothing, of course, stops you from specifying multiple arguments in the query part of the URL, but only one will be used. The precedence is in this order:

1. `docid` is considered first, and overrides anything else;
2. `uri` is considered next, and overrides `file`;
3. the value of `file` is considered only if neither of the other two are present.

## Not yet working

**geocsv** is at an early stage of development, and some features are not yet working.

### Missing map pin images

At the current stage of development, if no appropriate image exists in the `resources/public/img/map-pins` folder, that's your problem. See [issue #4](https://github.com/simon-brooke/geocsv/issues/4). If you fancy adding some more, I'll happily accept a pull request.

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
