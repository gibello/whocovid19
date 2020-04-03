# WHO covid19 CSV reports data + PDF parser

Parse WHO covid19 PDF reports, and provide data by country in CSV format.
This repository provides the extracted CSV data (starting from march 1, 2020) + the parser source code.

**Latest parsed data (CSV) are here: [data/csv](data/csv)** . Note that files are named against their date of publication + report #, and the date corresponds to data collected the day before (eg. 20200322-sitrep-62-covid-19.csv contains data as of march 21, and is report #62). Same naming convention as WHO reports.

WHO reports available here:
https://www.who.int/emergencies/diseases/novel-coronavirus-2019/situation-reports

Parser should not work for reports published before March 2, 2020 (data as of march 1st).

To aggregate all data in one single file, you may use the following unix command (in the example below, it is assumed the command is run in the directory where the csv files are, and the aggregated data is output in /tmp/data.csv):

```
for f in *.csv; do tail -n +2 $f >> /tmp/data.csv; done
```

To load data in a MySQL database, the following statements should fit (if using a daily report, with 1st line containing metadata, add "IGNORE 1 ROWS" to the "LOAD DATA" statement; if you do not wish to overwrite existing data, remove "REPLACE" option):

```
mysql> create table whocovid19 (date DATE, country_name VARCHAR(255), country_code VARCHAR(4), confirmed_cases INT, new_cases INT, deaths INT, new_deaths INT, transmission_type VARCHAR(32), days_since_last_case INT, PRIMARY KEY(date, country_code));
Query OK, 0 rows affected (0.41 sec)
mysql> LOAD DATA LOCAL INFILE '/tmp/data.csv' REPLACE INTO TABLE whocovid19 FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n';
Query OK, 4876 rows affected (0.20 sec)
Records: 4876  Deleted: 0  Skipped: 0  Warnings: 0
```

## Why are data important ?

Data can help people discover new things. They can be related to epidemiology (refine knowledge about how the virus spreads, when, where, for how long...), but may also help new ideas to emerge (correlate virus spread with external factors, like pollution, sociological facts, economy... by using other available data sources).

The more exploitable data sources are available, the more public, private or individual researchers, statisticians, data scientists or even journalists can easily get material to work.

Open data is certainly a key driver of future social progress, knowledge, freedom, and democracy.

## Are there other data sources ?

Among data sources available, I would recommend these ones:
- Johns Hopkins University (USA): https://github.com/CSSEGISandData/COVID-19 (CSV format, with daily data and time series).
- ECDC (European Center for Disease Control): https://www.ecdc.europa.eu/en/publications-data/download-todays-data-geographic-distribution-covid-19-cases-worldwide (Excel format + import script for R language).
- https://github.com/covid19-data/covid19-data provides aggregates from multiple datasets, including ECDC and WHO (CSV + JSON formats).

## Parser quick start

Read this only if you need to parse the WHO data yourself: for example, if you don't trust my data, or if I fail to provide data in the coming days :)

### Build:
```
mvn clean install
```

### Execute:

```
java -jar target/whocovid19-0.0.1-SNAPSHOT.jar <options>
```

Options can be:
* No option: parses /tmp/report.pdf and outputs to console.
* PDF report file name: parses report and outputs to console.
* sourceDirectory targetDirectory: parses all PDF files present in source dir, then outputs CSV in target dir (created if needed). File names are rpeserved (except extension, ".csv" instead of ".pdf").

### Examples:

Parse WHO march 19 PDF report:
```
java -jar target/whocovid19-0.0.1-SNAPSHOT.jar 20200319-sitrep-59-covid-19.pdf
```

Parse all reports in ~/who-reports directory, and place results in /tmp/results:
```
java -jar target/whocovid19-0.0.1-SNAPSHOT.jar ~/who-reports /tmp/results
```

### Output:

CSV data should be generated, looking like this (generated from march 4, 2020 report):

