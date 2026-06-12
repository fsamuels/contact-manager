<%-- Main page: paginated listing of people with notes/edit/delete actions. --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="People"/>
<%@ include file="../common/header.jspf" %>

<h2>People</h2>

<p class="new-experience-banner">
    <a class="new-experience-link" href="<c:url value='/app/'/>">
        <i class="fa-solid fa-wand-magic-sparkles new-experience-icon" aria-hidden="true"></i>
        Try the new experience
    </a>
</p>

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
        <%-- Changing the size restarts at page 1, since the old page number is meaningless. --%>
        <form class="page-size-form" method="get" action="<c:url value='/persons'/>">
            <label for="page-size">Per page:</label>
            <select id="page-size" name="size">
                <c:forEach var="option" items="${pageSizeOptions}">
                    <option value="${option}" ${option == personPage.pageSize ? 'selected' : ''}>${option}</option>
                </c:forEach>
            </select>
            <button type="submit" class="page-size-apply">Apply</button>
        </form>

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
                        <c:set var="personName"
                               value="${fn:escapeXml(person.firstName)} ${fn:escapeXml(person.lastName)}"/>
                        <c:set var="noteCount"
                               value="${empty noteCounts[person.id] ? 0 : noteCounts[person.id]}"/>
                        <a class="icon-link" href="<c:url value='/persons/${person.id}/notes'/>"
                           title="Notes for ${personName} (${noteCount})"
                           aria-label="Notes for ${personName} (${noteCount})">
                            <i class="fa-solid fa-note-sticky" aria-hidden="true"></i>
                        </a>
                        <a class="icon-link" href="<c:url value='/persons/${person.id}/edit'/>"
                           title="Edit ${personName}" aria-label="Edit ${personName}">
                            <i class="fa-solid fa-pen-to-square" aria-hidden="true"></i>
                        </a>
                        <a class="icon-link" href="<c:url value='/persons/${person.id}/delete'/>"
                           title="Delete ${personName}" aria-label="Delete ${personName}">
                            <i class="fa-solid fa-trash" aria-hidden="true"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <c:if test="${personPage.totalPages > 1}">
            <nav class="pagination" aria-label="Page navigation">
                <c:choose>
                    <c:when test="${personPage.first}">
                        <span class="page-arrow page-disabled" aria-hidden="true">&larr;</span>
                    </c:when>
                    <c:otherwise>
                        <c:url var="prevUrl" value="/persons">
                            <c:param name="page" value="${personPage.pageNumber - 1}"/>
                            <c:param name="size" value="${personPage.pageSize}"/>
                        </c:url>
                        <a class="page-arrow" href="${prevUrl}" aria-label="Previous page">&larr;</a>
                    </c:otherwise>
                </c:choose>

                <c:forEach var="i" begin="1" end="${personPage.totalPages}">
                    <c:choose>
                        <c:when test="${i == personPage.pageNumber}">
                            <span class="page-link page-current" aria-current="page">${i}</span>
                        </c:when>
                        <c:otherwise>
                            <c:url var="pageUrl" value="/persons">
                                <c:param name="page" value="${i}"/>
                                <c:param name="size" value="${personPage.pageSize}"/>
                            </c:url>
                            <a class="page-link" href="${pageUrl}" aria-label="Page ${i}">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <c:choose>
                    <c:when test="${personPage.last}">
                        <span class="page-arrow page-disabled" aria-hidden="true">&rarr;</span>
                    </c:when>
                    <c:otherwise>
                        <c:url var="nextUrl" value="/persons">
                            <c:param name="page" value="${personPage.pageNumber + 1}"/>
                            <c:param name="size" value="${personPage.pageSize}"/>
                        </c:url>
                        <a class="page-arrow" href="${nextUrl}" aria-label="Next page">&rarr;</a>
                    </c:otherwise>
                </c:choose>
            </nav>
        </c:if>
    </c:otherwise>
</c:choose>

<script src="<c:url value='/resources/js/person-list.js'/>"></script>

<%@ include file="../common/footer.jspf" %>
