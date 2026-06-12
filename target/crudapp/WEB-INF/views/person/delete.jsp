<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Delete Person - Contact Manager</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/app.css">
</head>
<body>
<div class="container">
    <h1>Delete Person</h1>

    <div class="confirm-box">
        <p>You are about to delete the person:
            <strong><c:out value="${person.firstName}"/> <c:out value="${person.lastName}"/></strong>,
            are you sure?</p>
    </div>

    <form method="post"
          action="${pageContext.request.contextPath}/person/delete/${person.personId}">
        <button type="submit" name="action" value="delete" class="btn btn-danger">Delete</button>
        &nbsp;
        <button type="submit" name="action" value="cancel" class="btn btn-secondary">Cancel</button>
    </form>
</div>
</body>
</html>
