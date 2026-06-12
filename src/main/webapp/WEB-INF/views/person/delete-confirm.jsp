<%-- Delete confirmation page for the Delete Person workflow. --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Delete Person"/>
<%@ include file="../common/header.jspf" %>

<h2>Delete Person</h2>

<p>
    You are about to delete the person:
    <strong><c:out value="${person.firstName}"/> <c:out value="${person.lastName}"/></strong>,
    are you sure?
</p>

<%-- Deletion is a state-changing action, so it is submitted as a POST. --%>
<form method="post" action="<c:url value='/persons/${person.id}/delete'/>" class="delete-form">
    <button type="submit" class="button button-danger">Delete</button>
    <a href="<c:url value='/persons'/>">Cancel</a>
</form>

<%@ include file="../common/footer.jspf" %>
