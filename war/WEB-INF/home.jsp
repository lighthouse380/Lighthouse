<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html lang="en">

<!-- 
 Name: 		    	home.jsp
 Author:			Harout Grigoryan
 Date Created:  	03-14-2016
 Purpose:			Homepage for Lighthouse, serves for subscribing/unsubscribing,
 					Logging in/out, and searching movies.
 -->
 
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
    
    
	<link rel="stylesheet" href="http://netdna.bootstrapcdn.com/bootstrap/3.1.0/css/bootstrap-theme.min.css">
	<script src="http://code.jquery.com/jquery.min.js"></script>
	<script src="http://netdna.bootstrapcdn.com/bootstrap/3.1.0/js/bootstrap.min.js"></script>
    
    <script type="text/javascript">
    function validateForm() {
    	  var x = document.forms["search-form"]["movie_title"].value;
    	  if (x == null || x == "") {
    	    alert("Please enter a movie title!");
    	    return false;
    	  }
    	}

    	var movieFunc = function(e) {
    	  e.preventDefault();
    	  var button = $(this);
    	  var form = $(this.form);
    	  var form_data = form.serialize();
    	  var form_method = form.attr("method").toUpperCase();
    	  if (form_method != 'POST') {
    	    return false;
    	  }

    	  console.log(button);
    	  $.ajax({
    	    url: "/",
    	    type: form_method,
    	    data: form_data,
    	    success: function() {
    	      button.button('complete');
    	      console.log('end of subscribe...\n');
    	    }
    	  });
    	};

    	  $(document).ready(function() {

    	    $("form[ajax=true]").submit(function(e) {
    	      e.preventDefault();
    	      var form_data = $(this).serialize();
    	      var form_url = $(this).attr("action");
    	      var form_method = $(this).attr("method").toUpperCase();

    	      console.log("search starting\n");
    	      $.ajax({
    	        type: form_method,
    	        url: form_url,
    	        data: form_data,
    	        dataType: "html",
    	        success: function(results) {
    	          $("#searchResults").html(results);
    	          $(".movie").click(movieFunc);
    	          return false;
    	        },
    	        error: console.log("error")
    	      });
    	    });
    	  });
    </script>

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
								<a href="/accountsettings" style="color:#FFF;">${user.email}</a>
							</li>
							<li>
								<a href="/subscriptions" style="color:#FFF;">My Subscriptions</a>
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
                <h1 class="page-header">Search Movies
                	<h4>Subscribe to the movies you want to be reminded about before they're out!</h4>
		            <div id="imaginary_container">
							<div class="input-group stylish-input-group">
			                	<form method="get" action="/searchresults" ajax="true">
				                    <input type="text" class="form-control"  placeholder="Movie title" name="movie_title" id="movie_title" type="text" value="${movie_title}">
				                    <span class="input-group-addon">
				                        <button type="button" value="Submit">
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
<div id="searchResults">

</div>
        
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
