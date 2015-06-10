CONNECT remote:localhost:2425/msd_2.0 admin admin;

CREATE PROPERTY SeUpdate.updateDate2 DATETIME;
update MeIdentification set meMaintenance.seUpdate.updateDate2 = meMaintenance.seUpdate.updateDate where meMaintenance.seUpdate.updateDate is not null
DROP PROPERTY SeUpdate.updateDate;

CREATE PROPERTY SeUpdate.updateDate DATETIME;
update MeIdentification set meMaintenance.seUpdate.updateDate = meMaintenance.seUpdate.updateDate2 where meMaintenance.seUpdate.updateDate2 is not null
DROP PROPERTY SeUpdate.updateDate2;

CREATE PROPERTY MeIdentification.creationDate2 DATETIME;
update MeIdentification set creationDate2 = creationDate where creationDate is not null
DROP PROPERTY MeIdentification.creationDate;

CREATE PROPERTY MeIdentification.creationDate DATETIME;
update MeIdentification set creationDate = creationDate2 where creationDate2 is not null
DROP PROPERTY MeIdentification.creationDate2;


DISCONNECT;