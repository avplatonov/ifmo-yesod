<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
	<head>
		<meta charset="utf-8" />
		<title>YESOD</title>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css"/>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/bootstrap.min.css"/>
	</head>
	<body>
		<header class="header">
			 
			   <nav class="navbar navbar-dark bg-dark">
					<a class="navbar-brand" href="/">
		    			<img src="${pageContext.request.contextPath}/img/logo.png" width="30" height="30" alt="logo">    		
						System of document searching
					</a>
		  		</nav>
		  	
		</header>
		<section class="query_form">
      		<div class = "container">
      			<div class="row justify-content-center">
      				<div class="col-lg-7">
	      				<form action="/search" method="get">
							<div class="form-row">
								<div class = "col-10">
									<input type="text" class="form-control" id="query" placeholder="write 2 or more words" name="query" value="${query}">
		      					</div>
		      					<div class = "col-1">
		      						<button type="submit"class="btn btn-dark">Search</button>
	      						</div>
	      						<div class="col-8 d-flex">
	      							<div class="p-2"><a href="/search?query=${query}&sort=0">Bell sort </a></div>
	      							<div class="p-2"><a href="/search?query=${query}&sort=1">Concurrence sort </a></div>
	      							<div class="p-2"><a href="/search?query=${query}&sort=2">TfIdf sort</a></div>
	      							<div class="p-2"><a href="#">users Marks</a></div>
	      						</div>
	      					</div>	
	      				</form>
	      			</div>
	      			<div class="col-2 d-flex justify-content-end">
	      				<div class="alert alert-success" role="alert">
	      					<p class="info">Total documents: 424 I ndexed documents: 424</p>
	      				</div>
	      				
	      			</div>
      			</div>
      		<section>
	      		<div class="container">
	      			<div class="row">
	      				<div class="col-lg-10 col-sm-10">
							<table class="table table-striped">
							  <thead>
							    <tr>
							      <th scope="col">Result</th>
							      <th scope="col">Bell</th>
							      <th scope="col">Concurrence</th>
							      <th scope="col">Tf-Idf</th>
							      <th scope="col">Users Mark</th>
							    </tr>
							  </thead>
							   <tbody>
							  <c:forEach  items="${results}" var ="result">
							  	<tr>
							      <td><h5>
										<a href="/search?viewId=${result.viewId}&query=${query}">${result.name}</a>
									</h5>
							      </td>
							      <td>${result.pointsBell}</td>
							      <td>${result.pontsConcurrence}</td>
							      <td>${result.pointsTfIdf}</td>
							      <td><input class="form-control form-control-sm" type="text"></td>
							    </tr>
							  </c:forEach>
							    </tbody>
								</table>
	      				</div>
	      			</div>
	      		</div>
	      	</section>	      
         	</div>
         </section> 
   </body>
   
</html>