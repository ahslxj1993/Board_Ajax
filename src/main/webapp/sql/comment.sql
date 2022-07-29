drop table comm cascade constraints purge;

create table comm (
num	number	primary key,
id varchar2(30) references member(id),
content varchar2(200),
reg_date date,
comment_board_num number references board(board_num) on delete cascade,
										--comm ���̺��� �����ϴ� ����� ��ȣ
comment_re_lev number(1) check(comment_re_lev in (0,1,2)), --�����̸� 0 ����̸� 1
comment_re_seq number, --�����̸� 0 , 1�����̸� 1���� ������ +1
comment_re_ref number	-- ������ �ڽ� �� �Ѥ���ȣ, ����̸� ���� �۹�ȣ
);

drop sequence com_seq;

create sequence com_seq;
