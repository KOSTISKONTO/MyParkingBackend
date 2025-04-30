package myParking_Backend.Backend.Parking;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.maps.PlaceAutocompleteRequest;
import com.google.maps.model.ComponentFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import myParking_Backend.Backend.Parking.Prices.Enum.Policy;
import myParking_Backend.Backend.Parking.Prices.PolicyPrices.*;
import myParking_Backend.Backend.Parking.ResponceEntities.ResponceCities;
import myParking_Backend.Backend.Parking.ResponceEntities.ResponcemyParking;
import myParking_Backend.Backend.Users.Service_Users;
import myParking_Backend.Backend.Users.Users;
import myParking_Backend.Backend.config.WebSocket.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import com.google.maps.model.AddressComponentType;

import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;


import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.AutocompletePrediction;
import com.google.maps.model.PlaceAutocompleteType;
import com.google.maps.model.PlaceDetails;


import java.util.*;


@RestController
@RequestMapping("/parking")
public class Controller_Parking {

    @PersistenceContext
    private EntityManager entityManager;


    private WebSocketController webSocketController;
    private final Service_Users service_Users;
    private final Service_Parking service_parking;
    @Value("${google.api.key}") // το βάζεις στο application.properties
    private String apiKey;
    private final Path uploadDir = Paths.get("uploads");


    @Autowired
    public Controller_Parking(Service_Users service_Users, Service_Parking service_parking, WebSocketController webSocketController) {
        this.service_Users = service_Users;
        this.service_parking = service_parking;
        this.webSocketController = webSocketController;
    }


    /**
     ------------------------OWNER'S CONTROLLER------------------------------
     */

