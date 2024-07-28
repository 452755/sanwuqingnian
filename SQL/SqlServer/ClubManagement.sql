-- 判断某个数据库是否存在，存在就删除时的语句
-- 创建数据库是必须先使用master数据库
-- if 判断
-- db_id 数据库ID名称
-- is not null 判断是否为空
-- drop 删除关键字
-- database 数据库关键字
-- 比如
-- 判断学校数据库是否存在，存在就删除
use master 
go
if db_id('student') is not null
    drop database student
go 
-- 创建数据库时的语句
-- 创建数据库是必须先使用master数据库
-- create 创建数据库关键字
-- database 表示创建为数据库
-- on 表示数据库的具体信息
-- name 数据库名 用法：name=‘数据库名称’
-- filename 数据库文件名 用法：filename=‘文件的绝对路径，文件的后缀为(.mdf)’
-- size 数据库初始大小，单位为MB 用法：size=初始大小
-- maxsize 数据库的最大大小 用法：maxsize=数据库的最大大小
-- filegrowth 数据库文件大小增长速度 用法：filegrowth=增长的速度
-- 比如
-- 创建学校数据库
use master
go
create database school on
(
    -- 数据库名为student
    name='student',
    -- 文件名为
    filename='D:\\db\student.mdf',
    -- 初始大小为5，单位为MB
    size=5,
    -- 最大大小为20，单位为MB
    maxsize=20,
    -- 数据库文件大小的增长速度为10%
    filegrowth=10%
)
go
-- 创建表时的各种约束
-- 创建表时必须先使用需要的数据库
-- primary key 主键 用法：直接跟在字段数据类型后
-- default 默认值 用法：字段数据类型后，default(字段默认值)
-- unique 唯一键 用法：字段数据类型后
-- check 检查约束 用法：字段数据类型后 check(约束的具体内容)
-- not null 非空约束 用法：直接跟在字段数据类型后
-- identity 自增 用法：字段数据类型后 identity(自增种子 英文逗号 自增量)
-- references 外键关系 用法：字段数据类型后 references [主表名](主表的外键字段)
-- 比如
-- 在学校数据库创建班级表
use school
go
create table classes
(
    -- 编号 整型        主键       自增（种子为1，增量为1）
    ID          int   primary key          identity(1,1),
    -- 班级名   nvarchar         唯一    非空
    className nvarchar(50) unique not null
)
go
-- 在学校数据库创建学生表
create table student
(
     -- 编号 整型        主键       自增（种子为1，增量为1）
    ID          int   primary key          identity(1,1),
    -- 学号    varchar             唯一       非空
    stuID       nvarchar(10)   unique    not null,
    -- 学生名    nvarchar        非空
    stuName  nvarchar(20)   not null,
    -- 性别    nvarchar     检查约束为 只能为男或女    默认为男
    stuSex   nvarchar(4)    check(stuSex in('男','女'))   default('男'),
    -- 班级编号 整型     外键班级表班级编号
    classID          int    references [classes](ID)
)
go




use master														--使用系统数据库
go
if db_id('ClubManagement') is not null							--判断社团管理数据库是否存在
	drop database ClubManagement								--存在则删除社团管理数据库
