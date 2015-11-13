CREATE DATABASE preprava2
GO
USE preprava2
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[tb_destinace]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[tb_destinace](
	[destinace_id] [numeric](19, 0) IDENTITY(1,1) NOT NULL,
	[nazev] [varchar](255) NULL,
	[cislo] [int] NULL,
	[kontaktni_osoba] [varchar](255) NULL,
	[kontakt] [varchar](255) NULL,
	[ulice] [varchar](255) NULL,
	[mesto] [varchar](255) NULL,
	[p_s_c] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[destinace_id] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[cislo] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[tb_dopravce]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[tb_dopravce](
	[dopravce_id] [numeric](19, 0) IDENTITY(1,1) NOT NULL,
	[nazev] [varchar](255) NULL,
	[ulice] [varchar](255) NULL,
	[mesto] [varchar](255) NULL,
	[dic] [varchar](255) NULL,
	[sap_cislo] [varchar](255) NULL,
	[kontaktni_osoba] [varchar](255) NULL,
	[kontaktni_telefon] [varchar](255) NULL,
	[kontakt_ostatni] [varchar](255) NULL,
	[psc] [varchar](255) NULL,
	[ic] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[dopravce_id] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[sap_cislo] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[tb_user]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[tb_user](
	[user_id] [numeric](19, 0) IDENTITY(1,1) NOT NULL,
	[username] [varchar](255) NULL,
	[password] [varchar](255) NULL,
	[administrator] [tinyint] NULL,
PRIMARY KEY CLUSTERED 
(
	[user_id] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[username] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[tb_objednatel]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[tb_objednatel](
	[objednatel_id] [numeric](19, 0) NOT NULL,
	[o1] [varchar](255) NULL,
	[o2] [varchar](255) NULL,
	[o3] [varchar](255) NULL,
	[o4] [varchar](255) NULL,
	[o5] [varchar](255) NULL,
	[o6] [varchar](255) NULL,
	[o7] [varchar](255) NULL,
	[o8] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[objednatel_id] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[tb_pozadavek]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[tb_pozadavek](
	[pozadavek_id] [numeric](19, 0) IDENTITY(1,1) NOT NULL,
	[datum] [datetime] NULL,
	[datum_nakladky] [varchar](255) NULL,
	[datum_vykladky] [varchar](255) NULL,
	[pocet_palet] [varchar](255) NULL,
	[je_termin_konecny] [tinyint] NULL,
	[taxi] [tinyint] NULL,
	[poznamka] [varchar](255) NULL,
	[destinace_z_id] [numeric](19, 0) NULL,
	[celkova_hmotnost] [varchar](255) NULL,
	[zadavatel_id] [numeric](19, 0) NULL,
	[attr] [int] NULL,
	[hodina_nakladky] [varchar](255) NULL,
	[destinace_do_id] [numeric](19, 0) NULL,
	[je_stohovatelne] [tinyint] NULL,
	[hodina_vykladky] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[pozadavek_id] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[tb_objednavka]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[tb_objednavka](
	[objednavka_id] [numeric](19, 0) IDENTITY(1,1) NOT NULL,
	[faze] [int] NULL,
	[cena] [numeric](19, 2) NULL,
	[mena] [varchar](255) NULL,
	[zmena_nakladky] [varchar](255) NULL,
	[puvodni_termin_nakladky] [varchar](255) NULL,
	[cislo_faktury_dopravce] [int] NULL,
	[pozadavek_id] [numeric](19, 0) NULL,
	[dopravce_id] [numeric](19, 0) NULL,
	[pridruzena_objednavka_id] [numeric](19, 0) NULL,
	[cislo_objednavky] [numeric](19, 0) NULL,
	[datum] [datetime] NULL,
	[spec_zbozi] [varchar](255) NULL,
	[adr] [varchar](10) NULL,
	[preprav_podminky] [varchar](255) NULL,
	[poznamka1] [varchar](255) NULL,
	[poznamka2] [varchar](255) NULL,
	[poznamka3] [varchar](255) NULL,
	[poznamka4] [varchar](255) NULL,
	[poznamka5] [varchar](255) NULL,
	[objednavka1] [varchar](255) NULL,
	[objednavka2] [varchar](255) NULL,
	[objednavka3] [varchar](255) NULL,
	[objednavka4] [varchar](255) NULL,
	[objednavka5] [varchar](255) NULL,
	[objednavka6] [varchar](255) NULL,
	[objednavka7] [varchar](255) NULL,
	[objednavka8] [varchar](255) NULL,
	[dod_nazev] [varchar](255) NULL,
	[dod_ulice] [varchar](255) NULL,
	[dod_psc] [varchar](255) NULL,
	[dod_mesto] [varchar](255) NULL,
	[dod_dic] [varchar](255) NULL,
	[dod_ic] [varchar](255) NULL,
	[nakl_nazev] [varchar](255) NULL,
	[nakl_ulice] [varchar](255) NULL,
	[nakl_psc] [varchar](255) NULL,
	[nakl_mesto] [varchar](255) NULL,
	[nakl_kontakt_osoba] [varchar](255) NULL,
	[nakl_kontakt] [varchar](255) NULL,
	[vykl_nazev] [varchar](255) NULL,
	[vykl_ulice] [varchar](255) NULL,
	[vykl_psc] [varchar](255) NULL,
	[vykl_mesto] [varchar](255) NULL,
	[vykl_kontakt_osoba] [varchar](255) NULL,
	[vykl_kontakt] [varchar](255) NULL,
	[dod_sap_cislo] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[objednavka_id] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[cislo_objednavky] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
END
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[fk_pozadavek_destinace_do_id]') AND parent_object_id = OBJECT_ID(N'[dbo].[tb_pozadavek]'))
ALTER TABLE [dbo].[tb_pozadavek]  WITH CHECK ADD  CONSTRAINT [fk_pozadavek_destinace_do_id] FOREIGN KEY([destinace_do_id])
REFERENCES [dbo].[tb_destinace] ([destinace_id])
GO
ALTER TABLE [dbo].[tb_pozadavek] CHECK CONSTRAINT [fk_pozadavek_destinace_do_id]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[fk_pozadavek_destinace_z_id]') AND parent_object_id = OBJECT_ID(N'[dbo].[tb_pozadavek]'))
ALTER TABLE [dbo].[tb_pozadavek]  WITH CHECK ADD  CONSTRAINT [fk_pozadavek_destinace_z_id] FOREIGN KEY([destinace_z_id])
REFERENCES [dbo].[tb_destinace] ([destinace_id])
GO
ALTER TABLE [dbo].[tb_pozadavek] CHECK CONSTRAINT [fk_pozadavek_destinace_z_id]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[fk_pozadavek_zadavatel_id]') AND parent_object_id = OBJECT_ID(N'[dbo].[tb_pozadavek]'))
ALTER TABLE [dbo].[tb_pozadavek]  WITH CHECK ADD  CONSTRAINT [fk_pozadavek_zadavatel_id] FOREIGN KEY([zadavatel_id])
REFERENCES [dbo].[tb_user] ([user_id])
GO
ALTER TABLE [dbo].[tb_pozadavek] CHECK CONSTRAINT [fk_pozadavek_zadavatel_id]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[fk_objednavka_dopravce_id]') AND parent_object_id = OBJECT_ID(N'[dbo].[tb_objednavka]'))
ALTER TABLE [dbo].[tb_objednavka]  WITH CHECK ADD  CONSTRAINT [fk_objednavka_dopravce_id] FOREIGN KEY([dopravce_id])
REFERENCES [dbo].[tb_dopravce] ([dopravce_id])
GO
ALTER TABLE [dbo].[tb_objednavka] CHECK CONSTRAINT [fk_objednavka_dopravce_id]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[fk_objednavka_pozadavek_id]') AND parent_object_id = OBJECT_ID(N'[dbo].[tb_objednavka]'))
ALTER TABLE [dbo].[tb_objednavka]  WITH CHECK ADD  CONSTRAINT [fk_objednavka_pozadavek_id] FOREIGN KEY([pozadavek_id])
REFERENCES [dbo].[tb_pozadavek] ([pozadavek_id])
GO
ALTER TABLE [dbo].[tb_objednavka] CHECK CONSTRAINT [fk_objednavka_pozadavek_id]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[fk_objednavka_pridruzena_objednavka_id]') AND parent_object_id = OBJECT_ID(N'[dbo].[tb_objednavka]'))
ALTER TABLE [dbo].[tb_objednavka]  WITH CHECK ADD  CONSTRAINT [fk_objednavka_pridruzena_objednavka_id] FOREIGN KEY([pridruzena_objednavka_id])
REFERENCES [dbo].[tb_objednavka] ([objednavka_id])
GO
ALTER TABLE [dbo].[tb_objednavka] CHECK CONSTRAINT [fk_objednavka_pridruzena_objednavka_id]

INSERT INTO [dbo].[tb_objednatel]
           ([objednatel_id]
           ,[o1]
           ,[o2]
           ,[o3]
           ,[o4]
           ,[o5]
           ,[o6]
           ,[o7]
           ,[o8])
     VALUES (1, 'KERN-LIEBERS CR s.r.o.', 'Okružní 607,', '370 01 Èeské Budìjovice', 'DIÈ: CZ60849827', 'Kontakt: Alena Fuèíková', 'tel. 389 608 124', 'mobil: 734 310 217', '')
