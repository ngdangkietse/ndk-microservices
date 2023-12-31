package dev.ngdangkiet.service;

import dev.ngdangkiet.dkmicroservices.location.protobuf.PGetLocationRequest;
import dev.ngdangkiet.dkmicroservices.location.protobuf.PLocationResponse;
import dev.ngdangkiet.dkmicroservices.location.protobuf.PLocationsResponse;

public interface LocationService {

    PLocationsResponse getSameNameLocation(String address);

    PLocationResponse calculateDistanceFrom2Point(PGetLocationRequest request);
}
