package com.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.model.Authorities;
import com.model.Cart;
import com.model.Customer;
import com.model.User;

@Repository
public class CustomerDaoImpl implements CustomerDao {

	private static final Logger logger = LoggerFactory.getLogger(CustomerDaoImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void addCustomer(Customer customer) {
		if (customer == null) {
			logger.warn("Attempted to add null customer");
			throw new IllegalArgumentException("Customer cannot be null");
		}
		
		if (customer.getUsers() == null) {
			logger.warn("Attempted to add customer with null user");
			throw new IllegalArgumentException("Customer must have a user");
		}
		
		Session session = null;
		try {
			session = sessionFactory.openSession();
			customer.getUsers().setEnabled(true);
			
			Authorities authorities = new Authorities();
			authorities.setAuthorities("ROLE_USER");
			authorities.setEmailId(customer.getUsers().getEmailId());
			
			Cart cart = new Cart();
			customer.setCart(cart);
			cart.setCustomer(customer);
			
			session.save(customer);
			session.save(authorities);
			session.flush();
			logger.info("Customer added successfully with email: {}", customer.getUsers().getEmailId());
		} catch (Exception e) {
			logger.error("Error adding customer", e);
			throw new RuntimeException("Failed to add customer", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public List<Customer> getAllCustomers() {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			@SuppressWarnings("unchecked")
			List<Customer> customerList = session.createQuery("from Customer").list();
			logger.debug("Retrieved {} customers from database", customerList.size());
			return customerList;
		} catch (Exception e) {
			logger.error("Error retrieving all customers", e);
			return new ArrayList<>();
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public Customer getCustomerByemailId(String emailId) {
		if (emailId == null || emailId.trim().isEmpty()) {
			logger.warn("Attempted to get customer with null or empty emailId");
			return null;
		}
		
		Session session = null;
		try {
			session = sessionFactory.openSession();
			Query query = session.createQuery("from User where emailId=?");
			query.setString(0, emailId);
			User users = (User) query.uniqueResult();
			
			if (users == null) {
				logger.debug("User not found with emailId: {}", emailId);
				return null;
			}
			
			Customer customer = users.getCustomer();
			if (customer == null) {
				logger.debug("Customer not found for user with emailId: {}", emailId);
			}
			return customer;
		} catch (Exception e) {
			logger.error("Error retrieving customer by emailId: {}", emailId, e);
			return null;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}
	
	
}
