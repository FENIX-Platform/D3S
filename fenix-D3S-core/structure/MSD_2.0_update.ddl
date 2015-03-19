CONNECT remote:localhost/msd_2.0 admin admin;

DROP PROPERTY MeIdentification.creationDate;
CREATE PROPERTY MeIdentification.creationDate DATETIME;
DROP PROPERTY SeUpdate.updateDate;
CREATE PROPERTY SeUpdate.updateDate DATETIME;

DISCONNECT;