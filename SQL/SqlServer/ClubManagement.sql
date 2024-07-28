-- �ж�ĳ�����ݿ��Ƿ���ڣ����ھ�ɾ��ʱ�����
-- �������ݿ��Ǳ�����ʹ��master���ݿ�
-- if �ж�
-- db_id ���ݿ�ID����
-- is not null �ж��Ƿ�Ϊ��
-- drop ɾ���ؼ���
-- database ���ݿ�ؼ���
-- ����
-- �ж�ѧУ���ݿ��Ƿ���ڣ����ھ�ɾ��
use master 
go
if db_id('student') is not null
    drop database student
go 
-- �������ݿ�ʱ�����
-- �������ݿ��Ǳ�����ʹ��master���ݿ�
-- create �������ݿ�ؼ���
-- database ��ʾ����Ϊ���ݿ�
-- on ��ʾ���ݿ�ľ�����Ϣ
-- name ���ݿ��� �÷���name=�����ݿ����ơ�
-- filename ���ݿ��ļ��� �÷���filename=���ļ��ľ���·�����ļ��ĺ�׺Ϊ(.mdf)��
-- size ���ݿ��ʼ��С����λΪMB �÷���size=��ʼ��С
-- maxsize ���ݿ������С �÷���maxsize=���ݿ������С
-- filegrowth ���ݿ��ļ���С�����ٶ� �÷���filegrowth=�������ٶ�
-- ����
-- ����ѧУ���ݿ�
use master
go
create database school on
(
    -- ���ݿ���Ϊstudent
    name='student',
    -- �ļ���Ϊ
    filename='D:\\db\student.mdf',
    -- ��ʼ��СΪ5����λΪMB
    size=5,
    -- ����СΪ20����λΪMB
    maxsize=20,
    -- ���ݿ��ļ���С�������ٶ�Ϊ10%
    filegrowth=10%
)
go
-- ������ʱ�ĸ���Լ��
-- ������ʱ������ʹ����Ҫ�����ݿ�
-- primary key ���� �÷���ֱ�Ӹ����ֶ��������ͺ�
-- default Ĭ��ֵ �÷����ֶ��������ͺ�default(�ֶ�Ĭ��ֵ)
-- unique Ψһ�� �÷����ֶ��������ͺ�
-- check ���Լ�� �÷����ֶ��������ͺ� check(Լ���ľ�������)
-- not null �ǿ�Լ�� �÷���ֱ�Ӹ����ֶ��������ͺ�
-- identity ���� �÷����ֶ��������ͺ� identity(�������� Ӣ�Ķ��� ������)
-- references �����ϵ �÷����ֶ��������ͺ� references [������](���������ֶ�)
-- ����
-- ��ѧУ���ݿⴴ���༶��
use school
go
create table classes
(
    -- ��� ����        ����       ����������Ϊ1������Ϊ1��
    ID          int   primary key          identity(1,1),
    -- �༶��   nvarchar         Ψһ    �ǿ�
    className nvarchar(50) unique not null
)
go
-- ��ѧУ���ݿⴴ��ѧ����
create table student
(
     -- ��� ����        ����       ����������Ϊ1������Ϊ1��
    ID          int   primary key          identity(1,1),
    -- ѧ��    varchar             Ψһ       �ǿ�
    stuID       nvarchar(10)   unique    not null,
    -- ѧ����    nvarchar        �ǿ�
    stuName  nvarchar(20)   not null,
    -- �Ա�    nvarchar     ���Լ��Ϊ ֻ��Ϊ�л�Ů    Ĭ��Ϊ��
    stuSex   nvarchar(4)    check(stuSex in('��','Ů'))   default('��'),
    -- �༶��� ����     ����༶��༶���
    classID          int    references [classes](ID)
)
go




use master														--ʹ��ϵͳ���ݿ�
go
if db_id('ClubManagement') is not null							--�ж����Ź������ݿ��Ƿ����
	drop database ClubManagement								--������ɾ�����Ź������ݿ�
create database ClubManagement on								--�������Ź������ݿ�
(
	name = 'ClubManagement',									--���ݿ�����
	filename = 'd:\db\ClubManagement.mdf',						--���ݿ������ļ�·������
	size=5,														--���ݿ��С
	maxsize=20,													--����С
	filegrowth=10%												--��С����
)
go
use ClubManagement												--ʹ�����Ź������ݿ�
go
create table AccountType										--�����˻����ͱ�
(
	typeID int identity(1,1) primary key,						--����ID����������
	typeName nvarchar(20) not null unique						--�������ƣ��ǿգ�Ψһ
)
go
create table Account											--�����˻���
(
	accountID int identity(1,1) primary key,					--�˻�ID������������
	accountName nvarchar(20) not null unique,					--�˻������ǿգ�Ψһ
	accountPwd nvarchar(20) not null,							--�˻����룬�ǿ�
	typeID int references AccountType(typeID) default(2),		--����ID������˻����ͱ�����ID
	studentID nvarchar(20) default(null),						--ѧ��ID��
	studentName nvarchar(20) default(null),						--ѧ�����֣��ǿ�
	studentSex nvarchar(4) check(studentSex in ('��','Ů')) default(null),	--ѧ���Ա�ֻ��Ϊ�л�Ů
	studentCollege nvarchar(20)default(null),					--���ڶ���ѧԺ
	studentDiscipline nvarchar(30) default(null),				--����רҵ
	studentClass nvarchar(20) default(null),					--���ڰ༶
	studentPhone nvarchar(20) default(null)						--�ֻ�����
)
go
create table Status												--�����״̬��
(
	statusID int identity(1,1) primary key,						--״̬ID����������
	statusName nvarchar(10) not null unique						--״̬���ƣ��ǿգ�Ψһ
)
go
create table Club												--�������ű�
(
	clubID int identity(1,1) primary key,						--����ID����������
	clubName nvarchar(20) not null unique,						--�������ƣ��ǿգ�Ψһ
	clubDesc text default('������Ÿ����˺�����ʲô��û�н���'),--���ż�飬Ĭ��ֵ
	clubCreateDate date default(getdate()),						--���Ŵ���ʱ�䣬Ĭ�ϵ�ǰʱ��
	clubProperty money	default(0.0000),						--�����˻����
	statusID int references Status(statusId)					--״̬ID�����״̬��״̬ID
)
go
create table ClubPosition										--��������ְλ��
(
	positionID int identity(1,1) primary key,					--ְλID����������
	positionName nvarchar(20) not null unique					--ְλ���ƣ��ǿգ�Ψһ
)
go
create table ClubAccount										--�����˻������ŵĹ�ϵ��
(
	id int identity(1,1) primary key,							--id����������
	clubID	int references Club(clubID),						--����ID��������ű����ű�ID
	accountID int references Account(accountID),				--�˻�ID������˻����˻�ID
	positionID int references ClubPosition(positionID),			--ְλID�����ְλ��ְλID
	positionStart date default(getdate()),						--��ʼ��ְʱ��
	positionStop date default(null),							--������ְʱ��
	positionStatus nvarchar(10)	check(positionStatus in ('���� ','����','��ְ')) default('����')								--��ְ״̬����ְ�����Σ�Ĭ����ְ
)
go
create table ClubActivity										--�������Ż��
(
	activityID int identity(1,1) primary key,					--����,��������
	activityName nvarchar(50) not null,							--����ƣ��ǿ�
	activityDesc text,											--����ժҪ				
	activityStartDate datetime not null,						--���ʼʱ�䣬�ǿ�
	activityStopDate datetime not null,							--�����ʱ�䣬�ǿ�
	activityLocation nvarchar(20) not null,						--��ص�ID���ǿ�
	statusID int references Status(statusID),					--�״̬������״̬��״̬ID
	clubID	int references Club(clubID),						--�ٰ�����ID��������ű�����ID
	accountID int references Account(accountID)					--��������˻�ID������˻����˻�ID
)
go
create table ClubNews											--�����������ű�
(
	newsID int identity(1,1) primary key,						--���ű��,��������
	newsName nvarchar(50) not null,								--����ƣ��ǿ�
	newsContent text,											--���ž�������				
	newsDate datetime not null default(getdate()),				--���ŷ���ʱ�䣬�ǿգ�Ĭ��Ϊ��ǰʱ��	
	statusID int references Status(statusID),					--�������״̬�����״̬��״̬ID
	clubID	int references Club(clubID),						--��������ID��������ű�����ID
	accountID int references Account(accountID)					--���ű�׫���˻�ID������˻����˻�ID
)
go
create table ClubFinance										--�������Ų����¼��
(
	financeID int identity(1,1) primary key,					--�����¼ID
	financeName nvarchar(30) not null,							--��������
	financeMoney money not null,								--������
	financeDate datetime default(getdate()),					--����ʱ��
	financeType nvarchar(4) not null check(financeType in ('����','֧��')),--�������ͣ������֧��
	clubID int references Club(clubID),							--����ID��������ű�����ID
	accountID int references Account(accountID),				--�������˻�ID�������˻����˻�ID
	activityID int references ClubActivity(activityID)			--�ID��������ID
)
go
--���˻����ͱ��������
insert into AccountType values('�������ϻ�')
insert into AccountType values('��ͨѧ��')
--��״̬���������
insert into Status values('�����')
insert into Status values('���ͨ��')
insert into Status values('��˲�ͨ��')
insert into Status values('���ڽ�����')
insert into Status values('�ѹ���')
insert into Status values('������')
insert into Status values('��ע��')
--������ְλ�����������
insert into ClubPosition values('���Ŵ�����')
insert into ClubPosition values('�糤')
insert into ClubPosition values('���糤')
insert into ClubPosition values('����Ա')
insert into ClubPosition values('��ͨ��Ա')

