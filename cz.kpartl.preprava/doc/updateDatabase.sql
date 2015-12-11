use preprava;
ALTER TABLE dbo.tb_objednavka ADD datum DATETIME NULL,
spec_zbozi VARCHAR(255) NULL,
adr VARCHAR(10) NULL,
preprav_podminky VARCHAR(255) NULL,
poznamka1 VARCHAR(255) NULL,
poznamka2 VARCHAR(255) NULL,
poznamka3 VARCHAR(255) NULL,
poznamka4 VARCHAR(255) NULL,
poznamka5 VARCHAR(255) NULL,
objednavka1 VARCHAR(255) NULL,
objednavka2 VARCHAR(255) NULL,
objednavka3 VARCHAR(255) NULL,
objednavka4 VARCHAR(255) NULL,
objednavka5 VARCHAR(255) NULL,
objednavka6 VARCHAR(255) NULL,
objednavka7 VARCHAR(255) NULL,
objednavka8 VARCHAR(255) NULL,
dod_nazev VARCHAR(255) NULL,
dod_ulice VARCHAR(255) NULL,
dod_psc VARCHAR(255) NULL,
dod_mesto VARCHAR(255) NULL,
dod_dic VARCHAR(255) NULL,
dod_ic VARCHAR(255) NULL,
dod_sap_cislo VARCHAR(255) NULL,
nakl_nazev VARCHAR(255) NULL,
nakl_ulice VARCHAR(255) NULL,
nakl_psc VARCHAR(255) NULL,
nakl_mesto VARCHAR(255) NULL,
nakl_kontakt_osoba VARCHAR(255) NULL,
nakl_kontakt VARCHAR(255) NULL,
vykl_nazev VARCHAR(255) NULL,
vykl_ulice VARCHAR(255) NULL,
vykl_psc VARCHAR(255) NULL,
vykl_mesto VARCHAR(255) NULL,
vykl_kontakt_osoba VARCHAR(255) NULL,
vykl_kontakt VARCHAR(255) NULL;

go

use preprava;
UPDATE tb_objednavka
SET datum = p.datum
FROM dbo.tb_objednavka o, dbo.tb_pozadavek p
WHERE o.pozadavek_id = p.pozadavek_id;

use preprava;
UPDATE tb_objednavka
SET dod_nazev = d.nazev,
dod_ulice = d.ulice,
dod_psc = d.psc,
dod_mesto = d.mesto,
dod_dic = d.dic,
dod_ic = d.ic,
dod_sap_cislo = d.sap_cislo
FROM dbo.tb_objednavka o, dbo.tb_dopravce d
WHERE o.dopravce_id = d.dopravce_id;

use preprava;
UPDATE dbo.tb_objednavka
SET nakl_nazev = d.nazev,
nakl_ulice = d.ulice,
nakl_psc = d.p_s_c,
nakl_mesto = d.mesto,
nakl_kontakt_osoba = d.kontaktni_osoba,
nakl_kontakt = d.kontakt
FROM dbo.tb_objednavka o, dbo.tb_destinace d, dbo.tb_pozadavek p
WHERE o.pozadavek_id = p.pozadavek_id AND d.destinace_id = destinace_z_id;

use preprava;
UPDATE dbo.tb_objednavka
SET vykl_nazev = d.nazev,
vykl_ulice = d.ulice,
vykl_psc = d.p_s_c,
vykl_mesto = d.mesto,
vykl_kontakt_osoba = d.kontaktni_osoba,
vykl_kontakt = d.kontakt
FROM dbo.tb_objednavka o, dbo.tb_destinace d, dbo.tb_pozadavek p
WHERE o.pozadavek_id = p.pozadavek_id AND d.destinace_id = destinace_do_id;

use preprava;
CREATE TABLE dbo.tb_objednatel
(objednatel_id NUMERIC(19,0) PRIMARY KEY,
o1 VARCHAR(255) NULL,
o2 VARCHAR(255) NULL,
o3 VARCHAR(255) NULL,
o4 VARCHAR(255) NULL,
o5 VARCHAR(255) NULL,
o6 VARCHAR(255) NULL,
o7 VARCHAR(255) NULL,
o8 VARCHAR(255) NULL);