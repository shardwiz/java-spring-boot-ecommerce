<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ page isELIgnored="false"%>

<!-- 	navigation Bar -->
<%@ include file="navbar.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Product Management</title>
<link rel="icon" type="image/x-icon"
	href="<c:url value="/resource/images/favicon1.png"/>" />
<link rel="stylesheet"
	href="<c:url value="../resource/bootstrap/css/bootstrap.min.css"/>">
<script src="<c:url value="../resource/js/jquery.js"/>"></script>
<script src="<c:url value="../resource/bootstrap/js/bootstrap.min.js"/>"></script>
<link rel="stylesheet" type="text/css"
	href="<c:url value="../resource/css/ProductList.css"/>">
<script
	src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.8/angular.min.js"></script>
<script src="<c:url value="../resource/js/productController.js"/>"></script>
</head>
<body ng-app="myapp">
	<div class="container" id="productTable"
		style="width: 1145px; margin-bottom: 180px;">
		<h2>Product Management</h2>
		<p>The List of Products in our Database</p>
		
		<!-- Search and Filter Form -->
		<div class="panel panel-default" style="margin-bottom: 20px;">
			<div class="panel-heading">
				<h4 class="panel-title">
					<span class="glyphicon glyphicon-search"></span> Search & Filter Products
				</h4>
			</div>
			<div class="panel-body">
				<form action="<c:url value='/searchProducts'/>" method="GET" class="form-horizontal">
					<div class="row">
						<div class="col-sm-4">
							<div class="form-group">
								<label for="searchTerm" class="control-label">Search by Name:</label>
								<input type="text" class="form-control" id="searchTerm" name="searchTerm" 
									placeholder="Enter product name..." value="${searchTerm}">
							</div>
						</div>
						<div class="col-sm-3">
							<div class="form-group">
								<label for="category" class="control-label">Category:</label>
								<select class="form-control" id="category" name="category">
									<option value="All" ${selectedCategory == 'All' ? 'selected' : ''}>All Categories</option>
									<option value="Android" ${selectedCategory == 'Android' ? 'selected' : ''}>Android</option>
									<option value="Windows" ${selectedCategory == 'Windows' ? 'selected' : ''}>Windows</option>
									<option value="Linux" ${selectedCategory == 'Linux' ? 'selected' : ''}>Linux</option>
									<option value="Mac" ${selectedCategory == 'Mac' ? 'selected' : ''}>Mac</option>
								</select>
							</div>
						</div>
						<div class="col-sm-2">
							<div class="form-group">
								<label for="minPrice" class="control-label">Min Price:</label>
								<input type="number" class="form-control" id="minPrice" name="minPrice" 
									placeholder="Min" min="0" step="0.01" value="${minPrice}">
							</div>
						</div>
						<div class="col-sm-2">
							<div class="form-group">
								<label for="maxPrice" class="control-label">Max Price:</label>
								<input type="number" class="form-control" id="maxPrice" name="maxPrice" 
									placeholder="Max" min="0" step="0.01" value="${maxPrice}">
							</div>
						</div>
						<div class="col-sm-1">
							<div class="form-group">
								<label class="control-label">&nbsp;</label>
								<button type="submit" class="btn btn-primary btn-block">
									<span class="glyphicon glyphicon-search"></span> Search
								</button>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-12">
							<a href="<c:url value='/getAllProducts'/>" class="btn btn-default">
								<span class="glyphicon glyphicon-refresh"></span> Clear Filters
							</a>
							<c:if test="${not empty searchTerm or (not empty selectedCategory and selectedCategory != 'All') or not empty minPrice or not empty maxPrice}">
								<span class="label label-info" style="margin-left: 10px;">
									Found ${fn:length(products)} product(s)
								</span>
							</c:if>
						</div>
					</div>
				</form>
			</div>
		</div>
		
		<table class="table table-hover" id="productList">
			<thead>
				<tr>
					<th>Screen-Shot</th>
					<th>Product Id</th>
					<th>Category</th>
					<th>Product Name</th>
					<th>Products Price</th>
					<th>Stock Unit</th>
					<th>Description</th>
					<th>Manufacturer</th>
					<th>View <security:authorize access="hasAnyRole('ROLE_USER')">
					/ Add to Cart
					</security:authorize> <!-- 					views only to the admin --> <security:authorize
							 access="hasAnyRole('ROLE_ADMIN')">
					/Edit/Delete
					</security:authorize>
					</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${products}" var="prod">
					<tr>
						<td style="width: 171px"><img
							src="<c:url value="/resource/images/products/${prod.productId}.jpg"/>"
							style="width: 100px; height: 90px;" ${prod.productName}"  /></td>
						<td>${prod.productId}</td>
						<td>${prod.productCategory}</td>
						<td>${prod.productName}</td>
						<td>${prod.productPrice}</td>
						<td>${prod.unitStock}</td>
						<td style="width: 180px">${prod.productDescription}</td>
						<td>${prod.productManufacturer}</td>
						<td ng-controller="myController"><a
							href="getProductById/${prod.productId}" class="btn btn-info"
							role="button"> <span class="glyphicon glyphicon-info-sign"></span></a>

							<!-- 						view only for user --> <security:authorize
								 access="hasAnyRole('ROLE_USER')">
								<a href="#" ng-click="addToCart(${prod.productId})"
									class="btn btn-primary" style="margin-left: 5px"> <span
									class="glyphicon glyphicon-shopping-cart"></span></a>
							</security:authorize> <!-- 						view only to the admin --> <security:authorize
								access="hasAnyRole('ROLE_ADMIN')">
								<a href="admin/product/editProduct/${prod.productId}"
									class="btn btn-success" style="margin-left: 5px"> <span
									class="glyphicon glyphicon-edit"></span></a>
								<a href="admin/delete/${prod.productId}" class="btn btn-danger"
									style="margin-left: 5px"> <span
									class="glyphicon glyphicon-trash"></span></a>
							</security:authorize></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</body>
</html>
<%@ include file="footer.jsp"%>