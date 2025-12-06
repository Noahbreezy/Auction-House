package be.ehb.auctionhousebackend.service;


import be.ehb.auctionhousebackend.dto.AuctionDto;
import be.ehb.auctionhousebackend.exception.AuctionClosedException;
import be.ehb.auctionhousebackend.exception.FraudException;
import be.ehb.auctionhousebackend.exception.InsufficientBidException;
import be.ehb.auctionhousebackend.model.Auction;
import be.ehb.auctionhousebackend.model.AuctionBid;
import be.ehb.auctionhousebackend.model.Category;
import be.ehb.auctionhousebackend.model.Person;
import be.ehb.auctionhousebackend.repository.AuctionBidRepository;
import be.ehb.auctionhousebackend.repository.AuctionRepository;
import be.ehb.auctionhousebackend.repository.CategoryRepository;
import be.ehb.auctionhousebackend.repository.PersonRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTest {

    private AuctionService auctionService;
    private FakeMailService fakeMailService;

    @Mock
    private AuctionRepository auctionRepository;
    @Mock
    private AuctionBidRepository auctionBidRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private MeterRegistry meterRegistry;

    Person auctioneer = new Person("123-456-789", "Test", "test@auction.com","987654");
    Person bidder =  new Person("999-999-999", "Bidder", "bidder@auction.com","476349");

    @BeforeEach
    void setUp() {
        fakeMailService = new FakeMailService();
        auctionService = new AuctionService(auctionRepository, auctionBidRepository, fakeMailService,
                meterRegistry, personRepository, categoryRepository);
    }

    @Test
    void givenAuctionWithInvalidEndTime_whenSaveAuction_thenThrowsAuctionClosedException() {
        AuctionDto dto = new AuctionDto();
        dto.setProductName("Auction");
        dto.setStartPrice(50.0);
        dto.setEndTime(LocalDateTime.now().minusHours(1));
        dto.setCategoryId(1);

        Category category = new Category();
        category.setId(1);
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));


        assertThrows(AuctionClosedException.class, () -> {
            auctionService.save(dto);
        });
    }

    @Test
    void givenAuctionWithValidEndTimeAndCategory_whenCreateAuction_thenAuctionIsSaved() {
        AuctionDto dto = new AuctionDto();
        dto.setProductName("Auction");
        dto.setStartPrice(50.0);
        dto.setEndTime(LocalDateTime.now().plusDays(1));
        dto.setCategoryId(1);

        Category category = new Category();
        category.setId(1);

        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        mockLoggedInUser("test@auction.com", auctioneer);

        auctionService.save(dto);

        verify(auctionRepository).save(any(Auction.class));
    }


    @Test
    void givenClosedAuction_whenBid_thenThrowAuctionClosedException() {
        Auction auction = new Auction("Auction", 20.0, auctioneer, LocalDateTime.now().minusDays(1),"", "");
        auction.setId(1);
        when(auctionRepository.findById(1)).thenReturn(Optional.of(auction));

        AuctionBid bid = new AuctionBid(30, auction,bidder);

        assertThrows(AuctionClosedException.class, () -> auctionService.bidOnAuction(1, bid));
    }

    @Test
    void givenBidderIsOwner_whenBid_thenThrowFraudExceptionAndSendEmail() {
        Auction auction = new Auction("Auction", 50.0, auctioneer, LocalDateTime.now().plusHours(1),"", "");
        auction.setId(1);
        when(auctionRepository.findById(1)).thenReturn(Optional.of(auction));

        AuctionBid bid = new AuctionBid(60, auction, auctioneer);

        mockLoggedInUser("test@auction.com", auctioneer);

        assertThrows(FraudException.class, () -> auctionService.bidOnAuction(1, bid));
        assertEquals(1, fakeMailService.getEmails().size());
    }


    @Test
    void givenNoPreviousBidsAndBidTooLow_whenBid_thenThrowInsufficientBidException() {
        Auction auction = new Auction("Auction", 100.0, auctioneer, LocalDateTime.now().plusDays(1),"", "");
        auction.setId(1);
        when(auctionRepository.findById(1)).thenReturn(Optional.of(auction));
        when(auctionBidRepository.findAllByAuction_Id(1)).thenReturn(List.of());

        AuctionBid bid = new AuctionBid(90, auction, bidder);
        mockLoggedInUser("bidder@example.com", bidder);

        assertThrows(InsufficientBidException.class, () -> auctionService.bidOnAuction(1, bid));
    }


    @Test
    void givenExistingHigherBid_whenLowerBid_thenThrowInsufficientBidException() {
        Auction auction = new Auction("Auction", 50.0, auctioneer, LocalDateTime.now().plusDays(1),"", "");
        auction.setId(1);
        when(auctionRepository.findById(1)).thenReturn(Optional.of(auction));

        AuctionBid oldBid = new AuctionBid(100, auction, new Person("888-888-888", "Old Bidder", "old@auction.com", "123456"));
        when(auctionBidRepository.findAllByAuction_Id(1)).thenReturn(List.of(oldBid));

        AuctionBid newBid = new AuctionBid(90, auction, bidder);
        mockLoggedInUser("bidder@example.com", bidder);

        assertThrows(InsufficientBidException.class, () -> auctionService.bidOnAuction(1, newBid));
    }



    @Test
    void givenValidBid_whenNoConflicts_thenBidIsSaved() {
        Auction auction = new Auction("Valid Auction", 50.0, auctioneer, LocalDateTime.now().plusDays(1),"", "");
        auction.setId(1);
        when(auctionRepository.findById(1)).thenReturn(Optional.of(auction));
        when(auctionBidRepository.findAllByAuction_Id(1)).thenReturn(List.of());

        AuctionBid bid = new AuctionBid(60, auction, bidder);
        mockLoggedInUser("bidder@example.com", bidder);

        auctionService.bidOnAuction(1, bid);

        verify(auctionBidRepository).save(bid);
    }



    @Test
    void testSearchAuctions_withCategoryAndMinPrice() {
        String category = "Electronics";
        Double minPrice = 50.0;
        Auction auction = new Auction();
        auction.setProductName("Phone");
        auction.setStartPrice(100);
        auction.setEndTime(LocalDateTime.now().plusDays(1));

        List<Auction> expectedAuctions = List.of(auction);

        when(auctionRepository.findAll(any(Specification.class))).thenReturn(expectedAuctions);

        List<Auction> result = auctionService.searchAuctions(category, minPrice, null, null);

        assertEquals(1, result.size());
        assertEquals("Phone", result.get(0).getProductName());

        verify(auctionRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void testSearchAuctions_withActiveStatusOnly() {
        Auction activeAuction = new Auction();
        activeAuction.setProductName("Active Laptop");
        activeAuction.setEndTime(LocalDateTime.now().plusDays(1));

        when(auctionRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(activeAuction));

        List<Auction> result = auctionService.searchAuctions(null, null, null, "ACTIVE");

        assertEquals(1, result.size());
        assertTrue(result.get(0).getEndTime().isAfter(LocalDateTime.now()));
    }

    @Test
    void testSearchAuctions_withMaxPriceOnly() {
        Auction expectedAuction = new Auction();
        expectedAuction.setProductName("TV");
        when(auctionRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(expectedAuction));

        List<Auction> result = auctionService.searchAuctions(null, null, 500.0, null);

        assertEquals(1, result.size());
        assertEquals("TV", result.get(0).getProductName());
        verify(auctionRepository).findAll(any(Specification.class));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    private void mockLoggedInUser(String email, Person user) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(email);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(securityContext);
        when(personRepository.findByEmail(email)).thenReturn(Optional.of(user));
    }


}