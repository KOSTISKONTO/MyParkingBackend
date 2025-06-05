package myParking_Backend.Backend.Bookings;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.TravelMode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import myParking_Backend.Backend.Bookings.ResponceEntities.ResponcemyBookings;
import myParking_Backend.Backend.EmailService.EmailService;
import myParking_Backend.Backend.Parking.Parking;
import myParking_Backend.Backend.Parking.Prices.Enum.DayType;
import myParking_Backend.Backend.Parking.Prices.Enum.Policy;
import myParking_Backend.Backend.Parking.Prices.PolicyPrices.*;
import myParking_Backend.Backend.Parking.ResponceEntities.ResponceCities;
import myParking_Backend.Backend.Parking.Service_Parking;
import myParking_Backend.Backend.Users.Users;

import java.net.URLDecoder;
import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import myParking_Backend.Backend.config.WebSocket.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;




@RestController
@RequestMapping("/booking")
public class Controller_Booking {


    private final Service_Booking service_Booking;
    private final Service_Parking service_parking;
    private WebSocketController webSocketController;
    private EmailService emailService;
    @Value("${google.api.key}") // το βάζεις στο application.properties
    private String apiKey;


    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public Controller_Booking(Service_Booking service_Booking, Service_Parking serviceParking, WebSocketController webSocketController, EmailService emailservice) {
        this.service_Booking = service_Booking;
        this.service_parking = serviceParking;
        this.webSocketController = webSocketController;
        this.emailService = emailservice;

    }


    private GeoApiContext createContext() {
        return new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
    }


    //---------------Availability of parking------------------------------------------//

    @GetMapping("/availability")
    public ResponseEntity<?> availability(@RequestParam String City, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
                                          @RequestParam int hours) {
        City = URLDecoder.decode(City, StandardCharsets.UTF_8);
        System.out.println(City);
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> parkingsresponces = new ArrayList<>();
        List<Parking> parkings = this.service_parking.getParkingsByCity(City);

        System.out.println("Μέγεθος Πάρκινγ: " + parkings.size());
        System.out.println("Όνομα: " + parkings.get(0).getName_of_parking());


        String daytype;
        DayOfWeek day = date.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY)
            daytype = "Weekend";
        else daytype = "Daily";
        LocalDateTime startDateTime = LocalDateTime.of(date, time);
        LocalDateTime endDateTime = startDateTime.plusHours(hours);

