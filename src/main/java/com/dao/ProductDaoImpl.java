package com.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.model.Product;

@Repository(value = "productDao")
public class ProductDaoImpl implements ProductDao {

	private static final Logger logger = LoggerFactory.getLogger(ProductDaoImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	// this will create one sessionFactory for this class
	// there is only one sessionFactory should be created for the applications
	// we can create multiple sessions for a sessionFactory
	// each session can do some functions

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public List<Product> getAllProducts() {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			@SuppressWarnings("unchecked")
			List<Product> products = session.createCriteria(Product.class).list();
			logger.debug("Retrieved {} products from database", products.size());
			return products;
		} catch (Exception e) {
			logger.error("Error retrieving all products", e);
			return new ArrayList<>();
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public Product getProductById(String productId) {
		if (productId == null || productId.trim().isEmpty()) {
			logger.warn("Attempted to get product with null or empty productId");
			return null;
		}
		
		Session session = null;
		try {
			session = sessionFactory.openSession();
			Product product = (Product) session.get(Product.class, productId);
			if (product == null) {
				logger.debug("Product not found with id: {}", productId);
			}
			return product;
		} catch (Exception e) {
			logger.error("Error retrieving product with id: {}", productId, e);
			return null;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public void deleteProduct(String productId) {
		if (productId == null || productId.trim().isEmpty()) {
			logger.warn("Attempted to delete product with null or empty productId");
			return;
		}
		
		Session session = null;
		try {
			session = sessionFactory.openSession();
			Product product = (Product) session.get(Product.class, productId);
			if (product != null) {
				session.delete(product);
				session.flush();
				logger.info("Product deleted successfully with id: {}", productId);
			} else {
				logger.warn("Product not found for deletion with id: {}", productId);
			}
		} catch (Exception e) {
			logger.error("Error deleting product with id: {}", productId, e);
			throw new RuntimeException("Failed to delete product", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public void addProduct(Product product) {
		if (product == null) {
			logger.warn("Attempted to add null product");
			throw new IllegalArgumentException("Product cannot be null");
		}
		
		Session session = null;
		try {
			session = sessionFactory.openSession();
			session.save(product);
			session.flush();
			logger.info("Product added successfully with id: {}", product.getProductId());
		} catch (Exception e) {
			logger.error("Error adding product", e);
			throw new RuntimeException("Failed to add product", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public void editProduct(Product product) {
		if (product == null) {
			logger.warn("Attempted to edit null product");
			throw new IllegalArgumentException("Product cannot be null");
		}
		
		Session session = null;
		try {
			session = sessionFactory.openSession();
			session.update(product);
			session.flush();
			logger.info("Product updated successfully with id: {}", product.getProductId());
		} catch (Exception e) {
			logger.error("Error updating product with id: {}", product.getProductId(), e);
			throw new RuntimeException("Failed to update product", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	@Override
	public List<Product> searchProductsByName(String searchTerm) {
		if (searchTerm == null || searchTerm.trim().isEmpty()) {
			logger.debug("Empty search term provided, returning all products");
			return getAllProducts();
		}
		
		Session session = null;
		try {
			session = sessionFactory.openSession();
			Criteria criteria = session.createCriteria(Product.class);
			criteria.add(Restrictions.ilike("productName", "%" + searchTerm + "%"));
			@SuppressWarnings("unchecked")
			List<Product> products = criteria.list();
			logger.debug("Found {} products matching search term: {}", products.size(), searchTerm);
			return products;
		} catch (Exception e) {
			logger.error("Error searching products by name with term: {}", searchTerm, e);
			return new ArrayList<>();
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	@Override
	public List<Product> searchProductsByCategory(String category) {
		if (category == null || category.trim().isEmpty()) {
			logger.debug("Empty category provided, returning all products");
			return getAllProducts();
		}
		
		Session session = null;
		try {
			session = sessionFactory.openSession();
			Criteria criteria = session.createCriteria(Product.class);
			criteria.add(Restrictions.eq("productCategory", category));
			@SuppressWarnings("unchecked")
			List<Product> products = criteria.list();
			logger.debug("Found {} products in category: {}", products.size(), category);
			return products;
		} catch (Exception e) {
			logger.error("Error searching products by category: {}", category, e);
			return new ArrayList<>();
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	@Override
	public List<Product> searchProductsByPriceRange(double minPrice, double maxPrice) {
		if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
			logger.warn("Invalid price range: min={}, max={}", minPrice, maxPrice);
			return new ArrayList<>();
		}
		
		Session session = null;
		try {
			session = sessionFactory.openSession();
			Criteria criteria = session.createCriteria(Product.class);
			criteria.add(Restrictions.between("productPrice", minPrice, maxPrice));
			@SuppressWarnings("unchecked")
			List<Product> products = criteria.list();
			logger.debug("Found {} products in price range: {} - {}", products.size(), minPrice, maxPrice);
			return products;
		} catch (Exception e) {
			logger.error("Error searching products by price range: {} - {}", minPrice, maxPrice, e);
			return new ArrayList<>();
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	@Override
	public List<Product> searchProducts(String searchTerm, String category, Double minPrice, Double maxPrice) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			Criteria criteria = session.createCriteria(Product.class);
			
			if (searchTerm != null && !searchTerm.trim().isEmpty()) {
				criteria.add(Restrictions.ilike("productName", "%" + searchTerm + "%"));
			}
			
			if (category != null && !category.trim().isEmpty() && !category.equals("All")) {
				criteria.add(Restrictions.eq("productCategory", category));
			}
			
			if (minPrice != null && minPrice > 0) {
				criteria.add(Restrictions.ge("productPrice", minPrice));
			}
			
			if (maxPrice != null && maxPrice > 0) {
				criteria.add(Restrictions.le("productPrice", maxPrice));
			}
			
			// Validate price range if both are provided
			if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
				logger.warn("Invalid price range: min={}, max={}", minPrice, maxPrice);
				return new ArrayList<>();
			}
			
			@SuppressWarnings("unchecked")
			List<Product> products = criteria.list();
			logger.debug("Found {} products matching search criteria", products.size());
			return products;
		} catch (Exception e) {
			logger.error("Error searching products with criteria", e);
			return new ArrayList<>();
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

}