--���˻����������
insert into Account(accountName,accountPwd,typeID) values('admin','admin',1)
insert into Account(accountName,accountPwd) values('452755','452755')
insert into Account(accountName,accountPwd) values('342999','342999')
--�˻������ɾ�Ĳ�
--���˻����в����˻���Ϊ�����˻�����Ϊ��������IDΪ2��ѧ���ֻ���Ϊ����������ΪĬ��ֵ���˻�
insert into Account values('','',2,default,default,default,default,default,default,'')
--��ѯ���е��˻�ID���˻������˻����룬ѧ��ѧ�ţ�ѧ���Ա𣬶���ѧԺ��רҵ���༶���ֻ��ţ��˻�����ID���˻���������
select A.accountID,A.accountName,A.accountPwd,A.studentID,A.studentName,A.studentSex,A.studentCollege,A.studentDiscipline,A.studentClass,A.studentPhone,T.typeID,T.typeName from Account A, AccountType T where A.typeID=T.typeID
--��ѯ�˻���Ϊ�������˻�����Ϊ�������˻���Ϣ
select A.accountID,A.accountName,A.accountPwd,A.studentID,A.studentName,A.studentSex,A.studentCollege,A.studentDiscipline,A.studentClass,A.studentPhone,T.typeID,T.typeName from Account A, AccountType T where A.typeID=T.typeID and A.accountName='452755' and A.accountPwd='4527555'
--��ѯ�˻���Ϊ�������˻���Ϣ
select A.accountID,A.accountName,A.accountPwd,A.studentID,A.studentName,A.studentSex,A.studentCollege,A.studentDiscipline,A.studentClass,A.studentPhone,T.typeID,T.typeName from Account A, AccountType T where A.typeID=T.typeID and A.accountName=''
--�޸��˻���Ϊ�������˻�����Ϊ����
update Account set accountPwd='' where accountName=''
--�޸��˻���Ϊ������ѧ��ѧ��Ϊ������ѧ������=������ѧ���Ա�Ϊ����������ѧԺΪ������רҵΪ�������༶Ϊ�������ֻ���Ϊ����
update Account set studentID='',studentName='',studentSex='',studentCollege='',studentDiscipline='',studentClass='',studentPhone='' where accountName=''
--�޸��˻���Ϊ�������˻�����IDΪ��
update Account set typeID=1 where accountName=''
--ɾ���˻���Ϊ�������˻�
delete from Account where accountName=''
--�˻������ɾ�Ĳ�

--�˻����ͱ����ɾ�Ĳ�
--�����˻�������Ϊ�������˻�����
insert into AccountType values('')
--�޸�����IDΪ������������Ϊ����
update AccountType set typeName='' where typeID=1
--ɾ������IDΪ����������Ϊ�������˻�����
delete from AccountType where typeID=1 or typeName=''
--��ѯ�����˻����Ͳ���������ID����
select typeID, typeName from AccountType order by typeID
--��ѯ����ID=������������=��
select typeID, typeName from AccountType where typeID=2 or typeName='1212'
--�˻����ͱ����ɾ�Ĳ�

--��������
INSERT INTO [dbo].[Club]([clubName],[statusID]) VALUES('�������ϻ�',6)
--�޸�������Ϣ
go
--�������ű�
--������ѯ��������������ͼ
create view v_ClubNews 
as
select newsID,newsName,newsContent,newsDate,n.statusID,n.clubID,n.accountID,s.statusName,c.clubName,a.accountName,a.studentName from ClubNews n, Club c ,Account a, Status s where n.clubID=c.clubID and n.accountID=a.accountID and n.statusID=s.statusID
go
--�������¹�����ͼ
create view v_LatestAnnouncement
as
select top 5 * from v_ClubNews where clubID=1 and statusID=2 order by newsDate desc
go
--��������������ͼ
create view v_LatestNews
as
select top 5 * from v_ClubNews where clubID != 1 and statusID = 2 order by newsDate desc
go
select * from v_ClubNews where newsName like '%����%'
insert into ClubNews values('ssss','sss',getdate(),2,1,2)
update ClubNews set newsName='a',newsContent='a',newsDate=getdate(),statusID = 1 ,clubID=1,accountID=2 where newsID=3
go
--���Ż��
--������ѯ�������Ż����ͼ
create view v_ClubActivity
as
select ca.activityID,ca.activityName,ca.activityDesc,ca.activityStartDate,ca.activityStopDate,ca.activityLocation,ca.clubID,ca.accountID,ca.statusID,c.clubName,a.accountName,a.studentName,s.statusName from ClubActivity ca,Club c,Account a,Status s where ca.clubID=c.clubID and ca.accountID=a.accountID and ca.statusID=s.statusID
go
select * from v_ClubActivity
go
--�������»��ͼ
create view v_LatestActivity
as
select top 5 * from v_ClubActivity where statusID != 1 order by activityID desc