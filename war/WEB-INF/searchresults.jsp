<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${(not (empty movie_title)) and (empty searchResults)}">
	<h2>No movies found :(</h2>
	<p>
		Try checking your spelling.
	</p>
</c:if>

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
					                <form method="post">
					                	<input type="hidden" name="movie_title" value="${movie_title}" />
					                	<input type="hidden" name="title" value="${movie.title}" />
					                	<input type="hidden" name="releaseDate" value="${movie.releaseDate}" />
					                	<input type="hidden" name="imgUrl" value="${movie.imgUrl}" />
					                	<input type="hidden" name="movieDBID" value="${movie.movieDBID}" />
					                	<input type="hidden" name="subscribed" value="false" />
									    <button type="button" value="Submit" class="btn btn-success btn-lg movie" style="width:200px" 
									    	data-loading-text="Loading..." data-complete-text="Subscribed!" autocomplete="off" >
									    		Subscribe
									    </button>
									</form>
								</c:when>
								<c:otherwise>
									    <a href="${loginUrl }" class="btn btn-success btn-lg" style="width:200px"> Subscribe </a>
								</c:otherwise>
							</c:choose>
			                </h3>
			            </c:when>
			            <c:otherwise>
			            	<h3>
			            	<form action="/" method="post">
					                	<input type="hidden" name="movie_title" value="${movie_title}" />
					                	<input type="hidden" name="title" value="${movie.title}" />
					                	<input type="hidden" name="releaseDate" value="${movie.releaseDate}" />
					                	<input type="hidden" name="imgUrl" value="${movie.imgUrl}" />
					                	<input type="hidden" name="movieDBID" value="${movie.movieDBID}" />
					                	<input type="hidden" name="subscribed" value="true" />
			                			<button type="button" value="Submit" class="btn btn-danger btn-lg movie" style="width:200px" 
									    	data-loading-text="Loading..." data-complete-text="Unsubscribed!" autocomplete="off" >
									    		Unsubscribe
									    </button>
									</form>
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