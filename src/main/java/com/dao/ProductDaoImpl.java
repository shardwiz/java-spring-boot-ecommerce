package com.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.model.Product;

@Repository(value = "productDao")
public class ProductDaoImpl implements ProductDao {

	// this class is wired with the sessionFactory to do some operation in the
	// database

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
		// Reading the records from the table
		Session session = sessionFactory.openSession();
		// List<Product> products = session.createQuery("from Product").list();
		List<Product> products = session.createCriteria(Product.class).list();
		System.out.println("----- List of Products-----");
		System.out.println(products);
		// session.flush is used for clear cache in the session
		session.flush();
		// it will close the particular session after completing the process
		session.close();
		return products;
	}

	public Product getProductById(String productId) {

		// Reading the records from the table
		Session session = sessionFactory.openSession();
		// select * from Product where isbn=i
		Product product = (Product) session.get(Product.class, productId);
		session.close();
		return product;
	}

	public void deleteProduct(String productId) {
		Session session = sessionFactory.openSession();
		Product product = (Product) session.get(Product.class, productId);
		session.delete(product);
		session.flush();
		session.close();// close the session
	}

	public void addProduct(Product product) {
		Session session = sessionFactory.openSession();
		session.save(product);
		session.close();
	}

	public void editProduct(Product product) {
		Session session = sessionFactory.openSession();
		session.update(product);
		session.flush();
		session.close();
	}

	@Override
	public List<Product> searchProductsByName(String searchTerm) {
		Session session = sessionFactory.openSession();
		Criteria criteria = session.createCriteria(Product.class);
		criteria.add(Restrictions.ilike("productName", "%" + searchTerm + "%"));
		@SuppressWarnings("unchecked")
		List<Product> products = criteria.list();
		session.close();
		return products;
	}

	@Override
	public List<Product> searchProductsByCategory(String category) {
		Session session = sessionFactory.openSession();
		Criteria criteria = session.createCriteria(Product.class);
		criteria.add(Restrictions.eq("productCategory", category));
		@SuppressWarnings("unchecked")
		List<Product> products = criteria.list();
		session.close();
		return products;
	}

	@Override
	public List<Product> searchProductsByPriceRange(double minPrice, double maxPrice) {
		Session session = sessionFactory.openSession();
		Criteria criteria = session.createCriteria(Product.class);
		criteria.add(Restrictions.between("productPrice", minPrice, maxPrice));
		@SuppressWarnings("unchecked")
		List<Product> products = criteria.list();
		session.close();
		return products;
	}

	@Override
	public List<Product> searchProducts(String searchTerm, String category, Double minPrice, Double maxPrice) {
		Session session = sessionFactory.openSession();
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
		
		@SuppressWarnings("unchecked")
		List<Product> products = criteria.list();
		session.close();
		return products;
	}

}
