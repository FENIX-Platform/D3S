CONNECT local:database/databases/msd_1.0 admin admin;


CREATE INDEX CMContactIdentity.textKey FULLTEXT;


CREATE INDEX Index_CSVersion_key ON CSVersion (system,version) UNIQUE;

CREATE INDEX Index_CSCode_key ON CSCode (system,code) UNIQUE;

CREATE INDEX DSDContextSystem.name UNIQUE;

CREATE INDEX DSDDimension.name UNIQUE;


CREATE INDEX DMMain.uid UNIQUE;



DISCONNECT;