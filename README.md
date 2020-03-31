# WHO covid19 CSV reports data + PDF parser

Parse WHO covid19 PDF reports, and provide data by country in CSV format.
This repository provides the extracted CSV data (starting from march 1, 2020) + the parser source code.

**Latest parsed data (CSV) are here: [data/csv](data/csv)** . Note that files are named against their date of publication + report #, and the date corresponds to data collected the day before (eg. 20200322-sitrep-62-covid-19.csv contains data as of march 21, and is report #62). Same naming convention as WHO reports.

WHO reports available here:
https://www.who.int/emergencies/diseases/novel-coronavirus-2019/situation-reports

Parser should not work for reports published before March 2, 2020 (data as of march 1st).

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
Country,ISO-3166 code,Confirmed cases,New cases,Deaths,New deaths,Transmission type,Days since last case
Republic of Korea,KR,5328,516,32,4,Local transmission,0
Japan,JP,284,16,6,0,Local transmission,0
Singapore,SG,110,2,0,0,Local transmission,0
Malaysia,MY,50,21,0,0,Local transmission,0
Australia,AU,43,10,1,0,Local transmission,0
Viet Nam,VN,16,0,0,0,Local transmission,20
Philippines,PH,3,0,1,0,Imported cases only,28
New Zealand,NZ,2,0,0,0,Imported cases only,1
Cambodia,KH,1,0,0,0,Imported cases only,36
Italy,IT,2502,466,80,28,Local transmission,0
France,FR,212,21,4,1,Local transmission,0
Germany,DE,196,39,0,0,Local transmission,0
Spain,ES,151,37,0,0,Local transmission,0
The United Kingdom,GB,51,12,0,0,Local transmission,0
Switzerland,CH,37,7,0,0,Local transmission,0
Norway,NO,33,7,0,0,Local transmission,0
Netherlands,NL,28,10,0,0,Local transmission,0
Austria,AT,24,6,0,0,Imported cases only,0
Sweden,SE,24,9,0,0,Local transmission,0
Iceland,IS,16,7,0,0,Imported cases only,0
Israel,IL,12,2,0,0,Local transmission,0
Croatia,HR,9,1,0,0,Local transmission,0
Belgium,BE,8,0,0,0,Imported cases only,1
Denmark,DK,8,3,0,0,Local transmission,0
San Marino,SM,8,0,0,0,Local transmission,1
Finland,FI,7,0,0,0,Local transmission,1
Greece,GR,7,0,0,0,Local transmission,2
Czechia,CZ,5,2,0,0,Imported cases only,0
Romania,RO,4,1,0,0,Local transmission,0
Azerbaijan,AZ,3,0,0,0,Imported cases only,3
Georgia,GE,3,0,0,0,Imported cases only,3
Russian Federation,RU,3,0,0,0,Imported cases only,1
Estonia,EE,2,1,0,0,Imported cases only,0
Ireland,IE,2,1,0,0,Imported cases only,0
Portugal,PT,2,0,0,0,Imported cases only,1
Andorra,AD,1,0,0,0,Imported cases only,1
Armenia,AM,1,0,0,0,Imported cases only,2
Belarus,BY,1,0,0,0,Imported cases only,5
Latvia,LV,1,0,0,0,Imported cases only,1
Lithuania,LT,1,0,0,0,Imported cases only,5
Luxembourg,LU,1,0,0,0,Imported cases only,2
Monaco,MC,1,0,0,0,Under investigation,3
North Macedonia,MK,1,0,0,0,Imported cases only,6
Poland,PL,1,1,0,0,Imported cases only,0
Ukraine,UA,1,1,0,0,Imported cases only,0
Thailand,TH,43,0,1,0,Local transmission,1
India,IN,6,1,0,0,Imported cases only,0
Indonesia,ID,2,0,0,0,Local transmission,2
Nepal,NP,1,0,0,0,Imported cases only,40
Sri Lanka,LK,1,0,0,0,Imported cases only,37
Iran (Islamic Republic of),IR,2336,835,77,11,Local transmission,0
Kuwait,KW,56,0,0,0,Imported cases only,2
Bahrain,BH,49,0,0,0,Imported cases only,1
Iraq,IQ,31,5,0,0,Imported cases only,0
United Arab Emirates,AE,27,6,0,0,Local transmission,0
Lebanon,LB,13,0,0,0,Local transmission,1
Oman,OM,12,6,0,0,Imported cases only,0
Qatar,QA,8,1,0,0,Imported cases only,0
Pakistan,PK,5,0,0,0,Imported cases only,1
Egypt,EG,2,0,0,0,Imported cases only,2
Afghanistan,AF,1,0,0,0,Imported cases only,8
Jordan,JO,1,0,0,0,Imported cases only,1
Morocco,MA,1,0,0,0,Imported cases only,1
Saudi Arabia,SA,1,0,0,0,Imported cases only,1
Tunisia,TN,1,0,0,0,Imported cases only,1
United States of America,US,108,44,6,4,Local transmission,0
Canada,CA,30,3,0,0,Local transmission,0
Ecuador,EC,7,1,0,0,Local transmission,0
Mexico,MX,5,0,0,0,Imported cases only,2
Brazil,BR,2,0,0,0,Imported cases only,3
Argentina,AR,1,1,0,0,Imported cases only,0
Chile,CL,1,1,0,0,Imported cases only,0
Dominican Republic,DO,1,0,0,0,Imported cases only,2
Algeria,DZ,5,0,0,0,Local transmission,1
Nigeria,NG,1,0,0,0,Imported cases only,5
Senegal,SN,1,0,0,0,Imported cases only,1
International conveyance (Diamond Princess),n/a,706,0,6,0,Local transmission,2
China,CN,80422,120,2984,38,Under investigation,0
Grand total,n/a,93091,2223,3198,86,n/a,n/a
```

