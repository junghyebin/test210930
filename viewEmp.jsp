<%@page import="emp.EmpBean"%>
<%@page import="emp.EmpDBBean"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>

<%
   	EmpDBBean db = EmpDBBean.getInstance();
	ArrayList<EmpBean> emplist = db.listEmp();
	int empno, sal, comm, deptno, mgr;
	String ename, job, hiredate;
%>
<html>
<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
</head>

<body>
	<table width="900" border="1">
		<tr>
			<td>��� ��ȣ</td>
			<td>�����</td>
			<td>����</td>
			<td>�����ȣ</td>
			<td>�Ի�����</td>
			<td>�޿�</td>
			<td>Ŀ�̼�</td>
			<td>�μ���ȣ</td>
		</tr>
		<%
			for(int i=0; i<emplist.size(); i++){
				EmpBean emp = emplist.get(i);
				empno = emp.getE_empno();
				ename = emp.getE_ename();
				job = emp.getE_job();
				mgr = emp.getE_mgr();
				hiredate = emp.getE_hiredate();
				sal = emp.getE_sal();
				comm = emp.getE_comm();
				deptno = emp.getE_deptno();
		%>
				<tr>
					<td><%= empno %></td>
					<td><%= ename %></td>
					<td><%= job %></td>
					<td><%= mgr %></td>
					<td><%= hiredate %></td>
					<td><%= sal %></td>
					<td><%= comm %></td>
					<td><%= deptno %></td>
				</tr>
		<%
			}
		%>
	</table>
</body>
</html>