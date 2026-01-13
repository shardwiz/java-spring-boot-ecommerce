
package com.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.model.Product;
import com.service.ProductService;

@Controller
public class ProductController {

	private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
	private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
	private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/gif");

	@Autowired
	private ProductService productService;

	// Request Mapping

	// which displays the list of products to the productList page

	/* Product List using Angular
	* @RequestMapping("/getAllProducts")
	* public ModelAndView getAllProducts() {
	*	List<Product> products = productService.getAllProducts();
	*	return new ModelAndView("productListAngular", "products", products);
	*}
	*/
	// Normal ProductList view
	@RequestMapping("/getAllProducts")
	public ModelAndView getAllProducts() {
		List<Product> products = productService.getAllProducts();
		return new ModelAndView("productList", "products", products);
	}
	 
	
	// this is used for getting the product by productId

	@RequestMapping("getProductById/{productId}")
	public ModelAndView getProductById(@PathVariable(value = "productId") String productId) {
		if (productId == null || productId.trim().isEmpty()) {
			logger.warn("Attempted to access product with null or empty productId");
			return new ModelAndView("redirect:/getAllProducts");
		}
		
		Product product = productService.getProductById(productId);
		if (product == null) {
			logger.warn("Product not found with id: {}", productId);
			return new ModelAndView("redirect:/getAllProducts");
		}
		
		return new ModelAndView("productPage", "productObj", product);
	}

	@RequestMapping("/admin/delete/{productId}")
	public String deleteProduct(@PathVariable(value = "productId") String productId, 
			HttpServletRequest request, RedirectAttributes redirectAttributes) {
		
		if (productId == null || productId.trim().isEmpty()) {
			logger.warn("Attempted to delete product with null or empty productId");
			redirectAttributes.addFlashAttribute("error", "Invalid product ID");
			return "redirect:/getAllProducts";
		}

		try {
			// Delete product image file
			ServletContext servletContext = request.getServletContext();
			String realPath = servletContext.getRealPath("/WEB-INF/resource/images/products/");
			if (realPath != null) {
				Path path = Paths.get(realPath + productId + ".jpg");
				if (Files.exists(path)) {
					try {
						Files.delete(path);
						logger.info("Product image deleted: {}", path);
					} catch (IOException e) {
						logger.error("Error deleting product image for productId: {}", productId, e);
						// Continue with product deletion even if image deletion fails
					}
				}
			}

			productService.deleteProduct(productId);
			redirectAttributes.addFlashAttribute("success", "Product deleted successfully");
			logger.info("Product deleted successfully with id: {}", productId);
		} catch (Exception e) {
			logger.error("Error deleting product with id: {}", productId, e);
			redirectAttributes.addFlashAttribute("error", "Failed to delete product");
		}
		
		return "redirect:/getAllProducts";
	}

	@RequestMapping(value = "/admin/product/addProduct", method = RequestMethod.GET)
	public String getProductForm(Model model) {
		Product product = new Product();
		// New Arrivals
		// set the category as 1 for the Book book
		product.setProductCategory("Android");
		model.addAttribute("productFormObj", product);
		return "addProduct";

	}

	@RequestMapping(value = "/admin/product/addProduct", method = RequestMethod.POST)
	public String addProduct(@Valid @ModelAttribute(value = "productFormObj") Product product, 
			BindingResult result, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		
		if (result.hasErrors()) {
			logger.debug("Validation errors in product form");
			return "addProduct";
		}
		
		try {
			productService.addProduct(product);
			
			// Handle image upload
			MultipartFile image = product.getProductImage();
			if (image != null && !image.isEmpty()) {
				// Validate file
				if (!isValidImageFile(image)) {
					redirectAttributes.addFlashAttribute("error", "Invalid image file. Only JPEG, PNG, and GIF are allowed.");
					return "redirect:/admin/product/addProduct";
				}
				
				ServletContext servletContext = request.getServletContext();
				String realPath = servletContext.getRealPath("/WEB-INF/resource/images/products/");
				if (realPath != null) {
					// Ensure directory exists
					Path directory = Paths.get(realPath);
					if (!Files.exists(directory)) {
						Files.createDirectories(directory);
					}
					
					Path path = Paths.get(realPath + product.getProductId() + ".jpg");
					image.transferTo(new File(path.toString()));
					logger.info("Product image saved: {}", path);
				}
			}
			
			redirectAttributes.addFlashAttribute("success", "Product added successfully");
			logger.info("Product added successfully with id: {}", product.getProductId());
		} catch (Exception e) {
			logger.error("Error adding product", e);
			redirectAttributes.addFlashAttribute("error", "Failed to add product: " + e.getMessage());
			return "addProduct";
		}
		
		return "redirect:/getAllProducts";
	}
	
