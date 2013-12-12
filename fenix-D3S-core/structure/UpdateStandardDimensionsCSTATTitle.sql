connect remote:hqlprfenixapp1.hq.un.fao.org/msd_1.0 admin admin

update DSDDimension set title = {"EN":"Year", "FR":"Années"} where name = 'TIME'
update DSDDimension set title = {"EN":"Products", "FR":"Produits"} where name = 'ITEM'
update DSDDimension set title = {"EN":"Value", "FR":"Valeur"} where name = 'VALUE'
update DSDDimension set title = {"EN":"Flag", "FR":"Flag"} where name = 'FLAG'
update DSDDimension set title = {"EN":"Administrative level", "FR":"Niveau administratif"} where name = 'GEO'
update DSDDimension set title = {"EN":"Other", "FR":"Autre"} where name = 'OTHER'
update DSDDimension set title = {"EN":"Type of item", "FR":"Type of item"} where name = 'ITEM_TYPE'
update DSDDimension set title = {"EN":"Element", "FR":"Element"} where name = 'ELEMENT'
update DSDDimension set title = {"UM":"Unit of measure", "FR":"Unitè de mesure"} where name = 'UM'

disconnect;
