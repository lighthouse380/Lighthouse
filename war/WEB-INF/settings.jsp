<!-- 
 Name: 		    	subscription.jsp
 Author:			Khajag Basmajian
 Date Created:  	04-17-2016
 Purpose:			Page for account settings
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
                <h1 class="page-header">Account Settings</h1>
            </div>
        </div>  
		
		<div class="col-md-4 portfolio-item">	               
			
			<h3>
			<form action="/accountsettings" method="post">
				<button type="submit" value="Submit" class="btn btn-danger btn-lg" style="width:250px">Delete Account</button>
			</form>
			</h3>	
			
			<!-- Possible JQuery integration. Present to team. 
			<a href="#popupDialog" data-rel="popup" data-position-to="window" data-transition="pop" 
			class="ui-btn ui-corner-all ui-shadow ui-btn-inline ui-icon-delete ui-btn-icon-left ui-btn-b" style="width:250px">Delete Account</a>
			
			<div data-role="popup" id="popupDialog" data-overlay-theme="b" data-theme="b" data-dismissible="false" style="max-width:400px;">
			<div data-role="header" data-theme="a">
			<h1>Delete Account?</h1>
			</div>
			<div role="main" class="ui-content">
				<a href="#" class="ui-btn ui-corner-all ui-shadow ui-btn-inline ui-btn-b" data-rel="back">Cancel</a>
				<button type="submit" value="Submit" class="btn btn-danger btn-lg" style="width:100px">Delete</button>
			</div>
			</div>
			-->
			
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
