<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title><c:choose>
        <c:when test="${not empty person.personId}">Edit Person</c:when>
        <c:otherwise>New Person</c:otherwise>
    </c:choose> - Contact Manager</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/app.css">
</head>
<body>
<div class="container">
    <h1><c:choose>
        <c:when test="${not empty person.personId}">Edit Person</c:when>
        <c:otherwise>New Person</c:otherwise>
    </c:choose></h1>

    <c:if test="${not empty errors}">
        <div class="errors">
            <strong>Please correct the following:</strong>
            <ul>
                <c:forEach var="error" items="${errors}">
                    <li><c:out value="${error}"/></li>
                </c:forEach>
            </ul>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${not empty person.personId}">
            <c:set var="formAction" value="${pageContext.request.contextPath}/person/edit/${person.personId}"/>
        </c:when>
        <c:otherwise>
            <c:set var="formAction" value="${pageContext.request.contextPath}/person/create"/>
        </c:otherwise>
    </c:choose>

    <form method="post" action="${formAction}" novalidate>
        <div class="form-group">
            <label for="firstName">First Name <span style="color:#c00">*</span></label>
            <input type="text" id="firstName" name="firstName" maxlength="30"
                   value="<c:out value='${person.firstName}'/>">
        </div>
        <div class="form-group">
            <label for="lastName">Last Name <span style="color:#c00">*</span></label>
            <input type="text" id="lastName" name="lastName" maxlength="30"
                   value="<c:out value='${person.lastName}'/>">
        </div>
        <div class="form-group">
            <label for="email">Email Address <span style="color:#c00">*</span></label>
            <input type="text" id="email" name="email" maxlength="30"
                   value="<c:out value='${person.email}'/>">
        </div>
        <div class="form-group">
            <label for="street">Street Address <span style="color:#c00">*</span></label>
            <input type="text" id="street" name="street" maxlength="60"
                   value="<c:out value='${person.street}'/>">
        </div>
        <div class="form-group">
            <label for="city">City <span style="color:#c00">*</span></label>
            <input type="text" id="city" name="city" maxlength="30"
                   value="<c:out value='${person.city}'/>">
        </div>
        <div class="form-group">
            <label for="state">State (2-letter abbreviation) <span style="color:#c00">*</span></label>
            <input type="text" id="state" name="state" maxlength="2" size="4"
                   value="<c:out value='${person.state}'/>">
        </div>
        <div class="form-group">
            <label for="zip">Zip Code (5 digits) <span style="color:#c00">*</span></label>
            <input type="text" id="zip" name="zip" maxlength="5" size="8"
                   value="<c:out value='${person.zip}'/>">
        </div>

        <div style="margin-top:16px">
            <button type="submit" class="btn">Save</button>
            &nbsp;
            <a href="${pageContext.request.contextPath}/person/list" class="btn btn-secondary">Cancel</a>
        </div>
    </form>
</div>
</body>
</html>
