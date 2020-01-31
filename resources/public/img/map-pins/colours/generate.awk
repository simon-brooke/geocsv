#!/usr/bin/awk -f

BEGIN {
  FS=",";
  print( "#!/bin/bash");
  print( "# Automatically generated bash script to generate all the named colours.");
}
{
  gsub( / /, "-", $1);
  printf( "cat Blank-pin.svg | sed  's/fill:#dddddc;/fill:%s/' > \"%s-pin.svg\"\n", $2, $1);
}
END {
  print( "mogrify -format png *.svg");
}
