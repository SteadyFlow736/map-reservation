package org.example.mapreservation.initialization;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.customer.application.CustomerServiceImpl;
import org.example.mapreservation.hairshop.dto.CreateHairShopRequest;
import org.example.mapreservation.hairshop.service.HairShopService;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.owner.repository.OwnerRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class DataLoadService {

    private final HairShopService hairShopService;
    private final OwnerRepository ownerRepository;
    private final CustomerServiceImpl customerService;

    public void load() {
        Long ownerId = createOwner();
        createHairShops(ownerId);
        createCustomer();
    }

    private Long createOwner() {
        Owner owner = new Owner("소유자1");
        return ownerRepository.save(owner).getId();
    }

    private void createHairShops(Long ownerId) {
        List<CreateHairShopRequest> requests = List.of(
                new CreateHairShopRequest(
                        "디그헤어 광화문점", "서울 종로구 새문안로5길 5 4층", "4층", ownerId
                        , List.of(
                        "https://images.pexels.com/photos/897262/pexels-photo-897262.jpeg?auto=compress&cs=tinysrgb&w=800",
                        "https://images.pexels.com/photos/3065209/pexels-photo-3065209.jpeg?auto=compress&cs=tinysrgb&w=800",
                        "https://images.pexels.com/photos/668196/pexels-photo-668196.jpeg?auto=compress&cs=tinysrgb&w=800")
                ),
                new CreateHairShopRequest("라리엔 명동점", "서울 중구 명동길 60 7F", "7F", ownerId),
                new CreateHairShopRequest("코리아나호텔바버샵", "서울 중구 세종대로 135 코리아나호텔 (9층)", "9층", ownerId)
        );
        for (CreateHairShopRequest request : requests) {
            hairShopService.createHairShop(request);
        }
    }

    private void createCustomer() {
        customerService.createCustomer("abc@gmail.com", "Password1!");
    }
}
