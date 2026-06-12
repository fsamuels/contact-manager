<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Contact Manager &mdash; Delete Person</title>
    <link rel="stylesheet" href="<c:url value='/resources/css/style.css'/>">
</head>
<body>
<div class="container">
    <h1>Delete Person</h1>

    <p class="confirm">
        You are about to delete the person:
        <strong><c:out value="${person.firstName}"/> <c:out value="${person.lastName}"/></strong>,
        are you sure?
    </p>

    <div class="actions">
        <form action="<c:url value='/people/${person.id}/delete'/>" method="post" class="inline">
            <button type="submit" class="button danger">Delete</button>
        </form>
        <a class="button secondary" href="<c:url value='/people'/>">Cancel</a>
    </div>
</div>
</body>
</html>
