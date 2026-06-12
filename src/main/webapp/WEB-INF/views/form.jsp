<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Shared form for both the Create and Edit workflows. When the bound person has an id the
  form posts to the edit endpoint; otherwise it posts to the create endpoint.
--%>
<c:choose>
    <c:when test="${person.id != null}">
        <c:set var="heading" value="Edit Person"/>
        <c:url var="action" value="/people/${person.id}/edit"/>
    </c:when>
    <c:otherwise>
        <c:set var="heading" value="Create Person"/>
        <c:url var="action" value="/people/create"/>
    </c:otherwise>
</c:choose>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Contact Manager &mdash; ${heading}</title>
    <link rel="stylesheet" href="<c:url value='/resources/css/style.css'/>">
</head>
<body>
<div class="container">
    <h1>${heading}</h1>

    <form:form modelAttribute="person" action="${action}" method="post" id="person-form" novalidate="novalidate">
        <div class="field">
            <label for="firstName">First Name</label>
            <form:input path="firstName" id="firstName" maxlength="30" required="required"/>
            <form:errors path="firstName" cssClass="error"/>
        </div>

        <div class="field">
            <label for="lastName">Last Name</label>
            <form:input path="lastName" id="lastName" maxlength="30" required="required"/>
            <form:errors path="lastName" cssClass="error"/>
        </div>

        <div class="field">
            <label for="emailAddress">Email Address</label>
            <form:input path="emailAddress" id="emailAddress" maxlength="30" required="required"/>
            <form:errors path="emailAddress" cssClass="error"/>
        </div>

        <div class="field">
            <label for="streetAddress">Street Address</label>
            <form:input path="streetAddress" id="streetAddress" maxlength="60" required="required"/>
            <form:errors path="streetAddress" cssClass="error"/>
        </div>

        <div class="field">
            <label for="city">City</label>
            <form:input path="city" id="city" maxlength="30" required="required"/>
            <form:errors path="city" cssClass="error"/>
        </div>

        <div class="field">
            <label for="state">State</label>
            <form:input path="state" id="state" maxlength="2" required="required"
                        cssClass="state-input" placeholder="e.g. CA"/>
            <form:errors path="state" cssClass="error"/>
        </div>

        <div class="field">
            <label for="zipCode">Zip Code</label>
            <form:input path="zipCode" id="zipCode" maxlength="5" required="required"
                        cssClass="zip-input" placeholder="e.g. 90210"/>
            <form:errors path="zipCode" cssClass="error"/>
        </div>

        <div class="actions">
            <button type="submit" class="button">Save</button>
            <a class="button secondary" href="<c:url value='/people'/>">Cancel</a>
        </div>
    </form:form>
</div>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="<c:url value='/resources/js/validation.js'/>"></script>
</body>
</html>
