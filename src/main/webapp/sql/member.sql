drop table member cascade constraints purge;
--1. index.jsjp ���� �����մϴ�
--2. ������ ������ admin, ��� 1234�� ����ϴ�
--3. ����� ������ 3�� ����ϴ�.

create table member(
id varchar2(12),
password varchar2(10),
name varchar2(15),
age number(2),
gender varchar2(3),
email varchar2(30),
memberfile VARCHAR2(50),
primary key (id)
);

--memberfile�� ȸ�� ���� ������ �����մϴ�
select * from member;