	/**
	 * Validates if the uploaded file is a valid image
	 */
	private boolean isValidImageFile(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			return false;
		}
		
		String contentType = file.getContentType();
		if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
			logger.warn("Invalid file type: {}", contentType);
			return false;
		}
		
		if (file.getSize() > MAX_FILE_SIZE) {
			logger.warn("File size too large: {} bytes", file.getSize());
			return false;
		}
		
		return true;
	}

	@RequestMapping(value = "/admin/product/editProduct/{productId}")
	public ModelAndView getEditForm(@PathVariable(value = "productId") String productId) {
		if (productId == null || productId.trim().isEmpty()) {
			logger.warn("Attempted to edit product with null or empty productId");
			return new ModelAndView("redirect:/getAllProducts");
		}
		
		Product product = productService.getProductById(productId);
		if (product == null) {
			logger.warn("Product not found for editing with id: {}", productId);
			return new ModelAndView("redirect:/getAllProducts");
		}
		
		return new ModelAndView("editProduct", "editProductObj", product);
	}

	@RequestMapping(value = "/admin/product/editProduct", method = RequestMethod.POST)
	public String editProduct(@ModelAttribute(value = "editProductObj") Product product, 
			RedirectAttributes redirectAttributes) {
		if (product == null || product.getProductId() == null) {
			logger.warn("Attempted to edit null product or product with null id");
			redirectAttributes.addFlashAttribute("error", "Invalid product data");
			return "redirect:/getAllProducts";
		}
		
		try {
			productService.editProduct(product);
			redirectAttributes.addFlashAttribute("success", "Product updated successfully");
			logger.info("Product updated successfully with id: {}", product.getProductId());
		} catch (Exception e) {
			logger.error("Error updating product with id: {}", product.getProductId(), e);
			redirectAttributes.addFlashAttribute("error", "Failed to update product");
		}
		
		return "redirect:/getAllProducts";
	}

	@RequestMapping("/getProductsList")
	public @ResponseBody List<Product> getProductsListInJson() {
		return productService.getAllProducts();
	}

	@RequestMapping("/productsListAngular")
	public String getProducts() {
		return "productListAngular";
	}

	// Search and filter endpoints
	@RequestMapping(value = "/searchProducts", method = RequestMethod.GET)
	public ModelAndView searchProducts(@RequestParam(value = "searchTerm", required = false) String searchTerm,
			@RequestParam(value = "category", required = false) String category,
			@RequestParam(value = "minPrice", required = false) Double minPrice,
			@RequestParam(value = "maxPrice", required = false) Double maxPrice) {
		
		List<Product> products;
		
		if (searchTerm != null || (category != null && !category.equals("All")) || minPrice != null || maxPrice != null) {
			products = productService.searchProducts(searchTerm, category, minPrice, maxPrice);
		} else {
			products = productService.getAllProducts();
		}
		
		ModelAndView modelAndView = new ModelAndView("productList", "products", products);
		modelAndView.addObject("searchTerm", searchTerm != null ? searchTerm : "");
		modelAndView.addObject("selectedCategory", category != null ? category : "All");
		modelAndView.addObject("minPrice", minPrice != null ? minPrice : "");
		modelAndView.addObject("maxPrice", maxPrice != null ? maxPrice : "");
		return modelAndView;
	}

}
