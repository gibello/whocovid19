#!/bin/bash
echo "Date,Country,ISO-3166 code,Confirmed cases,New cases,Deaths,New deaths,Transmission type,Days since last case" > global_who_data.csv
for f in data/csv/*.csv
do
tail -n +2 $f|sed '/^$/d' >> global_who_data.csv
done

