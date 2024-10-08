USE [master]
GO
/****** Object:  Database [ClubManagement]    Script Date: 2021/1/13 22:40:23 ******/
CREATE DATABASE [ClubManagement]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'ClubManagement', FILENAME = N'd:\db\ClubManagement.mdf' , SIZE = 8192KB , MAXSIZE = 20480KB , FILEGROWTH = 10%)
 LOG ON 
( NAME = N'ClubManagement_log', FILENAME = N'd:\db\ClubManagement_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 WITH CATALOG_COLLATION = DATABASE_DEFAULT
GO
ALTER DATABASE [ClubManagement] SET COMPATIBILITY_LEVEL = 150
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [ClubManagement].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [ClubManagement] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [ClubManagement] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [ClubManagement] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [ClubManagement] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [ClubManagement] SET ARITHABORT OFF 
GO
ALTER DATABASE [ClubManagement] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [ClubManagement] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [ClubManagement] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [ClubManagement] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [ClubManagement] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [ClubManagement] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [ClubManagement] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [ClubManagement] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [ClubManagement] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [ClubManagement] SET  DISABLE_BROKER 
GO
ALTER DATABASE [ClubManagement] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [ClubManagement] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [ClubManagement] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [ClubManagement] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [ClubManagement] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [ClubManagement] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [ClubManagement] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [ClubManagement] SET RECOVERY FULL 
GO
ALTER DATABASE [ClubManagement] SET  MULTI_USER 
GO
ALTER DATABASE [ClubManagement] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [ClubManagement] SET DB_CHAINING OFF 
GO
ALTER DATABASE [ClubManagement] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [ClubManagement] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [ClubManagement] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [ClubManagement] SET ACCELERATED_DATABASE_RECOVERY = OFF  
GO
EXEC sys.sp_db_vardecimal_storage_format N'ClubManagement', N'ON'
GO
ALTER DATABASE [ClubManagement] SET QUERY_STORE = OFF
GO
USE [ClubManagement]
GO
/****** Object:  Table [dbo].[Account]    Script Date: 2021/1/13 22:40:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Account](
	[accountID] [int] IDENTITY(1,1) NOT NULL,
	[accountName] [nvarchar](20) NOT NULL,
	[accountPwd] [nvarchar](20) NOT NULL,
	[typeID] [int] NULL,
	[studentID] [nvarchar](20) NULL,
	[studentName] [nvarchar](20) NULL,
	[studentSex] [nvarchar](4) NULL,
	[studentCollege] [nvarchar](20) NULL,
	[studentDiscipline] [nvarchar](30) NULL,
	[studentClass] [nvarchar](20) NULL,
	[studentPhone] [nvarchar](20) NULL,
PRIMARY KEY CLUSTERED 
(
	[accountID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[AccountType]    Script Date: 2021/1/13 22:40:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[AccountType](
	[typeID] [int] IDENTITY(1,1) NOT NULL,
	[typeName] [nvarchar](20) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[typeID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Club]    Script Date: 2021/1/13 22:40:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Club](
	[clubID] [int] IDENTITY(1,1) NOT NULL,
	[clubName] [nvarchar](20) NOT NULL,
	[clubDesc] [text] NULL,
	[clubCreateDate] [date] NULL,
	[clubProperty] [money] NULL,
	[statusID] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[clubID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ClubAccount]    Script Date: 2021/1/13 22:40:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ClubAccount](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[clubID] [int] NULL,
	[accountID] [int] NULL,
	[positionID] [int] NULL,
	[positionStart] [date] NULL,
	[positionStop] [date] NULL,
	[positionStatus] [nvarchar](10) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ClubActivity]    Script Date: 2021/1/13 22:40:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ClubActivity](
	[activityID] [int] IDENTITY(1,1) NOT NULL,
	[activityName] [nvarchar](50) NOT NULL,
	[activityDesc] [text] NULL,
	[activityStartDate] [datetime] NOT NULL,
	[activityStopDate] [datetime] NOT NULL,
	[activityLocation] [nvarchar](20) NOT NULL,
	[statusID] [int] NULL,
	[clubID] [int] NULL,
	[accountID] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[activityID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ClubFinance]    Script Date: 2021/1/13 22:40:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ClubFinance](
	[financeID] [int] IDENTITY(1,1) NOT NULL,
	[financeName] [nvarchar](30) NOT NULL,
	[financeMoney] [money] NOT NULL,
	[financeDate] [datetime] NULL,
	[financeType] [nvarchar](4) NOT NULL,
	[clubID] [int] NULL,
	[accountID] [int] NULL,
	[activityID] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[financeID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ClubNews]    Script Date: 2021/1/13 22:40:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ClubNews](
	[newsID] [int] IDENTITY(1,1) NOT NULL,
	[newsName] [nvarchar](50) NOT NULL,
	[newsContent] [text] NULL,
	[newsDate] [datetime] NOT NULL,
	[statusID] [int] NULL,
	[clubID] [int] NULL,
	[accountID] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[newsID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ClubPosition]    Script Date: 2021/1/13 22:40:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ClubPosition](
	[positionID] [int] IDENTITY(1,1) NOT NULL,
	[positionName] [nvarchar](20) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[positionID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Status]    Script Date: 2021/1/13 22:40:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Status](
	[statusID] [int] IDENTITY(1,1) NOT NULL,
	[statusName] [nvarchar](10) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[statusID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET IDENTITY_INSERT [dbo].[Account] ON 

INSERT [dbo].[Account] ([accountID], [accountName], [accountPwd], [typeID], [studentID], [studentName], [studentSex], [studentCollege], [studentDiscipline], [studentClass], [studentPhone]) VALUES (2, N'452755', N'452755', 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL)
INSERT [dbo].[Account] ([accountID], [accountName], [accountPwd], [typeID], [studentID], [studentName], [studentSex], [studentCollege], [studentDiscipline], [studentClass], [studentPhone]) VALUES (3, N'342999', N'342999', 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL)
INSERT [dbo].[Account] ([accountID], [accountName], [accountPwd], [typeID], [studentID], [studentName], [studentSex], [studentCollege], [studentDiscipline], [studentClass], [studentPhone]) VALUES (4, N'123456', N'123456', 2, NULL, NULL, NULL, NULL, NULL, NULL, N'15166288624')
INSERT [dbo].[Account] ([accountID], [accountName], [accountPwd], [typeID], [studentID], [studentName], [studentSex], [studentCollege], [studentDiscipline], [studentClass], [studentPhone]) VALUES (5, N'147258', N'147258', 2, NULL, NULL, NULL, NULL, NULL, NULL, N'13580883306')
INSERT [dbo].[Account] ([accountID], [accountName], [accountPwd], [typeID], [studentID], [studentName], [studentSex], [studentCollege], [studentDiscipline], [studentClass], [studentPhone]) VALUES (12, N'123123', N'123123', 2, NULL, NULL, NULL, NULL, NULL, NULL, N'13580883306')
SET IDENTITY_INSERT [dbo].[Account] OFF
GO
SET IDENTITY_INSERT [dbo].[AccountType] ON 

INSERT [dbo].[AccountType] ([typeID], [typeName]) VALUES (2, N'普通学生')
INSERT [dbo].[AccountType] ([typeID], [typeName]) VALUES (1, N'社团联合会')
SET IDENTITY_INSERT [dbo].[AccountType] OFF
GO
SET IDENTITY_INSERT [dbo].[ClubPosition] ON 

INSERT [dbo].[ClubPosition] ([positionID], [positionName]) VALUES (3, N'副社长')
INSERT [dbo].[ClubPosition] ([positionID], [positionName]) VALUES (4, N'管理员')
INSERT [dbo].[ClubPosition] ([positionID], [positionName]) VALUES (5, N'普通社员')
INSERT [dbo].[ClubPosition] ([positionID], [positionName]) VALUES (2, N'社长')
INSERT [dbo].[ClubPosition] ([positionID], [positionName]) VALUES (1, N'社团创建人')
SET IDENTITY_INSERT [dbo].[ClubPosition] OFF
GO
SET IDENTITY_INSERT [dbo].[Status] ON 

INSERT [dbo].[Status] ([statusID], [statusName]) VALUES (6, N'开设中')
INSERT [dbo].[Status] ([statusID], [statusName]) VALUES (3, N'审核不通过')
INSERT [dbo].[Status] ([statusID], [statusName]) VALUES (2, N'审核通过')
INSERT [dbo].[Status] ([statusID], [statusName]) VALUES (1, N'审核中')
INSERT [dbo].[Status] ([statusID], [statusName]) VALUES (5, N'已过期')
INSERT [dbo].[Status] ([statusID], [statusName]) VALUES (7, N'已注销')
INSERT [dbo].[Status] ([statusID], [statusName]) VALUES (4, N'正在进行中')
SET IDENTITY_INSERT [dbo].[Status] OFF
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__Account__62DA37728ABCD9EB]    Script Date: 2021/1/13 22:40:24 ******/
ALTER TABLE [dbo].[Account] ADD UNIQUE NONCLUSTERED 
(
	[accountName] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__AccountT__A20CDB5840151864]    Script Date: 2021/1/13 22:40:24 ******/
ALTER TABLE [dbo].[AccountType] ADD UNIQUE NONCLUSTERED 
(
	[typeName] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__Club__266CC91E64ADC529]    Script Date: 2021/1/13 22:40:24 ******/
ALTER TABLE [dbo].[Club] ADD UNIQUE NONCLUSTERED 
(
	[clubName] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__ClubPosi__8AC696A5199753FB]    Script Date: 2021/1/13 22:40:24 ******/
ALTER TABLE [dbo].[ClubPosition] ADD UNIQUE NONCLUSTERED 
(
	[positionName] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__Status__6A50C21245EDA30C]    Script Date: 2021/1/13 22:40:24 ******/
ALTER TABLE [dbo].[Status] ADD UNIQUE NONCLUSTERED 
(
	[statusName] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Account] ADD  DEFAULT ((2)) FOR [typeID]
GO
ALTER TABLE [dbo].[Account] ADD  DEFAULT (NULL) FOR [studentID]
GO
ALTER TABLE [dbo].[Account] ADD  DEFAULT (NULL) FOR [studentName]
GO
ALTER TABLE [dbo].[Account] ADD  DEFAULT (NULL) FOR [studentSex]
GO
ALTER TABLE [dbo].[Account] ADD  DEFAULT (NULL) FOR [studentCollege]
GO
ALTER TABLE [dbo].[Account] ADD  DEFAULT (NULL) FOR [studentDiscipline]
GO
ALTER TABLE [dbo].[Account] ADD  DEFAULT (NULL) FOR [studentClass]
GO
ALTER TABLE [dbo].[Account] ADD  DEFAULT (NULL) FOR [studentPhone]
GO
ALTER TABLE [dbo].[Club] ADD  DEFAULT ('这个社团负责人很懒，什么都没有介绍') FOR [clubDesc]
GO
ALTER TABLE [dbo].[Club] ADD  DEFAULT (getdate()) FOR [clubCreateDate]
GO
ALTER TABLE [dbo].[Club] ADD  DEFAULT ((0.0000)) FOR [clubProperty]
GO
ALTER TABLE [dbo].[ClubAccount] ADD  DEFAULT (getdate()) FOR [positionStart]
GO
ALTER TABLE [dbo].[ClubAccount] ADD  DEFAULT (NULL) FOR [positionStop]
GO
ALTER TABLE [dbo].[ClubAccount] ADD  DEFAULT ('申请') FOR [positionStatus]
GO
ALTER TABLE [dbo].[ClubFinance] ADD  DEFAULT (getdate()) FOR [financeDate]
GO
ALTER TABLE [dbo].[ClubNews] ADD  DEFAULT (getdate()) FOR [newsDate]
GO
ALTER TABLE [dbo].[Account]  WITH CHECK ADD FOREIGN KEY([typeID])
REFERENCES [dbo].[AccountType] ([typeID])
GO
ALTER TABLE [dbo].[Club]  WITH CHECK ADD FOREIGN KEY([statusID])
REFERENCES [dbo].[Status] ([statusID])
GO
ALTER TABLE [dbo].[ClubAccount]  WITH CHECK ADD FOREIGN KEY([accountID])
REFERENCES [dbo].[Account] ([accountID])
GO
ALTER TABLE [dbo].[ClubAccount]  WITH CHECK ADD FOREIGN KEY([clubID])
REFERENCES [dbo].[Club] ([clubID])
GO
ALTER TABLE [dbo].[ClubAccount]  WITH CHECK ADD FOREIGN KEY([positionID])
REFERENCES [dbo].[ClubPosition] ([positionID])
GO
ALTER TABLE [dbo].[ClubActivity]  WITH CHECK ADD FOREIGN KEY([accountID])
REFERENCES [dbo].[Account] ([accountID])
GO
ALTER TABLE [dbo].[ClubActivity]  WITH CHECK ADD FOREIGN KEY([clubID])
REFERENCES [dbo].[Club] ([clubID])
GO
ALTER TABLE [dbo].[ClubActivity]  WITH CHECK ADD FOREIGN KEY([statusID])
REFERENCES [dbo].[Status] ([statusID])
GO
ALTER TABLE [dbo].[ClubFinance]  WITH CHECK ADD FOREIGN KEY([accountID])
REFERENCES [dbo].[Account] ([accountID])
GO
ALTER TABLE [dbo].[ClubFinance]  WITH CHECK ADD FOREIGN KEY([activityID])
REFERENCES [dbo].[ClubActivity] ([activityID])
GO
ALTER TABLE [dbo].[ClubFinance]  WITH CHECK ADD FOREIGN KEY([clubID])
REFERENCES [dbo].[Club] ([clubID])
GO
ALTER TABLE [dbo].[ClubNews]  WITH CHECK ADD FOREIGN KEY([accountID])
REFERENCES [dbo].[Account] ([accountID])
GO
ALTER TABLE [dbo].[ClubNews]  WITH CHECK ADD FOREIGN KEY([clubID])
REFERENCES [dbo].[Club] ([clubID])
GO
ALTER TABLE [dbo].[ClubNews]  WITH CHECK ADD FOREIGN KEY([statusID])
REFERENCES [dbo].[Status] ([statusID])
GO
ALTER TABLE [dbo].[Account]  WITH CHECK ADD CHECK  (([studentSex]='女' OR [studentSex]='男'))
GO
ALTER TABLE [dbo].[ClubAccount]  WITH CHECK ADD CHECK  (([positionStatus]='在职' OR [positionStatus]='历任' OR [positionStatus]='申请 '))
GO
ALTER TABLE [dbo].[ClubFinance]  WITH CHECK ADD CHECK  (([financeType]='支出' OR [financeType]='收入'))
GO
USE [master]
GO
ALTER DATABASE [ClubManagement] SET  READ_WRITE 
GO
