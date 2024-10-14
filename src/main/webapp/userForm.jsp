<%--
  Created by IntelliJ IDEA.
  User: sirge
  Date: 14/10/2024
  Time: 17:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>User Form</title>
    <style>
        dl {
            background: none repeat scroll 0 0 #FAFAFA;
            margin: 8px 0;
            padding: 0;
        }

        dt {
            display: inline-block;
            width: 170px;
        }

        dd {
            display: inline-block;
            margin-left: 8px;
            vertical-align: top;
        }
    </style>
</head>
<body>
<section>
    <h3><a href="index.html">Home</a></h3>
    <hr>
    <h2>${param.action == 'create' ? 'Create User' : 'Edit User'}</h2>
    <jsp:useBean id="user" type="ru.javawebinar.topjava.model.User" scope="request"/>
    <form method="post" action="users">
        <input type="hidden" name="id" value="${user.id}">
        <dl>
            <dt>Name:</dt>
            <dd><input type="text" value="${user.name}" name="name" required></dd>
        </dl>
        <dl>
            <dt>Email:</dt>
            <dd><input type="email" value="${user.email}" name="email" required></dd>
        </dl>
        <dl>
            <dt>Password:</dt>
            <dd><input type="password" name="password" ${param.action == 'create' ? 'required' : ''}></dd>
        </dl>
        <dl>
            <dt>Role:</dt>
            <dd>
                <select name="role" required>
                    <option value="CUSTOMER" ${user.role == 'CUSTOMER' ? 'selected' : ''}>Customer</option>
                    <option value="MANAGER" ${user.role == 'MANAGER' ? 'selected' : ''}>Manager</option>
                    <option value="ADMIN" ${user.role == 'ADMIN' ? 'selected' : ''}>Admin</option>
                </select>
            </dd>
        </dl>
        <button type="submit">Save</button>
        <button onclick="window.history.back()" type="button">Cancel</button>
    </form>
</section>
</body>
</html>

