CONNECT remote:localhost/msd_2.0 admin admin;

CREATE PROPERTY DSD.datasources EMBEDDEDLIST STRING;
UPDATE DSD add datasources = datasource WHERE datasource IS NOT NULL;
DROP PROPERTY DSD.datasource;

DISCONNECT;