drop table board cascade constraints purge;

CREATE TABLE BOARD (
	BOARD_NUM	NUMBER, --�۹�ȣ
	BOARD_NAME	VARCHAR2(30), --�ۼ���
	BOARD_PASS	VARCHAR2(30), --��й�ȣ
	BOARD_SUBJECT	VARCHAR2(300), --����
	BOARD_CONTENT	VARCHAR2(4000), --����
	BOARD_FILE	VARCHAR2(50), --÷�ε� ���ϸ�
	BOARD_RE_REF NUMBER, --�㺯�� �ۼ��� �����Ǵ� ���� ��ȣ
	BOARD_RE_LEV NUMBER, --�亯 ���� ����
	BOARD_RE_SEQ NUMBER,-- �亯���� ����
	BOARD_READCOUNT NUMBER, --���� ��ȸ��
	BOARD_DATE DATE default sysdate, --���� �ۼ� ��¥
	PRIMARY KEY (BOARD_NUM)
);

select * form emp, dept;

select nvl(max(board_num),0) +1 from board;

select * from board;
delete from board;

insert into board (BOARD_NUM, BOARD_SUBJECT, BOARD_NAME, BOARD_RE_REF) values(1,'ó��','admin',1);
insert into board (BOARD_NUM, BOARD_SUBJECT, BOARD_NAME, BOARD_RE_REF) values(2,'��°','admin',2);
insert into board (BOARD_NUM, BOARD_SUBJECT, BOARD_NAME, BOARD_RE_REF) values(3,'��°','admin',3);

insert into comm (num, id, comment_board_num) values (1,'admin', 1);
insert into comm (num, id, comment_board_num) values (2,'admin', 1);
insert into comm (num, id, comment_board_num) values (3,'admin', 2);
insert into comm (num, id, comment_board_num) values (4,'admin', 2);

--1. comm ���̺��� comment_board_num ��  ������ ���մϴ�
select comment_board_num , count(*) as cnt from comm group by comment_board_num;

--2. board�� ������ �մϴ�

select board_num , board_subject, cnt
from board join(select comment_board_num, count(*) cnt
				from comm
				group by comment_board_num)
on board_num = comment_board_num;

-- ������ ) ���� board ���̺��� ���� ������ ����� ���� ��� ��ȸ�� ���� �ʽ��ϴ�.

--3. outer join�� �̿��ؼ� board �� ���� ��� ǥ�õǰ� cnt�� null�� ��� 0���� ǥ���ϵ��� �մϴ�
select board_num , board_subject, nvl(cnt,0) as cnt
from board left outer join(select comment_board_num, count(*) cnt
				from comm
				group by comment_board_num)
on board_num = comment_board_num
order by board_re_ref desc,
board_re_seq asc;

--4. �ζ��� �並 �̿��� ������ �ۼ�


select *
from (select rownum rnum, j.* 
		from (select board.* , nvl(cnt,0) cnt 
				from board left outer join (select comment_board_num, count(*) cnt
											from comm
											group by comment_board_num)
				on board_num = comment_board_num
				order by BOARD_RE_REF desc,
				BOARD_RE_SEQ asc
				) j
				where rownum <= 10
		)
where rnum>=1 and rnum <=10