    @Transactional
    @PostMapping("/newParking")
    public ResponseEntity<?> newParking(@RequestBody Map<String, Object> request, Authentication authentication) {
        Users user = (Users) authentication.getPrincipal();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Parking parking = objectMapper.convertValue(request.get("parking"), Parking.class); //ΜΕΤΑΤΡΟΠΗ ΤΟΥ ΕΝΟΣ ΚΟΜΜΑΤΙΟΥ JSON ΣΕ PARKING
        parking.setAvailable_spaces(parking.getTotal_spaces());
        parking.setIsactive(false);
        //parking.setLocation("ΤΥΧΑΙΟ TODO");
        Set<GeneralPolicy> generalPolicies = parking.getGeneralPolicyList();
        if (generalPolicies.size() < 2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Δεν εμπεριέχονται όλοι τύποι ημερών χρέωσης!");
        }
        if (parking.getIs24()) {
            LocalTime timestart = LocalTime.parse("00:00");
            parking.setStartworking(timestart);
            LocalTime timeend = LocalTime.parse("23:59");
            parking.setEndworking(timeend);
        }
        for (GeneralPolicy generalPolicy : generalPolicies) {
            Policy policy = generalPolicy.getPolicy();
            switch (policy) {
                case FlatCost:
                    generalPolicy.setFlatCost(generalPolicy.getFlatCost());
                    generalPolicy.getFlatCost().setGeneralPolicy(generalPolicy);
                    break;
                case CostByHour:
                    generalPolicy.setCostByHour(generalPolicy.getCostByHour());
                    generalPolicy.getCostByHour().setGeneralPolicy(generalPolicy);
                    break;
                case ByHour:
                    Set<ByHour> byHours = generalPolicy.getByHour();
                    for (ByHour byHour : byHours) {
                        byHour.setGeneralPolicy(generalPolicy);
                    }
                    generalPolicy.setByHour(byHours);
                    break;
                case ByHourCustom:
                    Set<ByHourCustom> byHourCustoms = generalPolicy.getByHourCustom();
                    for (ByHourCustom byHourcustom : byHourCustoms) {
                        byHourcustom.setGeneralPolicy(generalPolicy);
                    }
                    generalPolicy.setByHourCustom(byHourCustoms);

                    break;
                case ByLocalTime:
                    Set<ByLocalTime> byLocalTimes = generalPolicy.getByLocalTime();
                    for (ByLocalTime byLocalTime : byLocalTimes) {
                        byLocalTime.setGeneralPolicy(generalPolicy);
                    }
                    generalPolicy.setByLocalTime(byLocalTimes);
                    break;
                case ByLocalTimeCustom:
                    Set<ByLocalTimeCustom> localTimeCustoms = generalPolicy.getByLocalTimeCustom();
                    for (ByLocalTimeCustom localTimeCustom : localTimeCustoms) {
                        localTimeCustom.setGeneralPolicy(generalPolicy);
                    }
                    generalPolicy.setByLocalTimeCustom(localTimeCustoms);
                    break;
            }
            generalPolicy.setParking(parking);
        }

        parking.setStatus("Αναμονή Αποστολής Εγγράφων");
        List<String> AfmOwners = (List<String>) request.get("AfmOwners");
        if (AfmOwners.size()==1 && AfmOwners.get(0).equals("")) {
            user = entityManager.merge(user);
            service_parking.savewithoneuser(parking, user);
            List<ResponceCities> cities = service_parking.getCities();
            webSocketController.broadcastCities(cities);
            return ResponseEntity.ok(Map.of("message", "Το πάρκινγκ καταχωρήθηκε με επιτυχία!(Αναμονή Έγκρισης από διαχειριστή εφαρμογής.)"));
        }
        Set<Users> users = new HashSet<>();
        for (String AfmOwner : AfmOwners) {
            if (!service_Users.existUserByAfm_Owner(AfmOwner)) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Ο Χρήστης με ΑΦΜ:" + AfmOwner + ", δεν υπάρχει!");
            }
            users.add(service_Users.getUserByAfm_Owner(AfmOwner));
            users.add(service_Users.getUserByAfm_Owner(user.getAfm_Owner()));
        }
        service_parking.savewithmanyusers(parking, users);
        return ResponseEntity.ok(Map.of("message", "Το πάρκινγκ καταχωρήθηκε με επιτυχία! (Αναμονή Έγκρισης από έτερους ιδιοκτήτες και κατόπιν από διαχειριστή εφαρμογής."));
        //todo email notification

    }


    @GetMapping("/myparking")
    public ResponseEntity<?> myparking(Authentication authentication) {
        Users user = (Users) authentication.getPrincipal();
        Set<Parking> parkings = this.service_parking.getParkingByUsers(user);
        List<ResponcemyParking> responcemyParkings = new ArrayList<>(); //ΛΙΣΤΑ ΓΙΑ ΚΑΘΕ ΠΑΡΚΙΝΓΚ
        for (Parking parking : parkings) {
            ResponcemyParking responcemyParking = new ResponcemyParking();
            responcemyParking.setId(parking.getId());
            responcemyParking.setNameofparking(parking.getName_of_parking());
            responcemyParking.setAddress(parking.getAddress());
            responcemyParking.setCity(parking.getCity());
            responcemyParking.setTK(parking.getTK());
            responcemyParking.setAfm(parking.getAfm());
            if (parking.getIsactive()) {
                responcemyParking.setStatus("Ενεργό");
            } else {
                responcemyParking.setStatus(parking.getStatus());
            }
            List<Map<String, Object>> users = new ArrayList<>();
            for (Users user1 : parking.getUsers()) {
                Map<String, Object> userspakring = new HashMap<>();
                userspakring.put("userId", user1.getId());
                userspakring.put("username", user1.getUsername());
                users.add(userspakring);
            }

            List<Map<String, String>> fileList = new ArrayList<>();

            for (String path : parking.getDocumentPaths()) {
                Map<String, String> fileData = new HashMap<>();

                // Παίρνουμε μόνο το όνομα του αρχείου (χωρίς path)
                String fileName = path.substring(path.lastIndexOf("/") + 1);

                // Βγάζουμε το UUID για να εμφανιστεί το αρχικό όνομα
                String originalName = fileName.contains("_")
                        ? fileName.substring(fileName.indexOf("_") + 1)
                        : fileName;

                fileData.put("fullPath", fileName); // αυτό στέλνεις στον GET /files/{fileName}
                fileData.put("fileName", originalName); // για εμφάνιση στο frontend

                fileList.add(fileData);
            }

            responcemyParking.setDocumentPaths(fileList);
            responcemyParking.setUsers(users);
            responcemyParkings.add(responcemyParking);
        }
        return ResponseEntity.ok(responcemyParkings);
    }




    @PostMapping("/upload-files")
    public ResponseEntity<?> uploadFiles(
            @RequestParam Long id,
            @RequestParam("files") MultipartFile[] files, Authentication authentication
    ) {
        Users user = (Users) authentication.getPrincipal();
        Set<Parking> parkings = service_parking.getParkingByUsers(user);
        boolean exists=false;
        for (Parking p:parkings){
            if (p.getId()==id){
                exists=true;
                break;
            }
        }
        if(!exists){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(Map.of("message", "Το Parking για το οποίο απευθείνεστε έιτε δεν υπάρχει είτε δεν έχετε δικαίωμα"));
        }
        try {
            Parking parking = service_parking.getParkingById(id);
            List<String> paths = new ArrayList<>();
            Files.createDirectories(uploadDir);

            for (MultipartFile file : files) {
                // Optional: MIME type validation
                String contentType = file.getContentType();
                if (!List.of("application/pdf", "image/jpeg", "image/png").contains(contentType)) {
                    return ResponseEntity.badRequest().body("Unsupported file type: " + contentType);
                }

                // Δημιουργία μοναδικού filename
                String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path targetPath = uploadDir.resolve(filename);
                Files.write(targetPath, file.getBytes());

                // Αποθήκευση ΜΟΝΟ του filename (π.χ. "abc123_Αρχείο.pdf")
                paths.add(filename);
            }

            // Προσθήκη των paths στον πίνακα εγγράφων του parking
            parking.getDocumentPaths().addAll(paths);
            if(parking.getUsers().size()==1){
                parking.setStatus("Αναμονή Έγκρισης από Διαχειριστή.");
            }
            else {
                parking.setStatus("Αναμονή Έγκρισης από Συνιδιοκτήτες και κατόπιν από Διαχειριστή.");
            }
            service_parking.save(parking);

            return ResponseEntity.ok(Map.of("message", "Αρχεία αποθηκεύτηκαν επιτυχώς."));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Σφάλμα κατά την αποθήκευση των αρχείων.");
        }
    }



    @GetMapping("/getfiles_of_myparking")
    public ResponseEntity<Resource> downloadFileOfMyParking(
            @RequestParam String filename,
            Authentication authentication) {

        Users user = (Users) authentication.getPrincipal();
        System.out.println(user.getRoles());
        Set<Parking> parkings = service_parking.getParkingByUsers(user);

        boolean exists = parkings.stream()
                .flatMap(p -> p.getDocumentPaths().stream())
                .anyMatch(doc -> doc.endsWith(filename));

        if (!exists) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        try {
            Path filePath = uploadDir.resolve(filename).normalize();

            if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                return ResponseEntity.notFound().build();
            }

            UrlResource resource = new UrlResource(filePath.toUri());
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                    .replace("+", "%20");

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename*=UTF-8''" + encodedFilename)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    /**
     ----------------------------OWNER'S CONTROLLER END -----------------------------------------------------
     */


    /**
     *
   -----------------------------WEBSOCKET CITIES---------------------
     */
    @GetMapping("/cities")
    public ResponseEntity<List<ResponceCities>> getCities() {
        List<ResponceCities> cities = service_parking.getCities();
        List<ResponceCities> uniquecities = new ArrayList<>();
        List<String> seenNames = new ArrayList<>();

        for (ResponceCities city : cities) {
            if (!seenNames.contains(city.getNamecity())) {
                seenNames.add(city.getNamecity());
                uniquecities.add(city);
            }
        }

        return ResponseEntity.ok(uniquecities);

    }


    /**
     *
     -----------------------------WEBSOCKET CITIES END---------------------
     */




    /**
     *
     -----------------------------GOOGLE API LOCATION---------------------
     */

    @GetMapping("/location")
    public ResponseEntity<?> getPredictions(@RequestParam String q) throws Exception {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
        PlaceAutocompleteRequest.SessionToken sessionToken = new PlaceAutocompleteRequest.SessionToken();

        AutocompletePrediction[] predictions = PlacesApi.placeAutocomplete(context, q, sessionToken)
                .language("el")
                .components(ComponentFilter.country("gr"))
                .types(PlaceAutocompleteType.GEOCODE)
                .await();

        List<String> descriptions = new ArrayList<>();
        List<String> areas = new ArrayList<>();
        List<String> ids = new ArrayList<>();

        for (AutocompletePrediction prediction : predictions) {
            String placeId = prediction.placeId;
            PlaceDetails details = PlacesApi.placeDetails(context, placeId).language("el").await();

            descriptions.add(prediction.description);
            ids.add(placeId);

            String neighborhood = Arrays.stream(details.addressComponents)
                    .filter(c -> {
                        List<AddressComponentType> types = Arrays.asList(c.types);
                        return types.contains(AddressComponentType.SUBLOCALITY_LEVEL_1)
                                || types.contains(AddressComponentType.SUBLOCALITY)
                                || types.contains(AddressComponentType.NEIGHBORHOOD);
                    })
                    .map(c -> c.longName)
                    .findFirst()
                    .orElse("Άγνωστη Περιοχή");
            areas.add(neighborhood);
        }

        Map<String, List> maps = new HashMap<>();
        maps.put("Description", descriptions);
        maps.put("Neighborhood", areas);
        maps.put("Id", ids);
        return ResponseEntity.ok(maps);
    }



    /**
     *
     -----------------------------GOOGLE API LOCATION END---------------------
     */





    /**
     *
     -----------------------------ADMIN CONTROLLERS---------------------
     */


    @GetMapping("/getParkings")
    public ResponseEntity<?> getParkings() {
        List<Parking> parkings = service_parking.getParkings();
        List<ResponcemyParking> responcemyParkingList = new ArrayList<>();
        for (Parking parking : parkings) {
            ResponcemyParking responcemyParking = new ResponcemyParking();
            responcemyParking.setId(parking.getId());
            responcemyParking.setNameofparking(parking.getName_of_parking());
            responcemyParking.setAddress(parking.getAddress());
            responcemyParking.setCity(parking.getCity());
            responcemyParking.setTK(parking.getTK());
            responcemyParking.setAfm(parking.getAfm());
            if (parking.getIsactive()) {
                responcemyParking.setStatus("Ενεργό");
            } else {
                responcemyParking.setStatus(parking.getStatus());
            }
            List<Map<String, Object>> users = new ArrayList<>();
            for (Users user1 : parking.getUsers()) {
                Map<String, Object> userspakring = new HashMap<>();
                userspakring.put("userId", user1.getId());
                userspakring.put("username", user1.getUsername());
                userspakring.put("lastname", user1.getLastname());
                userspakring.put("name", user1.getName());
                userspakring.put("email", user1.getEmail());
                userspakring.put("Afm", user1.getAfm_Owner());
                userspakring.put("Identity", user1.getIdentity());
                users.add(userspakring);
            }

            List<Map<String, String>> fileList = new ArrayList<>();

            for (String path : parking.getDocumentPaths()) {
                Map<String, String> fileData = new HashMap<>();

                // Παίρνουμε μόνο το όνομα του αρχείου (χωρίς path)
                String fileName = path.substring(path.lastIndexOf("/") + 1);

                // Βγάζουμε το UUID για να εμφανιστεί το αρχικό όνομα
                String originalName = fileName.contains("_")
                        ? fileName.substring(fileName.indexOf("_") + 1)
                        : fileName;

                fileData.put("fullPath", fileName); // αυτό στέλνεις στον GET /files/{fileName}
                fileData.put("fileName", originalName); // για εμφάνιση στο frontend

                fileList.add(fileData);
            }

            responcemyParking.setDocumentPaths(fileList);
            responcemyParking.setUsers(users);
            responcemyParkingList.add(responcemyParking);
        }
        return ResponseEntity.ok(responcemyParkingList);
    }


    //TODO
    @PostMapping("/validate_parking_admin")
    public ResponseEntity<?> validateParking(@RequestParam Long Id) {
        Parking parking = service_parking.getParkingById(Id);
        parking.setIsactive(true);
        parking.setStatus("Ενεργό");
        this.service_parking.save(parking);
        return ResponseEntity.ok(Map.of("message", "Το parking ενεργοποιήθηκε και είναι πλέον διαθέσιμο"));
    }


    @PostMapping("/reject_parking_admin")
    public ResponseEntity<?> rejectParking(@RequestParam Long Id, @RequestParam String status) {
        Parking parking = service_parking.getParkingById(Id);
        parking.setStatus("Απόρριψη:\n"+ status);
        parking.setIsactive(false);
        this.service_parking.save(parking);
        return ResponseEntity.ok(Map.of("message", "Το parking απορρίφθηκε προς το παρόν."));
    }


    @GetMapping("/getfilesofparking")
    public ResponseEntity<Resource> downloadFilesParking(
            @RequestParam String filename) {

        try {
            Path filePath = uploadDir.resolve(filename).normalize();

            if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                return ResponseEntity.notFound().build();
            }

            UrlResource resource = new UrlResource(filePath.toUri());
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                    .replace("+", "%20");

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename*=UTF-8''" + encodedFilename)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     *
     -----------------------------ADMIN CONTROLLERS END---------------------
     */





}

