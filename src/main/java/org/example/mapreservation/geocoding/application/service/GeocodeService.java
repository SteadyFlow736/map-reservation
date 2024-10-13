package org.example.mapreservation.geocoding.application.service;

import org.example.mapreservation.geocoding.domain.GeocodeRequest;
import org.example.mapreservation.geocoding.domain.GeocodeResponse;

public interface GeocodeService {
    GeocodeResponse geocode(GeocodeRequest request);
}
