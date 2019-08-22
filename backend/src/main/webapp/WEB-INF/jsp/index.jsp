<!DOCTYPE HTML>
<html>
	<head>
		<meta charset="UTF-8" />
		<title>Welcome</title>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css"/>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/bootstrap.min.css"/>
	</head>
	<body>
		<header class="header">
			 
			   <nav class="navbar navbar-dark bg-dark">
					<a class="navbar-brand" href="#">
		    			<img src="${pageContext.request.contextPath}/img/logo.png" width="30" height="30" alt="logo">    		
						System of content retrieval
					</a>
		  		</nav>
		  	
		</header>
		<section class="query_form">
      		<div class = "container ">
      			<div class="row justify-content-center">
      				<div class="col-lg-7">
	      				<form action="/" method="get">
							<div class="form-row">
								<div class = "col-10">
									<input type="text" class="form-control" id="query" placeholder="write 2 or more words" name="query">
		      					</div>
		      					<div class = "col-2">
		      					<button type="submit"class="btn btn-dark">Search</button>
		      					</div>
	      					</div>
	      				</form>
	      			</div>
	      			<div class="col-3 ml-auto">
	      				<div class="alert alert-success" role="alert">
	      					<p class="info">Total documents: 424</p>
	      					<p class="info">Indexed documents: 424</p>
	      				</div>
	      				
	      			</div>
      			</div>      
         	</div>
         </section> 
   </body>
   
</html>