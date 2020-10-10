package csd.api.modules;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import csd.api.tables.*;


/** Start an actual HTTP server listening at a random port*/
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserIntegrationTest {

	@LocalServerPort
	private int port;

	private final String baseUrl = "http://localhost:";

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
    private UserRepository users;
    
    @Autowired
	private CustomerRepository customers;

	@Autowired
	private BCryptPasswordEncoder encoder;


	@BeforeEach
	void init() {
		ApplicationUser user = users.save(new ApplicationUser("NewUser", "newuser123", "ROLE_USER"));
		Customer customer = customers.save(new Customer(user, "New User"));
		// System.out.println(customer.getId());

		users.save(new ApplicationUser("NewManager", "newmanager123", "ROLE_MANAGER"));

		users.save(new ApplicationUser("NewAnalyst", "newanalyst123", "ROLE_ANALYST"));
		
	}



	@AfterEach
	void tearDown(){
		customers.deleteAll();
		users.deleteAll();
    }
    


	@Test
	public void getUsers_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/users");
        		
        // Need to use array with a ReponseEntity here
        ResponseEntity<ApplicationUser[]> result = restTemplate
										.exchange(uri, HttpMethod.GET, null, ApplicationUser[].class);
		// ResponseEntity<ApplicationUser[]> result = restTemplate.getForEntity(uri, ApplicationUser[].class);
		ApplicationUser[] userList = result.getBody();
		
		assertEquals(200, result.getStatusCode().value());
        assertEquals(3, userList.length);
	}




    @Test
	public void getCustomers_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/customers");
        
		
		// Need to use array with a ReponseEntity here
        ResponseEntity<Customer[]> result = restTemplate.exchange(uri, HttpMethod.GET, null, Customer[].class);
        Customer[] customerList = result.getBody();
		
		assertEquals(200, result.getStatusCode().value());
		assertEquals(1, customerList.length);
    }
    

    @Test
	public void getCustomerDetails_ValidCustomerId_Success() throws Exception {

		Integer id = customers.findAll().get(0).getId();
		URI uri = new URI(baseUrl + port + "/customers/" + id);
	
		ResponseEntity<Customer> result = restTemplate.getForEntity(uri, Customer.class);
			
		assertEquals(200, result.getStatusCode().value());
		assertEquals("NewUser", result.getBody().getUsername());
    }
    
    @Test
	public void getCustomerDetails_InvalidCustomerId_Failure() throws Exception {
		URI uri = new URI(baseUrl + port + "/customers/100");
		
		ResponseEntity<Customer> result = restTemplate.getForEntity(uri, Customer.class);
			
		assertEquals(404, result.getStatusCode().value());
	}
/*
	

	@Test
	public void addBook_Success() throws Exception {
		URI uri = new URI(baseUrl + port + "/books");
		Book book = new Book("A New Hope");
		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_ADMIN"));

		ResponseEntity<Book> result = restTemplate.withBasicAuth("admin", "goodpassword")
										.postForEntity(uri, book, Book.class);
			
		assertEquals(201, result.getStatusCode().value());
		assertEquals(book.getTitle(), result.getBody().getTitle());
	}

	/**
	 * TODO: Activity 2 (Week 6)
	 * Add integration tests for delete/update a book.
	 * For delete operation: there should be two tests for success and failure (book not found) scenarios.
	 * Similarly, there should be two tests for update operation.
	 * You should assert both the HTTP response code, and the value returned if any
	 * 
	 * For delete and update, you should use restTemplate.exchange method to send the request
	 * E.g.: ResponseEntity<Void> result = restTemplate.withBasicAuth("admin", "goodpassword")
										.exchange(uri, HttpMethod.DELETE, null, Void.class);
	 
	// your code here

	@Test
	public void deleteBook_Success() throws Exception {
		Book book = new Book("A New Hope");
		Long id = books.save(book).getId();

		URI uri = new URI(baseUrl + port + "/books/" + id);
		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_ADMIN"));


		ResponseEntity<Void> result = restTemplate.withBasicAuth("admin", "goodpassword")
										.exchange(uri, HttpMethod.DELETE, null, Void.class);
			
		assertEquals(200, result.getStatusCode().value());
		// assertEquals(book.getTitle(), result.getBody().getTitle());
	}

	@Test
	public void deleteBook_InvalidBookId_Failure() throws Exception {
		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_ADMIN"));

		URI uri = new URI(baseUrl + port + "/books/1");
		
		ResponseEntity<Void> result = restTemplate.withBasicAuth("admin", "goodpassword")
										.exchange(uri, HttpMethod.DELETE, null, Void.class);

		assertEquals(404, result.getStatusCode().value());
		
	}


	@Test
	public void updateBook_Success() throws Exception {
		Book book = new Book("A New Hope");
		Long id = books.save(book).getId();

		URI uri = new URI(baseUrl + port + "/books/" + id);
		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_ADMIN"));
		Book newBook = new Book("A New");	


		ResponseEntity<Book> result = restTemplate.withBasicAuth("admin", "goodpassword")
										.exchange(uri, HttpMethod.PUT, new HttpEntity<>(newBook), Book.class);
			
		assertEquals(200, result.getStatusCode().value());
		assertEquals(newBook.getTitle(), result.getBody().getTitle());
	}

	
	@Test
	public void updateBook_InvalidBookId_Failure() throws Exception {

		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_ADMIN"));
		Book newBook = new Book("A New");	


		URI uri = new URI(baseUrl + port + "/books/1");
		
		ResponseEntity<Book> result = restTemplate.withBasicAuth("admin", "goodpassword")
										.exchange(uri, HttpMethod.PUT, new HttpEntity<>(newBook), Book.class);

		assertEquals(404, result.getStatusCode().value());


	}

*/
}
