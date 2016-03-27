<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<html lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Lighthouse</title>

    <!-- Bootstrap Core CSS -->
    <link href="css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="css/3-col-portfolio.css" rel="stylesheet">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

</head>

<body>

    <!-- Navigation -->
    <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
        <div class="container">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="/">Lighthouse</a>
            </div>
            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav">
                        <c:choose>
							<c:when test="${user != null}">
							<li>
								<a href="#">Welcome, ${user.email}!</a>
							</li>
							<li>
								<a href="${logoutUrl}">Sign Out</a>.
							</li>
							</c:when>
							<c:otherwise>
								<li>
									<a href="${loginUrl }">Google Sign in</a>
								</li>
							</c:otherwise>
						</c:choose>
                    
                </ul>
            </div>
            <!-- /.navbar-collapse -->
        </div>
        <!-- /.container -->
    </nav>

    <!-- Page Content -->
    <div class="container">

        <!-- Page Header -->
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header">Search Movies
		            <div id="imaginary_container"> 
		                <div class="input-group stylish-input-group">
		                	<form class="navbar-form" action="/" method="get">
			                    <input type="text" class="form-control"  placeholder="Movie title" name="movie_title" id="movie_title" type="text" value="${movie_title}">
			                    <span class="input-group-addon">
			                        <button type="submit">
			                            <span class="glyphicon glyphicon-search"></span>
			                        </button>  
			                    </span>
		                    </form>
		                </div>
		            </div>
                </h1>
            </div>
        </div>
        <!-- /.row -->

	<c:forEach items="${searchResults}" var="movie" varStatus="loop">
	        <c:if test="${loop.first or loop.index % 3 == 0}"> 
	        <!-- Projects Row -->
	        <div class="row">
	        </c:if>
	            <div class="col-md-4 portfolio-item">
	                <img src="${movie.imgUrl}" style="height:300px;">

	                <h2>
	                    ${movie.title}
	                </h2>
	                <h3>
	                	<fmt:formatDate type="date" value="${movie.releaseDate}" />
	                </h3>
	                
	                
	                
	      <!--  if release date is after current date -->
	         <jsp:useBean id="now" class="java.util.Date"/>
		         <c:choose>
					<c:when test="${movie.releaseDate gt now}">
						<c:choose>
							<c:when test="${not movie.subscribed}">
				                <h3>
				                <c:choose>
					                <c:when test="${user != null}">
						                <form action="/" method="post">
						                	<input type="hidden" name="title" value="${movie.title}" />
						                	<input type="hidden" name="releaseDate" value="${movie.releaseDate}" />
						                	<input type="hidden" name="imgUrl" value="${movie.imgUrl}" />
										    <button type="submit" value="Submit">Subscribe</button>
										</form>
									</c:when>
									<c:otherwise>
										<form action="${loginUrl}">
										    <input type="submit" value="Subscribe">
										</form>
									</c:otherwise>
								</c:choose>
				                </h3>
				            </c:when>
				            <c:otherwise>
				            	<h3>
				                <a href="#">Unsubscribe</a>
				                </h3>
				            </c:otherwise>
				        </c:choose>
			         </c:when>
			         <c:otherwise>
			         	<h3>
			         	Already Released!
		                </h3>
			         </c:otherwise>
			     </c:choose>
	            </div>
	        <c:if test="${loop.last or (loop.index + 1) % 3 == 0}"> 
	        </div>
	        <!-- /.end row -->
	        </c:if>
	        
	</c:forEach>
        
        <hr>

        <!-- Footer -->
        <footer>
            <div class="row">
                <div class="col-lg-12">
                    <p>Copyright &copy; Lighthouse 2016</p>
                </div>
            </div>
            <!-- /.row -->
        </footer>

    </div>
    <!-- /.container -->

    <!-- jQuery -->
    <script src="js/jquery.js"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="js/bootstrap.min.js"></script>

</body>

</html>
