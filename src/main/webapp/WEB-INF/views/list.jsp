<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Contact Manager &mdash; People</title>
    <link rel="stylesheet" href="<c:url value='/resources/css/style.css'/>">
</head>
<body>
<div class="container">
    <h1>People</h1>

    <c:if test="${not empty message}">
        <p class="notice">${message}</p>
    </c:if>

    <p>
        <a class="button" href="<c:url value='/people/create'/>">Create New Person</a>
    </p>

    <c:choose>
        <c:when test="${empty people}">
            <p class="empty">No results found</p>
        </c:when>
        <c:otherwise>
            <table class="people">
                <thead>
                <tr>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Email Address</th>
                    <th class="actions">Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="person" items="${people}">
                    <tr>
                        <td><c:out value="${person.firstName}"/></td>
                        <td><c:out value="${person.lastName}"/></td>
                        <td><c:out value="${person.emailAddress}"/></td>
                        <td class="actions">
                            <a href="<c:url value='/people/${person.id}/edit'/>">Edit</a>
                            <span class="sep">|</span>
                            <a href="<c:url value='/people/${person.id}/delete'/>">Delete</a>
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
