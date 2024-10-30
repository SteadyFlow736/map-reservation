package org.example.mapreservation.initialization;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.common.Address;
import org.example.mapreservation.customer.application.CustomerServiceImpl;
import org.example.mapreservation.hairshop.application.repository.HairShopRepository;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.owner.domain.Owner;
import org.example.mapreservation.owner.repository.OwnerRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class DataLoadService {

    private final OwnerRepository ownerRepository;
    private final CustomerServiceImpl customerService;
    private final HairShopRepository hairShopRepository;
    private final CsvReader csvReader;

    public void load() {
        Owner owner = createOwner();
        createHairShops(owner);
        createCustomer();
    }

    private Owner createOwner() {
        Owner owner = new Owner("소유자1");
        return ownerRepository.save(owner);
    }

    private void createHairShops(Owner owner) {
        List<String[]> csvLines = csvReader.readLines();
        List<HairShop> hairShops = new ArrayList<>();
        List<String> imageUrls = getImageUrls();
        for (String[] csvLine : csvLines) {
            HairShop hairShop = HairShop.builder()
                    .name(CsvLineHandler.getHairShopName(csvLine))
                    .address(new Address(CsvLineHandler.getRoadAddress(csvLine), ""))
                    .longitude(CsvLineHandler.getLongitude(csvLine))
                    .latitude(CsvLineHandler.getLatitude(csvLine))
                    .owner(owner)
                    .imageUrls(imageUrls)
                    .build();
            hairShops.add(hairShop);
        }

        hairShopRepository.saveAll(hairShops);
    }

    private List<String> getImageUrls() {
        return List.of(
                "https://images.pexels.com/photos/897262/pexels-photo-897262.jpeg?auto=compress&cs=tinysrgb&w=800",
                "https://images.pexels.com/photos/3065209/pexels-photo-3065209.jpeg?auto=compress&cs=tinysrgb&w=800",
                "https://images.pexels.com/photos/668196/pexels-photo-668196.jpeg?auto=compress&cs=tinysrgb&w=800"
        );
    }

    private void createCustomer() {
        customerService.createCustomer("abc@gmail.com", "Password1!");
    }
}
