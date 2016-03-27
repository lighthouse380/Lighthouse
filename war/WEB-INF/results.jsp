<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
	<head>
		<title>The Time Is...</title>
		
	<!-- Bootstrap Core CSS - Uses Bootswatch Flatly Theme: http://bootswatch.com/flatly/ -->
    <link href="css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="css/freelancer.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic" rel="stylesheet" type="text/css">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

	</head>
	<body>
	
		<c:choose>
			<c:when test="${user != null}">
				<p>
					Welcome, ${user.email}!
					You can <a href="${logoutUrl}">sign out</a>.
				</p>
			</c:when>
			<c:otherwise>
				<p>
					Welcome!
					<a href="${loginUrl }">Sign in or register</a> to customize.
				</p>
			</c:otherwise>
		</c:choose>
		
		<c:if test="${user != null}">

			<form action="/search" method="post">
				<label for="movie_title">
					Search Movie Title:
				</label>
				<input name="movie_title" id="movie_title" type="text" size="10" value="${movie_title}" />
				<input type="submit" value="Search"/>
			</form>
			
			<p>
			<table border="1">
			  <tr>
			    <th>  Movie  </th>
			    <th>  Release Date  </th>
			    <th>  Subscribe  </th>
			  </tr>
			  <c:forEach items="${searchResults}" var="movie">
			    <tr>
			      <td>${fn:escapeXml(movie.title)}</td>
			      <td>${fn:escapeXml(movie.releaseDate)}</td>
			    </tr>
			  </c:forEach>
			</p>
		</c:if>
	</body>
</html>