<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Contact Manager</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/app.css">
</head>
<body>
<div class="container">
    <h1>Contact Manager</h1>

    <div class="action-bar">
        <a href="${pageContext.request.contextPath}/person/create" class="btn">+ New Person</a>
    </div>

    <c:choose>
        <c:when test="${empty people}">
            <p class="no-results">No results found.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                    <tr>
                        <th>First Name</th>
                        <th>Last Name</th>
                        <th>Email Address</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="person" items="${people}">
                        <tr>
                            <td><c:out value="${person.firstName}"/></td>
                            <td><c:out value="${person.lastName}"/></td>
                            <td><c:out value="${person.email}"/></td>
                            <td>
                                <a class="action-link"
                                   href="${pageContext.request.contextPath}/person/edit/${person.personId}">Edit</a>
                                <a href="${pageContext.request.contextPath}/person/delete/${person.personId}">Delete</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
