<%-- Main page: listing of all people with edit/delete actions. --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="People"/>
<%@ include file="../common/header.jspf" %>

<h2>People</h2>

<c:if test="${not empty successMessage}">
    <p class="flash flash-success"><c:out value="${successMessage}"/></p>
</c:if>
<c:if test="${not empty errorMessage}">
    <p class="flash flash-error"><c:out value="${errorMessage}"/></p>
</c:if>

<p>
    <a class="button" href="<c:url value='/persons/new'/>">Create Person</a>
</p>

<c:choose>
    <c:when test="${empty people}">
        <p class="no-results">No results found</p>
    </c:when>
    <c:otherwise>
        <table class="person-table">
            <thead>
            <tr>
                <th scope="col">First name</th>
                <th scope="col">Last name</th>
                <th scope="col">Email address</th>
                <th scope="col">Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="person" items="${people}">
                <tr>
                    <td><c:out value="${person.firstName}"/></td>
                    <td><c:out value="${person.lastName}"/></td>
                    <td><c:out value="${person.emailAddress}"/></td>
                    <td class="actions">
                        <a href="<c:url value='/persons/${person.id}/edit'/>">Edit</a>
                        <a href="<c:url value='/persons/${person.id}/delete'/>">Delete</a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

<%@ include file="../common/footer.jspf" %>
