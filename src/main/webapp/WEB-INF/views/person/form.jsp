<%--
  Shared form for the Create Person and Edit Person workflows. The page posts
  back to its own URL, so one view serves both flows. Server-side validation
  errors render next to each field; client-side validation in person-form.js
  mirrors the same rules.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="pageTitle" value="${formTitle}"/>
<%@ include file="../common/header.jspf" %>

<h2><c:out value="${formTitle}"/></h2>

<form:form modelAttribute="person" method="post" cssClass="person-form" novalidate="novalidate">
    <div class="field">
        <form:label path="firstName">First name</form:label>
        <form:input path="firstName" maxlength="30" data-validate="name"/>
        <form:errors path="firstName" cssClass="field-error"/>
    </div>
    <div class="field">
        <form:label path="lastName">Last name</form:label>
        <form:input path="lastName" maxlength="30" data-validate="name"/>
        <form:errors path="lastName" cssClass="field-error"/>
    </div>
    <div class="field">
        <form:label path="emailAddress">Email address</form:label>
        <form:input path="emailAddress" maxlength="30" data-validate="email"/>
        <form:errors path="emailAddress" cssClass="field-error"/>
    </div>
    <div class="field">
        <form:label path="streetAddress">Street address</form:label>
        <form:input path="streetAddress" maxlength="60" data-validate="street"/>
        <form:errors path="streetAddress" cssClass="field-error"/>
    </div>
    <div class="field">
        <form:label path="city">City</form:label>
        <form:input path="city" maxlength="30" data-validate="city"/>
        <form:errors path="city" cssClass="field-error"/>
    </div>
    <div class="field">
        <form:label path="state">State</form:label>
        <form:input path="state" maxlength="2" size="2" data-validate="state"/>
        <form:errors path="state" cssClass="field-error"/>
    </div>
    <div class="field">
        <form:label path="zipCode">Zip code</form:label>
        <form:input path="zipCode" maxlength="5" size="5" inputmode="numeric" data-validate="zip"/>
        <form:errors path="zipCode" cssClass="field-error"/>
    </div>
    <div class="form-actions">
        <button type="submit" class="button">Save</button>
        <a href="<c:url value='/persons'/>">Cancel</a>
    </div>
</form:form>

<script src="<c:url value='/resources/js/person-form.js'/>"></script>

<%@ include file="../common/footer.jspf" %>
