<%-- Notes for a single person: add-note form and a soft-deletable list. --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="pageTitle" value="Notes"/>
<%@ include file="../common/header.jspf" %>

<h2>Notes for <c:out value="${person.firstName} ${person.lastName}"/></h2>

<c:if test="${not empty successMessage}">
    <p class="flash flash-success"><c:out value="${successMessage}"/></p>
</c:if>
<c:if test="${not empty errorMessage}">
    <p class="flash flash-error"><c:out value="${errorMessage}"/></p>
</c:if>

<form:form modelAttribute="noteForm" method="post" cssClass="note-form"
           action="${pageContext.request.contextPath}/persons/${person.id}/notes">
    <div class="field">
        <form:label path="noteText">New note</form:label>
        <form:textarea path="noteText" maxlength="1000" rows="3"/>
        <form:errors path="noteText" cssClass="field-error"/>
    </div>
    <button type="submit" class="button">Add Note</button>
</form:form>

<c:choose>
    <c:when test="${empty notes}">
        <p class="no-results">No notes found</p>
    </c:when>
    <c:otherwise>
        <ul class="note-list">
            <c:forEach var="note" items="${notes}">
                <li class="note">
                    <p class="note-text"><c:out value="${note.noteText}"/></p>
                    <div class="note-meta">
                        <span>Added ${note.createdAt.toLocalDate()}</span>
                        <form method="post"
                              action="<c:url value='/persons/${person.id}/notes/${note.id}/delete'/>">
                            <button type="submit" class="icon-button"
                                    title="Delete note" aria-label="Delete note">
                                <i class="fa-solid fa-trash" aria-hidden="true"></i>
                            </button>
                        </form>
                    </div>
                </li>
            </c:forEach>
        </ul>
    </c:otherwise>
</c:choose>

<p class="back-link">
    <a href="<c:url value='/persons'/>">&larr; Back to people</a>
</p>

<%@ include file="../common/footer.jspf" %>