create database ClubManagement on								--创建社团管理数据库
(
	name = 'ClubManagement',									--数据库名称
	filename = 'd:\db\ClubManagement.mdf',						--数据库物理文件路径名称
	size=5,														--数据库大小
	maxsize=20,													--最大大小
	filegrowth=10%												--大小增长
)
go
use ClubManagement												--使用社团管理数据库
go
create table AccountType										--创建账户类型表
(
	typeID int identity(1,1) primary key,						--类型ID，主键自增
	typeName nvarchar(20) not null unique						--类型名称，非空，唯一
)
go
create table Account											--创建账户表
(
	accountID int identity(1,1) primary key,					--账户ID，自增，主键
	accountName nvarchar(20) not null unique,					--账户名，非空，唯一
	accountPwd nvarchar(20) not null,							--账户密码，非空
	typeID int references AccountType(typeID) default(2),		--类型ID，外键账户类型表类型ID
	studentID nvarchar(20) default(null),						--学生ID，
	studentName nvarchar(20) default(null),						--学生名字，非空
	studentSex nvarchar(4) check(studentSex in ('男','女')) default(null),	--学生性别，只能为男或女
	studentCollege nvarchar(20)default(null),					--所在二级学院
	studentDiscipline nvarchar(30) default(null),				--所在专业
	studentClass nvarchar(20) default(null),					--所在班级
	studentPhone nvarchar(20) default(null)						--手机号码
)
go
create table Status												--创建活动状态表
(
	statusID int identity(1,1) primary key,						--状态ID，主键自增
	statusName nvarchar(10) not null unique						--状态名称，非空，唯一
)
go
create table Club												--创建社团表
(
	clubID int identity(1,1) primary key,						--社团ID，主键自增
	clubName nvarchar(20) not null unique,						--社团名称，非空，唯一
	clubDesc text default('这个社团负责人很懒，什么都没有介绍'),--社团简介，默认值
	clubCreateDate date default(getdate()),						--社团创建时间，默认当前时间
	clubProperty money	default(0.0000),						--社团账户余额
	statusID int references Status(statusId)					--状态ID，外键状态表状态ID
)
go
create table ClubPosition										--创建社团职位表
(
	positionID int identity(1,1) primary key,					--职位ID，主键自增
	positionName nvarchar(20) not null unique					--职位名称，非空，唯一
)
go
create table ClubAccount										--创建账户与社团的关系表
(
	id int identity(1,1) primary key,							--id，主键自增
	clubID	int references Club(clubID),						--社团ID，外键社团表社团表ID
	accountID int references Account(accountID),				--账户ID，外键账户表账户ID
	positionID int references ClubPosition(positionID),			--职位ID，外键职位表职位ID
	positionStart date default(getdate()),						--开始任职时间
	positionStop date default(null),							--结束任职时间
	positionStatus nvarchar(10)	check(positionStatus in ('申请 ','历任','在职')) default('申请')								--任职状态，在职，历任，默认在职
)
go
create table ClubActivity										--创建社团活动表
(
	activityID int identity(1,1) primary key,					--活动编号,主键自增
	activityName nvarchar(50) not null,							--活动名称，非空
	activityDesc text,											--活动简介摘要				
	activityStartDate datetime not null,						--活动开始时间，非空
	activityStopDate datetime not null,							--活动结束时间，非空
	activityLocation nvarchar(20) not null,						--活动地点ID，非空
	statusID int references Status(statusID),					--活动状态，外键活动状态表状态ID
	clubID	int references Club(clubID),						--举办社团ID，外键社团表社团ID
	accountID int references Account(accountID)					--活动发起人账户ID，外键账户表账户ID
)
go
create table ClubNews											--创建社团新闻表
(
	newsID int identity(1,1) primary key,						--新闻编号,主键自增
	newsName nvarchar(50) not null,								--活动名称，非空
	newsContent text,											--新闻具体内容				
	newsDate datetime not null default(getdate()),				--新闻发布时间，非空，默认为当前时间	
	statusID int references Status(statusID),					--新闻审核状态，外键状态表状态ID
	clubID	int references Club(clubID),						--发布社团ID，外键社团表社团ID
	accountID int references Account(accountID)					--新闻编撰人账户ID，外键账户表账户ID
)
go
create table ClubFinance										--创建社团财务记录表
(
	financeID int identity(1,1) primary key,					--财务记录ID
	financeName nvarchar(30) not null,							--财务名称
	financeMoney money not null,								--财务金额
	financeDate datetime default(getdate()),					--财务时间
	financeType nvarchar(4) not null check(financeType in ('收入','支出')),--财务类型，收入或支出
	clubID int references Club(clubID),							--社团ID，外键社团表社团ID
	accountID int references Account(accountID),				--财务经手账户ID，参照账户表账户ID
	activityID int references ClubActivity(activityID)			--活动ID，外键活动表活动ID
)
go
--向账户类型表添加数据
insert into AccountType values('社团联合会')
insert into AccountType values('普通学生')
--向状态表添加数据
insert into Status values('审核中')
insert into Status values('审核通过')
insert into Status values('审核不通过')
insert into Status values('正在进行中')
insert into Status values('已过期')
insert into Status values('开设中')
insert into Status values('已注销')
--向社团职位表中添加数据
insert into ClubPosition values('社团创建人')
insert into ClubPosition values('社长')
insert into ClubPosition values('副社长')
insert into ClubPosition values('管理员')
insert into ClubPosition values('普通社员')

