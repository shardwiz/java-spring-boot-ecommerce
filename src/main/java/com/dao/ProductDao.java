package com.dao;

import java.util.List;

import com.model.Product;

public interface ProductDao {

	List<Product> getAllProducts();

	Product getProductById(String productId);

	void deleteProduct(String productId);

	void addProduct(Product product);
	
	void editProduct(Product product);
	
	// Search methods
	List<Product> searchProductsByName(String searchTerm);
	
	List<Product> searchProductsByCategory(String category);
	
	List<Product> searchProductsByPriceRange(double minPrice, double maxPrice);
	
	List<Product> searchProducts(String searchTerm, String category, Double minPrice, Double maxPrice);
	
}
