drop view v_ClubAccount
go
create view v_ClubAccount 
as
select id,ca.clubID,ca.accountID,ca.positionID,ca.positionStart,ca.positionStop,ca.statusID casStatusID,cas.statusName casStatusName,cp.positionName,a.accountName,a.studentName,a.studentCollege,a.studentDiscipline,a.studentClass,a.studentSex,c.clubName,c.clubDesc,c.clubCreateDate,c.clubProperty,c.statusID csStatusID,cs.statusName csStatusName from ClubAccount ca, ClubPosition cp, Status cs, Status cas, Account a, Club c where ca.clubID=c.clubID and ca.accountID=a.accountID and ca.positionID = cp.positionID and c.statusID=cs.statusID and ca.statusID=cas.statusID
go
drop view v_ClubNumberOfPeople
go
create view v_ClubNumberOfPeople
as
select President.clubID ,clubName,clubDesc,clubCreateDate,csStatusID,csStatusName,accountID, accountName,studentName, numberOfPeople from (select clubID,clubName,clubDesc,clubCreateDate,csStatusID,csStatusName,accountID,accountName,studentName from v_ClubAccount where positionID=2 and casStatusID = 10) President,(select count(*) numberOfPeople, clubID from v_ClubAccount where casStatusID = 10 and positionID!=1 group by clubID) CountOfPeople where President.clubID=CountOfPeople.clubID
go
drop view v_ClubCreator
go
create view v_ClubCreator
as
select creator.clubID ,clubName,clubDesc,clubCreateDate,csStatusID,csStatusName,accountID, accountName,studentName, numberOfPeople from (select clubID,clubName,clubDesc,clubCreateDate,csStatusID,csStatusName,accountID,accountName,studentName from v_ClubAccount where positionID=1) creator,(select count(*) numberOfPeople, clubID from v_ClubAccount where casStatusID = 10 and positionID!=1 group by clubID) CountOfPeople where creator.clubID=CountOfPeople.clubID
go
drop view v_LatestActivity
go
create view v_LatestActivity
as 
select top 5 ca.activityID, ca.activityName, ca.activityDesc, ca.activityPromotionalGraphics, ca.activityStartDate, ca.activityStopDate, ca.activityLocation, ca.clubID, ca.accountID, ca.statusID,c.clubName, a.accountName, a.studentName, s.statusName from ClubActivity ca, Club c, Account a ,Status s where ca.clubID=c.clubID and ca.accountID=a.accountID and ca.statusID=s.statusID order by activityStartDate, activityStopDate desc
go
drop view v_ClubActivity
go
create view v_ClubActivity
as 
select ca.activityID, ca.activityName, ca.activityDesc, ca.activityPromotionalGraphics, ca.activityStartDate, ca.activityStopDate, ca.activityLocation, ca.clubID, ca.accountID, ca.statusID,c.clubName, a.accountName, a.studentName, s.statusName from ClubActivity ca, Club c, Account a ,Status s where ca.clubID=c.clubID and ca.accountID=a.accountID and ca.statusID=s.statusID 
go
drop view v_ClubFinance
go
create view v_ClubFinance
as
select cf.financeID,cf.financeName,cf.financeMoney, cf.financeDate,cf.financeType,cf.clubID,c.clubName,cf.accountID,a.accountName,a.studentName from ClubFinance cf, Club c, Account a where cf.clubID=c.clubID and cf.accountID=a.accountID
go
drop view v_IndividualJoinClubs
go
create view v_IndividualJoinClubs
as
select IndividualJoinClubs.clubID ,clubName,clubDesc,clubCreateDate,csStatusID,csStatusName,accountID, accountName,studentName, numberOfPeople from (select clubID,clubName,clubDesc,clubCreateDate,csStatusID,csStatusName,accountID,accountName,studentName from v_ClubAccount where casStatusID = 10 and positionID != 1) IndividualJoinClubs, (select count(*) numberOfPeople, clubID from v_ClubAccount where casStatusID = 10 and positionID!=1 group by clubID) CountOfPeople where IndividualJoinClubs.clubID = CountOfPeople.clubID