--向账户表添加数据
insert into Account(accountName,accountPwd,typeID) values('admin','admin',1)
insert into Account(accountName,accountPwd) values('452755','452755')
insert into Account(accountName,accountPwd) values('342999','342999')
--账户表的增删改查
--向账户表中插入账户名为‘’账户密码为‘’类型ID为2，学生手机号为‘’，其余为默认值的账户
insert into Account values('','',2,default,default,default,default,default,default,'')
--查询所有的账户ID，账户名，账户密码，学生学号，学生性别，二级学院，专业，班级，手机号，账户类型ID，账户类型名称
select A.accountID,A.accountName,A.accountPwd,A.studentID,A.studentName,A.studentSex,A.studentCollege,A.studentDiscipline,A.studentClass,A.studentPhone,T.typeID,T.typeName from Account A, AccountType T where A.typeID=T.typeID
--查询账户名为‘’和账户密码为‘’的账户信息
select A.accountID,A.accountName,A.accountPwd,A.studentID,A.studentName,A.studentSex,A.studentCollege,A.studentDiscipline,A.studentClass,A.studentPhone,T.typeID,T.typeName from Account A, AccountType T where A.typeID=T.typeID and A.accountName='452755' and A.accountPwd='4527555'
--查询账户名为‘’的账户信息
select A.accountID,A.accountName,A.accountPwd,A.studentID,A.studentName,A.studentSex,A.studentCollege,A.studentDiscipline,A.studentClass,A.studentPhone,T.typeID,T.typeName from Account A, AccountType T where A.typeID=T.typeID and A.accountName=''
--修改账户名为‘’的账户密码为‘’
update Account set accountPwd='' where accountName=''
--修改账户名为‘’的学生学号为‘’，学生姓名=‘’，学生性别为‘’，二级学院为‘’，专业为‘’，班级为‘’，手机号为‘’
update Account set studentID='',studentName='',studentSex='',studentCollege='',studentDiscipline='',studentClass='',studentPhone='' where accountName=''
--修改账户名为‘’的账户类型ID为？
update Account set typeID=1 where accountName=''
--删除账户名为‘’的账户
delete from Account where accountName=''
--账户表的增删改查

--账户类型表的增删改查
--插入账户类型名为‘’的账户类型
insert into AccountType values('')
--修改类型ID为？的类型名称为‘’
update AccountType set typeName='' where typeID=1
--删除类型ID为？或类型名为‘’的账户类型
delete from AccountType where typeID=1 or typeName=''
--查询所有账户类型并按照类型ID升序
select typeID, typeName from AccountType order by typeID
--查询类型ID=？或类型名称=？
select typeID, typeName from AccountType where typeID=2 or typeName='1212'
--账户类型表的增删改查

--创建社团
INSERT INTO [dbo].[Club]([clubName],[statusID]) VALUES('社团联合会',6)
--修改社团信息
go
--社团新闻表
--创建查询所有社团新闻视图
create view v_ClubNews 
as
select newsID,newsName,newsContent,newsDate,n.statusID,n.clubID,n.accountID,s.statusName,c.clubName,a.accountName,a.studentName from ClubNews n, Club c ,Account a, Status s where n.clubID=c.clubID and n.accountID=a.accountID and n.statusID=s.statusID
go
--创建最新公告视图
create view v_LatestAnnouncement
as
select top 5 * from v_ClubNews where clubID=1 and statusID=2 order by newsDate desc
go
--创建最新新闻视图
create view v_LatestNews
as
select top 5 * from v_ClubNews where clubID != 1 and statusID = 2 order by newsDate desc
go
select * from v_ClubNews where newsName like '%测试%'
insert into ClubNews values('ssss','sss',getdate(),2,1,2)
update ClubNews set newsName='a',newsContent='a',newsDate=getdate(),statusID = 1 ,clubID=1,accountID=2 where newsID=3
go
--社团活动表
--创建查询所有社团活动的视图
create view v_ClubActivity
as
select ca.activityID,ca.activityName,ca.activityDesc,ca.activityStartDate,ca.activityStopDate,ca.activityLocation,ca.clubID,ca.accountID,ca.statusID,c.clubName,a.accountName,a.studentName,s.statusName from ClubActivity ca,Club c,Account a,Status s where ca.clubID=c.clubID and ca.accountID=a.accountID and ca.statusID=s.statusID
go
select * from v_ClubActivity
go
--创建最新活动视图
create view v_LatestActivity
as
select top 5 * from v_ClubActivity where statusID != 1 order by activityID desc