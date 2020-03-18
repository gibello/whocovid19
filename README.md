# WHO covid19 PDF reports parser

Parse WHO covid19 PDF reports, and retrieve data by country in CSV format.

WHO reports available here:
https://www.who.int/emergencies/diseases/novel-coronavirus-2019/situation-reports

Should not work for reports published before March 2, 2020.

## Quick start

Build:
```
mvn clean install
```

Execute:

```
java -jar target/whocovid19-0.0.1-SNAPSHOT.jar <who-covid-report-file.pdf>
```

Output:

CSV data should be generated, looking like this (generated from march 17, 2020 report):

```
Country,Confirmed cases,Deaths,New deaths,Transmission type,Days since last case
China,81116,39,3231,13,Local transmission,0
Republic of Korea,8320,84,81,6,Local transmission,0
Japan,829,15,28,4,Local transmission,0
Malaysia,553,0,0,0,Local transmission,1
Australia,375,77,5,0,Local transmission,0
Singapore,243,0,0,0,Local transmission,1
Philippines,187,47,12,0,Local transmission,0
Viet Nam,61,4,0,0,Local transmission,0
Brunei Darussalam,50,0,0,0,Local transmission,1
Cambodia,24,12,0,0,Local transmission,0
New Zealand,11,5,0,0,Local transmission,0
Mongolia,4,3,0,0,Imported cases only,0
French Polynesia,3,0,0,0,Imported cases only,2
Guam,3,3,0,0,Local transmission,0
Italy,27980,3233,2503,349,Local transmission,0
Spain,9191,1438,309,21,Local transmission,0
France,6573,1193,148,21,Local transmission,0
Germany,6012,1174,13,1,Local transmission,0
Switzerland,2200,0,14,1,Local transmission,1
The United Kingdom,1547,152,55,20,Local transmission,0
Netherlands,1413,278,24,4,Local transmission,0
Norway,1169,92,3,2,Local transmission,0
Austria,1132,173,3,2,Local transmission,0
Belgium,1085,0,5,0,Local transmission,1
Sweden,1059,67,3,0,Local transmission,0
Denmark,960,62,4,3,Local transmission,0
Czechia,383,85,0,0,Local transmission,0
Greece,331,0,4,0,Local transmission,1
Portugal,331,86,0,0,Local transmission,0
Israel,250,50,0,0,Local transmission,0
Finland,272,5,0,0,Local transmission,0
Slovenia,253,34,0,0,Local transmission,0
Ireland,223,54,2,0,Local transmission,0
Estonia,205,0,0,0,Local transmission,1
Iceland,199,19,0,0,Local transmission,0
Romania,158,0,0,0,Local transmission,1
Poland,150,0,3,0,Local transmission,1
San Marino,102,10,9,4,Local transmission,0
Russian Federation,93,30,0,0,Imported cases only,0
Luxembourg,81,43,1,0,Local transmission,0
Slovakia,72,11,0,0,Local transmission,0
Bulgaria,67,16,2,0,Local transmission,0
Serbia††,70,24,0,0,Local transmission,0
Croatia,56,7,0,0,Local transmission,0
Armenia,52,26,0,0,Local transmission,0
Albania,51,9,1,0,Local transmission,0
Hungary,50,11,1,0,Local transmission,0
Turkey,47,42,0,0,Imported cases only,0
Belarus,36,0,0,0,Local transmission,1
Latvia,36,5,0,0,Imported cases only,0
Cyprus,33,0,0,0,Imported cases only,1
Georgia,33,0,0,0,Imported cases only,1
Malta,30,9,0,0,Imported cases only,0
Republic of Moldova,29,6,0,0,Local transmission,0
Azerbaijan,19,0,0,0,Imported cases only,2
Bosnia and Herzegovina,19,0,0,0,Local transmission,1
North Macedonia,19,6,0,0,Local transmission,0
Lithuania,17,3,0,0,Imported cases only,0
Andorra,14,12,0,0,Imported cases only,0
Monaco,9,0,0,0,Under investigation,1
Liechtenstein,7,0,0,0,Imported cases only,1
Ukraine,7,4,1,0,Local transmission,0
Kazakhstan,6,0,0,0,Imported cases only,2
Uzbekistan,4,0,0,0,Under investigation,1
Holy See,1,0,0,0,Under investigation,11
Faroe Islands,47,36,0,0,Imported cases only,0
Gibraltar,3,2,0,0,Under investigation,0
Jersey,2,0,0,0,Imported cases only,4
Guernsey,1,0,0,0,Imported cases only,7
Indonesia,172,55,5,1,Local transmission,0
Thailand,147,33,1,0,Local transmission,0
India,137,23,3,1,Local transmission,0
Sri Lanka,29,10,0,0,Local transmission,0
Maldives,13,0,0,0,Local transmission,1
Bangladesh,8,3,0,0,Local transmission,0
Bhutan,1,0,0,0,Imported cases only,11
Nepal,1,0,0,0,Imported cases only,53
Iran (Islamic Republic of),14991,0,853,0,Local transmission,1
Qatar,439,38,0,0,Local transmission,0
Bahrain,229,8,1,0,Local transmission,0
Pakistan,187,135,0,0,Imported cases only,0
Egypt,166,40,4,2,Local transmission,0
Saudi Arabia,133,30,0,0,Local transmission,0
Kuwait,130,18,0,0,Local transmission,0
Iraq,124,0,9,0,Local transmission,1
Lebanon,109,10,3,0,Local transmission,0
United Arab Emirates,98,0,0,0,Local transmission,1
Morocco,38,10,2,1,Local transmission,0
Jordan,35,29,0,0,Imported cases only,0
Oman,24,2,0,0,Imported cases only,0
Afghanistan,21,5,0,0,Imported cases only,0
Tunisia,20,2,0,0,Local transmission,0
Sudan,1,0,1,0,Imported cases only,2
Somalia,1,1,0,0,Imported cases only,0
occupied Palestinian Territory,39,1,0,0,Local transmission,0
United States of America,3503,1825,58,17,Local transmission,0
Canada,424,120,1,0,Local transmission,0
Brazil,234,34,0,0,Local transmission,0
Chile,156,81,0,0,Local transmission,0
Peru,86,15,0,0,Local transmission,0
Panama,69,26,1,0,Local transmission,0
Argentina,65,9,2,0,Local transmission,0
Ecuador,58,21,2,0,Local transmission,0
Mexico,53,0,0,0,Imported cases only,1
Colombia,45,21,0,0,Local transmission,0
Costa Rica,41,18,0,0,Local transmission,0
Venezuela (Bolivarian Republic of),33,16,0,0,Imported cases only,0
Dominican Republic,21,16,1,1,Local transmission,0
Bolivia (Plurinational State of),11,0,0,0,Imported cases only,1
Jamaica,10,0,0,0,Local transmission,1
Paraguay,9,1,0,0,Local transmission,0
Honduras,8,6,0,0,Imported cases only,0
Uruguay,6,2,0,0,Imported cases only,0
Cuba,5,1,0,0,Imported cases only,0
Trinidad and Tobago,5,3,0,0,Imported cases only,0
Guyana,4,0,1,0,Local transmission,1
Saint Lucia,2,0,0,0,Imported cases only,1
Antigua and Barbuda,1,0,0,0,Imported cases only,3
Bahamas,1,1,0,0,Local transmission,0
Guatemala,1,0,1,0,Imported cases only,2
Saint Vincent and the Grenadines,1,0,0,0,Imported cases only,4
Suriname,1,0,0,0,Imported cases only,1
Guadeloupe,18,12,0,0,Imported cases only,0
Martinique,16,1,0,0,Imported cases only,0
French Guiana,7,0,0,0,Imported cases only,3
Curaçao,3,1,0,0,Imported cases only,0
Puerto Rico,3,0,0,0,Imported cases only,3
Saint Barthelemy,3,0,0,0,Under investigation,1
Aruba,2,2,0,0,Imported cases only,0
Saint Martin,2,0,0,0,Under investigation,14
United States Virgin Islands,2,2,0,0,Imported cases only,0
Cayman Islands,1,0,1,0,Imported cases only,3
South Africa,62,11,0,0,Local transmission,0
Algeria,60,11,4,1,Local transmission,0
Senegal,27,1,0,0,Local transmission,0
Burkina Faso,15,0,0,0,Imported cases only,1
Rwanda,7,2,0,0,Local transmission,0
Cote d’Ivoire,6,3,0,0,Imported cases only,0
Ghana,6,0,0,0,Imported cases only,1
Cameroon,5,2,0,0,Local transmission,0
Ethiopia,5,4,0,0,Imported cases only,0
Seychelles,4,2,0,0,Imported cases only,0
Democratic Republic of the Congo,3,1,0,0,Imported cases only,0
Kenya,3,2,0,0,Local transmission,0
Namibia,2,0,0,0,Imported cases only,2
Nigeria,2,0,0,0,Imported cases only,8
Benin,1,1,0,0,Imported cases only,0
Central African Republic,1,0,0,0,Imported cases only,2
Congo,1,0,0,0,Imported cases only,2
Equatorial Guinea,1,0,0,0,Imported cases only,2
Eswatini,1,0,0,0,Imported cases only,2
Gabon,1,0,0,0,Imported cases only,3
Guinea,1,0,0,0,Imported cases only,3
Liberia,1,1,0,0,Imported cases only,0
Mauritania,1,0,0,0,Imported cases only,2
Togo,1,0,0,0,Imported cases only,10
United Republic of Tanzania,1,1,0,0,Imported cases only,0
Réunion,9,0,0,0,Imported cases only,1
Mayotte,1,0,0,0,Imported cases only,2
Grand total,179111,11525,7426,475,n/a,n/a
```