        for (Parking parking : parkings) {

            if (parking.getIsactive()) {

            Map<String, Object> parkingresponce = new HashMap<>();
            LocalDateTime open = LocalDateTime.of(date, parking.getStartworking());
            LocalDateTime close = LocalDateTime.of(date, parking.getEndworking());


            if (!parking.getIs24()) {
                if (startDateTime.isBefore(open)) {
                    parkingresponce.put("isopen", false);
                } else if (startDateTime.isAfter(close)) {
                    parkingresponce.put("isopen", false);
                } else if (endDateTime.isEqual(close)) {
                    parkingresponce.put("isopen", false);
                } else if (endDateTime.isAfter(close)) {
                    LocalTime newtime = endDateTime.toLocalTime();
                    if (!(newtime.isAfter(parking.getStartworking()) && parking.getAccept24())) {

                        parkingresponce.put("isopen", false);
                    } else {
                        parkingresponce.put("isopen", true);
                    }
                } else {
                    parkingresponce.put("isopen", true);
                }
            } else {
                parkingresponce.put("isopen", true);
            }
            LocalTime endTime = time.plusHours(hours);
            List<Booking> bookings = service_Booking.getBookingsByDateandTime(date, time, endTime);
            if (bookings.size() < parking.getTotal_spaces()) {
                parkingresponce.put("available", true);
                Set<GeneralPolicy> policies = parking.getGeneralPolicyList();
                for (GeneralPolicy policy : policies) {
                    DayType inputDayType = DayType.valueOf(daytype);
                    if (policy.getDayType() == (inputDayType)) {
                        Policy pol = policy.getPolicy();
                        switch (pol) {
                            case FlatCost -> {
                                double cost = policy.getFlatCost().getFlatcost();
                                parkingresponce.put("cost", cost);
                            }
                            case CostByHour -> {
                                double cost = policy.getCostByHour().getCostOfHour();
                                cost = cost * hours;
                                parkingresponce.put("cost", cost);

                            }
                            case ByHourCustom -> {
                                Set<ByHourCustom> byHoursCustom = policy.getByHourCustom();
                                double cost;
                                List<Double> tohours = new ArrayList<>();
                                List<Double> costs = new ArrayList<>();
                                for (ByHourCustom byhourcst : byHoursCustom) {
                                    tohours.add(byhourcst.getToHour());
                                    costs.add(byhourcst.getCost());
                                }
                                Collections.sort(tohours);
                                Collections.sort(costs);
                                for (int i = 0; i < tohours.size(); i++) {
                                    if (hours <= tohours.get(i)) {
                                        cost = costs.get(i);
                                        parkingresponce.put("cost", cost);
                                    }
                                }
                            }

                            case ByHour -> {
                                Set<ByHour> byHours = policy.getByHour();
                                double cost = 0;
                                List<Double> tohours = new ArrayList<>();
                                List<Double> costs = new ArrayList<>();
                                for (ByHour byhour : byHours) {
                                    tohours.add(byhour.getToHour());
                                    costs.add(byhour.getCost());
                                }
                                Collections.sort(tohours);
                                Collections.sort(costs);

                                for (int i = 0; i < tohours.size(); i++) {
                                    if (hours <= tohours.get(i)) {
                                        cost = hours * costs.get(i);

                                        parkingresponce.put("cost", cost);
                                        break;
                                    }
                                    cost = cost + tohours.get(i) * costs.get(i);
                                }

                            }

                            case ByLocalTimeCustom -> {
                                List<ByLocalTimeCustom> slots = new ArrayList<>(policy.getByLocalTimeCustom());
                                slots.sort(Comparator.comparing(ByLocalTimeCustom::getFromhour));
                                LocalTime endTimee = time.plusHours(hours);
                                double totalCost = 0.0;
                                ByLocalTimeCustom by;
                                for (int i = 0; i < slots.size(); i++) {
                                    if ((time.equals(slots.get(i).getFromhour()) || time.isAfter(slots.get(i).getFromhour())) && time.isBefore(slots.get(i).getTohour())) {
                                        by = slots.get(i);
                                        totalCost = by.getCost();
                                        if (endTimee.isBefore(slots.get(i).getTohour()) || endTimee.equals(slots.get(i).getTohour())) {
                                            break;
                                        } else {
                                            for (i = i + 1; i < slots.size(); i++) {
                                                if (!(endTimee.isBefore(slots.get(i).getTohour()) || endTimee.equals(slots.get(i).getTohour()))) {
                                                    totalCost = totalCost + slots.get(i).getCost();
                                                } else {
                                                    totalCost = totalCost + slots.get(i).getCost();
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }

                                parkingresponce.put("cost", Math.round(totalCost * 100.0) / 100.0);
                            }

                            case ByLocalTime -> {
                                List<ByLocalTime> slots = new ArrayList<>(policy.getByLocalTime());
                                slots.sort(Comparator.comparing(ByLocalTime::getFromhour));
                                LocalTime endTimee = time.plusHours(hours);
                                double totalCost = 0.0;
                                ByLocalTime by;
                                for (int i = 0; i < slots.size(); i++) {
                                    if ((time.equals(slots.get(i).getFromhour()) || time.isAfter(slots.get(i).getFromhour())) && time.isBefore(slots.get(i).getTohour())) {
                                        by = slots.get(i);
                                        if (endTimee.isBefore(slots.get(i).getTohour()) || endTimee.equals(slots.get(i).getTohour())) {
                                            totalCost = by.getCostofTime() * hours;
                                            break;
                                        } else {
                                            long todurationhours = Math.abs(Duration.between(time, slots.get(i).getTohour()).toHours());
                                            totalCost = by.getCostofTime() * todurationhours;
                                            for (i = i + 1; i < slots.size(); i++) {
                                                if ((endTimee.isBefore(slots.get(i).getTohour()) || endTimee.equals(slots.get(i).getTohour()))) {
                                                    long newdurationshours = Math.abs(Duration.between(slots.get(i).getFromhour(), endTimee).toHours());
                                                    totalCost = totalCost + slots.get(i).getCostofTime() * newdurationshours;
                                                    break;
                                                } else {
                                                    long newdurationshours = Math.abs(Duration.between(slots.get(i).getFromhour(), slots.get(i).getTohour()).toHours());
                                                    totalCost = totalCost + slots.get(i).getCostofTime() * newdurationshours;

                                                }
                                            }
                                        }
                                    }
                                }
                                parkingresponce.put("cost", Math.round(totalCost * 100.0) / 100.0);
                            }


                        }

                    }
                }

            } else {
                parkingresponce.put("available", false);

            }
            parkingresponce.put("nameofParking", parking.getName_of_parking());
            parkingresponce.put(("Idparking"), parking.getId());
            parkingresponce.put("address", parking.getAddress());


            parkingsresponces.add(parkingresponce);
        }
    }

        response.put("Parkings", parkingsresponces);

        return ResponseEntity.ok(parkingsresponces);


    }


    //---------------NEW BOOKING------------------------------------------//

    @Transactional
    @PostMapping("/newBooking")
    public ResponseEntity<?> newBooking(@RequestBody Booking booking, Authentication authentication){
        Parking parking =  service_parking.getParkingById(booking.getBooking_parking().getId());
        LocalDate making_date = LocalDate.now();
        booking.setBooking_making_date(making_date);
        String email;
        if (authentication!=null)
        {
            Users user;
            user = (Users) authentication.getPrincipal();
            user = entityManager.merge(user);
            booking.setUser_of_booking(user);
            booking.setNameguest(null);
            booking.setLastnameguest(null);
            booking.setEmailguest(null);
            email=user.getEmail();
        }
        else{
            if(booking.getEmailguest()==null || booking.getNameguest()==null || booking.getLastnameguest()==null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Ελλιπή Στοιχεία"));
            }
            email = booking.getEmailguest();
        }

        parking = entityManager.merge(parking);
        booking.setBooking_parking(parking);
        service_Booking.newBooking(booking);
        List<ResponceCities> popularcities = this.service_Booking.getpopularCities();
        webSocketController.broadcastCities(popularcities);
        emailService.sendSimpleEmail(email, "Κράτηση", "Καταχωρήθηκε με επιτυχία!" );
        return ResponseEntity.ok(Map.of("message", "Καταχωρήθηκε με επιτυχία!"));

    }

    @GetMapping("/myBookings")
    public ResponseEntity<?> myBookings(Authentication authentication){
        Users user = (Users) authentication.getPrincipal();
            List<Booking> bookings = service_Booking.findByUsers_of_booking(user);
            List<ResponcemyBookings> responcemyBookingsList = new ArrayList<>();
            for (Booking booking:bookings){
                ResponcemyBookings responcemyBookings = new ResponcemyBookings();
                responcemyBookings.setId(booking.getId());
                responcemyBookings.setBooking_making_date(booking.getBooking_making_date());
                responcemyBookings.setBooking_date(booking.getBooking_date());
                responcemyBookings.setTime_date(booking.getTime_date());
                responcemyBookings.setHours_of_booking(booking.getHours_of_booking());
                responcemyBookings.setCost(booking.getCost());
                Parking parkingofbooking = booking.getBooking_parking();
                Map<String, Object> parking = new HashMap<>();
                parking.put("name_of_parking", parkingofbooking.getName_of_parking());
                parking.put("City", parkingofbooking.getCity());
                parking.put("address", parkingofbooking.getAddress());
                parking.put("TK", parkingofbooking.getTK());
                responcemyBookings.setParking(parking);
                responcemyBookingsList.add(responcemyBookings);
            }

            if(responcemyBookingsList.isEmpty()){
                return ResponseEntity.ok(Map.of("message", "Δεν έχες ακόμα κρατήσεις!"));
            }

        return ResponseEntity.ok( responcemyBookingsList);
    }


    @GetMapping("/popularcities")
    public ResponseEntity<?> popularcities(){
        List<ResponceCities> cities = this.service_Booking.getpopularCities();
        return ResponseEntity.ok(cities);

    }


@GetMapping("/coordinates")
    public ResponseEntity<?> getLatLngFromAddress(@RequestParam String address) {
        try {
            String[] split = address.split("\\s*,\\s*");
            String city;
            city = split[1];
            System.out.println(city);
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + apiKey;
            System.out.println("ADDRESS RECEIVED: " + address);

            RestTemplate restTemplate = new RestTemplate();
            String responseJson = restTemplate.getForObject(url, String.class);
            System.out.println(responseJson);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseJson);
            String longName = root.path("results")
                    .get(0)
                    .path("address_components")
                    .get(5)
                    .path("long_name")
                    .asText();
            System.out.println(longName);
            String status = root.path("status").asText();
            if (!"OK".equals(status)) {
                System.out.println("GOOGLE STATUS: " + status);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Διεύθυνση δεν βρέθηκε."));
            }

            JsonNode location = root.path("results").get(0).path("geometry").path("location");
            double lat = location.path("lat").asDouble();
            double lng = location.path("lng").asDouble();

            return ResponseEntity.ok(Map.of("lat", lat, "lng", lng));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Σφάλμα κατά την επεξεργασία."));
        }
    }




    //@GetMapping("/distance")
    public Map<String, String> getDistance(
          String origin,
           String destination) {
        try {

            GeoApiContext context = createContext();
            DistanceMatrix matrix = DistanceMatrixApi.newRequest(context)
                    .origins(origin)
                    .destinations(destination)
                    .mode(TravelMode.WALKING) // 🦶 με τα πόδια
                    .await();

            DistanceMatrixRow row = matrix.rows[0];
            //return "Απόσταση: " + row.elements[0].distance.humanReadable +
              //      ", Διάρκεια: " + row.elements[0].duration.humanReadable;
            Map<String, String> responce = new HashMap<>();
            responce.put("Distance", row.elements[0].distance.humanReadable);
            responce.put("Duration", row.elements[0].duration.humanReadable);
            return  responce;

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> responce = new HashMap<>();
            responce.put("error", "Σφάλμα κατά τον υπολογισμό απόστασης.");
            return  responce;
           // return "Σφάλμα κατά τον υπολογισμό απόστασης.";
        }
    }


    @GetMapping("/todo")
    public ResponseEntity<?> todo(@RequestParam String address){
        List<Map<String, String>> responce = new ArrayList<>();
        String[] split = address.split("\\s*,\\s*");
        String city;
        if(split.length==3) city=split[1];
        else city=split[0];
        List<Parking> parkings = service_parking.getParkingsByCity(city);
        for (Parking parking:parkings){
            Map<String, String> responceparking = new HashMap<>();
            String addressofparking = parking.getAddress() + ", " + parking.getCity() + ", " + " Ελλάδα";
            responceparking = getDistance(address, addressofparking);
            responceparking.put("Name", parking.getName_of_parking());
            responceparking.put("Address", parking.getAddress());
            responceparking.put("City", city);
            responce.add(responceparking);
            //System.out.println(responceparking);
        }
        return ResponseEntity.ok(responce);

    }



















}
