

use master
go
if exists(select * from sys.databases where name='Exam_DB')
	drop database Exam_DB
create database Exam_DB
on primary
(
	name='Exam_DB',
	filename='D:\DB\Exam_DB.mdf',
	size=3MB,
	filegrowth=20%
)
log on
(
	name='Exam_DB_log',
	filename='D:\DB\Exam_DB_log.ldf',
	size=1MB,
	filegrowth=10%
)

use Exam_DB
GO
/****** Object:  Table [dbo].[tbTestPaper]    Script Date: 07/24/2013 08:40:52 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[tbTestPaper](
	[SjID]   [int] IDENTITY(1,1) NOt NuLL,
	[LsName] [varchar](20) NULL,
	[SjName] [varchar](50) NOT NULL,
	[KmID] [int] NOT NULL,
	[KsTime] [datetime] NOT NULL,
	[JsTime] [datetime] NOT NULL,
	[CjTime] [datetime] NOT NULL,
	[Zt] [int] NOT NULL,
	[Remark] [varchar](100) NULL
)

 ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'试卷编号' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbTestPaper', @level2type=N'COLUMN',@level2name=N'SjID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'创建人' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbTestPaper', @level2type=N'COLUMN',@level2name=N'LsName'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'试卷名称 当前试卷标题' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbTestPaper', @level2type=N'COLUMN',@level2name=N'SjName'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'科目编号 外联科目ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbTestPaper', @level2type=N'COLUMN',@level2name=N'KmID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'开始时间 考试开始时间' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbTestPaper', @level2type=N'COLUMN',@level2name=N'KsTime'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'结束时间 考试结束时间' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbTestPaper', @level2type=N'COLUMN',@level2name=N'JsTime'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'创建时间 考试创建时间' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbTestPaper', @level2type=N'COLUMN',@level2name=N'CjTime'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'状态（1）无答案（2）有答案' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbTestPaper', @level2type=N'COLUMN',@level2name=N'Zt'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'备注' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbTestPaper', @level2type=N'COLUMN',@level2name=N'Remark'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'试卷信息表 包含上传试卷中应该具备的信息' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbTestPaper'
GO

ALTER TABLE [dbo].[tbTestPaper] ADD  CONSTRAINT [DF_tbTestPaper_CjTime]  DEFAULT ((getdate())) FOR [CjTime]
GO
ALTER TABLE [dbo].[tbTestPaper] ADD  CONSTRAINT [DF_tbTestPaper_Zt]  DEFAULT ((1)) FOR [Zt]
/****** Object:  Table [dbo].[tbTeacher]    Script Date: 07/24/2013 08:40:52 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tbTeacher](
	[LsID] [int] IDENTITY(1,1) NOT NULL,
	[YhID] [int] NULL,
	[LsName] [varchar](20) NULL,
	[ZyID] [int] NOT NULL,
	[Remark] [varchar](100) NULL,
 CONSTRAINT [PK_tbTeacher_LsID] PRIMARY KEY CLUSTERED 
(
	[LsID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'老师编号 老师自增编号' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbTeacher', @level2type=N'COLUMN',@level2name=N'LsID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'用户ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbTeacher', @level2type=N'COLUMN',@level2name=N'YhID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'老师姓名 姓名' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbTeacher', @level2type=N'COLUMN',@level2name=N'LsName'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'专业ID 外键专业ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbTeacher', @level2type=N'COLUMN',@level2name=N'ZyID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'备注' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbTeacher', @level2type=N'COLUMN',@level2name=N'Remark'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'老师信息表' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbTeacher'
GO
/****** Object:  Table [dbo].[tbSubject]    Script Date: 07/24/2013 08:40:52 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tbSubject](
	[KmID] [int]IDENTITY(1,1) NOT NULL,
	[KmName] [varchar](30) NULL,
	[Remark] [varchar](100) NULL,
	[ZyID] [int] NOT NULL,
 CONSTRAINT [PK_tbSubject_KmID] PRIMARY KEY CLUSTERED 
(
	[KmID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'编号 自增ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbSubject', @level2type=N'COLUMN',@level2name=N'KmID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'科目名称 科目名称' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbSubject', @level2type=N'COLUMN',@level2name=N'KmName'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'专业ID 外键专业ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbSubject', @level2type=N'COLUMN',@level2name=N'ZyID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'备注' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbSubject', @level2type=N'COLUMN',@level2name=N'Remark'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'科目信息表' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbSubject'
GO
/****** Object:  Table [dbo].[tbStudent]    Script Date: 07/24/2013 08:40:52 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tbStudent](
	[XsID] [int]IDENTITY(1,1) NOT NULL,
	[XsName] [varchar](10) NULL,
	[XsSex] [varchar](3) NULL,
	[YhID] [varchar](30) NOT NULL,
	[BjName] [varchar](30) NULL,
	[Remark] [varchar](100) NULL,
 CONSTRAINT [PK_tbStudent_XsID] PRIMARY KEY CLUSTERED 
(
	[XsID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'学生编号 唯一标号' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbStudent', @level2type=N'COLUMN',@level2name=N'XsID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'学生姓名 ' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbStudent', @level2type=N'COLUMN',@level2name=N'XsName'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'学生性别 ' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbStudent', @level2type=N'COLUMN',@level2name=N'XsSex'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'学生用户ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbStudent', @level2type=N'COLUMN',@level2name=N'YhID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'班级名称' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbStudent', @level2type=N'COLUMN',@level2name=N'BjName'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'备注' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbStudent', @level2type=N'COLUMN',@level2name=N'Remark'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'学生表' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbStudent'
GO
/****** Object:  Table [dbo].[tbSpeciality]    Script Date: 07/24/2013 08:40:52 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tbSpeciality](
	[ZyID] [int]IDENTITY(1,1) NOT NULL,
	[ZyName] [varchar](100) NOT NULL,
	[Remark] [varchar](100) NULL,
 CONSTRAINT [PK_TaSpeciality_ZyID] PRIMARY KEY CLUSTERED 
(
	[ZyID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'专业ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbSpeciality', @level2type=N'COLUMN',@level2name=N'ZyID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'专业名称' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbSpeciality', @level2type=N'COLUMN',@level2name=N'ZyName'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'备注' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbSpeciality', @level2type=N'COLUMN',@level2name=N'Remark'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'专业信息表' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbSpeciality'
GO
/****** Object:  Table [dbo].[tbScore]    Script Date: 07/24/2013 08:40:52 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[tbScore](
	[CjID] [int]IDENTITY(1,1) NOT NULL,
	[KgtID] [int] NOT NULL,
	[DxtScore] [float] NULL,
	[DuoxtScore] [float] NULL,
	[PdtScore] [float] NULL,
	[ZgtScore] [float] NULL,
	[Zt] [int] NULL,
	CONSTRAINT [PK_TbScore_CjID] PRIMARY KEY CLUSTERED 
(
	[CjID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'成绩编号' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbScore', @level2type=N'COLUMN',@level2name=N'CjID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'答题卡编号' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbScore', @level2type=N'COLUMN',@level2name=N'KgtID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'单选题得分' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbScore', @level2type=N'COLUMN',@level2name=N'DxtScore'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'多选题得分' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbScore', @level2type=N'COLUMN',@level2name=N'DuoxtScore'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'判断题得分' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbScore', @level2type=N'COLUMN',@level2name=N'PdtScore'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'主观题得分' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbScore', @level2type=N'COLUMN',@level2name=N'ZgtScore'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'状态1:未交卷，2已交卷，默认1' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbScore', @level2type=N'COLUMN',@level2name=N'Zt'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'成绩信息表' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbScore'
GO

ALTER TABLE [dbo].[tbScore] ADD  CONSTRAINT [DF_tbScore_Zt]  DEFAULT ((1)) FOR [Zt]
Go
/****** Object:  Table [dbo].[tbResult]    Script Date: 07/24/2013 08:40:52 ******/
----主观题表部分已修改07 26 2013  2:37PM

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[tbResult](
	[ZgtID] [int]IDENTITY(1,1) NOT NULL,
	[KgtID] [int] NOT NULL,
	[ZgtNo] [varchar](200) NOT NULL,
	[ZgtAnswer] [text] NOT NULL,
	[Zt] [int] NOT NULL,
	[Remark] [varchar](100) NULL,
	CONSTRAINT [PK_tbResult_ZgtID] PRIMARY KEY CLUSTERED 
(
	[ZgtID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'主观题编号' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbResult', @level2type=N'COLUMN',@level2name=N'ZgtID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'客观题编号(外键)' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbResult', @level2type=N'COLUMN',@level2name=N'KgtID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'主观题题号' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbResult', @level2type=N'COLUMN',@level2name=N'ZgtNo'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'主观题答案' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbResult', @level2type=N'COLUMN',@level2name=N'ZgtAnswer'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'答题卡状态（老师，1,；学生（2没改，3，已改））' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbResult', @level2type=N'COLUMN',@level2name=N'Zt'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'备注' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbResult', @level2type=N'COLUMN',@level2name=N'Remark'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'主观题表' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbResult'
GO

/****** Object:  Table [dbo].[tbQuestionTypes]    Script Date: 07/24/2013 08:40:52 ******/
--题型部分确认正确07 26 2013  2:20PM
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[tbQuestionTypes](
	[TxID] [int]IDENTITY(1,1) NOT NULL,
	[TxName] [varchar](100) NULL,
	[TxCount] [int] NULL,
	[Txzf] [int] NOT NULL,
	[SjID] [int] NOT NULL,
 CONSTRAINT [PK_tbQustionTypes_TxID] PRIMARY KEY CLUSTERED 
(
	[TxID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'题型编号' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbQuestionTypes', @level2type=N'COLUMN',@level2name=N'TxID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'题型名称' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbQuestionTypes', @level2type=N'COLUMN',@level2name=N'TxName'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'题型个数 当前题型在试卷中有多少个' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbQuestionTypes', @level2type=N'COLUMN',@level2name=N'TxCount'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'题型总分 本题型总分' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbQuestionTypes', @level2type=N'COLUMN',@level2name=N'Txzf'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'试卷编号 所外联的试卷编号' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbQuestionTypes', @level2type=N'COLUMN',@level2name=N'SjID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'题型表 存储上传试卷题型，个数，及分值等信息' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbQuestionTypes'
GO
/****** Object:  Table [dbo].[tbObjTopic]    Script Date: 07/24/2013 08:40:52 ******/
----客观题部分已修改07 26 2013  2:38PM
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[tbObjTopic](
	[KgtID] [int]IDENTITY(1,1) NOT NULL,
	[SjID] [int] NOT NULL,
	[YhID] [int] NOT NULL,
	[DxtNo] [varchar](400) NOT NULL,
	[DxtAnswer] [varchar](400) NULL,
	[DuoxtNo] [varchar](300) NOT NULL,
	[DuoxtAnswer] [varchar](300) NOT NULL,
	[PdtNo] [varchar](300) NOT NULL,
	[PdtAnswer] [varchar](300) NOT NULL,
	[Zt] [int] NOT NULL,
	[Remark] [varchar](100) NULL,
 CONSTRAINT [PK_tbObjTopic] PRIMARY KEY CLUSTERED 
(
	[YhID] ASC,
	[SjID] ASC
)
WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'答题卡ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbObjTopic', @level2type=N'COLUMN',@level2name=N'KgtID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'试卷ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbObjTopic', @level2type=N'COLUMN',@level2name=N'SjID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'创建当前答案用户名' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbObjTopic', @level2type=N'COLUMN',@level2name=N'YhID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'单选题题号' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbObjTopic', @level2type=N'COLUMN',@level2name=N'DxtNo'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'单选题答案' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbObjTopic', @level2type=N'COLUMN',@level2name=N'DxtAnswer'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'多选题题号' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbObjTopic', @level2type=N'COLUMN',@level2name=N'DuoxtNo'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'多选题答案' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbObjTopic', @level2type=N'COLUMN',@level2name=N'DuoxtAnswer'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'判断题题号' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbObjTopic', @level2type=N'COLUMN',@level2name=N'PdtNo'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'判断题答案' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbObjTopic', @level2type=N'COLUMN',@level2name=N'PdtAnswer'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'答题卡状态（老师，1,；学生（2没改，3，已改））' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbObjTopic', @level2type=N'COLUMN',@level2name=N'Zt'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'备注' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbObjTopic', @level2type=N'COLUMN',@level2name=N'Remark'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'答题卡表' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbObjTopic'
GO
/****** Object:  Table [dbo].[tbImages]    Script Date: 07/24/2013 08:40:52 ******/
---图片表确认正确
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tbImages](
	[TpID] [int]IDENTITY(1,1) NOT NULL,
	[SjID] [int] NOT NULL,
	[TpYm] [int] NOT NULL,
	[Tpian] [varchar](200) NOT NULL,
	[Remark] [varchar](100) NULL,
 CONSTRAINT [PK_tbImages_TpID] PRIMARY KEY CLUSTERED 
(
	[TpID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'图片ID 图片存入数据库ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbImages', @level2type=N'COLUMN',@level2name=N'TpID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'试卷编号 图片所在试卷编号' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbImages', @level2type=N'COLUMN',@level2name=N'SjID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'图片页面 图片页面顺序' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbImages', @level2type=N'COLUMN',@level2name=N'TpYm'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'图片 存储路径或者二进制编码' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbImages', @level2type=N'COLUMN',@level2name=N'Tpian'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'备注' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbImages', @level2type=N'COLUMN',@level2name=N'Remark'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'试卷图片表 包含所有试卷以图片的形式储存' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbImages'
GO
/****** Object:  Table [dbo].[tbCourse]    Script Date: 07/24/2013 08:40:52 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO


CREATE TABLE [dbo].[tbClass](
	[BjID] [int]IDENTITY(1,1) NOT NULL,
	[BjName] [varchar](30) NOT NULL,
	[Nj] [varchar](20) NOT NULL,
	[ZyID] [int] NOT NULL,
 CONSTRAINT [PK_tbClass_BjID] PRIMARY KEY CLUSTERED 
(
	[BjID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'班级编号' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbClass', @level2type=N'COLUMN',@level2name=N'BjID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'班级名称' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbClass', @level2type=N'COLUMN',@level2name=N'BjName'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'年级' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbClass', @level2type=N'COLUMN',@level2name=N'Nj'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'专业ID 外键专业ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbClass', @level2type=N'COLUMN',@level2name=N'ZyID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'班级信息表' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbClass'
GO
/****** Object:  Table [dbo].[tbUser]    Script Date: 07/24/2013 08:40:52 ******/
--用户表 已修改07 26 2013  2:49PM
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tbUser](
	[YhID] [int]IDENTITY(1,1) NOT NULL,
	[YhName] [varchar](30) NOT NULL,
	[Xh]	[varchar](20),
	[YhPwd] [varchar](50) NOT NULL,
	[Zt] [int] NOT NULL,
 CONSTRAINT [PK_tbUser_YhID] PRIMARY KEY CLUSTERED 
(
	[YhID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Default [DF_tbUser_YhID]    Script Date: 07/24/2013 08:40:52 ******/
GO
/****** Object:  Default [DF__tbStudent__XsID__0DAF0CB0]    Script Date: 07/24/2013 08:40:52 ******/
GO
/****** Object:  Default [DF_tbTestPaper_CjTime]    Script Date: 07/24/2013 08:40:52 ******/
GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'管理员ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbUser', @level2type=N'COLUMN',@level2name=N'YhID'
Go
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'用户名' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbUser', @level2type=N'COLUMN',@level2name=N'YhName'
Go
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'学号' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbUser', @level2type=N'COLUMN',@level2name=N'Xh'
Go
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'用户密码' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbUser', @level2type=N'COLUMN',@level2name=N'YhPwd'
Go
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'用户名状态（1管理员，2老师，3学生）' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbUser', @level2type=N'COLUMN',@level2name=N'Zt'
Go
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'用户表' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbUser'
Go


-- tbTestMark
create table  tbTestMark
(
       Id      INT IDENTITY(1,1) not null,
       SjID    NUMERIC(38),
       BjID    NUMERIC(38) not null
);
alter  table tbTestMark
       add constraint PK_tbTestMark_Id primary key (Id);

Go
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'编号' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbTestMark', @level2type=N'COLUMN',@level2name=N'ID'
Go
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'试卷编号 外联试卷ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbTestMark', @level2type=N'COLUMN',@level2name=N'SjID'
Go
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'班级ID 外联班级ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbTestMark', @level2type=N'COLUMN',@level2name=N'BjID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'考试记录表' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'tbTestMark'
Go

--创建成绩表试图
IF EXISTS (SELECT * FROM sysobjects WHERE name = 'view_score')
     DROP VIEW view_score
GO
CREATE VIEW view_score
  AS
select s.KgtID,stu.XsName,stu.BjName,tp.SjID,tp.SjName,sp.ZyName,sb.KmName,s.DxtScore,s.DuoxtScore,s.PdtScore,s.ZgtScore 
from tbScore as s,tbObjTopic as ot,tbTestPaper as tp,tbUser as u,tbStudent as stu,tbSubject as sb,
tbSpeciality as sp where s.KgtID=ot.KgtID and tp.SjID=ot.SjID and u.YhID=ot.YhID and stu.YhID=ot.YhID 
and sb.KmID=tp.KmID and sp.ZyID=sb.ZyID
GO

---确认修改正确，运行无错误07 26 2013  3:10PM
insert into tbUser(YhName,YhPwd,Zt)values('admin','21232F297A57A5A743894A0E4A801FC3',1)