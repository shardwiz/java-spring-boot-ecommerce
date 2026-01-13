package com.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dao.ProductDao;
import com.model.Product;

@Service(value="productService")
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductDao productDao;

	public ProductDao getProductDao() {
		return productDao;
	}

	public void setProductDao(ProductDao productDao) {
		this.productDao = productDao;
	}

	@Transactional
	public List<Product> getAllProducts() {
		return productDao.getAllProducts();
	}

	
	public Product getProductById(String productId) {
		return productDao.getProductById(productId);
	}

	
	public void deleteProduct(String productId) {
		productDao.deleteProduct(productId);
	}
	
	public void addProduct(Product product){
		productDao.addProduct(product);
	}
	
	public void editProduct(Product product){
		productDao.editProduct(product);
	}

	@Transactional
	public List<Product> searchProductsByName(String searchTerm) {
		return productDao.searchProductsByName(searchTerm);
	}

	@Transactional
	public List<Product> searchProductsByCategory(String category) {
		return productDao.searchProductsByCategory(category);
	}

	@Transactional
	public List<Product> searchProductsByPriceRange(double minPrice, double maxPrice) {
		return productDao.searchProductsByPriceRange(minPrice, maxPrice);
	}

	@Transactional
	public List<Product> searchProducts(String searchTerm, String category, Double minPrice, Double maxPrice) {
		return productDao.searchProducts(searchTerm, category, minPrice, maxPrice);
	}

}
