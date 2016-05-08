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

    <!-- Custom CSS -->
    <link href="css/3-col-portfolio.css" rel="stylesheet">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    
    	<link href="css/bootstrap.min.css" rel="stylesheet">

	<!--link rel="stylesheet" href="http://netdna.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">-->
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
    	      setTimeout(function() { button.button('complete'); }, 200);
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
    	  
    	    
    	    
    	    var activeSystemClass = $('.list-group-item.active');
    	    //something is entered in search form
    	    $('#system-search').keyup( function() {
    	       var that = this;
    	        // affect all table rows on in systems table
    	        var tableBody = $('.table-list-search tbody');
    	        var tableRowsClass = $('.table-list-search tbody tr');
    	        $('.search-sf').remove();
    	        tableRowsClass.each( function(i, val) {
    	        
    	            //Lower text for case insensitive
    	            var rowText = $(val).text().toLowerCase();
    	            var inputText = $(that).val().toLowerCase();
    	            if(inputText != '')
    	            {
    	                $('.search-query-sf').remove();
    	                tableBody.prepend('<tr class="search-query-sf"><td colspan="6"><strong>Searching for: "'
    	                    + $(that).val()
    	                    + '"</strong></td></tr>');
    	            }
    	            else
    	            {
    	                $('.search-query-sf').remove();
    	            }

    	            if( rowText.indexOf( inputText ) == -1 )
    	            {
    	                //hide rows
    	                tableRowsClass.eq(i).hide();
    	                
    	            }
    	            else
    	            {
    	                $('.search-sf').remove();
    	                tableRowsClass.eq(i).show();
    	            }
    	        });
    	        //all tr elements are hidden
    	        if(tableRowsClass.children(':visible').length == 0)
    	        {
    	            tableBody.append('<tr class="search-sf"><td class="text-muted" colspan="6">No entries found.</td></tr>');
    	        }
    	    });
    	    
    	  });
    </script>
    
    <style>
	.navbar-brand {
		font-size: 38px;
	}
	</style>

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
                	<h4>Subscribe to the movies you want to be reminded about before they're out!<br>
                		When you subscribe we'll send you an email reminder 1 Month, 1 Week, and 1 Day before the release.</h4>
							<div class="row">
			                	<form method="get" action="/searchresults" ajax="true">
				                    <div class="input-group">
					                    <input class="form-control" id="system-search" name="movie_title" placeholder="Movie title" type="text"  value="${movie_title}" required>
					                    <span class="input-group-btn">
					                        <button type="submit" class="btn btn-default" style="height:34">
					                        	<i class="glyphicon glyphicon-search"></i>
					                        </button>
					                    </span>
					                </div>
			                    </form>
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
