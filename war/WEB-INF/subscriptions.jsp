<!-- 
 Name: 		    	subscription.jsp
 Author:			Khajag Basmajian and Sevan Gregorian
 Date Created:  	04-13-2016
 Purpose:			Page for list of subscriptions, servers for viewing a list 
 					of subscribed movies.
 -->

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
								<a href="#" style="color:#FFF;">Welcome, ${user.email}!</a>
							</li>
							<li>
								<a href="${logoutUrl}" style="color:#FFF;">Sign Out</a>.
							</li>
							</c:when>
							<c:otherwise>
								<li>
									<a href="${loginUrl }"><img src="img/btn_google_signin_light_normal_web.png" alt="Google Signin"></a>
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
                <h1 class="page-header">My Subscriptions 
                	<h4>Here is a list of movies you are currently subscribed to!</h4>
                </h1>
            </div>
        </div>
        <!-- /.row -->

	<c:forEach items="${subscribedMovies}" var="movie" varStatus="loop">
	        <!-- Projects Row -->
	        <div class="row">
	            <div class="col-md-4 portfolio-item">
	                <img src="${movie.imgUrl}" style="height:200px;">
	            	<!--  if release date is after current date -->
					<jsp:useBean id="now" class="java.util.Date"/>
					<c:choose>
						<c:when test="${movie.releaseDate gt now}">
							<h3>
							<form action="/subscriptions" method="post">
									<input type="hidden" name="title" value="${movie.title}" />
									<input type="hidden" name="releaseDate" value="${movie.releaseDate}" />
									<input type="hidden" name="imgUrl" value="${movie.imgUrl}" />
									<input type="hidden" name="subscribed" value="true" />
									<button type="submit" value="Submit" class="btn btn-danger btn-lg" style="width:150px">Unsubscribe</button>
							</form>
							</h3>
						</c:when>
						<c:otherwise>
							<h3>
							Already Released!
							</h3>
						</c:otherwise>
					</c:choose>
	            </div>    
	   
				<div class="col-md-10 col-sm-9">
				  <h3>${movie.title}</h3>
				  <div class="row">
					<div class="col-xs-9">
					  <h4>
					  <fmt:formatDate type="date" value="${movie.releaseDate}"/>
					  </h4>	
					</div>
				  </div>
				  <br><br>
				</div>
	        </div>
	        <!-- /.end row -->
	        
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
