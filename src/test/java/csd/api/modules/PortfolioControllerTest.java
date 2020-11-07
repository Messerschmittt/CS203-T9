// package csd.api.modules;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertNull;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;

// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import csd.api.tables.*;
// import csd.api.modules.portfolio.PortfolioController;

// @ExtendWith(MockitoExtension.class)
// public class PortfolioControllerTest {
//     @Mock
//     private PortfolioRepository portfolios;

//     @InjectMocks
//     private PortfolioController portfolioController;

//     @Test
//     void findportfolio_customer_Id_ReturnPortfolio() {
//         // arrange ***
//         ApplicationUser user  =  new ApplicationUser("NewUser", "newuser1", "ROLE_USER");
//         Customer customer = new Customer(user, "NewUser_1");
//         Portfolio portfolio = new Portfolio(customer);
        
//         // mock the "findbytitle" operation
//         when(portfolios.findByCustomer_Id(any(Integer.class))).thenReturn(portfolio);
        
//     }
// }