```
Date,Country,ISO-3166 code,Confirmed cases,New cases,Deaths,New deaths,Transmission type,Days since last case
2020-03-03,Republic of Korea,KOR,5328,516,32,4,Local transmission,0
2020-03-03,Japan,JPN,284,16,6,0,Local transmission,0
2020-03-03,Singapore,SGP,110,2,0,0,Local transmission,0
2020-03-03,Malaysia,MYS,50,21,0,0,Local transmission,0
2020-03-03,Australia,AUS,43,10,1,0,Local transmission,0
2020-03-03,Viet Nam,VNM,16,0,0,0,Local transmission,20
2020-03-03,Philippines,PHL,3,0,1,0,Imported cases only,28
2020-03-03,New Zealand,NZL,2,0,0,0,Imported cases only,1
2020-03-03,Cambodia,KHM,1,0,0,0,Imported cases only,36
2020-03-03,Italy,ITA,2502,466,80,28,Local transmission,0
2020-03-03,France,FRA,212,21,4,1,Local transmission,0
2020-03-03,Germany,DEU,196,39,0,0,Local transmission,0
2020-03-03,Spain,ESP,151,37,0,0,Local transmission,0
2020-03-03,The United Kingdom,GBR,51,12,0,0,Local transmission,0
2020-03-03,Switzerland,CHE,37,7,0,0,Local transmission,0
2020-03-03,Norway,NOR,33,7,0,0,Local transmission,0
2020-03-03,Netherlands,NLD,28,10,0,0,Local transmission,0
2020-03-03,Austria,AUT,24,6,0,0,Imported cases only,0
2020-03-03,Sweden,SWE,24,9,0,0,Local transmission,0
2020-03-03,Iceland,ISL,16,7,0,0,Imported cases only,0
2020-03-03,Israel,ISR,12,2,0,0,Local transmission,0
2020-03-03,Croatia,HRV,9,1,0,0,Local transmission,0
2020-03-03,Belgium,BEL,8,0,0,0,Imported cases only,1
2020-03-03,Denmark,DNK,8,3,0,0,Local transmission,0
2020-03-03,San Marino,SMR,8,0,0,0,Local transmission,1
2020-03-03,Finland,FIN,7,0,0,0,Local transmission,1
2020-03-03,Greece,GRC,7,0,0,0,Local transmission,2
2020-03-03,Czechia,CZE,5,2,0,0,Imported cases only,0
2020-03-03,Romania,ROU,4,1,0,0,Local transmission,0
2020-03-03,Azerbaijan,AZE,3,0,0,0,Imported cases only,3
2020-03-03,Georgia,GEO,3,0,0,0,Imported cases only,3
2020-03-03,Russian Federation,RUS,3,0,0,0,Imported cases only,1
2020-03-03,Estonia,EST,2,1,0,0,Imported cases only,0
2020-03-03,Ireland,IRL,2,1,0,0,Imported cases only,0
2020-03-03,Portugal,PRT,2,0,0,0,Imported cases only,1
2020-03-03,Andorra,AND,1,0,0,0,Imported cases only,1
2020-03-03,Armenia,ARM,1,0,0,0,Imported cases only,2
2020-03-03,Belarus,BLR,1,0,0,0,Imported cases only,5
2020-03-03,Latvia,LVA,1,0,0,0,Imported cases only,1
2020-03-03,Lithuania,LTU,1,0,0,0,Imported cases only,5
2020-03-03,Luxembourg,LUX,1,0,0,0,Imported cases only,2
2020-03-03,Monaco,MCO,1,0,0,0,Under investigation,3
2020-03-03,North Macedonia,MKD,1,0,0,0,Imported cases only,6
2020-03-03,Poland,POL,1,1,0,0,Imported cases only,0
2020-03-03,Ukraine,UKR,1,1,0,0,Imported cases only,0
2020-03-03,Thailand,THA,43,0,1,0,Local transmission,1
2020-03-03,India,IND,6,1,0,0,Imported cases only,0
2020-03-03,Indonesia,IDN,2,0,0,0,Local transmission,2
2020-03-03,Nepal,NPL,1,0,0,0,Imported cases only,40
2020-03-03,Sri Lanka,LKA,1,0,0,0,Imported cases only,37
2020-03-03,Iran (Islamic Republic of),IRN,2336,835,77,11,Local transmission,0
2020-03-03,Kuwait,KWT,56,0,0,0,Imported cases only,2
2020-03-03,Bahrain,BHR,49,0,0,0,Imported cases only,1
2020-03-03,Iraq,IRQ,31,5,0,0,Imported cases only,0
2020-03-03,United Arab Emirates,ARE,27,6,0,0,Local transmission,0
2020-03-03,Lebanon,LBN,13,0,0,0,Local transmission,1
2020-03-03,Oman,OMN,12,6,0,0,Imported cases only,0
2020-03-03,Qatar,QAT,8,1,0,0,Imported cases only,0
2020-03-03,Pakistan,PAK,5,0,0,0,Imported cases only,1
2020-03-03,Egypt,EGY,2,0,0,0,Imported cases only,2
2020-03-03,Afghanistan,AFG,1,0,0,0,Imported cases only,8
2020-03-03,Jordan,JOR,1,0,0,0,Imported cases only,1
2020-03-03,Morocco,MAR,1,0,0,0,Imported cases only,1
2020-03-03,Saudi Arabia,SAU,1,0,0,0,Imported cases only,1
2020-03-03,Tunisia,TUN,1,0,0,0,Imported cases only,1
2020-03-03,United States of America,USA,108,44,6,4,Local transmission,0
2020-03-03,Canada,CAN,30,3,0,0,Local transmission,0
2020-03-03,Ecuador,ECU,7,1,0,0,Local transmission,0
2020-03-03,Mexico,MEX,5,0,0,0,Imported cases only,2
2020-03-03,Brazil,BRA,2,0,0,0,Imported cases only,3
2020-03-03,Argentina,ARG,1,1,0,0,Imported cases only,0
2020-03-03,Chile,CHL,1,1,0,0,Imported cases only,0
2020-03-03,Dominican Republic,DOM,1,0,0,0,Imported cases only,2
2020-03-03,Algeria,DZA,5,0,0,0,Local transmission,1
2020-03-03,Nigeria,NGA,1,0,0,0,Imported cases only,5
2020-03-03,Senegal,SEN,1,0,0,0,Imported cases only,1
2020-03-03,International conveyance (Diamond Princess),---,706,0,6,0,Local transmission,2
2020-03-03,China,CHN,80422,120,2984,38,Under investigation,0
2020-03-03,Grand total,WLD,93091,2223,3198,86,n/a,0
```

Note: By convention, country code is set to "WLD" for world grand total, and "---" for facilities that are not countries (like Diamond Princess cruise ship).
